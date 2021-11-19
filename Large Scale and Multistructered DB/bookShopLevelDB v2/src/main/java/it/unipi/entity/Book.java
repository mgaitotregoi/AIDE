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
book:bookId:title
book:bookId:price
book:bookId:category
book:bookId:publicationYear
book:bookId:numPages
book:bookId:publisherId
book:bookId:publisherName
book:bookId:quantity
book:bookId:authorId         <--- These could be more than one
book:bookId:authorFullName   <-_/
 */

public class Book {

    private int idBook;
    private String title;
    private float price;
    private String category;
    private int publicationYear;
    private int numPages;
    private int quantity;
    private Publisher publisher;
    private List<Author> authors;

    public Book(int idBook, String title, float price, String category, int publicationYear, int numPages, int quantity, Publisher publisher, List<Author> authors) {
        this.idBook = idBook;
        this.title = title;
        this.price = price;
        this.category = category;
        this.publicationYear = publicationYear;
        this.numPages = numPages;
        this.quantity = quantity;
        this.publisher = publisher;
        this.authors = authors;
    }

    public Book(int idBook){
        this(idBook,null,0,null,-1,-1,-1,null,null);
    }

    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = new ArrayList<>();
        for (Author a:authors) {
            this.authors.add(a);
        }
    }

    @Override
    public String toString() {
        return "Book{" +
                "idBook=" + idBook +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", publicationYear=" + publicationYear +
                ", numPages=" + numPages +
                ", quantity=" + quantity +
                ", publisher=" + publisher +
                ", authors=" + authors +
                '}';
    }

    public void add(LevelDBConnector connector, String dbname){
        connector.openDB(dbname);

        HashMap<String,String> books = new HashMap<>();

        books.put("book:"+this.getIdBook()+":title",this.getTitle());
        books.put("book:"+this.getIdBook()+":price",String.valueOf(this.getPrice()));
        books.put("book:"+this.getIdBook()+":category",this.getCategory());
        books.put("book:"+this.getIdBook()+":publicationYear",String.valueOf(this.getPublicationYear()));
        books.put("book:"+this.getIdBook()+":numPages",String.valueOf(this.getNumPages()));
        books.put("book:"+this.getIdBook()+":quantity",String.valueOf(this.getQuantity()));
        books.put("book:"+this.getIdBook()+":publisherId",String.valueOf(this.getPublisher().getIdPublisher()));
        books.put("book:"+this.getIdBook()+":publisherName",this.getPublisher().getName());

        for (Author a : this.getAuthors()) {
            books.put("book:"+this.getIdBook()+":author:"+a.getIdAuthor(), a.getFirstName() + "-" + a.getLastName());
        }
        connector.writeBatch(books);

        connector.closeDB();
    }

    public void delete(LevelDBConnector connector,String dbname){
        connector.openDB(dbname);

        try (DBIterator iterator = connector.getIterator()) {
            for (iterator.seek(bytes("book:" + this.getIdBook() + ":")); iterator.hasNext(); iterator.next()) {

                String key = asString(iterator.peekNext().getKey());
                if (!key.startsWith("book:" + this.getIdBook() + ":")) {
                    break;
                }
                connector.deleteValue(key);

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            connector.closeDB();
        }
    }

    public static List<Book> browse(LevelDBConnector connector,String dbname){
        connector.openDB(dbname);

        try (DBIterator iterator = connector.getIterator()) {
            List<Book> bs = new ArrayList<>();
            List<Author> as = new ArrayList<>();
            String publisherName = "";
            int publisherId = 0;
            int id = -1;
            Book b = null;


            for (iterator.seek(bytes("book:")); iterator.hasNext(); iterator.next()) {

                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());

                //key should be like 1:attribute so I can split it into bookId and the attribute
                String values[] = key.split(":");

                if(Integer.valueOf(values[1]) != id) { //if I'm reading a different book
                    if(id != -1) {
                        b.setPublisher(new Publisher(publisherId));
                        b.getPublisher().setName(publisherName);
                        b.setAuthors(as);
                        bs.add(b);
                        as.clear();
                    }
                    b = new Book(Integer.valueOf(values[1]));
                    id = Integer.valueOf(values[1]);

                }

                if (!key.startsWith("book:")) {
                    break;
                }

                switch(values[2]){
                    case "title":
                        b.setTitle(value);
                        break;
                    case "price":
                        b.setPrice(Float.valueOf(value));
                        break;
                    case "category":
                        b.setCategory(value);
                        break;
                    case "publicationYear":
                        b.setPublicationYear(Integer.valueOf(value));
                        break;
                    case "numPages":
                        b.setNumPages(Integer.valueOf(value));
                        break;
                    case "publisherId":
                        publisherId = (Integer.valueOf(value));
                        break;
                    case "publisherName":
                        publisherName = value;
                        break;
                    case "quantity":
                        b.setQuantity(Integer.valueOf(value));
                        break;
                    case "author":
                        as.add(new Author(Integer.valueOf(values[3]), value.split("-")[0], value.split("-")[1]));
                        break;
                }

            }
            return bs;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            connector.closeDB();
        }
    }


    public void updateQuantity(int increase, int quantity, LevelDBConnector connector,String dbname){
        connector.openDB(dbname);
        int q = Integer.valueOf(connector.get("book:"+this.getIdBook()+":quantity"));
        if(increase == 0){
            connector.put("book:"+this.getIdBook()+":quantity",quantity);
        }else if(increase > 0){
            connector.put("book:"+this.getIdBook()+":quantity",q + quantity);
        }else{
            connector.put("book:"+this.getIdBook()+":quantity",q - quantity);
        }
        connector.closeDB();
    }

    public static Book getById(int Id,List<Book> books){
        return (Book) books.stream().filter(b->b.getIdBook() == Id).toArray() [0];
    }

}
