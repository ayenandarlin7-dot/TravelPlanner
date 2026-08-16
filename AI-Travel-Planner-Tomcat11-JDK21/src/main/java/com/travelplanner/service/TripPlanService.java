package com.travelplanner.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.travelplanner.dao.AttractionDAO;
import com.travelplanner.dao.FoodEstimateDAO;
import com.travelplanner.dao.HotelDAO;
import com.travelplanner.dao.RouteDAO;
import com.travelplanner.model.Attraction;
import com.travelplanner.model.FoodEstimate;
import com.travelplanner.model.Hotel;
import com.travelplanner.model.Route;
import com.travelplanner.model.TripPlan;

/**
 * Builds complete trip plans (route + hotel + food + attractions) and computes
 * the full estimated cost breakdown.
 *
 * <h2>Transportation selection</h2>
 * Bus / Train / Flight evaluate only that single mode. Auto (or "any") compares
 * the available Bus, Train and Flight options together.
 *
 * <h2>Cost formulas</h2>
 * <ul>
 * <li>Trip Days = Return Date - Departure Date + 1 (1 for a single day)</li>
 * <li>Hotel Nights = Return Date - Departure Date (0 for same-day trips)</li>
 * <li>Rooms Required = Ceil(Number of Travellers / Room Capacity)</li>
 * <li>Transportation Cost = Price per Person x Number of Travellers</li>
 * <li>Hotel Cost = Price per Night x Hotel Nights x Rooms Required</li>
 * <li>Food Cost = Daily Cost per Person x Trip Days x Number of Travellers</li>
 * <li>Attraction Cost = Sum(Entrance Fees) x Number of Travellers</li>
 * <li>Total = Transportation + Hotel + Food + Attraction</li>
 * </ul>
 *
 * <h2>Budget allocation</h2>
 * Budget is split in a deterministic order:
 * <ol>
 * <li>Transportation is taken from the route price.</li>
 * <li>Food is given 25% of the remaining budget; the highest tier
 * (Economy / Standard / Premium) that fits is chosen, otherwise Economy.</li>
 * <li>Hotels use the budget left after food. Categories are tried in a
 * preference-weighted order; the highest-rated affordable hotel wins.</li>
 * <li>Attractions use whatever is left. Free / low-cost attractions are
 * preferred automatically because they are sorted by entrance fee.</li>
 * </ol>
 *
 * <h2>Budget status</h2>
 * "Within Budget" when total &le; budget. "Nearly Over Budget" when total is
 * above budget by at most the 10% threshold. "Over Budget" otherwise.
 */
public class TripPlanService {

	/*
	 * A trip is "nearly over budget" when its total cost exceeds the user budget
	 * by no more than this ratio (10%).
	 */
	private static final BigDecimal NEARLY_OVER_BUDGET_RATIO = new BigDecimal("0.10");

	/*
	 * Fraction of the budget left after transportation that is allocated to food.
	 */
	private static final BigDecimal FOOD_BUDGET_RATIO = new BigDecimal("0.25");

	/*
	 * Maximum number of attractions recommended in one plan.
	 */
	private static final int MAX_ATTRACTIONS = 6;

	private static final String STATUS_WITHIN_BUDGET = "Within Budget";
	private static final String STATUS_NEARLY_OVER_BUDGET = "Nearly Over Budget";
	private static final String STATUS_OVER_BUDGET = "Over Budget";

	private final RouteDAO routeDAO = new RouteDAO();

	private final HotelDAO hotelDAO = new HotelDAO();

	private final FoodEstimateDAO foodEstimateDAO = new FoodEstimateDAO();

	private final AttractionDAO attractionDAO = new AttractionDAO();

	private final RecommendationService recommendationService = new RecommendationService();

	/**
	 * Finds every route between the two cities (honouring the transportation
	 * choice) and returns the full plans ranked by the user preference.
	 */
	public List<TripPlan> buildPlans(int startingCityId, int destinationCityId, Integer transportationId,
			LocalDate travelDate, LocalDate returnDate, int numberOfTravellers, BigDecimal budget, String preference)
			throws SQLException {

		return buildPlans(startingCityId, destinationCityId, transportationId, travelDate, returnDate,
				numberOfTravellers, budget, preference, null, null);
	}

