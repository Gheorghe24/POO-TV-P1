package strategy.filter;

import io.Movie;
import java.util.List;

public final class FilterName implements IFilterStrategy<String> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final String field) {
        return  movies
                .stream()
                .filter(movie
                        -> movie.getName().startsWith(field))
                .toList();
    }
}
