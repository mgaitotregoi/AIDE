package it.unipi.entity;

import it.unipi.connector.LevelDBConnector;
import org.iq80.leveldb.DBIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

/*
author:authorId:firstName
author:authorId:lastName
author:authorId:biography
 */

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

    public void add(LevelDBConnector connector, String dbname){
        connector.openDB(dbname);

        HashMap<String,String> authors = new HashMap<>();

        authors.put("author:"+this.getIdAuthor()+":firstName",this.getFirstName());
        authors.put("author:"+this.getIdAuthor()+":lastName",String.valueOf(this.getLastName()));
        authors.put("author:"+this.getIdAuthor()+":biography",this.getBiography());

        connector.writeBatch(authors);

        connector.closeDB();
    }

    public void delete(List<Book> books,LevelDBConnector connector,String dbname){
        connector.openDB(dbname);

        List<String> authors = new ArrayList<>();

        authors.add("author:"+this.getIdAuthor()+":firstName");
        authors.add("author:"+this.getIdAuthor()+":lastName");
        authors.add("author:"+this.getIdAuthor()+":biography");

        connector.deleteBatch(authors);
        deleteAssociatedBooks(this.getIdAuthor(),true,books,connector,dbname);
        connector.closeDB();
    }

    private void deleteAssociatedBooks(int Id, boolean isAuthor, List<Book> books, LevelDBConnector connector, String dbname){

        if(isAuthor){
            books.forEach(bs ->{
                bs.getAuthors().stream().filter(as -> as.getIdAuthor() == Id).forEach(bsA -> {
                    if(bs.getAuthors().toArray().length > 1){
                        connector.deleteValue("book:"+bs.getIdBook()+":author:"+bsA.getIdAuthor());
                    }else
                        connector.closeDB();
                        bs.delete(connector,dbname);
                        connector.openDB(dbname);
                });
            });
        }else{
            books.stream().filter(bs -> bs.getPublisher().getIdPublisher() == Id).forEach(bsP -> {
                connector.closeDB();
                bsP.delete(connector,dbname);
                connector.openDB(dbname);
            });
        }
    }

    public static List<Author> browse(LevelDBConnector connector,String dbname) {
        connector.openDB(dbname);
        try (DBIterator iterator = connector.getIterator()) {
            List<Author> as = new ArrayList<>();
            int id = -1;
            Author a = null;

            for (iterator.seek(bytes("author:")); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                //key should be like 1:attribute so I can split it into authorId and the attribute
                String values[] = key.split(":");

                if(Integer.valueOf(values[1]) != id) { //if I'm reading a different author
                    if(id != -1)
                        as.add(a);
                    a = new Author(Integer.valueOf(values[1]));
                    id = Integer.valueOf(values[1]);
                }

                switch(values[2]){
                    case "firstName":
                        a.setFirstName(value);
                        break;
                    case "lastName":
                        a.setLastName(value);
                        break;
                    case "biography":
                        a.setBiography(value);
                        break;
                }

                if (!key.startsWith("author:")) {break; }
            }
            return as;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            connector.closeDB();
        }
    }

    public static Author getById(int Id,List<Author> authors){
        return (Author) authors.stream().filter(b->b.getIdAuthor() == Id).toArray() [0];
    }
}

