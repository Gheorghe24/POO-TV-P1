package actions;

import io.Credentials;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnPage {
    private String type;
    private String page;
    private String feature;
    private ArrayList<Credentials> credentials;
}
