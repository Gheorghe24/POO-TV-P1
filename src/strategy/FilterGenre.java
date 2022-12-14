package strategy;

import io.Movie;
import java.util.ArrayList;
import java.util.List;

public class FilterGenre implements IStrategy<ArrayList<String>>{
    @Override
    public List<Movie> filterMovies(List<Movie> movies, ArrayList<String> field) {
        return null;
    }
}
