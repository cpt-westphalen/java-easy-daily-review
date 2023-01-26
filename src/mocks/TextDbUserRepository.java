package mocks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import application.entities.User;
import application.repositories.UserRepository;
import config.Config;

public class TextDbUserRepository implements UserRepository {
    private Map<String, User> users;

    private Path USER_DB_PATH = Path.of(Config.MOCK_DB_PATH, "users.txt");

    public TextDbUserRepository() {
        this.users = new HashMap<String, User>();
        List<User> usersDbList = readUsersFile();
        if (usersDbList != null && usersDbList.size() > 0) {
            for (User user : usersDbList) {
                this.users.put(user.getId(), user);
            }
        }

    }

    private List<User> readUsersFile() {
        List<User> usersDbList = new LinkedList<User>();
        try (BufferedReader userDbMock = new BufferedReader(
                new FileReader(USER_DB_PATH.toFile()))) {
            String line;
            while ((line = userDbMock.readLine()) != null) {
                if (!line.isEmpty()) {
                    try {
                        String[] splitUserData = line.split(":");
                        User newUser = new User(splitUserData[0], splitUserData[1], Integer.valueOf(splitUserData[2]));
                        usersDbList.add(newUser);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return usersDbList;
    }

    @Override
    public List<User> getAll() {
        List<User> users = this.users.values().stream().collect(Collectors.toList());
        return users;
    }

    @Override
    public User getById(String id) {
        return this.users.get(id);
    }

    @Override
    public User findByName(String name) {
        User user = null;
        for (User u : this.users.values()) {
            if (u.getName().equals(name)) {
                user = u;
            }
        }
        return user;
    }

    @Override
    public void add(User user) {
        this.users.put(user.getId(), user);
        try (BufferedWriter userFileWriter = new BufferedWriter(new FileWriter(USER_DB_PATH.toFile(), true))) {
            userFileWriter.newLine();
            userFileWriter.write(user.getId() + ":" + user.getName() + ":" + user.getPin());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeById(String id) {
        this.users.remove(id);
    }

}
