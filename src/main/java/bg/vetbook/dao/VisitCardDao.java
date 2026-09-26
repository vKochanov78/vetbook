package bg.vetbook.dao;

import bg.vetbook.model.Visit;
import bg.vetbook.model.VisitStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Работи с един преглед: зарежда го, записва го и казва
// кои прегледи има даден лекар за даден ден.
public class VisitCardDao {

    private Database database;

    public VisitCardDao(Database database) {
        this.database = database;
    }

    // Зарежда прегледа с този номер. Връща null, ако няма такъв.
    public Visit findById(int visitId) throws SQLException {
        String sql = SELECT_VISIT + "WHERE v.id = ?";

        try (Connection conn = database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visitId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return readVisit(rs);
                }
            }
        }
        return null;
    }

    // Прегледите на един лекар за една дата, подредени по час.
    // Отменените не се броят - те не заемат лекаря.
    // Ползва се и за дневния лимит, и за проверката за застъпване по час.
    public List<Visit> findByDoctorAndDate(int doctorId, String date) throws SQLException {
        List<Visit> visits = new ArrayList<>();
        String sql = SELECT_VISIT +
                "WHERE v.doctor_id = ? AND substr(v.visit_at, 1, 10) = ? AND v.status <> ? " +
                "ORDER BY v.visit_at";

        try (Connection conn = database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setString(2, date);
            stmt.setString(3, VisitStatus.CANCELLED);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visits.add(readVisit(rs));
                }
            }
        }
        return visits;
    }

    // Записва нов преглед и връща номера, който базата му е дала.
    public int insert(Visit visit) throws SQLException {
        String sql = "INSERT INTO visits " +
                "(animal_id, doctor_id, visit_at, complaint, diagnosis, status, consultation_fee) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // id не се подава - базата го дава сама.
        // RETURN_GENERATED_KEYS казва на базата да ни го върне след записа.
        try (Connection conn = database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, visit.getAnimalId());
            stmt.setInt(2, visit.getDoctorId());
            stmt.setString(3, visit.getVisitAt());
            stmt.setString(4, visit.getComplaint());
            stmt.setString(5, visit.getDiagnosis());
            stmt.setString(6, visit.getStatus());
            stmt.setDouble(7, visit.getConsultationFee());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    visit.setId(keys.getInt(1));
                }
            }
        }
        return visit.getId();
    }

    // Записва промените по съществуващ преглед.
    public void update(Visit visit) throws SQLException {
        String sql = "UPDATE visits SET " +
                "animal_id = ?, doctor_id = ?, visit_at = ?, complaint = ?, " +
                "diagnosis = ?, status = ?, consultation_fee = ? " +
                "WHERE id = ?";

        try (Connection conn = database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visit.getAnimalId());
            stmt.setInt(2, visit.getDoctorId());
            stmt.setString(3, visit.getVisitAt());
            stmt.setString(4, visit.getComplaint());
            stmt.setString(5, visit.getDiagnosis());
            stmt.setString(6, visit.getStatus());
            stmt.setDouble(7, visit.getConsultationFee());
            stmt.setInt(8, visit.getId());

            stmt.executeUpdate();
        }
    }

    // Заявката е една и съща за двата метода отгоре, затова стои на едно място.
    // Имената на животното, собственика и лекаря идват от съседните таблици.
    private static final String SELECT_VISIT =
            "SELECT v.id, v.animal_id, v.doctor_id, v.visit_at, v.complaint, " +
            "v.diagnosis, v.status, v.consultation_fee, " +
            "a.name AS animal_name, o.full_name AS owner_name, d.full_name AS doctor_name " +
            "FROM visits v " +
            "JOIN animals a ON v.animal_id = a.id " +
            "JOIN owners o ON a.owner_id = o.id " +
            "JOIN doctors d ON v.doctor_id = d.id ";

    // Прочита текущия ред и го налива в обект.
    // Отделен метод, за да не се пишат едни и същи единайсет реда два пъти.
    private Visit readVisit(ResultSet rs) throws SQLException {
        Visit visit = new Visit();
        visit.setId(rs.getInt("id"));
        visit.setAnimalId(rs.getInt("animal_id"));
        visit.setDoctorId(rs.getInt("doctor_id"));
        visit.setVisitAt(rs.getString("visit_at"));
        visit.setComplaint(rs.getString("complaint"));
        visit.setDiagnosis(rs.getString("diagnosis"));
        visit.setStatus(rs.getString("status"));
        visit.setConsultationFee(rs.getDouble("consultation_fee"));
        visit.setAnimalName(rs.getString("animal_name"));
        visit.setOwnerName(rs.getString("owner_name"));
        visit.setDoctorName(rs.getString("doctor_name"));
        return visit;
    }
}
