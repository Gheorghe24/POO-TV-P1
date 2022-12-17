package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Action;
import io.Contains;
import io.Input;
import io.Movie;
import io.Sort;
import io.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Setter;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterActor;
import strategy.filter.FilterGenre;
import strategy.filter.FilterName;
import strategy.sort.ContextForSort;
import strategy.sort.SortComplex;
import strategy.sort.SortDuration;
import strategy.sort.SortRating;
import utils.OrderPair;
import utils.Utils;

@Setter
public final class MovieService {

    private OutputService outputService;

    public MovieService() {
        outputService = new OutputService();
    }

    /**
     * @param action
     * @param movie
     * @return
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
     * @param testedList  list to filter
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
        List<Movie> filteredList = new ArrayList<>(moviesList);
        if (containsField != null) {
            if (containsField.getActors() != null) {
                filteredList = new ContextForFilter<>(new FilterActor())
                        .executeStrategy(moviesList, containsField.getActors());
            }
            if (containsField.getGenre() != null) {
                filteredList = new ContextForFilter<>(new FilterGenre())
                        .executeStrategy(filteredList, containsField.getGenre());
            }
        }
        return filteredList;
    }

    /**
     * @param sortField
     * @param moviesList
     * @return sort movies with different strategy
     * in this case with duration and rating
     */
    public List<Movie> sortInputMovies(final Sort sortField, final List<Movie> moviesList) {
        List<Movie> sortedList = new ArrayList<>(moviesList);
        if (sortField != null) {
            if (sortField.getRating() != null && sortField.getDuration() != null) {
                OrderPair pair = new OrderPair(sortField.getRating(), sortField.getDuration());
                sortedList = new ContextForSort<>(new SortComplex())
                        .executeStrategy(moviesList, pair);
            } else if (sortField.getRating() != null) {
                sortedList = new ContextForSort<>(new SortRating())
                        .executeStrategy(moviesList, sortField.getRating());
            } else {
                sortedList = new ContextForSort<>(new SortDuration())
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

    /**
     * checked failed cases and wrote added error to JSON
     * checked if the Movie exists in watched movies
     * Calculated average rating and added Movie to Rated
     * Updated Ratings in every
     */
    public void rateMovie(final ArrayNode jsonOutput, final Action action,
                          final ObjectMapper objectMapper,
                          final Input input,
                          final Page currentPage) {
        if (currentPage.getCurrentUser().getWatchedMovies().isEmpty()
                || action.getRate() < 1
                || action.getRate() > Utils.MAXIMUM_RATE) {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            return;
        }
        if (!getMoviesByName(extractMovieName(action, currentPage.getCurrentMovie()),
                currentPage.getCurrentUser().getWatchedMovies()).isEmpty()) {
            Movie movie = getMoviesByName(extractMovieName(action,
                            currentPage.getCurrentMovie()),
                    currentPage.getCurrentUser().getWatchedMovies()).get(0);
            int counterOfRatings = movie.getNumRatings();
            movie.setRating((movie.getRating() * counterOfRatings + action.getRate())
                    / (counterOfRatings + 1));
            movie.setNumRatings(movie.getNumRatings() + 1);
            updateMovieInAllObjects(movie, input, currentPage.getCurrentUser());
            currentPage.getCurrentUser().getRatedMovies().add(movie);
            outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                    objectMapper, new ArrayList<>(Collections.singleton(
                            new Movie(movie))));
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    public void purchaseMovie(final ArrayNode jsonOutput, final Action action,
                              final ObjectMapper objectMapper, Page currentPage) {
        List<Movie> availableMovies;
        if (action.getMovie() != null) {
            availableMovies = getMoviesByName(action.getMovie(), currentPage.getMoviesList());
        } else {
            availableMovies = getMoviesByName(currentPage.getCurrentMovie().getName(),
                    currentPage.getMoviesList());
        }
        if (!availableMovies.isEmpty()) {
            var firstAvailableMovie = availableMovies.get(0);
            if (!currentPage.getCurrentUser().getPurchasedMovies().contains(firstAvailableMovie)) {
                if (currentPage.getCurrentUser().getCredentials().getAccountType().equals("premium")
                        && currentPage.getCurrentUser().getNumFreePremiumMovies() > 0) {
                    currentPage.getCurrentUser().setNumFreePremiumMovies(
                            currentPage.getCurrentUser().getNumFreePremiumMovies() - 1);
                    currentPage.getCurrentUser().getPurchasedMovies().add(
                            new Movie(firstAvailableMovie));
                    outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                            objectMapper, availableMovies);
                } else if (currentPage.getCurrentUser().getTokensCount() >= 2) {
                    currentPage.getCurrentUser().setTokensCount(currentPage.getCurrentUser().getTokensCount() - 2);
                    currentPage.getCurrentUser().getPurchasedMovies().add(
                            new Movie(firstAvailableMovie));
                    outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                            objectMapper, availableMovies);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            } else {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            }
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    public void watchMovie(final ArrayNode jsonOutput, final Action action,
                            final ObjectMapper objectMapper, final Page currentPage) {
        if (currentPage.getCurrentUser().getPurchasedMovies().isEmpty()) {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        } else {
            String movieName = extractMovieName(action, currentPage.getCurrentMovie());
            List<Movie> availableFromPurchasedMovies = getMoviesByName(movieName,
                    currentPage.getCurrentUser().getPurchasedMovies());
            List<Movie> notFoundInWatchedMovies = getMoviesByName(movieName,
                    currentPage.getCurrentUser().getWatchedMovies());
            if (availableFromPurchasedMovies.isEmpty()) {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            } else if (notFoundInWatchedMovies.isEmpty()) {
                currentPage.setCurrentMovie(availableFromPurchasedMovies.get(0));
                currentPage.getCurrentUser().getWatchedMovies().add(
                        new Movie(currentPage.getCurrentMovie()));

                outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                        objectMapper, new ArrayList<>(Collections.singleton(
                                new Movie(currentPage.getCurrentMovie()))));
            }
        }
    }

    /**
     * verify that first the movie was watched
     * increment number of likes for every list containing him
     */
    public void likeMovie(final ArrayNode jsonOutput, final Action action,
                           final ObjectMapper objectMapper, final Input inputData,
                          final Page currentPage) {
        if (currentPage.getCurrentUser().getWatchedMovies().isEmpty()) {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            return;
        }
        if (!getMoviesByName(action.getMovie(),
                currentPage.getCurrentUser().getWatchedMovies()).isEmpty()) {
            Movie movie =
                    getMoviesByName(extractMovieName(action,
                                    currentPage.getCurrentMovie()),
                            currentPage.getCurrentUser().getWatchedMovies()).get(0);
            movie.setNumLikes(movie.getNumLikes() + 1);
            currentPage.setCurrentMovie(new Movie(movie));
            updateMovieInAllObjects(movie, inputData, currentPage.getCurrentUser());
            currentPage.getCurrentUser().getLikedMovies().add(movie);
            outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                    objectMapper, new ArrayList<>(Collections.singleton(
                            new Movie(movie))));
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }
}
