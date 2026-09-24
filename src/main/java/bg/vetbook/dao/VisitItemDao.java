package bg.vetbook.dao;

import bg.vetbook.model.VisitItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//  Отговаря за таблицата visit_items — процедурите и медикаментите по един преглед.
public class VisitItemDao {
    private Database database;

    public VisitItemDao(Database database) {
        this.database = database;
    }

    public List<VisitItem> findByVisit(int visitId) throws SQLException {
        List<VisitItem> items = new ArrayList<>();
        String sql = "SELECT id, visit_id, item_type, description, quantity, unit_price FROM visit_items " +
                "WHERE visit_id = ? ORDER BY id";

        try (Connection conn = database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visitId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VisitItem item = new VisitItem();
                    item.setId(rs.getInt("id"));
                    item.setVisitId(rs.getInt("visit_id"));
                    item.setItemType(rs.getString("item_type"));
                    item.setDescription(rs.getString("description"));
                    item.setQuantity(rs.getDouble("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));

                    items.add(item);

                }
            }
        }
        return items;
    }

    public void insert(VisitItem item) throws SQLException {
        String sql = "INSERT INTO visit_items (visit_id, item_type, description, quantity, unit_price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = database.connect();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            // id не се подава - базата го дава сама.
            stmt.setInt(1, item.getVisitId());
            stmt.setString(2, item.getItemType());
            stmt.setString(3, item.getDescription());
            stmt.setDouble(4, item.getQuantity());
            stmt.setDouble(5, item.getUnitPrice());

            stmt.executeUpdate();

        }
    }

    public void delete(int itemId) throws SQLException {
        String sql = "DELETE FROM visit_items WHERE id = ?";

        try(Connection conn = database.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);

            stmt.executeUpdate();

        }
    }
}