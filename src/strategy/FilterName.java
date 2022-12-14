package strategy;

import io.Movie;
import java.util.List;

public class FilterName implements IStrategy<String>{
    @Override
    public List<Movie> filterMovies(List<Movie> movies, String field) {
        return  movies
                .stream()
                .filter(movie
                        -> movie.getName().startsWith(field))
                .toList();
    }
}
