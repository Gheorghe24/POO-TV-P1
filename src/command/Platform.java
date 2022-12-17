package command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Credentials;
import io.Input;
import io.Movie;
import io.User;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import services.MovieService;
import services.OutputService;

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
        commandList.forEach(ICommand::executeCommand);
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
                }
            }
        });

        placeCommands();
    }

    /**
     * Preparing for New Test
     */
    public void prepareForNewEntry() {
        currentPage = Page.builder()
                .currentUser(null)
                .name("homepage")
                .moviesList(new ArrayList<>())
                .movieService(new MovieService())
                .outputService(new OutputService())
                .build();
        inputData.getUsers().forEach(User::prepareNewUser);
        inputData.getMovies().forEach(Movie::resetMovies);
    }
}
