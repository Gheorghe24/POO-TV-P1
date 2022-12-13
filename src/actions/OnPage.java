package actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.Credentials;
import io.Input;
import io.User;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import out.Output;
import platform.Page;
import services.UserService;

@Getter
@Setter
public class OnPage {
    public static void execute(ArrayNode jsonOutput, Page currentPage, String feature,
                               Input inputData, Credentials credentials) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserService userService = new UserService();
        switch (feature) {
            case "login" -> {
                if (currentPage.getCurrentUser() == null && currentPage.getName().equals("login")) {
                    User userFound = userService.checkForUserInData(inputData, credentials);

                    if (userFound != null) {
                        currentPage.setCurrentUser(userFound);
                        addPOJOWithPopulatedOutput(jsonOutput, currentPage, objectMapper);
                    } else {
                        addPOJOToArrayNode(jsonOutput, objectMapper);
                    }
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                currentPage.setName("homepage");
            }
            case "register" -> {
                if (currentPage.getCurrentUser() == null && currentPage.getName().equals(
                        "register")) {
                    var registeredNewUser = userService.registerNewUser(inputData, credentials);
                    if (registeredNewUser != null) {
                        currentPage.setCurrentUser(registeredNewUser);
                        addPOJOWithPopulatedOutput(jsonOutput, currentPage, objectMapper);
                    } else {
                        addPOJOToArrayNode(jsonOutput, objectMapper);
                        currentPage.setName("homepage");
                    }
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                    currentPage.setName("homepage");
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

}
