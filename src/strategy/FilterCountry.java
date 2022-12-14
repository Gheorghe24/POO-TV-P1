package strategy;

import io.Movie;
import java.util.List;

public class FilterCountry implements IStrategy<String>{
    @Override
    public List<Movie> filterMovies(List<Movie> movies, String field) {
        return movies
                .stream()
                .filter(movie
                        -> !movie
                        .getCountriesBanned()
                        .contains(field))
                .toList();
    }
}
