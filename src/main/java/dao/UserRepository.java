package dao;

import entity.User;

public interface UserRepository {
    public void userCreate ();

    public User userSelect();

    public void userUpdate();

    public User userDelete();
}
