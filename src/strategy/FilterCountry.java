package strategy;

import io.Movie;
import java.util.ArrayList;
import java.util.List;

public class FilterCountry implements IStrategy<String>{
    @Override
    public ArrayList<Movie> filterMovies(ArrayList<Movie> movies, String field) {
        List<Movie> filteredList = movies
                .stream()
                .filter(movie
                        -> !movie
                        .getCountriesBanned()
                        .contains(field))
                .toList();
        return null;
    }
}
