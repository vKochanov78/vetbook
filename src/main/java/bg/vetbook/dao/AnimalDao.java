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
    public void addAnimal(Animal animal) throws SQLException {
        String sql = "INSERT INTO animals (name, species, owner_id) VALUES (?, ?, ?)";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, animal.getName());
            pstmt.setString(2, animal.getSpecies());
            pstmt.setInt(3, animal.getOwnerId());
            pstmt.executeUpdate();
    }
  }
  public void updateAnimal(Animal animal) throws SQLException {
      String sql = "UPDATE animals SET name = ?, species = ?, owner_id = ? WHERE id = ?";
      try (Connection conn = database.connect();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          pstmt.setString(1, animal.getName());
          pstmt.setString(2, animal.getSpecies());
          pstmt.setInt(3, animal.getOwnerId());
          pstmt.setInt(4, animal.getId());
          pstmt.executeUpdate();
      }
  }
    public void deleteAnimal(int id) throws SQLException {
        String sql = "DELETE FROM animals WHERE id = ?";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
