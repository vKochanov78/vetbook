package bg.vetbook.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReportDao {

    private Database database;

    public ReportDao(Database database) {
        this.database = database;
    }

    // 1. Брой прегледи по статус за периода
    public Map<String, Integer> getVisitCountByStatus(String fromDate, String toDate) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();

        // ВНИМАНИЕ: Провери дали колоната за дата при теб е 'visit_date' или просто 'date'
        String sql = "SELECT status, COUNT(id) as total_visits " +
                "FROM visits " +
                "WHERE visit_at BETWEEN ? AND ? " +
                "GROUP BY status";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fromDate);
            pstmt.setString(2, toDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("total_visits");
                    stats.put(status != null ? status : "Непосочен", count);
                }
            }
        }
        return stats;
    }

    // 2. Приход по лекуващ лекар за периода
    public Map<String, Double> getIncomeByDoctor(String fromDate, String toDate) throws SQLException {
        Map<String, Double> incomeStats = new HashMap<>();

        // ВНИМАНИЕ: Провери дали колоната за цена е 'price', 'fee' и датата ('visit_date' или 'date')
        String sql = "SELECT d.full_name as doctor_name, SUM(v.consultation_fee) as total_income " +
                "FROM visits v " +
                "JOIN doctors d ON v.doctor_id = d.id " +
                "WHERE v.visit_at BETWEEN ? AND ? " +
                "GROUP BY d.full_name";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fromDate);
            pstmt.setString(2, toDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String docName = rs.getString("doctor_name");
                    double total = rs.getDouble("total_income");
                    incomeStats.put(docName != null ? docName : "Неизвестен", total);
                }
            }
        }
        return incomeStats;
    }
}