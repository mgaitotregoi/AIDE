package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Book {
    static int idCounter = 0;

    private int idBook;
    private String title;
    private float price;
    private String category;
    private int publicationYear;
    private int numPages;
    private int publisherId;
    private int quantity;


    public Book( String title, float price, String category, int publicationYear, int numPages, int publisherId, int quantity){
        this.title = title;
        this.price = price;
        this.category = category;
        this.publicationYear = publicationYear;
        this.numPages = numPages;
        this.publisherId = publisherId;
        this.quantity = quantity;
    }

    public static void updateCounter(Connection con) throws SQLException {
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery("SELECT idBook FROM book ORDER BY idBook DESC LIMIT 1");
        if(rs.next() != false)
        idCounter = rs.getInt("idBook");
        ++idCounter;
    }

    public static void readListOfBooks(Connection con) throws SQLException {
        List<String> result = new ArrayList();
        String temp;
        if(con.isClosed())
            return;
        Statement getList = con.createStatement();
        ResultSet rs = getList.executeQuery("SELECT idBook, title, price, category, publicationYear, numPages, " +
                "quantity, publisherId, publisher.name as publisherName, authorId, " +
                "author.firstName as authorFirstName, author.lastName as authorLastName " +
                "FROM book join book_has_author on (idBook = bookId) " +
                "join author on (authorId = idAuthor) " +
                "join publisher on (publisherId = idPublisher) ORDER BY idBook");

        temp = "idBook title price category publicationYear numPages quantity publisherId publisherName authorId authorFirstName authorLastName \n";
        result.add(temp);

        while(rs.next()) {
            temp = Integer.toString( rs.getInt("idBook") ) + " " +
                    rs.getString("title") + " " +
                    Float.toString(rs.getFloat("price") ) + " " +
                    rs.getString("category") + " " +
                    Integer.toString( rs.getInt("publicationYear") ) + " " +
                    Integer.toString( rs.getInt("numPages") ) + " " +
                    Integer.toString( rs.getInt("quantity") ) + " " +
                    Integer.toString( rs.getInt("publisherId") ) + " " +
                    rs.getString("publisherName") + " " +
                    Integer.toString( rs.getInt("authorId") ) + " " +
                    rs.getString("authorFirstName") + " " +
                    rs.getString("authorLastName");

            result.add(temp);
        }
        getList.close();
        rs.close();

        Util.print(result);

    }

    public static void deleteBook(Connection con, int id) throws SQLException {
        if(con.isClosed())
            return;
        PreparedStatement rmBookAuthor = con.prepareStatement("DELETE FROM book_has_author WHERE bookId = ?");
        PreparedStatement rmBook = con.prepareStatement("DELETE FROM book WHERE idBook = ?");
        rmBook.setInt(1,id);
        rmBookAuthor.setInt(1,id);

        Util.acidTrans(con,rmBookAuthor,rmBook);

        return;
    }

    public static boolean setQuantityBook(Connection con, int id, int quantity) throws SQLException {
        if(con.isClosed())
            return false;
        PreparedStatement setBook = con.prepareStatement("UPDATE book SET quantity = ? WHERE idBook = ?");
        setBook.setInt(1,quantity);
        setBook.setInt(2,id);
        int rs = setBook.executeUpdate();

        setBook.close();
        if(rs != 0)
            return true;
        return false;
    }

    public static boolean incDecBook(Connection con, int id, int quantity, boolean increase) throws SQLException {
        if(con.isClosed())
            return false;
        PreparedStatement setBook;
                if(increase == true)
                    setBook = con.prepareStatement("UPDATE book SET quantity = quantity + ? WHERE idBook = ?");
                else
                    setBook = con.prepareStatement("UPDATE book SET quantity = quantity - ? WHERE idBook = ?");
        setBook.setInt(1,quantity);
        setBook.setInt(2,id);
        int rs = setBook.executeUpdate();

        setBook.close();
        if(rs != 0)
            return true;
        return false;
    }

    public static void addBook(Connection con, String title, float price, String category, int publicationYear, int numPages, int publisherId, int quantity, int authorId) throws SQLException {
        if(con.isClosed())
            return;
        //check if publisher and author exist
        PreparedStatement checkAuthor = con.prepareStatement("SELECT * FROM author WHERE idAuthor = ?");
        PreparedStatement checkPublisher = con.prepareStatement("SELECT * FROM publisher WHERE idPublisher = ?");
        checkPublisher.setInt(1,publisherId);
        checkAuthor.setInt(1,authorId);

        ResultSet rs = checkPublisher.executeQuery();
        if(rs.next() == false) {
            System.out.println("Insert the book before inserting the publisher : " + publisherId);
            return;
        }
        rs = checkAuthor.executeQuery();
        if(rs.next() == false) {
            System.out.println("Insert the book before inserting the author : " + authorId);
            return;
        }

        PreparedStatement insertBook = con.prepareStatement("INSERT INTO book (idBook,title,price,category,publicationYear,numPages,publisherId,quantity) VALUE (?,?,?,?,?,?,?,?)");
        insertBook.setInt(1,idCounter);
        insertBook.setString(2,title);
        insertBook.setFloat(3,price);
        insertBook.setString(4,category);
        insertBook.setInt(5,publicationYear);
        insertBook.setInt(6,numPages);
        insertBook.setInt(7,publisherId);
        insertBook.setInt(8,quantity);

        PreparedStatement insertBookAuthor = con.prepareStatement("INSERT INTO book_has_author VALUE (?,?)");
        insertBookAuthor.setInt(1,idCounter);
        insertBookAuthor.setInt(2,authorId);

        Util.acidTrans(con,insertBook,insertBookAuthor);
        readListOfBooks(con);
        ++idCounter;
        return;

    }
}
