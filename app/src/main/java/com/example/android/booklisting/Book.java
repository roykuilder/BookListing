package com.example.android.booklisting;

import java.util.ArrayList;

public class Book {

    /**
     * Private variables to hold the information of 1 book.
     */
    private String title;
    private ArrayList<String> categorie;
    private String infoLink;
    private String description;

    /**
     * Constructor to create a new Book Object.
     */

    public Book(String title, ArrayList<String> author, String infoLink, String description) {
        this.title = title;
        this.categorie = author;
        this.infoLink = infoLink;
        this.description = description;
    }

    /**
     * Getter methods.
     */
    public String getTitle() {
        return title;
    }

    public ArrayList<String> getCategorie() {
        return categorie;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public String getDiscription() {
        return description;
    }
}
