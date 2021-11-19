package it.unipi;

import it.unipi.entity.*;

import java.util.List;

public class Viewer {

     static void printMainMenu(){
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

     static void printBookMenu(){
        System.out.println("****************************\n" +
            "Digit :\n" +
            "1 : to delete a book specifying the id (id) \n" +
            "2 : to set the quantity of a book (id,quantity) \n" +
            "3 : to increase or decrease the quantity of a book (id,quantity,true for increase false for decrease) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************");
    }

     static void printAuthorMenu(){
        System.out.println("****************************\n" +
            "Digit :\n" +
            "1 : to delete an author specifying the id (id) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************");
    }

     static void printPublisherMenu(){
        System.out.println("****************************\n" +
            "Digit :\n" +
            "1 : to delete a publisher specifying the id (id) \n" +
            "help : to see the possible commands\n" +
            "back : go back to main menu\n" +
            "****************************");
    }

    static void printResult(List rs){
         if(rs == null)
             return;
         for (int i = 0; i < rs.toArray().length; i++) {
                System.out.println(rs.get(i));
         }
    }
}
