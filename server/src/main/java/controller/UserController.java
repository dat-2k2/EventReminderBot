package controller;

import entity.User;
import services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequestMapping
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<User> addUser(@RequestBody User user){
        try {
            User _user = userService.addUser(user);
            return ResponseEntity.ok(_user);
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body(user);
        }
    }

    @GetMapping("/login/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id){
        Optional<User> userData = userService.findUserById(id);

        return userData.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public @ResponseBody String home() {
        return "Hello Docker World";
    }
}
