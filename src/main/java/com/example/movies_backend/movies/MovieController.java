package com.example.movies_backend.movies;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                byte[] imageBytes = movie.getPoster_img();

                if (imageBytes != null && imageBytes.length > 0) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"poster_" + id + ".jpg\"")
                            .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                            .body(imageBytes);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/video_file")
    public ResponseEntity<Resource> getVideo(@PathVariable int id,
                                             @RequestHeader(value = HttpHeaders.RANGE, defaultValue = "bytes=0-") String rangeHeader) {
        try {
            String moviePath = movieService.getMoviePath(id);
            Path videoPath = Paths.get(moviePath).normalize();

            if (Files.exists(videoPath) && Files.isReadable(videoPath)) {
                long fileSize = Files.size(videoPath);
                long startByte = 0;
                long endByte = fileSize - 1;
                String[] ranges = rangeHeader.replace("bytes=", "").split("-");
                if (ranges.length > 0) {
                    startByte = Long.parseLong(ranges[0]);
                }
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    endByte = Long.parseLong(ranges[1]);
                }

                long contentLength = endByte - startByte + 1;
                InputStream videoInputStream = Files.newInputStream(videoPath);
                videoInputStream.skip(startByte);

                InputStreamResource resource = new InputStreamResource(videoInputStream);

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoPath.getFileName() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                        .header(HttpHeaders.CONTENT_RANGE, "bytes " + startByte + "-" + endByte + "/" + fileSize)
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping
    public ResponseEntity<Movie> addMovie(
            @RequestParam("title") String title,
            @RequestParam("movie_year") int movieYear,
            @RequestParam("poster_image") MultipartFile posterImage,
            @RequestParam("movie_file") MultipartFile movieFile) throws IOException {

        byte[] posterBytes = posterImage.getBytes();
        String movie_title = title + "_" + movieService.getMovieTitleCount(title);

        Path rootLocation = Paths.get("../uploads");
        Path movie_path;

        try {
            if (movieFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            movie_path = rootLocation.resolve(Paths.get(movie_title))
                    .normalize().toAbsolutePath();

            if (!movie_path.getParent().equals(rootLocation.toAbsolutePath())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            try (InputStream inputStream = movieFile.getInputStream()) {
                Files.copy(inputStream, movie_path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Movie movie = new Movie(title, movieYear, movie_path.toString(), posterBytes);
        Movie savedMovie = movieService.addMovie(movie);

        return new ResponseEntity<>(savedMovie, HttpStatus.CREATED);
    }

}