	/**
	 * Same as {@link #buildPlans(int, int, Integer, LocalDate, LocalDate, int,
	 * BigDecimal, String)} but with optional search overrides used by the
	 * over-budget suggestions:
	 * <ul>
	 * <li>{@code hotelCategory} forces hotel selection to that category.</li>
	 * <li>{@code excludeAttractionIds} excludes the given attractions.</li>
	 * </ul>
	 */
	public List<TripPlan> buildPlans(int startingCityId, int destinationCityId, Integer transportationId,
			LocalDate travelDate, LocalDate returnDate, int numberOfTravellers, BigDecimal budget, String preference,
			String hotelCategory, List<Integer> excludeAttractionIds) throws SQLException {

		boolean compareAll = transportationId == null || transportationId == 4;

		List<Route> routes = compareAll ? routeDAO.findRoutes(startingCityId, destinationCityId, null)
				: routeDAO.findRoutes(startingCityId, destinationCityId, transportationId);

		List<TripPlan> plans = new ArrayList<>();

		for (Route route : routes) {

			/*
			 * "Auto" means compare Bus / Train / Flight; the Auto transport record
			 * itself is never a route option.
			 */
			if (route.getTransportation().getTransportationId() == 4) {
				continue;
			}

			plans.add(buildPlan(route, travelDate, returnDate, numberOfTravellers, budget, preference, null, null, null,
					hotelCategory, excludeAttractionIds));
		}

		return recommendationService.rankPlans(plans, preference);
	}

	/**
	 * Builds one plan for a route. When explicit selections are supplied
	 * (hotelId, foodTier, attractionIds) those are used instead of the automatic
	 * budget-driven choices, so the plan stays identical when it is rebuilt
	 * during the Trip Summary and Save steps.
	 */
	public TripPlan buildPlan(Route route, LocalDate travelDate, LocalDate returnDate, int numberOfTravellers,
			BigDecimal budget, String preference, Integer hotelId, String foodTier, List<Integer> attractionIds)
			throws SQLException {

		return buildPlan(route, travelDate, returnDate, numberOfTravellers, budget, preference, hotelId, foodTier,
				attractionIds, null, null);
	}

	/**
	 * Same as
	 * {@link #buildPlan(Route, LocalDate, LocalDate, int, BigDecimal, String,
	 * Integer, String, List)} with the optional search overrides used by the
	 * over-budget suggestions.
	 */
	public TripPlan buildPlan(Route route, LocalDate travelDate, LocalDate returnDate, int numberOfTravellers,
			BigDecimal budget, String preference, Integer hotelId, String foodTier, List<Integer> attractionIds,
			String hotelCategory, List<Integer> excludeAttractionIds) throws SQLException {

		TripPlan plan = new TripPlan();

		plan.setRoute(route);

		int tripDays = computeTripDays(travelDate, returnDate);

		int hotelNights = computeHotelNights(travelDate, returnDate);

		plan.setTripDays(tripDays);

		plan.setHotelNights(hotelNights);

		plan.setNumberOfTravellers(numberOfTravellers);

		/*
		 * Transportation Cost = Price per Person x Number of Travellers.
		 */
		BigDecimal transportationCost = route.getEstimatedCost().multiply(BigDecimal.valueOf(numberOfTravellers));

		plan.setTransportationCost(transportationCost);

		BigDecimal remaining = budget.subtract(transportationCost);

		/*
		 * Food Cost = Daily Cost per Person x Trip Days x Number of Travellers.
		 * The tier is chosen from the 25% food allocation of the remaining budget.
		 */
		FoodEstimate foodEstimate = null;

		if (foodTier != null && !foodTier.isBlank()) {

			Optional<FoodEstimate> selected = foodEstimateDAO
					.findByCityIdAndTier(route.getDestinationCity().getCityId(), foodTier);

			if (selected.isPresent()) {
				foodEstimate = selected.get();
			}
		}

		if (foodEstimate == null) {
			foodEstimate = selectFoodEstimate(route.getDestinationCity().getCityId(), tripDays, numberOfTravellers,
					remaining);
		}

		plan.setFoodEstimate(foodEstimate);

		BigDecimal foodCost = foodEstimate.getDailyCostPerPerson().multiply(BigDecimal.valueOf(tripDays))
				.multiply(BigDecimal.valueOf(numberOfTravellers));

		plan.setFoodCost(foodCost);

		BigDecimal remainingAfterFood = remaining.subtract(foodCost);

		/*
		 * Hotel Cost = Price per Night x Hotel Nights x Rooms Required.
		 * Same-day trips have zero hotel nights, so no hotel is needed.
		 */
		Hotel hotel = null;

		BigDecimal hotelCost = BigDecimal.ZERO;

		int roomsRequired = 0;

		if (hotelNights > 0) {

			if (hotelId != null) {

				Optional<Hotel> selected = hotelDAO.findById(hotelId);

				if (selected.isPresent()) {
					hotel = selected.get();
				}
			}

			if (hotel == null) {
				hotel = selectHotel(route.getDestinationCity().getCityId(), hotelNights, numberOfTravellers,
						remainingAfterFood, preference, hotelCategory);
			}

			if (hotel != null) {

				roomsRequired = roomsRequired(numberOfTravellers, hotel.getRoomCapacity());

				hotelCost = hotel.getPricePerNight().multiply(BigDecimal.valueOf(hotelNights))
						.multiply(BigDecimal.valueOf(roomsRequired));
			}
		}

		plan.setHotel(hotel);

		plan.setRoomsRequired(roomsRequired);

		plan.setHotelCost(hotelCost);

		BigDecimal remainingAfterHotel = remainingAfterFood.subtract(hotelCost);

		/*
		 * Attraction Cost = Sum(Entrance Fees) x Number of Travellers.
		 */
		List<Attraction> attractions;

		if (attractionIds != null && !attractionIds.isEmpty()) {

			attractions = attractionDAO.findByIds(attractionIds);

		} else {

			attractions = selectAttractions(route.getDestinationCity().getCityId(), remainingAfterHotel,
					numberOfTravellers, excludeAttractionIds);
		}

		plan.setAttractions(attractions);

		BigDecimal attractionCost = sumEntranceFees(attractions).multiply(BigDecimal.valueOf(numberOfTravellers));

		plan.setAttractionCost(attractionCost);

		BigDecimal total = transportationCost.add(hotelCost).add(foodCost).add(attractionCost);

		plan.setTotalEstimatedCost(total);

		plan.setRemainingBudget(budget.subtract(total));

		plan.setBudgetStatus(computeBudgetStatus(total, budget));

		return plan;
	}

