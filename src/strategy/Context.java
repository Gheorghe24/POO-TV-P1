package strategy;

import io.Movie;
import java.util.List;
import strategy.filter.IFilterStrategy;

public final class Context<T> {
    private final IFilterStrategy<T> stringStrategy;
    public Context(final IFilterStrategy<T> stringStrategy) {
        this.stringStrategy = stringStrategy;
    }

    /**
     * @param movies
     * @param fields for filtering list of movies
     * @return filtered list
     */
    public List<Movie> executeStrategy(final List<Movie> movies, final T fields) {
        return stringStrategy.filterMovies(movies, fields);
    }
}
