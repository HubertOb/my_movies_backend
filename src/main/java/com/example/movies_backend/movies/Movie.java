package com.example.movies_backend.movies;

import jakarta.persistence.*;


@Entity
public class Movie {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private int movie_year;
//    private String poster_path;
    private String movie_path;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] poster_img;

    public Movie(){}

    public Movie(String title, int movie_year, String movie_path, byte[] poster_img) {
        this.title = title;
        this.movie_year = movie_year;
//        this.poster_path = poster_path;
        this.movie_path = movie_path;
        this.poster_img = poster_img;
    }

    @Override
    public String toString() {
        return title+" "+movie_year+" "+movie_path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMovie_year() {
        return movie_year;
    }

    public void setMovie_year(int movie_year) {
        this.movie_year = movie_year;
    }

//    public String getPoster_path() {
//        return poster_path;
//    }

//    public void setPoster_path(String poster_path) {
//        this.poster_path = poster_path;
//    }

    public String getMovie_path() {
        return movie_path;
    }

    public void setMovie_path(String movie_path) {
        this.movie_path = movie_path;
    }

    public byte[] getPoster_img() {return this.poster_img;}

    public void setPoster_img(byte[] poster_img) {this.poster_img=poster_img;}
}
