package com.example.moviesapp;

public class MovieData {
    private String movieName;
    private String movieDate;
    private String movieImage;
    private String movieDescription;
    private int movieId;
    public MovieData(int movieId ,String movieName, String movieDate,
                       String movieImage) {
        this.movieName = movieName;
        this.movieDate = movieDate;
        this.movieImage = movieImage;
        this.movieId = movieId;
    }
    public int getMovieId() {
        return movieId;
    }
    public String getMovieName() {
        return movieName;
    }
    public String getMovieDate() {
        return movieDate;
    }
    public String getMovieImage() {
        return movieImage;
    }
    public String getMovieDescription() {
        return movieDescription;
    }
}