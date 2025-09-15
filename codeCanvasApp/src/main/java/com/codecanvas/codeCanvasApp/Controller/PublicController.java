package com.codecanvas.codeCanvasApp.Controller;

import com.codecanvas.codeCanvasApp.Repository.UserRepository;
import com.codecanvas.codeCanvasApp.Service.UserService;
import com.codecanvas.codeCanvasApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-user")
    public ResponseEntity<?> createNewUser(@RequestBody User user) {
        String username= user.getUserName();

        // Check null or empty username
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            return new ResponseEntity<>("Username cannot be null or empty", HttpStatus.BAD_REQUEST);
        }

        // Optional: Check null or empty password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return new ResponseEntity<>("Password cannot be null or empty", HttpStatus.BAD_REQUEST);
        }


        if(userRepository.existsByUserName(username)){
            return new ResponseEntity<>("User with this username already exists , Please try a different username",HttpStatus.FORBIDDEN);
        }
        boolean saved = userService.saveNewUser(user);
        if (saved) {
            return ResponseEntity.ok("User created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating user");
        }
    }

}