	/**
	 * Trip Days = Return Date - Departure Date + 1. When no return date is given
	 * (or it is on the same day) the trip is treated as a single day.
	 */
	private int computeTripDays(LocalDate travelDate, LocalDate returnDate) {

		if (travelDate == null) {
			return 1;
		}

		if (returnDate == null || !returnDate.isAfter(travelDate)) {
			return 1;
		}

		return (int) (ChronoUnit.DAYS.between(travelDate, returnDate) + 1);
	}

	/**
	 * Hotel Nights = Return Date - Departure Date, never negative. Same-day
	 * travel yields zero nights.
	 */
	private int computeHotelNights(LocalDate travelDate, LocalDate returnDate) {

		if (travelDate == null || returnDate == null || !returnDate.isAfter(travelDate)) {
			return 0;
		}

		return (int) ChronoUnit.DAYS.between(travelDate, returnDate);
	}

	/**
	 * Rooms Required = Ceil(Number of Travellers / Room Capacity).
	 */
	private int roomsRequired(int numberOfTravellers, int roomCapacity) {

		if (roomCapacity <= 0) {
			return Math.max(numberOfTravellers, 1);
		}

		return (numberOfTravellers + roomCapacity - 1) / roomCapacity;
	}

	/**
	 * Picks the highest food tier whose total cost fits the 25% food allocation.
	 * Economy is the fallback when even that cannot fit the allocation.
	 */
	private FoodEstimate selectFoodEstimate(int cityId, int tripDays, int numberOfTravellers, BigDecimal remaining)
			throws SQLException {

		List<FoodEstimate> estimates = foodEstimateDAO.findByCityId(cityId);

		if (estimates.isEmpty()) {
			throw new IllegalStateException("No food estimates configured for destination city id " + cityId);
		}

		BigDecimal foodAllocation = remaining.multiply(FOOD_BUDGET_RATIO);

		FoodEstimate bestFit = estimates.get(0);

		for (FoodEstimate estimate : estimates) {

			BigDecimal total = estimate.getDailyCostPerPerson().multiply(BigDecimal.valueOf(tripDays))
					.multiply(BigDecimal.valueOf(numberOfTravellers));

			if (total.compareTo(foodAllocation) <= 0) {

				/*
				 * Estimates come ordered by daily cost (Economy -> Standard ->
				 * Premium), so the last fit is the highest affordable tier.
				 */
				bestFit = estimate;
			}
		}

		return bestFit;
	}

