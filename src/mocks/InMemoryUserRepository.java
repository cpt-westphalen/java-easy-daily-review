package mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import application.entities.User;
import application.repositories.UserRepository;

public class InMemoryUserRepository implements UserRepository {
    public Map<String, User> users;

    public InMemoryUserRepository() {
        this.users = new HashMap<String, User>();
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
    }

    @Override
    public void removeById(String id) {
        this.users.remove(id);
    }

}
