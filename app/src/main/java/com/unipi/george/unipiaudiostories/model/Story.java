package com.unipi.george.unipiaudiostories.model;

public class Story {
    private String title;
    private String author;

    public Story() {} // Απαραίτητος κενός constructor για Firestore

    public Story(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}
