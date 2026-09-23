package bg.vetbook.dao;
import bg.vetbook.model.Animal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class AnimalDao {
    private Database database;
    public AnimalDao(Database database){
        this.database = database;
    }
    public List<Animal> getAllAnimals() throws SQLException {
        List<Animal> animals = new ArrayList<>();
        String sql = """
                      SELECT a.id, a.name, a.species, a.owner_id, o.full_name AS owner_name
                      FROM animals a
                      LEFT JOIN owners o ON a.owner_id = o.id;
                     """;
         try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             while (rs.next()) {
                 Animal animal = new Animal(
                         rs.getInt("id"),
                         rs.getString("name"),
                         rs.getString("species"),
                         rs.getInt("owner_id")
                 );
                 animal.setOwnerName(rs.getString("owner_name"));
                 animals.add(animal);
             }
         }
        return animals;
    }
}
