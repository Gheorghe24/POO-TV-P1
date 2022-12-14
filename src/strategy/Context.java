package strategy;

import io.Movie;
import java.util.ArrayList;

public class Context<T> {
    private final IStrategy<T> stringStrategy;
    public Context(IStrategy<T> stringStrategy) {
        this.stringStrategy = stringStrategy;
    }

    public ArrayList<Movie> executeStrategy(ArrayList<Movie> movies, T fields) {
        return stringStrategy.filterMovies(movies, fields);
    }
}
