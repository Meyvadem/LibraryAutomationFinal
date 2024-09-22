package com.mycompany.cpdeneme;


import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Merve
 */
public class BookDatabase {

    public static ArrayList<Book> bookDatabase = new ArrayList<>();

    static public void loadBookData() {
        
        
        Book book1 = new Book("Dark Valley", "1", "Joe Donnelly", "Horror", "1th", null, null, false, "400","3");
        Book book2 = new Book("Del Rey World War Z", "2", "Max Brooks", "Horror", "2th", null, null, true, "500","-1");
        Book book3 = new Book("The Song of the Cell", "3", "Siddhartha Mukherjee", "Science", "1th", null, null, true, "350","-1");
        Book book4 = new Book("Breatheless", "4", "David Quammen", "Science", "2th", null, null, true, "400","-1");
        Book book5 = new Book("Clean Code: A Handbook of Agile Software Craftsmanship", "5", "Robert C. Martin", "Computer Science", "1th", null, null, true, "550","-1");
        Book book6 = new Book("Introduction to Algorithms", "6", "Ron Rivest", "Computer Science", "1th", null, null, true, "500","-1");
        Book book7 = new Book("Hackers: Heroes of the Computer Revolutio", "7", "Steven Levy", "Computer Science", "1th", null, null, true, "500","-1");
        Book book8 = new Book("Born A Crime: Stories from a South African Childhood", "8", "Trevor Noah", "Comedy", "2th", null, null, true, "400","-1");
        Book book9 = new Book("Man’s Searching for Meaning", "9", "Viktor E. Frankl", "Philosophy", "1th", null, null, true, "400","-1");
        Book book10 = new Book("Beyond Good and Evil", "10", "Nietzsche", "Philosophy", "3th", null, null, true,"350","-1");

        bookDatabase.add(book1);
        bookDatabase.add(book2);
        bookDatabase.add(book3);
        bookDatabase.add(book4);
        bookDatabase.add(book5);
        bookDatabase.add(book6);
        bookDatabase.add(book7);
        bookDatabase.add(book8);
        bookDatabase.add(book9);
        bookDatabase.add(book10);
    }
    
    public static void UpdateBookState(int bookId,boolean isAvailable,String costID)
    {
        String value = String.valueOf(bookId);
        for (Book book : bookDatabase) {
            if(book.getBookID().equals(value)){
                book.setIsAvailable(isAvailable);
                book.setCostID(costID);
            }
        }
        
    }
}
