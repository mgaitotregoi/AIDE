package com.company;

import java.sql.*;
import java.util.*;

public class Publisher {

    private int idPublisher;
    private String name;
    private String location;

    public Publisher(String name,
                     String location){
        this.name = name;
        this.location = location;

    }

    public static void readListOfPublishers(Connection con) throws SQLException {
        List<String> result = new ArrayList();
        String temp;
        if(con.isClosed())
            return;
        Statement getList = con.createStatement();
        ResultSet rs = getList.executeQuery("SELECT * FROM publisher ORDER BY idPublisher");

        temp = "idPublisher name location \n";
        result.add(temp);

        while(rs.next()) {
            temp = Integer.toString( rs.getInt("idPublisher") ) + " " +
                    rs.getString("name") + " " +
                    rs.getString("location");

            result.add(temp);
        }
        getList.close();
        rs.close();

        Util.print(result);

    }

    public static void addPublisher(Connection con, String name, String location) throws SQLException {
        if(con.isClosed())
            return;

        PreparedStatement insertPublisher = con.prepareStatement("INSERT INTO publisher (idPublisher,name,location) VALUE (NULL,?,?)");
        insertPublisher.setString(1,name);
        insertPublisher.setString(2,location);

        insertPublisher.executeUpdate();

        insertPublisher.close();

        readListOfPublishers(con);

        return;

    }

    public static void deletePublisher(Connection con, int id) throws SQLException {
        if(con.isClosed())
            return;
        PreparedStatement rmPublisher = con.prepareStatement("DELETE FROM publisher WHERE idPublisher = ?");
        PreparedStatement searchBooks = con.prepareStatement("SELECT idBook FROM book WHERE publisherId = ?");

        rmPublisher.setInt(1,id);
        searchBooks.setInt(1,id);

        ResultSet rs = searchBooks.executeQuery();

        while(rs.next()) {
            Book.deleteBook(con,rs.getInt("idBook"));
        }

        rmPublisher.executeUpdate();

        rmPublisher.close();
        searchBooks.close();

        readListOfPublishers(con);

        return;
    }

}
