package application.useCases;

import application.Auth;
import application.entities.User;
import application.repositories.UserRepository;

public class LoginUser {
    private UserRepository userRepository;

    public LoginUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void exec(String username, String pin) throws Exception {
        Integer pinNumber;
        try {
            pinNumber = Integer.valueOf(pin);
        } catch (Exception e) {
            throw new Exception("Pin must be a sequence of numbers");
        }
        User user = this.userRepository.findByName(username);
        if (user == null) {
            throw new Exception("Username not found");
        }
        if (!Auth.login(user, pinNumber)) {
            throw new Exception("Incorrect pin");
        }
    }
}
