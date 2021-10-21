package it.unipi;

import java.util.*;

public class Main {

    private static int countVowels(String s){
        int count = 0;
        for(int i = 0; i < s.length(); i++){
            // if("aeiou".indexOf(c) != -1)
            if(s.charAt(i) == 'a' || s.charAt(i) == 'e' || s.charAt(i) == 'i' || s.charAt(i) == 'o' || s.charAt(i) == 'u')
                count++;
        }
        return count;
    }

    public static void main(String[] args) {
	// write your code here
        System.out.println("Hello world! \n");

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter your 3 numbers");
        int max = keyboard.nextInt();
        for(int i = 1; i < 3; i++){
            int newVal = keyboard.nextInt();
            if(max < newVal)
                max = newVal;
        }
        System.out.printf("The greatest : '%d'\n", max);

        System.out.println("Enter your string");
        keyboard = new Scanner(System.in);
        String frase = keyboard.nextLine();
        System.out.printf("Number of Vowels in the string: %d\n",countVowels(frase));

        String array[] = {"Red","Green","Orange","White","Black"};

        System.out.printf("First Element : %s\nThird Element :  %s\n",array[0],array[2]);

        /*
        ArrayList array = new ArrayList<String>(Arrays.asList("Red","Green","..."));
        array.get(0);
        array.get(2);
        */

    }
}
