package io;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.Utils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class User {
    private Credentials credentials;
    @Builder.Default
    private Integer tokensCount = 0;
    @Builder.Default
    private Integer numFreePremiumMovies = Utils.FREE_PREMIUM_MOVIES;
    @Builder.Default
    private ArrayList<Movie> purchasedMovies = new ArrayList<>();
    @Builder.Default
    private ArrayList<Movie> watchedMovies = new ArrayList<>();
    @Builder.Default
    private ArrayList<Movie> likedMovies = new ArrayList<>();
    @Builder.Default
    private ArrayList<Movie> ratedMovies = new ArrayList<>();

    public User(final User user) {
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

    /**
     * For each test, reinitialize list of Movies
     */
    public void prepareNewUser() {
        this.purchasedMovies = new ArrayList<>();
        this.watchedMovies = new ArrayList<>();
        this.likedMovies = new ArrayList<>();
        this.ratedMovies = new ArrayList<>();
        this.tokensCount = 0;
        this.numFreePremiumMovies = Utils.FREE_PREMIUM_MOVIES;
    }
}
