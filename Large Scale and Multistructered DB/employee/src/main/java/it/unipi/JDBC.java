package it.unipi;
import java.util.*;
import java.sql.*;

public class JDBC {

    private static void showEmployee(Connection con) throws SQLException{
        PreparedStatement ps = con.prepareStatement("Select * From Employee");
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            System.out.print(rs.getString("CompanyName"));
            System.out.print(" ");
            System.out.print(rs.getString("Address"));
            System.out.print(" ");
            System.out.print(rs.getInt("TotalEmployee"));
            System.out.print(" ");
            System.out.println(rs.getString("WebSite"));
        }

        rs.close();
        ps.close();
    }

    private static void insertEmployee(Connection con, String CN, String A, int TE, String W) throws SQLException{

        PreparedStatement ps = con.prepareStatement("INSERT INTO Employee VALUES (?,?,?,?)");
        ps.setString(1,CN);
        ps.setString(2,A);
        ps.setString(3,String.valueOf(TE));
        ps.setString(4,W);

        int rowAffected = ps.executeUpdate();

        System.out.println("Rows inserted : " + rowAffected);
        ps.close();

    }

    private static void flushTable(Connection con, String tabName) throws  SQLException{

        Statement s = con.createStatement();
        int r = s.executeUpdate("DELETE FROM " + tabName);
        System.out.println("Rows affected : " + r);
        s.close();

    }

    public static void main(String[] args) {
        Connection c = null;

        try{
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookShop?" +
                    "zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=CET", "root","matteo96");
            showEmployee(c);
            insertEmployee(c,"Unipi", "Lungarno Galilei", 5000, "www.unipi.it");
            insertEmployee(c,"Unige", "Mura del Molo", 2000, "www.unige.it");
            insertEmployee(c,"Unimi", "Via Francesco Sforza", 8000, "www.unimi.it");
            showEmployee(c);
            flushTable(c,"Employee");
            showEmployee(c);

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
