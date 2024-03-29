package com.driver.services.impl;

import com.driver.model.User;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository4;
    @Override
    public void deleteUser(Integer userId) {
            userRepository4.deleteById(userId);
    }

    @Override
    public User updatePassword(Integer userId, String password) {
        Optional<User> optionalUser = userRepository4.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setPassword(password);
            return userRepository4.save(user);
        }else{
            throw new NullPointerException("Invalid User Id");
        }
    }

    @Override
    public void register(String name, String phoneNumber, String password) {
        //check if user already exist

        //add user
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        userRepository4.save(user);
    }
}
