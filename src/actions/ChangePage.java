package actions;


import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ChangePage implements ICommand{
    private Page currentPage;
    private ArrayNode jsonOutput;
    private String feature;
    @Override
    public void executeCommand() {
        currentPage.changePage(jsonOutput, feature);
    }
}
