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
publisher:publisherId:name
publisher:publisherId:location
 */

public class Publisher {

    private int idPublisher;
    private String name;
    private String location;

    public Publisher(int idPublisher, String name, String location) {
        this.idPublisher = idPublisher;
        this.name = name;
        this.location = location;
    }

    public Publisher(int idPublisher){
        this(idPublisher,null,null);
    }

    public int getIdPublisher() {
        return idPublisher;
    }

    public void setIdPublisher(int idPublisher) {
        this.idPublisher = idPublisher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "idPublisher=" + idPublisher +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public void add(LevelDBConnector connector, String dbname){
        connector.openDB(dbname);

        HashMap<String,String> publs = new HashMap<>();

        publs.put("publisher:"+this.getIdPublisher()+":name",this.getName());
        publs.put("publisher:"+this.getIdPublisher()+":location",this.getLocation());

        connector.writeBatch(publs);
        connector.closeDB();

    }

    public void delete(List<Book> books, LevelDBConnector connector,String dbname){
        connector.openDB(dbname);

        List<String> delPubls = new ArrayList<>();

        delPubls.add("publisher:"+this.getIdPublisher()+":name");
        delPubls.add("publisher:"+this.getIdPublisher()+":location");

        connector.deleteBatch(delPubls);
        deleteAssociatedBooks(this.getIdPublisher(),false,books,connector,dbname);
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

    public static List<Publisher> browse(LevelDBConnector connector,String dbname) {
        connector.openDB(dbname);

        try (DBIterator iterator = connector.getIterator()) {
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
        }finally {
            connector.closeDB();
        }
    }


    public static Publisher getById(int Id,List<Publisher> publishers){
        return (Publisher) publishers.stream().filter(b->b.getIdPublisher() == Id).toArray() [0];
    }

}
