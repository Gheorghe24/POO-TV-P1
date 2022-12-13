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
public class Movie {
    private String name;
    private Integer year;
    private Integer duration;
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<String> actors = new ArrayList<>();
    private ArrayList<String> countriesBanned = new ArrayList<>();
}
