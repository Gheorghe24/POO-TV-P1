package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.Action;
import io.Credentials;
import io.Input;
import io.Movie;
import io.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import out.Output;
import services.UserService;
import strategy.Context;
import strategy.FilterCountry;
import strategy.FilterName;

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
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "logout" :
                if (this.getCurrentUser() != null) {
                    populateCurrentPage("homepage", new ArrayList<>(), null, null);
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;
            case "movies" :
                if (this.getCurrentUser() != null) {
                    List<Movie> movies = new ArrayList<>(input.getMovies());
                    populateCurrentPage(pageName,
                            new Context<>(new FilterCountry())
                                    .executeStrategy(movies,
                                            currentUser.getCredentials().getCountry())
                            , null
                            , currentUser);
                    addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper, this.moviesList);
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
            default: return;
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
                        addPOJOToArrayNode(jsonOutput, objectMapper);
                    }
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
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
                        addPOJOToArrayNode(jsonOutput, objectMapper);
                        this.setName("homepage");
                    }
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                    this.setName("homepage");
                }
            }
            case "search" -> {
                if (this.getName().equals("movies")) {
                    this.moviesList = new Context<>(new FilterName())
                            .executeStrategy(inputData.getMovies(),
                                    currentUser.getCredentials().getCountry());
                    addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper, this.moviesList);
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            default -> {
                return;
            }
        }
    }

    private void addPOJOWithPopulatedOutput(final ArrayNode jsonOutput,
                                                   final Page currentPage,
                                                   final ObjectMapper objectMapper,
                                            List<Movie> movies) {
        ObjectNode node = objectMapper.valueToTree(Output
                .builder()
                .currentMoviesList(movies)
                .currentUser(currentPage.getCurrentUser())
                .build());
        jsonOutput.add(node);
    }

    private void addPOJOToArrayNode(final ArrayNode jsonOutput,
                                           final ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.valueToTree(Output
                .builder()
                .error("Error")
                .currentMoviesList(new ArrayList<>())
                .build());
        jsonOutput.add(node);
    }

    private void populateCurrentPage(final String pageName, List<Movie> moviesList,
                                     Movie movie, User currentUser) {
        this.setName(pageName);
        this.setMoviesList(moviesList);
        this.setCurrentMovie(movie);
        this.setCurrentUser(currentUser);
    }


}
