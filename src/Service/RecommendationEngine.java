package Service;

import model.Hotel;
import model.Itinerary;
import java.util.*;

public class RecommendationEngine {
    public Itinerary generateItinerary(String destination, List<Hotel> hotels) {
        Itinerary plan = new Itinerary();
        plan.setDestination(destination);
        plan.setHotels(hotels);
        plan.setActivities(Arrays.asList("City Tour", "Museum Visit", "Local Food"));
        return plan;
    }
}
