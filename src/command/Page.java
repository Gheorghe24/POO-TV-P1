package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.Action;
import io.Contains;
import io.Credentials;
import io.Input;
import io.Movie;
import io.Sort;
import io.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import out.Output;
import services.UserService;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterActor;
import strategy.filter.FilterCountry;
import strategy.filter.FilterGenre;
import strategy.filter.FilterName;
import strategy.sort.ContextForSort;
import strategy.sort.SortDuration;
import strategy.sort.SortRating;
import utils.Utils;

@Getter
@Setter
@Builder
public final class Page {
    private String name;
    private User currentUser;
    private String startUserAction;
    private List<Movie> moviesList;
    private Movie currentMovie;

    /**
     * @param jsonOutput Output to add Json Objects
     * @param action from Input
     */
    public void changePage(final ArrayNode jsonOutput, final Action action, final Input input) {
        ObjectMapper objectMapper = new ObjectMapper();
        String pageName = action.getPage();
        switch (pageName) {
            case "register" :

            case "login" :
                if (this.getCurrentUser() == null && this.getName().equals("homepage")) {
                    populateCurrentPage(pageName, new ArrayList<>(), null, null);
                } else {
                    this.setName("homepage");
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "logout" :
                if (this.getCurrentUser() != null) {
                    populateCurrentPage("homepage", new ArrayList<>(), null, null);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;
            case "movies" :
                if (this.getCurrentUser() != null) {
                    populateCurrentPage(pageName,
                            new ContextForFilter<>(new FilterCountry())
                                    .executeStrategy(input.getMovies(),
                                            currentUser.getCredentials().getCountry()),
                            null,
                            currentUser);
                    addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper, this.moviesList);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;
            case "see details" :
                if (this.getCurrentUser() != null && this.getName().equals("movies")) {
                    List<Movie> movies = new ContextForFilter<>(new FilterCountry())
                            .executeStrategy(input.getMovies(),
                                    currentUser.getCredentials().getCountry());
                    List<Movie> foundMovie = new ContextForFilter<>(new FilterName())
                            .executeStrategy(movies,
                                    action.getMovie());
                    if (foundMovie.isEmpty()) {
                        addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                    } else {
                        populateCurrentPage(pageName, foundMovie, foundMovie.get(0), currentUser);
                        addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper, this.moviesList);
                    }
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;
            case "upgrades" :
                if (this.getCurrentUser() != null) {
                    populateCurrentPage(pageName, null, null, currentUser);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;
            default:
        }

    }

    /**
     * @param jsonOutput Output to add Json Objects
     * @param action from Input
     * @param inputData Database/Input class from Test File
     * @param credentials from Input for register operation
     */
    public void onPage(final ArrayNode jsonOutput, final Action action,
                       final Input inputData, final Credentials credentials) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserService userService = new UserService();
        String feature = action.getFeature();
        switch (feature) {
            case "login" -> {
                if (this.getCurrentUser() == null && this.getName().equals("login")) {
                    User userFound = userService.checkForUserInData(inputData, credentials);

                    if (userFound != null) {
                        this.setCurrentUser(userFound);
                        addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper, this.moviesList);
                    } else {
                        addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                    }
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                this.setName("homepage");
            }
            case "register" -> {
                if (this.getCurrentUser() == null && this.getName().equals(
                        "register")) {
                    var registeredNewUser = userService.registerNewUser(inputData, credentials);
                    if (registeredNewUser != null) {
                        this.setCurrentUser(registeredNewUser);
                        addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper, this.moviesList);
                    } else {
                        addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                        this.setName("homepage");
                    }
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                    this.setName("homepage");
                }
            }
            case "search" -> {
                if (this.getName().equals("movies")) {
                    this.moviesList = new ContextForFilter<>(new FilterName())
                            .executeStrategy(inputData.getMovies(),
                                    currentUser.getCredentials().getCountry());
                    addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper, this.moviesList);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "filter" -> {
                if (this.getName().equals("movies")) {
                    this.moviesList = new ContextForFilter<>(new FilterCountry())
                            .executeStrategy(inputData.getMovies(),
                                    currentUser.getCredentials().getCountry());
                    Sort sortField = action.getFilters().getSort();
                    sortInputMovies(sortField);
                    Contains containsField = action.getFilters().getContains();
                    filterInputMoviesByContains(containsField);
                    addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper, this.moviesList);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "buy tokens" -> {
                if (this.getName().equals("upgrades")) {
                    var balance =
                            Integer.parseInt(this.getCurrentUser().getCredentials().getBalance());
                    var count = Integer.parseInt(action.getCount());
                    if (balance > count) {
                        currentUser.setTokensCount(count);
                        currentUser.getCredentials().setBalance(String.valueOf(balance - count));
                    } else {
                        addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                    }
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }
            case "buy premium account" -> {
                if (this.getName().equals("upgrades")) {
                    var count = currentUser.getTokensCount();
                    if (count >= Utils.TOKEN_COST
                            && !currentUser.getCredentials().getAccountType().equals(
                            "premium")) {
                        currentUser.getCredentials().setAccountType("premium");
                        currentUser.setTokensCount(count - Utils.TOKEN_COST);
                    } else {
                        addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                    }
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "purchase" -> {
                if (this.getName().equals("upgrades") || this.getName().equals("see details")) {
                    purchaseMovie(jsonOutput, action, objectMapper);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "watch" -> {
                if (this.getName().equals("see details")) {
                    watchMovie(jsonOutput, action, objectMapper);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "like" -> {
                if (this.getName().equals("see details")) {
                    likeMovie(jsonOutput, action, objectMapper, inputData);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "rate" -> {
                if (this.getName().equals("see details")) {
                    rateMovie(jsonOutput, action, objectMapper, inputData);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            default -> {
            }
        }
    }

    private void rateMovie(final ArrayNode jsonOutput, final Action action,
                           final ObjectMapper objectMapper,
                           final Input input) {
        if (currentUser.getWatchedMovies().isEmpty()
                || action.getRate() < 1
                || action.getRate() > Utils.MAXIMUM_RATE) {
            addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            return;
        }
        if (!getMoviesByName(extractMovieName(action), currentUser.getWatchedMovies()).isEmpty()) {
            Movie movie = getMoviesByName(extractMovieName(action),
                    currentUser.getWatchedMovies()).get(0);
            int counterOfRatings = movie.getNumRatings();
            movie.setRating((movie.getRating() * counterOfRatings + action.getRate())
                    / (counterOfRatings + 1));
            updateMovieInAllObjects(movie, input);
            currentUser.getRatedMovies().add(movie);
            addPOJOWithPopulatedOutput(jsonOutput, this,
                    objectMapper, new ArrayList<>(Collections.singleton(
                            new Movie(movie))));
        } else {
            addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    /**
     * verify that first the movie was watched
     * increment number of likes for every list containing him
     */
    private void likeMovie(final ArrayNode jsonOutput, final Action action,
                           final ObjectMapper objectMapper, final Input inputData) {
        if (currentUser.getWatchedMovies().isEmpty()) {
            addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            return;
        }
        if (!getMoviesByName(action.getMovie(), currentUser.getWatchedMovies()).isEmpty()) {
            Movie movie =
                    getMoviesByName(extractMovieName(action),
                            currentUser.getWatchedMovies()).get(0);
            movie.setNumLikes(movie.getNumLikes() + 1);
            currentMovie = new Movie(movie);
            updateMovieInAllObjects(movie, inputData);
            currentUser.getLikedMovies().add(movie);
            addPOJOWithPopulatedOutput(jsonOutput, this,
                    objectMapper, new ArrayList<>(Collections.singleton(
                            new Movie(movie))));
        } else {
            addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    private void watchMovie(final ArrayNode jsonOutput, final Action action,
                            final ObjectMapper objectMapper) {
        if (currentUser.getPurchasedMovies().isEmpty()) {
            addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        } else {
            String movieName = extractMovieName(action);
            List<Movie> availableFromPurchasedMovies = getMoviesByName(movieName,
                    currentUser.getPurchasedMovies());
            List<Movie> notFoundInWatchedMovies = getMoviesByName(movieName,
                    currentUser.getWatchedMovies());
            if (availableFromPurchasedMovies.isEmpty()) {
                addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            } else if (notFoundInWatchedMovies.isEmpty()) {
                this.currentMovie = availableFromPurchasedMovies.get(0);
                currentUser.getWatchedMovies().add(
                        new Movie(currentMovie));

                addPOJOWithPopulatedOutput(jsonOutput, this,
                        objectMapper, new ArrayList<>(Collections.singleton(
                                new Movie(currentMovie))));
            }
        }
    }

    /**
     * Increment number of likes for every list containing movie.name
     */
    public void updateMovieInAllObjects(final Movie movie, final Input input) {
        input.getUsers().forEach((x) -> {
            x.getWatchedMovies().forEach((y) -> {
                if (y.getName().equals(movie.getName())) {
                    x.getWatchedMovies().set(x.getWatchedMovies().indexOf(y), new Movie(movie));
                }

            });
            x.getLikedMovies().forEach((y) -> {
                if (!x.getCredentials().getName().equals(currentUser.getCredentials().getName())
                        && y.getName().equals(movie.getName())) {
                    x.getLikedMovies().set(x.getLikedMovies().indexOf(y), new Movie(movie));
                }

            });
            x.getPurchasedMovies().forEach((y) -> {
                if (y.getName().equals(movie.getName())) {
                    x.getPurchasedMovies().set(x.getPurchasedMovies().indexOf(y), new Movie(movie));
                }

            });
            x.getRatedMovies().forEach((y) -> {
                if (y.getName().equals(movie.getName())) {
                    x.getRatedMovies().set(x.getRatedMovies().indexOf(y), new Movie(movie));
                }
            });
        });
        input.getMovies().forEach(m -> {
            if (m.getName().equals(movie.getName())) {
                input.getMovies().set(input.getMovies().indexOf(m), new Movie(movie));
            }
        });
    }



    private String extractMovieName(final Action action) {
        if (action.getMovie() != null) {
            return action.getMovie();
        } else {
            return currentMovie.getName();
        }
    }

    private void purchaseMovie(final ArrayNode jsonOutput, final Action action,
                               final ObjectMapper objectMapper) {
        List<Movie> availableMovies;
        if (action.getMovie() != null) {
            availableMovies = getMoviesByName(action.getMovie(), this.moviesList);
        } else {
            availableMovies = getMoviesByName(currentMovie.getName(), this.moviesList);
        }
        if (!availableMovies.isEmpty()) {
            var firstAvailableMovie = availableMovies.get(0);
            if (!currentUser.getPurchasedMovies().contains(firstAvailableMovie)) {
                if (currentUser.getCredentials().getAccountType().equals("premium")
                        && currentUser.getNumFreePremiumMovies() > 0) {
                    currentUser.setNumFreePremiumMovies(
                            currentUser.getNumFreePremiumMovies() - 1);
                    currentUser.getPurchasedMovies().add(
                            new Movie(firstAvailableMovie));
                    addPOJOWithPopulatedOutput(jsonOutput, this,
                            objectMapper, availableMovies);
                } else if (currentUser.getTokensCount() >= 2) {
                    currentUser.setTokensCount(currentUser.getTokensCount() - 2);
                    currentUser.getPurchasedMovies().add(
                            new Movie(firstAvailableMovie));
                    addPOJOWithPopulatedOutput(jsonOutput, this,
                            objectMapper, availableMovies);
                } else {
                    addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            } else {
                addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            }
        } else {
            addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }


    private List<Movie> getMoviesByName(final String fieldFilter, final List<Movie> testedList) {
        return new ContextForFilter<>(new FilterName())
                .executeStrategy(testedList,
                        fieldFilter);
    }

    private void filterInputMoviesByContains(final Contains containsField) {
        if (containsField != null) {
            if (containsField.getActors() != null) {
                this.moviesList = new ContextForFilter<>(new FilterActor())
                        .executeStrategy(moviesList, containsField.getActors());
            }
            if (containsField.getGenre() != null) {
                this.moviesList = new ContextForFilter<>(new FilterGenre())
                        .executeStrategy(moviesList, containsField.getGenre());
            }
        }
    }

    private void sortInputMovies(final Sort sortField) {
        if (sortField != null) {
            if (sortField.getRating() != null && sortField.getDuration() != null) {
                this.moviesList = new ContextForSort(new SortDuration())
                        .executeStrategy(moviesList, sortField.getDuration());
                if (moviesList.size() > 1) {
                    if (moviesList.get(0).getDuration()
                                    .equals(moviesList.get(1).getDuration())) {
                        this.moviesList = new ContextForSort(new SortRating())
                                .executeStrategy(moviesList, sortField.getRating());
                    }
                }
            } else if (sortField.getRating() != null) {
                this.moviesList = new ContextForSort(new SortRating())
                        .executeStrategy(moviesList, sortField.getRating());
            } else {
                this.moviesList = new ContextForSort(new SortDuration())
                        .executeStrategy(moviesList, sortField.getDuration());
            }
        }
    }

    private void addPOJOWithPopulatedOutput(final ArrayNode jsonOutput,
                                                   final Page currentPage,
                                                   final ObjectMapper objectMapper,
                                            final List<Movie> movies) {
        ObjectNode node = objectMapper.valueToTree(Output
                .builder()
                .currentMoviesList(movies)
                .currentUser(currentPage.getCurrentUser())
                .build());
        jsonOutput.add(node);
    }

    private void addErrorPOJOToArrayNode(final ArrayNode jsonOutput,
                                         final ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.valueToTree(Output
                .builder()
                .error("Error")
                .currentMoviesList(new ArrayList<>())
                .build());
        jsonOutput.add(node);
    }

    private void populateCurrentPage(final String pageName, final List<Movie> movies,
                                     final Movie movie, final User user) {
        this.setName(pageName);
        this.setMoviesList(movies);
        this.setCurrentMovie(movie);
        this.setCurrentUser(user);
    }


}
