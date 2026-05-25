package dao;

import entity.User;

import java.util.Optional;

public interface UserRepository {
    public void create (User user);

    public Optional<User> select(Long id);

    public void update(User user);

    public User delete(Long id);
}
