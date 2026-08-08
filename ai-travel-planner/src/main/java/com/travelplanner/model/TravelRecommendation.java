package com.travelplanner.model;

public class TravelRecommendation {
    private City city;
    private Route route;
    private double score;
    private String reason;

    public TravelRecommendation(City city, Route route, double score, String reason) {
        this.city = city;
        this.route = route;
        this.score = score;
        this.reason = reason;
    }

    public City getCity() { return city; }
    public Route getRoute() { return route; }
    public double getScore() { return score; }
    public String getReason() { return reason; }
}
