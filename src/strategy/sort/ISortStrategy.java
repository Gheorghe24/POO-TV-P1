package strategy.sort;

import io.Movie;
import java.util.List;

public interface ISortStrategy {
    /**
     * @param movies from input
     * @param order ascending(increasing) / desceding(decreasing)
     * @return sorted list
     */
    List<Movie> sortMovies(List<Movie> movies, String order);
}
