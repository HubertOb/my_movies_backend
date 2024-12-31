package com.example.movies_backend.movies;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


@Entity
public class Movie {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private int movie_year;
    private String poster_path;
    private String movie_path;

    public Movie(){}

    public Movie(String title, int movie_year, String poster_path, String movie_path) {
        this.title = title;
        this.movie_year = movie_year;
        this.poster_path = poster_path;
        this.movie_path = movie_path;
    }

    @Override
    public String toString() {
        return title+" "+movie_year+" "+poster_path+" "+movie_path;
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

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getMovie_path() {
        return movie_path;
    }

    public void setMovie_path(String movie_path) {
        this.movie_path = movie_path;
    }
}
