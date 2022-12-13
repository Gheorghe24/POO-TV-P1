package actions;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import out.Output;
import platform.Page;


public final class ChangePage {
    public static void execute(ArrayNode jsonOutput, Page currentPage, String pageName) {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (pageName) {
            case "register" :
                if (currentPage.getCurrentUser() == null && currentPage.getName().equals("homepage")) {
                    populateCurrentPage(currentPage, pageName);
                } else {
                    currentPage.setName("homepage");
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "login" :
                if (currentPage.getCurrentUser() == null && currentPage.getName().equals("homepage"))
                    populateCurrentPage(currentPage, pageName);
                else {
                    currentPage.setName("homepage");
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "logout" :
                if (currentPage.getCurrentUser() != null) {
                    populateCurrentPage(currentPage, "homepage");
                } else {
                    addPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;
        }

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
