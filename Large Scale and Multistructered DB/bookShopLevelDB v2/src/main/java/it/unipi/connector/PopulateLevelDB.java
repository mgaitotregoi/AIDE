package it.unipi.connector;

import it.unipi.entity.*;
import java.util.*;

public class PopulateLevelDB {
    public static void populate(LevelDBConnector DB, String file){

        Author a1 = new Author(1,"John Ronald Reuel","Tolkien","'Tolkien\'s immediate paternal ancestors were middle-class craftsmen who made and sold clocks, watches and pianos in London and Birmingham. The Tolkien family originated in the East Prussian town Kreuzburg near Königsberg, which was founded during medieval German eastward expansion, where his earliest-known paternal ancestor Michel Tolkien was born around 1620.'");
        Author a2 = new Author(2,"Agatha","Christie","Agatha Mary Clarissa Miller was born on 15 September 1890, into a wealthy upper-middle-class family in Torquay, Devon. She was the youngest of three children born to Frederick Alvah Miller, \"a gentleman of substance\", and his wife Clarissa Margaret (\"Clara\") Miller née Boehmer.");
        Author a3 = new Author(3,"Paula","Hawkins","Hawkins was born and raised in Salisbury, Rhodesia (now Harare, Zimbabwe), the daughter of Anthony \"Tony\" Hawkins and his wife Glynne.Her father was an economics professor and financial journalist. Before moving to London in 1989 at the age of 17, Hawkins attended Arundel School, Harare, Zimbabwe then studied for her A-Levels at Collingham College, an independent college in Kensington, West London.");

        a1.add(DB,file);
        a2.add(DB,file);
        a3.add(DB,file);

        Publisher p1 = new Publisher(1,"Astrolabio","Italy");
        Publisher p2 = new Publisher(2,"Collins Crime Club","United Kingdom");
        Publisher p3 = new Publisher(3,"Riverhead","United States");

        p1.add(DB,file);
        p2.add(DB,file);
        p3.add(DB,file);

        List<Author> authors = new ArrayList<>();
        authors.add(a1);

        (new Book(1,"Lord of the rings",50,"Fantasy",1954,1230,103000000,p1,authors)).add(DB,file);
        authors.clear();
        authors.add(a2);
        (new Book(2,"Murder on the Orient Express",12,"Crime novel",1934,256,50000000,p2,authors)).add(DB,file);
        (new Book(3,"The A.B.C. Murders",15,"Crime novel",1936,256,40000000,p2,authors)).add(DB,file);
        authors.clear();
        authors.add(a1);
        authors.add(a2);
        authors.add(a3);
        (new Book(4,"The Girl on the Train",8,"Thriller",2015,395,11000000,p3,authors)).add(DB,file);
        }
}
