package platform;

import actions.ChangePage;
import actions.OnPage;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Input;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class Platform {
    private Input inputData;
    private ArrayNode output;
    private Page currentPage;

    public void executeListOfActions() {

        for (var action : inputData.getActions()) {
            switch (action.getType()) {
                case "change page" -> ChangePage.execute(output, currentPage, action.getPage());
                case "on page" -> OnPage.execute(output, currentPage, action.getFeature(),
                        inputData, action.getCredentials());
            }
        }
    }

    public void prepareForNewEntry(Page page) {
        //resetUser
        //resetMovies
        //resetCurrentUser
    }
}
