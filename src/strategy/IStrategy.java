package strategy;

import io.Movie;
import java.util.List;

/**
 * Interface for strategy pattern
 */
public interface IStrategy<T> {
    List<Movie> filterMovies(List<Movie> movies, T field);
}
