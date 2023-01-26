package application;

import application.entities.User;

public class Auth {
    private static User loggedUser;

    public static boolean isAuthorized() {
        return loggedUser != null;
    }

    public static boolean login(User user, Integer pin) {
        if (user.verifyPin(pin)) {
            loggedUser = user;
            return true;
        }
        return false;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static void logout() {
        loggedUser = null;
    }
}
