package model;

import java.util.List;

public class Itinerary {
    private String destination;
    private List<Hotel> hotels;
    private List<String> activities;

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public List<Hotel> getHotels() { return hotels; }
    public void setHotels(List<Hotel> hotels) { this.hotels = hotels; }

    public List<String> getActivities() { return activities; }
    public void setActivities(List<String> activities) { this.activities = activities; }

    @Override
    public String toString() {
        return "Destination: " + destination +
               "\nHotels: " + hotels +
               "\nActivities: " + activities;
    }
}
