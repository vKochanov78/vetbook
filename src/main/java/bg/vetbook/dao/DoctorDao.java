package bg.vetbook.dao;

import bg.vetbook.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Чете лекарите от базата.
public class DoctorDao {
    private Database database;

    public DoctorDao(Database database) {
        this.database = database;
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT id, full_name, speciality FROM doctors " +
                "WHERE active = 1 ORDER BY full_name";

        try (Connection conn = database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Doctor doctor = new Doctor(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("speciality")
                );
                doctors.add(doctor);
            }
        }
        return doctors;
    }
}
