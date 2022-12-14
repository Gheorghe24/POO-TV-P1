package io;


import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class Movie {
    private String name;
    private Integer year;
    private Integer duration;
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<String> actors = new ArrayList<>();
    private ArrayList<String> countriesBanned = new ArrayList<>();
    private Integer numLikes = 0;
    private Double rating = 0.00;
    private Integer numRatings = 0;

    /**
     * For Each new Test, I reinitialize movie ratings and likes
     */
    public void resetMovies() {
        this.numLikes = 0;
        this.numRatings = 0;
        this.rating = 0.00;
    }
}
