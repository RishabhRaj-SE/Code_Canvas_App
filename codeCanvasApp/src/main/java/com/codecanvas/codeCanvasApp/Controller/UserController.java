package com.codecanvas.codeCanvasApp.Controller;

import com.codecanvas.codeCanvasApp.Repository.CodeEntryRepository;
import com.codecanvas.codeCanvasApp.Repository.UserRepository;
import com.codecanvas.codeCanvasApp.Service.UserService;
import com.codecanvas.codeCanvasApp.dto.PublicUserDTO;
import com.codecanvas.codeCanvasApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeEntryRepository codeEntryRepository;


    @GetMapping
    public ResponseEntity getUser(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username= auth.getName();
        User user =userRepository.findByUserName(username);

        try{
            return new ResponseEntity(user, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }







    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getPublicProfile(@PathVariable String username) {
        User user = userRepository.findByUserName(username);

        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        PublicUserDTO dto = new PublicUserDTO(
                user.getId(),
                user.getUserName(),
                user.getAbout(),
                user.getIsActive(),
                user.getRoles()
        );

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/update-user")
    public boolean updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInDb = userRepository.findByUserName(username);

        if (userInDb != null) {
            if(user.getUserName()!=null) {
                userInDb.setUserName(user.getUserName());
            }
            if(user.getPassword()!=null) {
                userInDb.setPassword(user.getPassword());
            }
         /*   if (user.getIsActive()!= null) {
                userInDb.setIsActive(user.getIsActive());
            }*/
            if(user.getAbout()!=null) {
                userInDb.setAbout(user.getAbout());
            }
            userService.saveNewUser(userInDb);
            return true;
        } else {
            return false;
        }

    }

    @PutMapping("/deactivate-profile")
    public boolean deactivateProfile(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user=userRepository.findByUserName(username);
        user.setIsActive(false);
        userRepository.save(user);
        return true;


    }

    @DeleteMapping
    public void deleteUserByName(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user=userRepository.findByUserName(username);
        codeEntryRepository.deleteByauthorId(user.getId().toString());
        userRepository.deleteByUserName(username);
    }



}
