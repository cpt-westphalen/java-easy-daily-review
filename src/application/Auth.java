package application;

import application.entities.User;

public class Auth {
    private static User loggedUser;
    private static boolean authorized = false;

    public static boolean isAuthorized() {
        return authorized;
    }

    public static boolean login(User user, Integer pin) {
        if (user.verifyPin(pin)) {
            loggedUser = user;
            authorized = true;
            return true;
        }
        return false;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }
}
