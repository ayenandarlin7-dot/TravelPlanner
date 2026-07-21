package model;

public class Hotel {
    private int id;
    private String name;
    private double price;
    private int destId;

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getDestId() { return destId; }
    public void setDestId(int destId) { this.destId = destId; }
}
