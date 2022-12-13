package platform;

import io.Movie;
import io.User;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Page {
    private String name;
    private User currentUser;
    private String startUserAction;
    private ArrayList<Movie> moviesList;
    private Movie currentMovie;
}
