package command;

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

    /**
     * @param command object
     * method for Invoker Class
     */
    public void takeCommand(final ICommand command) {
        commandList.add(command);
    }

    /**
     * method for Invoker Class
     */
    public void placeCommands() {
        for (ICommand command: commandList) {
            command.executeCommand();
        }
        commandList.clear();
    }

    /**
     * place Commands of each type
     */
    public void executeListOfActions() {

        inputData.getActions().forEach(action -> {
            switch (action.getType()) {
                case "change page" -> takeCommand(new ChangePage(currentPage, output,
                        action, inputData));
                case "on page" -> takeCommand(new OnPage(currentPage, output, action,
                        inputData, new Credentials(action.getCredentials())));
                default -> {
                    return;
                }
            }
        });

        placeCommands();
    }

    /**
     * Preparing for New Test
     */
    public void prepareForNewEntry() {
        currentPage.setCurrentUser(null);
        inputData.getUsers().forEach(User::resetUser);
        inputData.getMovies().forEach(Movie::resetMovies);
    }
}
