package it.unipi;

import it.unipi.Entity.*;
import org.iq80.leveldb.*;
import java.io.*;
import java.util.*;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

/*
BookShopDb Structure

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


author:authorId:firstName
author:authorId:lastName
author:authorId:biography


publisher:publisherId:name
publisher:publisherId:location

 */

public class LevelDBConnector {

    private DB db = null;

    private static class MyDBComparator implements DBComparator {
        @Override
        public String name() {
            return "simple";
        }
        @Override
        public byte[] findShortestSeparator(byte[] start, byte[] limit) {
            return start;
        }
        @Override
        public byte[] findShortSuccessor(byte[] key) {
            return key; }
        @Override
        public int compare(byte[] o1, byte[] o2) {
        String k1 = asString(o1);
        String k2 = asString(o2);
        String[] parts1 = k1.split(":");
        String[] parts2 = k2.split(":");
        int comparison = 0;
        for(int i = 0; i < parts1.length && i < parts2.length;i++){
            if(i!=1){
                comparison = parts1[i].compareTo(parts2[i]);
            }else {
                comparison = (Integer.valueOf(parts1[i])).compareTo(Integer.valueOf(parts2[i]));
            }
            if(comparison != 0) break;
        }
            return comparison;
        }
    }

    public void openDB(){
        Options options = new Options();
        //options.comparator(new MyDBComparator());
        options.createIfMissing(true);

        try{
            db = factory.open(new File("BookShopDB"), options);
        }
        catch (IOException ioe) { closeDB(); }
    }

    private void put(String key, String value)
    {
        db.put(bytes(key), bytes(value));
    }
    private void put(String key, int value)
    {
        db.put(bytes(key), bytes(String.valueOf(value)));
    }

    private String get(String key){
        byte[] bytes = db.get(bytes(key));
        return (bytes == null ? null : asString(bytes));
    }

    private void deleteValue(String key)
    {
        db.delete(bytes(key));
    }

