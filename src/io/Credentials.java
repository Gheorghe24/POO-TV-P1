package io;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Credentials {
    private String name;
    private String password;
    private String accountType;
    private String country;
    private Integer balance;
}
