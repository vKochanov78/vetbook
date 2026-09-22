package bg.vetbook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VisitDAO {

    private final Database database;

    public VisitDAO(Database database) {
        this.database = database;
    }

    // Зарежда всички прегледи
    public ResultSet getAllVisits() throws SQLException {

        String sql = """
                SELECT
                    v.id,
                    v.visit_at,
                    a.name AS animal_name,
                    o.full_name AS owner_name,
                    d.full_name AS doctor_name,
                    v.status,
                    v.complaint,
                    v.diagnosis,
                    v.consultation_fee
                FROM visits v
                JOIN animals a ON v.animal_id = a.id
                JOIN owners o ON a.owner_id = o.id
                JOIN doctors d ON v.doctor_id = d.id
                ORDER BY v.visit_at DESC
                """;

        Connection connection = database.connect();

        PreparedStatement statement =
                connection.prepareStatement(sql);

        return statement.executeQuery();
    }

    // Филтриране по статус
    // Търсенето по име се прави в Java,
    // за да работи правилно с кирилица.
    public ResultSet searchVisits(String status)
            throws SQLException {

        StringBuilder sql = new StringBuilder("""
                SELECT
                    v.id,
                    v.visit_at,
                    a.name AS animal_name,
                    o.full_name AS owner_name,
                    d.full_name AS doctor_name,
                    v.status,
                    v.complaint,
                    v.diagnosis,
                    v.consultation_fee
                FROM visits v
                JOIN animals a ON v.animal_id = a.id
                JOIN owners o ON a.owner_id = o.id
                JOIN doctors d ON v.doctor_id = d.id
                WHERE 1 = 1
                """);

        boolean hasStatus =
                status != null
                        && !status.isBlank()
                        && !status.equals("ALL");

        if (hasStatus) {
            sql.append("""
                    
                    AND v.status = ?
                    """);
        }

        sql.append("""
                
                ORDER BY v.visit_at DESC
                """);

        Connection connection = database.connect();

        PreparedStatement statement =
                connection.prepareStatement(
                        sql.toString()
                );

        if (hasStatus) {
            statement.setString(1, status);
        }

        return statement.executeQuery();
    }
}