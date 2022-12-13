package io;

import java.util.Objects;
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
    private String balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(name, that.name) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }

    public Credentials (Credentials credentials) {
        if (credentials != null) {
            this.name = credentials.name;
            this.password = credentials.password;
            this.accountType = credentials.accountType;
            this.country = credentials.country;
            this.balance = credentials.balance;
        }
    }
}
