import dao.HotelDAO;
import model.Hotel;
import model.Itinerary;
import Service.RecommendationEngine;

import java.util.*;

public class TravelPlanner {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter destination:");
        String destination = sc.nextLine();

        HotelDAO dao = new HotelDAO();
        List<Hotel> hotels = dao.getHotelsByDestination(destination);

        RecommendationEngine engine = new RecommendationEngine();
        Itinerary plan = engine.generateItinerary(destination, hotels);

        System.out.println("Your Travel Plan:");
        System.out.println(plan);
    }
}
