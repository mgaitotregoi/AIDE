package it.unipi.Entity;

import it.unipi.LevelDBConnector;

import java.util.List;

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

    public void add(LevelDBConnector connector){
        connector.openDB();
        connector.addPublisher(this);
        connector.closeDB();
    }

    public void delete(LevelDBConnector connector, List<Book> books){
        connector.openDB();
        connector.deletePublisher(this,books);
        connector.closeDB();
    }

    public static List<Publisher> browse(LevelDBConnector connector){
        List<Publisher> publishers;
        connector.openDB();
        publishers = connector.findPublisher();
        connector.closeDB();
        return publishers;
    }

    public static Publisher getById(int Id,List<Publisher> publishers){
        return (Publisher) publishers.stream().filter(b->b.getIdPublisher() == Id).toArray() [0];
    }

}
