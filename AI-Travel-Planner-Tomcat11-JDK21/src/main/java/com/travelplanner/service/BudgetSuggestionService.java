package com.travelplanner.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.travelplanner.dao.HotelDAO;
import com.travelplanner.dao.RouteDAO;
import com.travelplanner.model.BudgetSuggestion;
import com.travelplanner.model.Hotel;
import com.travelplanner.model.Route;
import com.travelplanner.model.TripPlan;

/**
 * Generates over-budget suggestions for a recommended trip plan together with
 * the potential savings of each suggestion.
 *
 * <p>
 * Every suggestion carries an {@code applyType}/{@code applyValue} pair so the
 * page can genuinely re-run the plan search with the change applied. No
 * suggestion is emitted unless a real, cheaper alternative exists in the data.
 *
 * <h2>Suggestion logic (only when the plan is over budget)</h2>
 * <ul>
 * <li>Cheaper route / transport: another route for the same city pair costs
 * less (covers "Flight -> Train or Bus" and "Select cheaper route").</li>
 * <li>Cheaper hotel: the destination has a hotel with a lower price per night
 * (covers "Luxury Hotel -> Budget Hotel").</li>
 * <li>Shorter trip: the trip is multi-day, so removing one night saves food and
 * hotel money.</li>
 * <li>Fewer paid attractions: the plan includes paid attractions that can be
 * replaced with free ones.</li>
 * </ul>
 */
public class BudgetSuggestionService {

	private final RouteDAO routeDAO = new RouteDAO();

	private final HotelDAO hotelDAO = new HotelDAO();

	/**
	 * Builds the list of over-budget suggestions for the given plan. When the
	 * plan is within budget, an empty list is returned.
	 */
	public List<BudgetSuggestion> generateSuggestions(TripPlan plan, BigDecimal budget, LocalDate travelDate,
			LocalDate returnDate) throws SQLException {

		List<BudgetSuggestion> suggestions = new ArrayList<>();

		if (plan == null || plan.getTotalEstimatedCost() == null || budget == null
				|| plan.getTotalEstimatedCost().compareTo(budget) <= 0) {
			return suggestions;
		}

		addCheaperTransportSuggestion(plan, suggestions);

		addCheaperHotelSuggestion(plan, suggestions);

		addShorterTripSuggestion(plan, travelDate, returnDate, suggestions);

		addFewerPaidAttractionsSuggestion(plan, suggestions);

		return suggestions;
	}

	/**
	 * Savings = (Current Route Cost - Cheapest Route Cost) x Travellers.
	 */
	private void addCheaperTransportSuggestion(TripPlan plan, List<BudgetSuggestion> suggestions) throws SQLException {

		Route currentRoute = plan.getRoute();

		List<Route> routes = routeDAO.findRoutes(currentRoute.getStartingCity().getCityId(),
				currentRoute.getDestinationCity().getCityId(), null);

		Route cheapestRoute = routes.stream()
				.filter(route -> route.getTransportation().getTransportationId() != 4)
				.min(Comparator.comparing(Route::getEstimatedCost)).orElse(null);

		if (cheapestRoute == null || cheapestRoute.getRouteId() == currentRoute.getRouteId()) {
			return;
		}

		BigDecimal savings = currentRoute.getEstimatedCost().subtract(cheapestRoute.getEstimatedCost())
				.multiply(BigDecimal.valueOf(plan.getNumberOfTravellers()));

		String title = "Take the " + cheapestRoute.getTransportation().getTransportName() + " instead of "
				+ currentRoute.getTransportation().getTransportName();

		String description = "A cheaper route exists: " + cheapestRoute.getTransportation().getTransportName() + " ("
				+ cheapestRoute.getRouteInfo() + ") for " + cheapestRoute.getEstimatedCost() + " MMK per person.";

		suggestions.add(new BudgetSuggestion("transportation",
				String.valueOf(cheapestRoute.getTransportation().getTransportationId()), title, description,
				rounded(savings)));
	}

