package com.company;

import java.sql.*;
import java.util.*;


public class Author {

    private int idAuthor;
    private String firstName;
    private String lastName;
    private String biography;

    public Author(String firstName,
                  String lastName,
                  String biography){
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
    }

    public static void readListOfAuthors(Connection con) throws SQLException {
        List<String> result = new ArrayList();
        String temp;
        if(con.isClosed())
            return;
        Statement getList = con.createStatement();
        ResultSet rs = getList.executeQuery("SELECT * FROM author ORDER BY idAuthor");

        temp = "idAuthor firstName lastName biography \n";
        result.add(temp);

        while(rs.next()) {
            temp = Integer.toString( rs.getInt("idAuthor") ) + " " +
                    rs.getString("firstName") + " " +
                    rs.getString("lastName") + " " +
                    rs.getString("biography");

            result.add(temp);
        }
        getList.close();
        rs.close();

        Util.print(result);

    }

    public static void addAuthor(Connection con, String firstName, String lastName, String biography) throws SQLException {
        if(con.isClosed())
            return;

        PreparedStatement insertAuthor = con.prepareStatement("INSERT INTO author (idAuthor,firstName,lastName,biography) VALUE (NULL,?,?,?)");
        insertAuthor.setString(1,firstName);
        insertAuthor.setString(2,lastName);
        insertAuthor.setString(3,biography);

        insertAuthor.executeUpdate();

        insertAuthor.close();

        readListOfAuthors(con);

        return;

    }

    public static void deleteAuthor(Connection con, int id) throws SQLException {
        if(con.isClosed())
            return;
        PreparedStatement rmBookAuthor = con.prepareStatement("DELETE FROM book_has_author WHERE authorId = ?");
        PreparedStatement rmAuthor = con.prepareStatement("DELETE FROM author WHERE idAuthor = ?");

        rmAuthor.setInt(1,id);
        rmBookAuthor.setInt(1,id);

        Util.acidTrans(con,rmBookAuthor,rmAuthor);

        readListOfAuthors(con);

        return;
    }


}
