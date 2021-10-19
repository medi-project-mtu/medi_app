package com.example.medi_android;

public class Review {
    String Rating;
    String Comment;

    public Review() {
    }

    public Review(String rating, String comment) {
        Rating = rating;
        Comment = comment;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
}
