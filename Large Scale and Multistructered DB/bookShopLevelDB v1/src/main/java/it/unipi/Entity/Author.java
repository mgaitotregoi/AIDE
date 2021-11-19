package it.unipi.Entity;

import it.unipi.LevelDBConnector;

import java.util.ArrayList;
import java.util.List;

public class Author {

    private int idAuthor;
    private String firstName;
    private String lastName;
    private String biography;

    public Author(int idAuthor, String firstName, String lastName, String biography) {
        this.idAuthor = idAuthor;
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
    }

    public Author(int idAuthor){
        this(idAuthor,null,null,null);
    }


    public Author(int idAuthor, String firstName, String lastName){
        this(idAuthor,firstName,lastName,null);
    }

    public int getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(int idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        return "Author{" +
                "idAuthor=" + idAuthor +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", biography='" + biography + '\'' +
                '}';
    }

    public void add(LevelDBConnector connector){
        connector.openDB();
        connector.addAuthor(this);
        connector.closeDB();
    }

    public void delete(LevelDBConnector connector, List<Book> books){
        connector.openDB();
        connector.deleteAuthor(this, books);
        connector.closeDB();
    }

    public static List<Author> browse(LevelDBConnector connector){
        List<Author> authors;
        connector.openDB();
        authors = connector.findAuthor();
        connector.closeDB();
        return authors;
    }

    public static Author getById(int Id,List<Author> authors){
        return (Author) authors.stream().filter(b->b.getIdAuthor() == Id).toArray() [0];
    }
}

