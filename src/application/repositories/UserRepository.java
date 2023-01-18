package application.repositories;

import java.util.List;

import application.entities.User;

public interface UserRepository {
    public List<User> getAll();

    public User getById(String id);

    public User findByName(String name);

    public void add(User user);

    public void removeById(String id);
}
