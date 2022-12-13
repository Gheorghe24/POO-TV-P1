package services;

import io.Credentials;
import io.Input;
import io.User;
import java.util.ArrayList;

public class UserService {

    public User checkForUserInData(Input inputData, Credentials credentials) {
        for (var user: inputData.getUsers()) {
            if (user.getCredentials().equals(credentials)) {
                return new User(user);
            }
        }
        return null;
    }

    public User registerNewUser(Input inputData, Credentials credentials) {
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
