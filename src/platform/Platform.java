package platform;

import actions.ChangePage;
import actions.ICommand;
import actions.OnPage;
import actions.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Credentials;
import io.Input;
import io.Movie;
import io.User;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class Platform {
    private Input inputData;
    private ArrayNode output;
    private Page currentPage;

    private List<ICommand> commandList;

    public void takeCommand(ICommand command) {
        commandList.add(command);
    }

    public void placeCommands() {
        for (ICommand command: commandList) {
            command.executeCommand();
        }
        commandList.clear();
    }

    public void executeListOfActions() {

        for (var action : inputData.getActions()) {
            switch (action.getType()) {
                case "change page" -> takeCommand(new ChangePage(currentPage, output,
                        action.getPage()));
                case "on page" -> takeCommand(new OnPage(currentPage, output, action.getFeature(),
                        inputData, new Credentials(action.getCredentials())));
            }
        }

        placeCommands();
    }

    public void prepareForNewEntry() {
        currentPage.setCurrentUser(null);
        inputData.getUsers().forEach(User::resetUser);
        inputData.getMovies().forEach(Movie::resetMovies);
    }
}
