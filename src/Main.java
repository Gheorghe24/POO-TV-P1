import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Input;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import actions.Page;
import platform.Platform;

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

        Page currentPage = Page.builder()
                .currentUser(null)
                .name("homepage")
                .moviesList(new ArrayList<>())
                .build();
        Platform platform = new Platform(input, arrayNode, currentPage, new ArrayList<>());
        platform.executeListOfActions();
        objectWriter.writeValue(new File(args[1]), arrayNode);
        platform.prepareForNewEntry();
    }
}
