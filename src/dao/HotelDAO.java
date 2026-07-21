package dao;

import model.Hotel;
import java.sql.*;
import java.util.*;

public class HotelDAO {
    public List<Hotel> getHotelsByDestination(String destination) {
        List<Hotel> hotels = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT h.hotel_id, h.name, h.price, h.dest_id " +
                         "FROM Hotel h JOIN Destination d ON h.dest_id=d.dest_id WHERE d.name=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, destination);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Hotel hotel = new Hotel();
                hotel.setId(rs.getInt("hotel_id"));
                hotel.setName(rs.getString("name"));
                hotel.setPrice(rs.getDouble("price"));
                hotel.setDestId(rs.getInt("dest_id"));
                hotels.add(hotel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hotels;
    }
}
