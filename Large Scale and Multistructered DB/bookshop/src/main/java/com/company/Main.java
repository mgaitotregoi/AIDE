package com.company;
// memorizzare in oggetti classi, usare printmaina printbook .... ,separa la classe connection dal resto.
import java.sql.*;
import java.util.*;

public class Main {

    private static final String MESS[] = {"****************************\n" +
            "Digit :\n" +
            "1 : to view the list of books \n" +
            "2 : to add a new book (title,price,category,publicationYear,numPages,publisherId,quantity,authorId)\n" +
            "3 : to view the list of authors \n" +
            "4 : to add a new author (firstName,lastName,biography)\n" +
            "5 : to view the list of publishers \n" +
            "6 : to add a new publisher (name,location)\n" +
            "help : to see the possible commands\n" +
            "exit : to stop the application\n" +
            "****************************",
            "****************************\n" +
            "Digit :\n" +
            "1 : to delete a book specifying the id (id) \n" +
            "2 : to set the quantity of a book (id,quantity) \n" +
            "3 : to increase or decrease the quantity of a book (id,quantity,true for increase false for decrease) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************",
            "****************************\n" +
            "Digit :\n" +
            "1 : to delete an author specifying the id (id) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************",
            "****************************\n" +
            "Digit :\n" +
            "1 : to delete a publisher specifying the id (id) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************"};


    public static void main(String[] args) {
        Connection c = null;
        boolean exited = false;

        try{
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookshop2?" +
                    "zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=CET", "root","matteo96");
            System.out.println(MESS[0]);
            Book.updateCounter(c);
            while(!exited){

                Scanner keyboard = new Scanner(System.in);
                String input = keyboard.nextLine();
                String[] parts = input.split(",");
                switch (parts[0]) {
                    case "1":
                        Book.readListOfBooks(c);
                        System.out.println(MESS[1]);
                        do{

                            keyboard = new Scanner(System.in);
                            input = keyboard.nextLine();
                            parts = input.split(",");
                            switch (parts[0]){
                                case "1":
                                    Book.deleteBook(c,Integer.valueOf(parts[1]));
                                    Book.readListOfBooks(c);
                                    break;
                                case "2":
                                    Book.setQuantityBook(c,Integer.valueOf(parts[1]),Integer.valueOf(parts[2]));
                                    break;
                                case "3":
                                    Book.incDecBook(c,Integer.valueOf(parts[1]),Integer.valueOf(parts[2]),Boolean.valueOf(parts[3]));
                                    break;
                                case "help":
                                    System.out.println(MESS[1]);
                                    break;
                            }
                        }
                        while(parts[0].compareTo("back") != 0);

                        break;

                    case "2":
                        Book.addBook(c,parts[1],Float.valueOf(parts[2]),parts[3],Integer.valueOf(parts[4]),Integer.valueOf(parts[5]),Integer.valueOf(parts[6]),Integer.valueOf(parts[7]),Integer.valueOf(parts[8]));
                        break;
                    case "3":
                        Author.readListOfAuthors(c);

                        System.out.println(MESS[2]);
                        do{

                            keyboard = new Scanner(System.in);
                            input = keyboard.nextLine();
                            parts = input.split(",");
                            switch (parts[0]){
                                case "1":
                                    Author.deleteAuthor(c,Integer.valueOf(parts[1]));
                                    break;
                                case "help":
                                    System.out.println(MESS[2]);
                                    break;
                            }
                        }
                        while(parts[0].compareTo("back") != 0);
                        break;
                    case "4":
                        Author.addAuthor(c,parts[1],parts[2],parts[3]);
                        break;
                    case "5":
                        Publisher.readListOfPublishers(c);

                        System.out.println(MESS[3]);
                        do{

                            keyboard = new Scanner(System.in);
                            input = keyboard.nextLine();
                            parts = input.split(",");
                            switch (parts[0]){
                                case "1":
                                    Publisher.deletePublisher(c,Integer.valueOf(parts[1]));
                                    break;
                                case "help":
                                    System.out.println(MESS[3]);
                                    break;
                            }
                        }
                        while(parts[0].compareTo("back") != 0);
                        break;
                    case "6":
                        Publisher.addPublisher(c,parts[1],parts[2]);
                        break;
                    case "exit":
                        exited = true;
                        break;
                    case "help":
                        System.out.println(MESS[0]);
                        break;
                }



            }




        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}