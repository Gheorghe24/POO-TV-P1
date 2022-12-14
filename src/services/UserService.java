package services;

import io.Credentials;
import io.Input;
import io.User;
import java.util.ArrayList;

public final class UserService {

    /**
     * @param inputData from Test
     * @param credentials for register action
     * @return valid user or null
     */
    public User checkForUserInData(final Input inputData, final Credentials credentials) {
        for (var user: inputData.getUsers()) {
            if (user.getCredentials().equals(credentials)) {
                return new User(user);
            }
        }
        return null;
    }

    /**
     * @param inputData from Test
     * @param credentials for register action
     * @return registered user or null
     */
    public User registerNewUser(final Input inputData, final Credentials credentials) {
        if (checkForUserInData(inputData, credentials) == null) {
            var user = User
                    .builder()
                    .credentials(new Credentials(credentials))
                    .numFreePremiumMovies(15)
                    .likedMovies(new ArrayList<>())
                    .purchasedMovies(new ArrayList<>())
                    .ratedMovies(new ArrayList<>())
                    .watchedMovies(new ArrayList<>())
                    .tokensCount(0)
                    .build();
            inputData.getUsers().add(user);
            return user;
        }
        return null;
    }

}
