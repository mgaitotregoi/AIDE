package it.unipi.Entity;

import it.unipi.LevelDBConnector;

import java.util.ArrayList;
import java.util.List;

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

    public void add(LevelDBConnector connector){
        connector.openDB();
        connector.addBook(this);
        connector.closeDB();
    }

    public void delete(LevelDBConnector connector){
        connector.openDB();
        connector.deleteBook(this);
        connector.closeDB();
    }

    public void updateQuantity(int increase, int quantity, LevelDBConnector connector){
        connector.openDB();
        connector.updateBookQuantity(this,increase,quantity);
        connector.closeDB();
    }

    public static List<Book> browse(LevelDBConnector connector){
        List<Book> books;
        connector.openDB();
        books = connector.findBook();
        connector.closeDB();
        return books;
    }

    public static Book getById(int Id,List<Book> books){
        return (Book) books.stream().filter(b->b.getIdBook() == Id).toArray() [0];
    }



}
