package services;

import entity.User;
import exception.UserNotFound;

import java.util.List;
public interface UserService {

    User addUser(long userId, String username, long chatId);

    List<User> allUsers();

    User findUserById(long id) throws UserNotFound;
}
