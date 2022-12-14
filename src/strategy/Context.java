package strategy;

import io.Movie;
import java.util.List;

public class Context<T> {
    private final IStrategy<T> stringStrategy;
    public Context(IStrategy<T> stringStrategy) {
        this.stringStrategy = stringStrategy;
    }

    public List<Movie> executeStrategy(List<Movie> movies, T fields) {
        return stringStrategy.filterMovies(movies, fields);
    }
}
