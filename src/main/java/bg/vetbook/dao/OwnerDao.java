package bg.vetbook.dao;
import bg.vetbook.model.Owner;

import java.sql.*;
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
    public void addOwners(Owner owner)throws SQLException{
        String sql = "INSERT INTO owners(full_name,phone,email)VALUES(?,?,?)";
        try (Connection conn = database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql))
        {
         pstmt.setString(1,owner.getName());
         pstmt.setString(2,owner.getPhone());
         pstmt.setString(3,owner.getEmail());

         pstmt.executeUpdate();
        }
    }
    public void updateOwner(Owner owner) throws SQLException{
        String sql = "UPDATE owners SET full_name=?,phone=?, email=? WHERE id=?";
        try(Connection conn = database.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, owner.getName());
            pstmt.setString(2, owner.getPhone());
            pstmt.setString(3, owner.getEmail());
            pstmt.setInt(4,owner.getId()); // За WHERE условието
            pstmt.executeUpdate();
        }
    }
    public void deleteOwner(int id)throws SQLException{
        String sql = "DELETE FROM owners WHERE id=?";
        try(Connection conn = database.connect();
        PreparedStatement pstmt= conn.prepareStatement(sql) ){
            pstmt.setInt(1,id);
            pstmt.executeUpdate();
        }
    }
 }