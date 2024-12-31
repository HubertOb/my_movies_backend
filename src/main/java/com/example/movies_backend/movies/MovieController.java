package com.example.movies_backend.movies;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(path="movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping(value="/{id}")
    public Movie getMovieById(@PathVariable int id) {
        return movieService.getMovieById(id).get();
    }

    @GetMapping("/{id}/poster")
    public ResponseEntity<byte[]> getImageFromDatabase(@PathVariable int id) {
        try {
            Optional<Movie> movieOptional = movieService.getMovieById(id);

            if (movieOptional.isPresent()) {
                Movie movie = movieOptional.get();
                byte[] imageBytes = movie.getPoster_img(); // Pobranie obrazu z obiektu `Movie`

                if (imageBytes != null && imageBytes.length > 0) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"poster_" + id + ".jpg\"")
                            .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                            .body(imageBytes);
                } else {
                    return ResponseEntity.notFound().build(); // Jeśli obraz jest pusty
                }
            } else {
                return ResponseEntity.notFound().build(); // Jeśli film o podanym ID nie istnieje
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping
    public ResponseEntity<Movie> addMovie(
            @RequestParam("title") String title,
            @RequestParam("movie_year") int movieYear,
            @RequestParam("movie_path") String moviePath,
            @RequestParam("poster_image") MultipartFile posterImage) throws IOException {

        byte[] posterBytes = posterImage.getBytes();
        Movie movie = new Movie(title, movieYear, moviePath, posterBytes);
        Movie savedMovie = movieService.addMovie(movie);
        return new ResponseEntity<>(savedMovie, HttpStatus.CREATED);
    }
}
