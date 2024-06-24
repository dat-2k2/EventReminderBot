package services.impl;

import entity.User;
import exception.UserNotFound;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repos.UserRepository;
import services.UserService;

import java.util.List;

//TODO: Test whether transaction is needed.

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Override
    public User addUser(long userId, String username, long chatId) {
        return userRepository.findById(userId).orElse(userRepository.save(new User(userId, username, chatId)));
    }

    @Override
    public List<User> allUsers(){
        return userRepository.findAll();
    }
    @Override
    public User findUserById(long id) throws UserNotFound {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
    }
}
