package com.codecanvas.codeCanvasApp.Controller;

import com.codecanvas.codeCanvasApp.Repository.UserRepository;
import com.codecanvas.codeCanvasApp.Service.UserService;
import com.codecanvas.codeCanvasApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    @PostMapping("/create-Admin")
    public void createAdmin(@RequestBody User user){
       userService.saveNewAdmin(user);
    }

    @PutMapping("/set-Admin/{username}")
    public ResponseEntity<?> setUserToAdmin(@PathVariable String username) {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            return new ResponseEntity<>("No such user exists", HttpStatus.NOT_FOUND);
        }
        try {
            if (!user.getRoles().contains("ADMIN")) {
                user.getRoles().add("ADMIN");
            }
            userRepository.save(user);
            return new ResponseEntity<>("The user " + username + " is now an Admin", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occured", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-user/id/{user_id}")
    public ResponseEntity<?> deleteUserById(@PathVariable ObjectId user_id){
        Optional<User> user_box=userRepository.findById(user_id);

        if(user_box.isEmpty()){
            return new ResponseEntity<>("No such user exist with this Id",HttpStatus.NOT_FOUND);
        }
        try {
            userRepository.deleteById(user_id);
            return new ResponseEntity<>("Succesfully deleted user",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Some problem occured",HttpStatus.BAD_REQUEST);
        }



    }
    @PutMapping("/deactivate-user/id/{user_id}")
    public ResponseEntity<?> deactivateUserById(@PathVariable ObjectId user_id) {
        Optional<User> userBox = userRepository.findById(user_id);

        if (userBox.isEmpty()) {
            return new ResponseEntity<>("No such user exists with this ID", HttpStatus.NOT_FOUND);
        }

        try {
            User user = userBox.get();
            user.setIsActive(false);  // 👈 Deactivate user
            userRepository.save(user);  // 👈 Save the updated user

            return new ResponseEntity<>("User successfully deactivated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Some problem occurred", HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/activate-user/id/{user_id}")
    public ResponseEntity<?> activateUserById(@PathVariable ObjectId user_id) {
        Optional<User> userBox = userRepository.findById(user_id);

        if (userBox.isEmpty()) {
            return new ResponseEntity<>("No such user exists with this ID", HttpStatus.NOT_FOUND);
        }

        try {
            User user = userBox.get();
            user.setIsActive(true);
            userRepository.save(user);
            return new ResponseEntity<>("User successfully activated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Some problem occurred", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-password/username/{username}")//dont dend passsword with json with " "
    public ResponseEntity<?> updatepassword(@PathVariable String username,@RequestBody String password){
        User userInDb=userRepository.findByUserName(username);
        if(userInDb==null){
            return new ResponseEntity<>("No such user exists",HttpStatus.NOT_FOUND);
        }
        userInDb.setPassword(password);
        userInDb.setPassword(passwordEncoder.encode(userInDb.getPassword()));
        userRepository.save(userInDb);
        return new ResponseEntity<>("Successfully updated password",HttpStatus.OK);
    }


    @GetMapping("/get-all-users")
    public ResponseEntity<?> getAllUsers(){
        List<User> allUsers = userRepository.findAll();


        List<Map<String, Object>> userInfo = allUsers.stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId().toString());
                    map.put("username", user.getUserName());
                    map.put("isActive", user.getIsActive());
                    map.put("email", user.getEmail());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userInfo);

    }
    @GetMapping("/count-active-users")
    public ResponseEntity<?> countUser(){
        try{
            long activeUserCount = userRepository.countByIsActiveTrue();

            Map<String, Object> response = new HashMap<>();
            response.put("activeUsers", activeUserCount);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
