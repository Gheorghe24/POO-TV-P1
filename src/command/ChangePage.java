package command;


import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Action;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ChangePage implements ICommand {
    private Page currentPage;
    private ArrayNode jsonOutput;
    private Action action;
    @Override
    public void executeCommand() {
        currentPage.changePage(jsonOutput, action);
    }
}
