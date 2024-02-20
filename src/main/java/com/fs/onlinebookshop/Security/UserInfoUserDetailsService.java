package com.fs.onlinebookshop.Security;

import com.fs.onlinebookshop.Entity.User;
import com.fs.onlinebookshop.Exception.UserNotFoundException;
import com.fs.onlinebookshop.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User>userInfo=userRepository.findByEmail(username);
        return userInfo.map(UserInfoUserDetails::new).orElseThrow(()->new UserNotFoundException("User Not Found"+username));
    }
}
