package strategy.sort;

import io.Movie;
import java.util.List;

public final class ContextForSort {
    private final ISortStrategy sortStrategy;

    public ContextForSort(final ISortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    /**
     * @param movies from input
     * @param order increasing / decreasing
     * @return sorted list
     */
    public List<Movie> executeStrategy(final List<Movie> movies, final String order) {
        return sortStrategy.sortMovies(movies, order);
    }
}
