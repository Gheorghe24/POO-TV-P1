package strategy;

import io.Movie;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for strategy pattern
 */
public interface IStrategy<T> {
    ArrayList<Movie> filterMovies(ArrayList<Movie> movies, T field);
}
