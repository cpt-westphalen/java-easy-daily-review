package application.useCases;

import java.util.UUID;

import application.entities.User;
import application.repositories.UserRepository;

public class RegisterNewUser {

    private UserRepository userRepository;

    public RegisterNewUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void exec(String username, String pin) throws Exception {
        if (username == null || username.length() < 3) {
            throw new Exception("Username must be at least 3 characters long");
        }
        Integer pinNumber;
        try {
            pinNumber = Integer.valueOf(pin);
            if (pin == null || pin.length() < 3) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("Pin must be a sequence of numbers with at least 3 digits");
        }
        String id = UUID.randomUUID().toString();
        User user = new User(id, username, pinNumber);

        userRepository.add(user);

    }
}
