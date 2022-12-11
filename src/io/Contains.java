package io;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Contains {
    private List<String> actors;
    private List<String> genre;
    private List<String> country;
}
