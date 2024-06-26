package controller;

import entity.User;
import exception.UserNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import services.UserService;

import java.util.List;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/signup")
    public @ResponseBody User addUser(@RequestBody User user)  {
        return userService.addUser(user.getId(), user.getName(), user.getChatId());
    }

//    This is a danger but sorry I got no time to learn Spring Security
    @GetMapping("/users")
    public @ResponseBody List<User> allUsers(){
        return userService.allUsers();
    }
    @GetMapping("/login/{id}")
    public @ResponseBody User getUser(@PathVariable(value="id") long id) throws UserNotFound {
        return userService.findUserById(id);
    }


}
