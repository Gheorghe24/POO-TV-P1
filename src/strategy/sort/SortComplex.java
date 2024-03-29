package strategy.sort;

import io.Movie;
import java.util.Comparator;
import java.util.List;
import utils.OrderPair;

public final class SortComplex implements ISortStrategy<OrderPair> {
    @Override
    public List<Movie> sortMovies(final List<Movie> movies, final OrderPair order) {
        Comparator<Movie> firstComparator = Comparator.comparing(Movie::getDuration);
        if (order.getDurationOrder().equals("increasing") && (order.getRatingOrder().equals(
                "increasing"))) {
           return movies.stream()
                    .sorted(firstComparator.thenComparing(Movie::getRating)).toList();
        } else if (order.getDurationOrder().equals("increasing") && (order.getRatingOrder().equals(
                "decreasing"))) {
            return movies.stream()
                    .sorted(firstComparator.thenComparing(Movie::getRating).reversed()).toList();
        } else if (order.getDurationOrder().equals("decreasing") && (order.getRatingOrder().equals(
                "increasing"))) {
            return movies.stream()
                    .sorted(firstComparator.reversed().thenComparing(Movie::getRating)).toList();
        } else {
            return movies.stream()
                    .sorted(firstComparator.reversed().thenComparing(Movie::getRating).reversed())
                    .toList();
        }
    }
}
