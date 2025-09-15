package com.codecanvas.codeCanvasApp.Service;

import com.codecanvas.codeCanvasApp.Repository.UserRepository;
import com.codecanvas.codeCanvasApp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    public boolean saveNewUser(User user){//new user ke password ko encrypt kro,roles set kro,active ko true kro
        try{

            if (!user.getPassword().startsWith("$2a$")) { // bcrypt passwords start with $2a$
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            if (user.getId() == null) {// id nhi hai mtlb user ekdu fresh bn rha database me koi existence nhi hai
                user.setIsActive(true);
                user.setRoles(Arrays.asList("USER"));
            }
            userRepository.save(user);
            return true;
        }catch(Exception e){
            log.error("Error occured for {} :" ,user.getUserName(),e);
            return false;
        }

    }



    public void entryUpdatedsaveUser(User user){ //Ye jb user new entry daalta hai tb update krne ke liye isko call krta hai
        userRepository.save(user);
    }

    public boolean saveNewAdmin(User user){
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setIsActive(true);
            user.setRoles(Arrays.asList("USER", "ADMIN"));
            userRepository.save(user);
            return true;
        }catch(Exception e){
            log.error("Error occured for {} :" ,user.getUserName(),e);
            return false;
        }
    }

}
