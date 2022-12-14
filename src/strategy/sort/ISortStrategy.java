package strategy.sort;

import io.Movie;
import java.util.List;

public interface ISortStrategy {
    List<Movie> sortMovies(List<Movie> movies, String order);
}