	/**
	 * Chooses a hotel for the destination within the budget remaining after food.
	 *
	 * <p>
	 * Categories are tried in an order derived from the preference (cheapest
	 * starts with Budget; the others start with Standard). The first category
	 * with an affordable option contributes its highest-rated hotel. When nothing
	 * fits, the cheapest overall hotel is returned as a fallback.
	 *
	 * <p>
	 * When {@code hotelCategory} is supplied (over-budget suggestion), only that
	 * category is considered.
	 */
	private Hotel selectHotel(int cityId, int hotelNights, int numberOfTravellers, BigDecimal budgetAfterFood,
			String preference, String hotelCategory) throws SQLException {

		List<Hotel> hotels = hotelDAO.findByCityId(cityId);

		if (hotels.isEmpty()) {
			return null;
		}

		String normalizedPreference = preference == null ? "cheapest" : preference.trim().toLowerCase(Locale.ROOT);

		List<String> categoryOrder;

		if (hotelCategory != null && !hotelCategory.isBlank()) {

			categoryOrder = List.of(hotelCategory.trim());

		} else {

			categoryOrder = switch (normalizedPreference) {

			case "fastest", "shortest", "balanced" -> List.of("Standard", "Budget", "Luxury");

			default -> List.of("Budget", "Standard", "Luxury");
			};
		}

		for (String category : categoryOrder) {

			List<Hotel> affordable = hotels.stream().filter(hotel -> category.equals(hotel.getCategory()))
					.filter(hotel -> totalHotelCost(hotel, hotelNights, numberOfTravellers).compareTo(budgetAfterFood) <= 0)
					.toList();

			if (!affordable.isEmpty()) {

				return affordable.stream()
						.max(Comparator.comparing(Hotel::getRating, Comparator.nullsFirst(Comparator.naturalOrder()))
								.thenComparing(Comparator.comparing(Hotel::getHotelName)))
						.get();
			}
		}

		/*
		 * Fallback: the cheapest option, even if it exceeds the budget.
		 */
		return hotels.stream().min(Comparator.comparing(hotel -> totalHotelCost(hotel, hotelNights, numberOfTravellers)))
				.orElse(null);
	}

	private BigDecimal totalHotelCost(Hotel hotel, int hotelNights, int numberOfTravellers) {

		return hotel.getPricePerNight().multiply(BigDecimal.valueOf(hotelNights))
				.multiply(BigDecimal.valueOf(roomsRequired(numberOfTravellers, hotel.getRoomCapacity())));
	}

	/**
	 * Recommends attractions for the destination using the budget left after
	 * food and hotel. Attractions are sorted by entrance fee (free first) and
	 * taken greedily while they still fit the remaining budget, which naturally
	 * prefers free and low-cost attractions when the budget is tight.
	 *
	 * <p>
	 * Attractions listed in {@code excludeAttractionIds} (over-budget suggestion)
	 * are skipped entirely.
	 */
	private List<Attraction> selectAttractions(int cityId, BigDecimal budgetAfterHotel, int numberOfTravellers,
			List<Integer> excludeAttractionIds) throws SQLException {

		List<Attraction> attractions = attractionDAO.findByCityId(cityId);

		attractions.sort(Comparator.comparing(Attraction::getEntranceFee, Comparator.nullsFirst(Comparator.naturalOrder())));

		List<Attraction> selected = new ArrayList<>();

		BigDecimal used = BigDecimal.ZERO;

		for (Attraction attraction : attractions) {

			if (selected.size() >= MAX_ATTRACTIONS) {
				break;
			}

			if (excludeAttractionIds != null && excludeAttractionIds.contains(attraction.getAttractionId())) {
				continue;
			}

			BigDecimal fee = attraction.getEntranceFee() == null ? BigDecimal.ZERO : attraction.getEntranceFee();

			BigDecimal cost = fee.multiply(BigDecimal.valueOf(numberOfTravellers));

			if (cost.compareTo(BigDecimal.ZERO) > 0 && used.add(cost).compareTo(budgetAfterHotel) > 0) {
				continue;
			}

			selected.add(attraction);

			used = used.add(cost);
		}

		return selected;
	}

	private BigDecimal sumEntranceFees(List<Attraction> attractions) {

		BigDecimal total = BigDecimal.ZERO;

		for (Attraction attraction : attractions) {

			if (attraction.getEntranceFee() != null) {
				total = total.add(attraction.getEntranceFee());
			}
		}

		return total;
	}

	/**
	 * Within Budget when total &le; budget. Nearly Over Budget when the total is
	 * above budget by no more than 10% of the budget. Over Budget otherwise.
	 */
	private String computeBudgetStatus(BigDecimal total, BigDecimal budget) {

		if (total.compareTo(budget) <= 0) {
			return STATUS_WITHIN_BUDGET;
		}

		BigDecimal nearlyLimit = budget.multiply(BigDecimal.ONE.add(NEARLY_OVER_BUDGET_RATIO));

		if (total.compareTo(nearlyLimit) <= 0) {
			return STATUS_NEARLY_OVER_BUDGET;
		}

		return STATUS_OVER_BUDGET;
	}

	/**
	 * Exposed for tests / JSP display: formats a rounded MMK value.
	 */
	public static BigDecimal rounded(BigDecimal value) {

		if (value == null) {
			return BigDecimal.ZERO;
		}

		return value.setScale(0, RoundingMode.HALF_UP);
	}
}
