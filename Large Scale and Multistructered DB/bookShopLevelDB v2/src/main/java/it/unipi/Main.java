package it.unipi;

import it.unipi.connector.LevelDBConnector;
import it.unipi.connector.PopulateLevelDB;
import it.unipi.entity.*;

import java.util.*;



// reduce the access to the DB using in-memory structures, insert the list of books into Author, separate the dbmanaging from entity class.
public class Main {

    public static void main(String[] args) {
        final String DB_Name = "BookShop";

        Viewer v  = new Viewer();
        LevelDBConnector bookShopDB = new LevelDBConnector();
        PopulateLevelDB.populate(bookShopDB,DB_Name);

        ArrayList<Book> boks = new ArrayList<>();
        ArrayList<Author> auts = new ArrayList<>();;
        ArrayList<Publisher> pbhs = new ArrayList<>();;

        boolean exited = false;

            Viewer.printMainMenu();

            while(!exited){

                Scanner keyboard = new Scanner(System.in);
                String input = keyboard.nextLine();
                String[] parts = input.split(",");

                    switch (parts[0]) {
                        case "1":
                            boks = (ArrayList<Book>) Book.browse(bookShopDB, DB_Name);
                            v.printResult(boks);
                            v.printBookMenu();
                            do {

                                keyboard = new Scanner(System.in);
                                input = keyboard.nextLine();
                                parts = input.split(",");
                                switch (parts[0]) {
                                    case "1":
                                        (Book.getById(Integer.valueOf(parts[1]), boks)).delete(bookShopDB, DB_Name);
                                        boks = (ArrayList<Book>) Book.browse(bookShopDB, DB_Name);
                                        v.printResult(boks);
                                        break;
                                    case "2":
                                        (Book.getById(Integer.valueOf(parts[1]), boks)).updateQuantity(0, Integer.valueOf(parts[2]), bookShopDB, DB_Name);
                                        break;
                                    case "3":
                                        if (Boolean.valueOf(parts[3]) == true) {// increase
                                            (Book.getById(Integer.valueOf(parts[1]), boks)).updateQuantity(1, Integer.valueOf(parts[2]), bookShopDB, DB_Name);
                                        } else { // decrease
                                            (Book.getById(Integer.valueOf(parts[1]), boks)).updateQuantity(-1, Integer.valueOf(parts[2]), bookShopDB, DB_Name);
                                        }
                                        break;
                                    case "help":
                                        v.printBookMenu();
                                        break;
                                }
                            }
                            while (parts[0].compareTo("back") != 0);

                            break;


                        case "2":
                            ArrayList<Author> as = new ArrayList<>();
                            for (int i = 9; i < parts.length; i++)
                                as.add(Author.getById(Integer.valueOf(parts[9]), auts));

                            (new Book(Integer.valueOf(parts[1]), parts[2], Float.valueOf(parts[3]), parts[4], Integer.valueOf(parts[5]), Integer.valueOf(parts[6]), Integer.valueOf(parts[7]), Publisher.getById(Integer.valueOf(parts[8]), pbhs), as)).add(bookShopDB, DB_Name);
                            break;
                        case "3":
                            auts = (ArrayList<Author>) Author.browse(bookShopDB, DB_Name);
                            v.printResult(auts);
                            v.printAuthorMenu();
                            do {

                                keyboard = new Scanner(System.in);
                                input = keyboard.nextLine();
                                parts = input.split(",");
                                switch (parts[0]) {
                                    case "1":
                                        Author.getById(Integer.valueOf(parts[1]), auts).delete(boks, bookShopDB, DB_Name);
                                        break;
                                    case "help":
                                        v.printAuthorMenu();
                                        break;
                                }
                            }
                            while (parts[0].compareTo("back") != 0);
                            break;
                        case "4":
                            new Author(Integer.valueOf(parts[1]), parts[2], parts[3], parts[4]).add(bookShopDB, DB_Name);
                            break;
                        case "5":
                            pbhs = (ArrayList<Publisher>) Publisher.browse(bookShopDB, DB_Name);
                            v.printResult(pbhs);

                            v.printPublisherMenu();
                            do {

                                keyboard = new Scanner(System.in);
                                input = keyboard.nextLine();
                                parts = input.split(",");
                                switch (parts[0]) {
                                    case "1":
                                        Publisher.getById(Integer.valueOf(parts[1]), pbhs).delete(boks, bookShopDB, DB_Name);
                                        break;
                                    case "help":
                                        v.printPublisherMenu();
                                        break;
                                }
                            }
                            while (parts[0].compareTo("back") != 0);
                            break;
                        case "6":
                            new Publisher(Integer.valueOf(parts[1]), parts[2], parts[3]).add(bookShopDB, DB_Name);
                            break;
                        case "exit":
                            exited = true;
                            break;
                        case "help":
                            v.printMainMenu();
                            break;
                    }

            }
    }
}
