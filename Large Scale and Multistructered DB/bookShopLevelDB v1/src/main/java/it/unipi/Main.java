package it.unipi;

import it.unipi.Entity.*;

import java.util.*;
import java.util.Scanner;

public class Main {

    private static void printMainMenu(){
        System.out.println("****************************\n" +
            "Digit :\n" +
            "1 : to view the list of books \n" +
            "2 : to add a new book (id,title,price,category,publicationYear,numPages,publisherId,quantity,authorId)\n" +
            "3 : to view the list of authors \n" +
            "4 : to add a new author (id,firstName,lastName,biography)\n" +
            "5 : to view the list of publishers \n" +
            "6 : to add a new publisher (id,name,location)\n" +
            "help : to see the possible commands\n" +
            "exit : to stop the application\n" +
            "****************************");
    }

    private static void printBookMenu(){
        System.out.println("****************************\n" +
            "Digit :\n" +
            "1 : to delete a book specifying the id (id) \n" +
            "2 : to set the quantity of a book (id,quantity) \n" +
            "3 : to increase or decrease the quantity of a book (id,quantity,true for increase false for decrease) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************");
    }

    private static void printAuthorMenu(){
        System.out.println("****************************\n" +
            "Digit :\n" +
            "1 : to delete an author specifying the id (id) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************");
    }

    private static void printPublisherMenu(){
        System.out.println("****************************\n" +
            "Digit :\n" +
            "1 : to delete a publisher specifying the id (id) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************");
    }

    public static void main(String[] args) {
        LevelDBConnector bookShopDB = new LevelDBConnector();
        bookShopDB.populateDB();

        ArrayList<Book> boks = new ArrayList<>();
        ArrayList<Author> auts = new ArrayList<>();;
        ArrayList<Publisher> pbhs = new ArrayList<>();;

        boolean exited = false;


            printMainMenu();

            while(!exited){

                Scanner keyboard = new Scanner(System.in);
                String input = keyboard.nextLine();
                String[] parts = input.split(",");
                switch (parts[0]) {
                    case "1":
                        boks = (ArrayList<Book>) Book.browse(bookShopDB);
                        System.out.println(boks);
                        printBookMenu();
                        do{

                            keyboard = new Scanner(System.in);
                            input = keyboard.nextLine();
                            parts = input.split(",");
                            switch (parts[0]){
                                case "1":
                                    (Book.getById(Integer.valueOf(parts[1]),boks)).delete(bookShopDB);
                                    boks = (ArrayList<Book>) Book.browse(bookShopDB);
                                    System.out.println(boks);
                                    break;
                                case "2":
                                    (Book.getById(Integer.valueOf(parts[1]),boks)).updateQuantity(0,Integer.valueOf(parts[2]),bookShopDB);
                                    break;
                                case "3":
                                    if(Boolean.valueOf(parts[3]) == true){// increase
                                        (Book.getById(Integer.valueOf(parts[1]),boks)).updateQuantity(1,Integer.valueOf(parts[2]),bookShopDB);
                                    }else{ // decrease
                                        (Book.getById(Integer.valueOf(parts[1]),boks)).updateQuantity(-1,Integer.valueOf(parts[2]),bookShopDB);
                                    }
                                    break;
                                case "help":
                                    printBookMenu();
                                    break;
                            }
                        }
                        while(parts[0].compareTo("back") != 0);

                        break;


                    case "2":
                        ArrayList<Author> as = new ArrayList<>();
                        for(int i = 9; i < parts.length; i++)
                            as.add(Author.getById(Integer.valueOf(parts[9]),auts));

                        (new Book(Integer.valueOf(parts[1]),parts[2],Float.valueOf(parts[3]),parts[4],Integer.valueOf(parts[5]),Integer.valueOf(parts[6]),Integer.valueOf(parts[7]),Publisher.getById(Integer.valueOf(parts[8]),pbhs),as)).add(bookShopDB);
                        break;
                    case "3":
                        auts = (ArrayList<Author>) Author.browse(bookShopDB);
                        System.out.println(auts);
                        printAuthorMenu();
                        do{

                            keyboard = new Scanner(System.in);
                            input = keyboard.nextLine();
                            parts = input.split(",");
                            switch (parts[0]){
                                case "1":
                                    Author.getById(Integer.valueOf(parts[1]),auts).delete(bookShopDB,boks);
                                    break;
                                case "help":
                                    printAuthorMenu();
                                    break;
                            }
                        }
                        while(parts[0].compareTo("back") != 0);
                        break;
                    case "4":
                        new Author(Integer.valueOf(parts[1]),parts[2],parts[3],parts[4]).add(bookShopDB);
                        break;
                    case "5":
                        pbhs = (ArrayList<Publisher>) Publisher.browse(bookShopDB);
                        System.out.println(pbhs);

                        printPublisherMenu();
                        do{

                            keyboard = new Scanner(System.in);
                            input = keyboard.nextLine();
                            parts = input.split(",");
                            switch (parts[0]){
                                case "1":
                                    Publisher.getById(Integer.valueOf(parts[1]),pbhs).delete(bookShopDB,boks);
                                    break;
                                case "help":
                                    printPublisherMenu();
                                    break;
                            }
                        }
                        while(parts[0].compareTo("back") != 0);
                        break;
                    case "6":
                        new Publisher(Integer.valueOf(parts[1]),parts[2],parts[3]).add(bookShopDB);
                        break;
                    case "exit":
                        exited = true;
                        break;
                    case "help":
                        printMainMenu();
                        break;
                }



            }

    }
}
