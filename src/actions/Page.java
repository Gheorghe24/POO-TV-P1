package actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.Credentials;
import io.Input;
import io.Movie;
import io.User;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import out.Output;
import services.UserService;

@Getter
@Setter
@Builder
public class Page {
    private String name;
    private User currentUser;
    private String startUserAction;
    private ArrayList<Movie> moviesList;
    private Movie currentMovie;

    public void changePage(ArrayNode jsonOutput, String pageName) {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (pageName) {
            case "register" :
                if (this.getCurrentUser() == null && this.getName().equals("homepage")) {
                    populateCurrentPage(this, pageName);
                } else {
                    this.setName("homepage");
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "login" :
                if (this.getCurrentUser() == null && this.getName().equals("homepage"))
                    populateCurrentPage(this, pageName);
                else {
                    this.setName("homepage");
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "logout" :
                if (this.getCurrentUser() != null) {
                    populateCurrentPage(this, "homepage");
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;
        }

    }

    public void onPage(ArrayNode jsonOutput, String feature,
                               Input inputData, Credentials credentials) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserService userService = new UserService();
        switch (feature) {
            case "login" -> {
                if (this.getCurrentUser() == null && this.getName().equals("login")) {
                    User userFound = userService.checkForUserInData(inputData, credentials);

                    if (userFound != null) {
                        this.setCurrentUser(userFound);
                        addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper);
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
                        addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper);
                    } else {
                        addPOJOToArrayNode(jsonOutput, objectMapper);
                        this.setName("homepage");
                    }
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                    this.setName("homepage");
                }

            }
        }
    }

    private static void addPOJOWithPopulatedOutput(ArrayNode jsonOutput, Page currentPage,
                                                   ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.valueToTree(Output
                .builder()
                .currentMoviesList(new ArrayList<>())
                .currentUser(currentPage.getCurrentUser())
                .build());
        jsonOutput.add(node);
    }

    private static void addPOJOToArrayNode(ArrayNode jsonOutput, ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.valueToTree(Output
                .builder()
                .error("Error")
                .currentMoviesList(new ArrayList<>())
                .build());
        jsonOutput.add(node);
    }

    private static void populateCurrentPage(Page currentPage, String pageName) {
        currentPage.setName(pageName);
        currentPage.setMoviesList(null);
        currentPage.setCurrentMovie(null);
        currentPage.setCurrentUser(null);
    }


}
