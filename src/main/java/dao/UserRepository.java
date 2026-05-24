package dao;

import entity.User;

import java.util.Optional;

public interface UserRepository {
    public void userCreate (User user);

    public Optional<User> userSelect(Long id);

    public void userUpdate(User user);

    public User userDelete(Long id);
}