    public void closeDB(){
        try {
            if( db != null) db.close();
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    public void addBook(Book b){

        try(WriteBatch batch = db.createWriteBatch())
        {
            batch.put(bytes("book:"+b.getIdBook()+":title"), bytes(b.getTitle()));
            batch.put(bytes("book:"+b.getIdBook()+":price"), bytes(String.valueOf(b.getPrice())));
            batch.put(bytes("book:"+b.getIdBook()+":category"), bytes(b.getCategory()));
            batch.put(bytes("book:"+b.getIdBook()+":publicationYear"), bytes(String.valueOf(b.getPublicationYear())));
            batch.put(bytes("book:"+b.getIdBook()+":numPages"), bytes(String.valueOf(b.getNumPages())));
            batch.put(bytes("book:"+b.getIdBook()+":quantity"), bytes(String.valueOf(b.getQuantity())));
            batch.put(bytes("book:"+b.getIdBook()+":publisherId"), bytes(String.valueOf(b.getPublisher().getIdPublisher())));
            batch.put(bytes("book:"+b.getIdBook()+":publisherName"), bytes(b.getPublisher().getName()));

            for (Author a :b.getAuthors()) {
                batch.put(bytes("book:"+b.getIdBook()+":author:"+a.getIdAuthor()), bytes( a.getFirstName() + "-" + a.getLastName()));
            }

            db.write(batch);
        }catch (IOException ioe) {ioe.printStackTrace(); }
    }

    public void deleteBook(Book b) {

        try (DBIterator iterator = db.iterator()) {
            for (iterator.seek(bytes("book:" + b.getIdBook() + ":")); iterator.hasNext(); iterator.next()) {

                String key = asString(iterator.peekNext().getKey());
                if (!key.startsWith("book:" + b.getIdBook() + ":")) {
                    break;
                }
                deleteValue(key);

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public List<Book> findBook() {
        try (DBIterator iterator = db.iterator()) {
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
        }
    }

    public void updateBookQuantity(Book b, int increase, int quaty){
        int q = Integer.valueOf(get("book:"+b.getIdBook()+":quantity"));
        if(increase == 0){
            put("book:"+b.getIdBook()+":quantity",quaty);
        }else if(increase > 0){
            put("book:"+b.getIdBook()+":quantity",q + quaty);
        }else{
            put("book:"+b.getIdBook()+":quantity",q - quaty);
        }
    }

    public void addAuthor(Author a){

        try(WriteBatch batch = db.createWriteBatch())
        {
            batch.put(bytes("author:"+a.getIdAuthor()+":firstName"), bytes(a.getFirstName()));
            batch.put(bytes("author:"+a.getIdAuthor()+":lastName"), bytes(String.valueOf(a.getLastName())));
            batch.put(bytes("author:"+a.getIdAuthor()+":biography"), bytes(a.getBiography()));

            db.write(batch);
        }catch (IOException ioe) {ioe.printStackTrace(); }
    }

    public void deleteAuthor(Author a, List<Book> books){

        try(WriteBatch batch = db.createWriteBatch())
        {

            batch.delete(bytes("author:"+a.getIdAuthor()+":firstName"));
            batch.delete(bytes("author:"+a.getIdAuthor()+":lastName"));
            batch.delete(bytes("author:"+a.getIdAuthor()+":biography"));

            db.write(batch);
            deleteAssociatedBooks(a.getIdAuthor(),true,books);
        }catch (IOException ioe) {ioe.printStackTrace(); }
    }

    public List<Author> findAuthor() {

        try (DBIterator iterator = db.iterator()) {
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
        }
    }

    public void addPublisher(Publisher p){


        try(WriteBatch batch = db.createWriteBatch())
        {
            batch.put(bytes("publisher:"+p.getIdPublisher()+":name"), bytes(p.getName()));
            batch.put(bytes("publisher:"+p.getIdPublisher()+":location"), bytes(p.getLocation()));

            db.write(batch);
        }catch (IOException ioe) {ioe.printStackTrace(); }

    }

    public void deletePublisher(Publisher p, List<Book> books){


        try(WriteBatch batch = db.createWriteBatch())
        {

            batch.delete(bytes("publisher:"+p.getIdPublisher()+":name"));
            batch.delete(bytes("publisher:"+p.getIdPublisher()+":location"));

            db.write(batch);
            deleteAssociatedBooks(p.getIdPublisher(),false,books);
        }catch (IOException ioe) {ioe.printStackTrace(); }

    }

    public List<Publisher> findPublisher() {

        try (DBIterator iterator = db.iterator()) {
            List<Publisher> ps = new ArrayList<>();
            int id = -1;
            Publisher p = null;

            for (iterator.seek(bytes("publisher:")); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                //key should be like 1:attribute so I can split it into bookId and the attribute
                String values[] = key.split(":");

                if(Integer.valueOf(values[1]) != id) { //if I'm reading a different book
                    if(id != -1)
                        ps.add(p);
                    p = new Publisher(Integer.valueOf(values[1]));
                    id = Integer.valueOf(values[1]);
                }

                switch(values[2]){
                    case "name":
                        p.setName(value);
                        break;
                    case "location":
                        p.setLocation(value);
                        break;
                }

                if (!key.startsWith("publisher:")) {break; }
            }
            return ps;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteAssociatedBooks(int Id, boolean isAuthor, List<Book> books){

        if(isAuthor){
            books.forEach(bs ->{
                bs.getAuthors().stream().filter(as -> as.getIdAuthor() == Id).forEach(bsA -> {
                    if(bs.getAuthors().toArray().length > 1){
                        db.delete(bytes("book:"+bs.getIdBook()+":author:"+bsA.getIdAuthor()));
                    }else
                        deleteBook(bs);
                });
            });
        }else{
            books.stream().filter(bs -> bs.getPublisher().getIdPublisher() == Id).forEach(bsP -> {
                deleteBook(bsP);
            });
        }

    }

    public void Iterator() {
        System.out.println("-------------------------");
        openDB();
        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                System.out.println(key + " = " + value);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            closeDB();
            System.out.println("-------------------------");
        }
    }

    public void populateDB(){

        Author a1 = new Author(1,"John Ronald Reuel","Tolkien","'Tolkien\'s immediate paternal ancestors were middle-class craftsmen who made and sold clocks, watches and pianos in London and Birmingham. The Tolkien family originated in the East Prussian town Kreuzburg near Königsberg, which was founded during medieval German eastward expansion, where his earliest-known paternal ancestor Michel Tolkien was born around 1620.'");
        Author a2 = new Author(2,"Agatha","Christie","Agatha Mary Clarissa Miller was born on 15 September 1890, into a wealthy upper-middle-class family in Torquay, Devon. She was the youngest of three children born to Frederick Alvah Miller, \"a gentleman of substance\", and his wife Clarissa Margaret (\"Clara\") Miller née Boehmer.");
        Author a3 = new Author(3,"Paula","Hawkins","Hawkins was born and raised in Salisbury, Rhodesia (now Harare, Zimbabwe), the daughter of Anthony \"Tony\" Hawkins and his wife Glynne.Her father was an economics professor and financial journalist. Before moving to London in 1989 at the age of 17, Hawkins attended Arundel School, Harare, Zimbabwe then studied for her A-Levels at Collingham College, an independent college in Kensington, West London.");

        a1.add(this);
        a2.add(this);
        a3.add(this);

        Publisher p1 = new Publisher(1,"Astrolabio","Italy");
        Publisher p2 = new Publisher(2,"Collins Crime Club","United Kingdom");
        Publisher p3 = new Publisher(3,"Riverhead","United States");

        p1.add(this);
        p2.add(this);
        p3.add(this);

        List<Author> authors = new ArrayList<>();
        authors.add(a1);

        (new Book(1,"Lord of the rings",50,"Fantasy",1954,1230,103000000,p1,authors)).add(this);
        authors.clear();
        authors.add(a2);
        (new Book(2,"Murder on the Orient Express",12,"Crime novel",1934,256,50000000,p2,authors)).add(this);
        (new Book(3,"The A.B.C. Murders",15,"Crime novel",1936,256,40000000,p2,authors)).add(this);
        authors.clear();
        authors.add(a1);
        authors.add(a2);
        authors.add(a3);
        (new Book(4,"The Girl on the Train",8,"Thriller",2015,395,11000000,p3,authors)).add(this);
        closeDB();
    }


}

