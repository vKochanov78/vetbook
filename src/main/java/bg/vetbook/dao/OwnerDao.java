package bg.vetbook.dao;
import bg.vetbook.model.Owner;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class OwnerDao {

    private final Database database;
    public OwnerDao(Database database){
    this.database=database;
  }
  public List<Owner> getAllOwners() throws SQLException {
        List<Owner> owners = new ArrayList<>();
        String sql= "SELECT id,full_name,phone,email FROM owners";
        try(Connection conn=database.connect();
        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery((sql))) {
            while (rs.next()){
                Owner owner = new Owner(rs.getInt("id"),
                                        rs.getString("full_name"),
                                        rs.getString("phone"),
                                        rs.getString("email")
                        );
              owners.add(owner);
            }

        }
        return owners;
    }
 }