import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Input;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import command.Page;
import command.Platform;

public final class Main {
    private Main() {
    }
    /**
     * @param args Main method where action happens
     */
    public static void main(final String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input input = objectMapper.readValue(new File(args[0]), Input.class);
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        Platform platform = Platform.getInstance();
        platform.setInputData(input);
        platform.setOutput(arrayNode);
        platform.setCommandList(new ArrayList<>());
        platform.setCurrentPage(Page.builder().build());
        platform.prepareForNewEntry();
        platform.executeListOfActions();
        objectWriter.writeValue(new File(args[1]), arrayNode);

    }
}
