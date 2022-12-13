package out;

import io.Movie;
import io.User;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public final class Output {
    private String error;
    private ArrayList<Movie> currentMoviesList;
    private User currentUser;
}
