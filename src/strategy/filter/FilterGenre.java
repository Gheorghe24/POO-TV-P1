package strategy.filter;

import io.Movie;
import java.util.ArrayList;
import java.util.List;

public final class FilterGenre implements IFilterStrategy<ArrayList<String>> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final ArrayList<String> field) {
        return null;
    }
}
