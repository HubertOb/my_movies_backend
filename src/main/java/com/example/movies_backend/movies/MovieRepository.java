package com.example.movies_backend.movies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Query("SELECT COUNT(m) FROM Movie m WHERE m.title = :title")
    int countByTitle(@Param("title") String title);

    @Query("SELECT movie_path FROM Movie m WHERE m.id = :id")
    String getMoviePath(@Param("id") int id);
}
