package io;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public final class User {
    private Credentials credentials;
    private Integer tokensCount = 0;
    private Integer numFreePremiumMovies = 15;
    private ArrayList<Movie> purchasedMovies = new ArrayList<>();
    private ArrayList<Movie> watchedMovies = new ArrayList<>();
    private ArrayList<Movie> likedMovies = new ArrayList<>();
    private ArrayList<Movie> ratedMovies = new ArrayList<>();

    public User(User user) {
        this.credentials = new Credentials(user.credentials);
        this.tokensCount = user.tokensCount;
        this.numFreePremiumMovies = user.numFreePremiumMovies;
        this.purchasedMovies = new ArrayList<>();
        this.purchasedMovies.addAll(user.purchasedMovies);
        this.watchedMovies = new ArrayList<>();
        this.watchedMovies.addAll(user.watchedMovies);
        this.likedMovies = new ArrayList<>();
        this.likedMovies.addAll(user.likedMovies);
        this.ratedMovies = new ArrayList<>();
        this.ratedMovies.addAll(user.ratedMovies);
    }

    public void resetUser() {
        this.purchasedMovies = new ArrayList<>();
        this.watchedMovies = new ArrayList<>();
        this.likedMovies = new ArrayList<>();
        this.ratedMovies = new ArrayList<>();
        this.tokensCount = 0;
        this.numFreePremiumMovies = 15;
    }
}