	/**
	 * Savings = (Current Hotel Price - Cheapest Hotel Price) x Nights x Rooms.
	 */
	private void addCheaperHotelSuggestion(TripPlan plan, List<BudgetSuggestion> suggestions) throws SQLException {

		Hotel currentHotel = plan.getHotel();

		if (currentHotel == null || plan.getHotelNights() <= 0) {
			return;
		}

		List<Hotel> hotels = hotelDAO.findByCityId(currentHotel.getCity().getCityId());

		Hotel cheapestHotel = hotels.stream()
				.filter(hotel -> hotel.getPricePerNight().compareTo(currentHotel.getPricePerNight()) < 0)
				.min(Comparator.comparing(Hotel::getPricePerNight)).orElse(null);

		if (cheapestHotel == null) {
			return;
		}

		int roomsRequired = roomsRequired(plan.getNumberOfTravellers(), currentHotel.getRoomCapacity());

		BigDecimal savings = currentHotel.getPricePerNight().subtract(cheapestHotel.getPricePerNight())
				.multiply(BigDecimal.valueOf(plan.getHotelNights())).multiply(BigDecimal.valueOf(roomsRequired));

		String title = "Switch to the " + cheapestHotel.getCategory() + " hotel (" + cheapestHotel.getHotelName() + ")";

		String description = "The " + cheapestHotel.getCategory().toLowerCase() + " option costs "
				+ cheapestHotel.getPricePerNight() + " MMK per night instead of " + currentHotel.getPricePerNight()
				+ " MMK.";

		suggestions.add(new BudgetSuggestion("hotel", cheapestHotel.getCategory(), title, description,
				rounded(savings)));
	}

	/**
	 * Savings = (Food per Person per Day x Travellers) + (Hotel Price per Night
	 * x Rooms) for one removed day.
	 */
	private void addShorterTripSuggestion(TripPlan plan, LocalDate travelDate, LocalDate returnDate,
			List<BudgetSuggestion> suggestions) {

		if (travelDate == null || returnDate == null || !returnDate.isAfter(travelDate) || plan.getHotelNights() <= 0) {
			return;
		}

		LocalDate shorterReturnDate = returnDate.minusDays(1);

		BigDecimal dailyFood = BigDecimal.ZERO;

		if (plan.getFoodEstimate() != null) {
			dailyFood = plan.getFoodEstimate().getDailyCostPerPerson()
					.multiply(BigDecimal.valueOf(plan.getNumberOfTravellers()));
		}

		BigDecimal dailyHotel = BigDecimal.ZERO;

		if (plan.getHotel() != null) {
			dailyHotel = plan.getHotel().getPricePerNight()
					.multiply(BigDecimal.valueOf(plan.getRoomsRequired()));
		}

		BigDecimal savings = dailyFood.add(dailyHotel);

		String title = "Shorten the trip by one day (return " + shorterReturnDate + ")";

		String description = "One fewer day saves " + rounded(dailyFood) + " MMK on food and "
				+ rounded(dailyHotel) + " MMK on the hotel.";

		suggestions.add(new BudgetSuggestion("duration", shorterReturnDate.toString(), title, description,
				rounded(savings)));
	}

	/**
	 * Savings = Sum of the entrance fees of all paid attractions x Travellers.
	 */
	private void addFewerPaidAttractionsSuggestion(TripPlan plan, List<BudgetSuggestion> suggestions) {

		List<Integer> paidAttractionIds = new ArrayList<>();

		BigDecimal paidFees = BigDecimal.ZERO;

		for (com.travelplanner.model.Attraction attraction : plan.getAttractions()) {

			if (attraction.getEntranceFee() != null && attraction.getEntranceFee().signum() > 0) {

				paidAttractionIds.add(attraction.getAttractionId());

				paidFees = paidFees.add(attraction.getEntranceFee());
			}
		}

		if (paidAttractionIds.isEmpty()) {
			return;
		}

		BigDecimal savings = paidFees.multiply(BigDecimal.valueOf(plan.getNumberOfTravellers()));

		String title = "Replace " + paidAttractionIds.size() + " paid attraction(s) with free ones";

		String description = "The selected paid attractions cost " + paidFees
				+ " MMK per person. Free attractions in this destination can be visited instead.";

		String excludedIds = paidAttractionIds.stream().map(String::valueOf).collect(Collectors.joining(","));

		suggestions.add(new BudgetSuggestion("attractions", excludedIds, title, description, rounded(savings)));
	}

	private int roomsRequired(int numberOfTravellers, int roomCapacity) {

		if (roomCapacity <= 0) {
			return Math.max(numberOfTravellers, 1);
		}

		return (numberOfTravellers + roomCapacity - 1) / roomCapacity;
	}

	private BigDecimal rounded(BigDecimal value) {

		return value == null ? BigDecimal.ZERO : value.setScale(0, RoundingMode.HALF_UP);
	}
}
