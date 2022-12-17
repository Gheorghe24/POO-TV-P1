package services;

import io.Action;
import io.Contains;
import io.Input;
import io.Movie;
import io.Sort;
import io.User;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterActor;
import strategy.filter.FilterGenre;
import strategy.filter.FilterName;
import strategy.sort.ContextForSort;
import strategy.sort.SortDuration;
import strategy.sort.SortRating;

@NoArgsConstructor
public final class MovieService {
    /**
     * @param action from user
     * @param movie from user
     * @return correctName
     */
    public String extractMovieName(final Action action, final Movie movie) {
        if (action.getMovie() != null) {
            return action.getMovie();
        } else {
            return movie.getName();
        }
    }

    /**
     * @param fieldFilter
     * @param testedList list to filter
     * @return final result
     */
    public List<Movie> getMoviesByName(final String fieldFilter, final List<Movie> testedList) {
        return new ContextForFilter<>(new FilterName())
                .executeStrategy(testedList,
                        fieldFilter);
    }

    /**
     * @param containsField
     * @param moviesList
     * @return
     */
    public List<Movie> filterInputMoviesByContains(final Contains containsField,
                                                   final List<Movie> moviesList) {
        if (containsField != null) {
            if (containsField.getActors() != null) {
                return new ContextForFilter<>(new FilterActor())
                        .executeStrategy(moviesList, containsField.getActors());
            }
            if (containsField.getGenre() != null) {
                return new ContextForFilter<>(new FilterGenre())
                        .executeStrategy(moviesList, containsField.getGenre());
            }
        }
        return moviesList;
    }

    /**
     * @param sortField
     * @param moviesList
     * @return
     */
    public List<Movie> sortInputMovies(final Sort sortField, final List<Movie> moviesList) {
        List<Movie> sortedList = new ArrayList<>(moviesList);
        if (sortField != null) {
            if (sortField.getRating() != null && sortField.getDuration() != null) {
                sortedList = new ContextForSort(new SortDuration())
                        .executeStrategy(moviesList, sortField.getDuration());
                if (moviesList.size() > 1) {
                    if (moviesList.get(0).getDuration()
                            .equals(moviesList.get(1).getDuration())) {
                        sortedList = new ContextForSort(new SortRating())
                                .executeStrategy(moviesList, sortField.getRating());
                    }
                }
            } else if (sortField.getRating() != null) {
                sortedList = new ContextForSort(new SortRating())
                        .executeStrategy(moviesList, sortField.getRating());
            } else {
                sortedList = new ContextForSort(new SortDuration())
                        .executeStrategy(moviesList, sortField.getDuration());
            }
        }
        return sortedList;
    }

    /**
     * Increment number of likes for every list containing movie.name
     */
    public void updateMovieInAllObjects(final Movie movie, final Input input,
                                        final User currentUser) {
        input.getMovies().forEach(m -> {
            if (m.getName().equals(movie.getName())) {
                input.getMovies().set(input.getMovies().indexOf(m), new Movie(movie));
            }
        });
        input.getUsers().forEach((user) -> {
            user.getWatchedMovies().forEach((y) -> {
                if (y.getName().equals(movie.getName())) {
                    user.getWatchedMovies().set(user.getWatchedMovies().indexOf(y),
                            new Movie(movie));
                }

            });
            user.getLikedMovies().forEach((y) -> {
                if (!user.getCredentials().getName().equals(currentUser.getCredentials().getName())
                        && y.getName().equals(movie.getName())) {
                    user.getLikedMovies().set(user.getLikedMovies().indexOf(y),
                            new Movie(movie));
                }

            });
            user.getPurchasedMovies().forEach((y) -> {
                if (y.getName().equals(movie.getName())) {
                    user.getPurchasedMovies().set(user.getPurchasedMovies().indexOf(y),
                            new Movie(movie));
                }

            });
            user.getRatedMovies().forEach((y) -> {
                if (y.getName().equals(movie.getName())) {
                    user.getRatedMovies().set(user.getRatedMovies().indexOf(y), new Movie(movie));
                }
            });
        });
    }
}
