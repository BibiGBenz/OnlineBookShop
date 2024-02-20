package com.fs.onlinebookshop.Services;

import com.fs.onlinebookshop.Entity.AuthRequest;
import com.fs.onlinebookshop.Entity.Token;
import com.fs.onlinebookshop.Entity.User;
import com.fs.onlinebookshop.Exception.UserNotFoundException;
import com.fs.onlinebookshop.Repository.TokenRepository;
import com.fs.onlinebookshop.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenRepository tokenRepository;

    //all
    public ResponseEntity<String> registerUser(User user) {
        if (!user.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$")){
            throw new IllegalArgumentException("Password must contain one uppercase,one lowercase and one digit");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>("You have registered successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<User>viewUserInfo(String email){
        User user=userRepository.findByEmail(email).get();
        return new ResponseEntity<>(user,HttpStatus.FOUND);
    }

    //admin & user
    public ResponseEntity<String> deleteUser(String email) {
        User user = userRepository.findByEmail(email).get();
        userRepository.delete(user);
        return new ResponseEntity<>("Account deleted successfully", HttpStatus.OK);
    }

    //admin & user
    public ResponseEntity<User> updateUser(String email, User user) {
        User userinfo = userRepository.findByEmail(email).get();

        if (user.getUserName() != null) {
            userinfo.setUserName(user.getUserName());
        }
        if (user.getAddress() != null) {
            userinfo.setAddress(user.getAddress());
        }
        if (user.getEmail() != null) {
            userinfo.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            if (!user.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$")){
                throw new IllegalArgumentException("Password must contain one uppercase,one lowercase and one digit");
            }
            userinfo.setPassword(passwordEncoder.encode(user.getPassword()));
            revokeAllUserTokens(userinfo);
        }
        if (user.getRoles() != null) {
            userinfo.setRoles(user.getRoles());
        }
        if (user.getPhoneNumber() != 0) {
            userinfo.setPhoneNumber(user.getPhoneNumber());
        }
        User updatedUser = userRepository.save(userinfo);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    //admin and user
    //change password
    public ResponseEntity<String> changeUserPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).get();
        if (!user.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$")) {
            return new ResponseEntity<>("Password doesn't meet the requirements", HttpStatus.EXPECTATION_FAILED);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        revokeAllUserTokens(user);
        return new ResponseEntity<>("password change successfully", HttpStatus.OK);
    }

    //admin
    public ResponseEntity<User> findUserById(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("No user found with id " + userId);
        }
        User user = optionalUser.get();
        return new ResponseEntity<>(user, HttpStatus.FOUND);
    }

    //admin
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new UserNotFoundException("No registered users found");
        }
        return new ResponseEntity<>(userList, HttpStatus.FOUND);
    }

    public String authenticateAndGetToken(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            String jwtToken = jwtService.generateToken(authRequest.getEmail());
            revokeAllUserTokens(userRepository.findByEmail(authRequest.getEmail()).get());
            var token = Token.builder().token(jwtToken).user(userRepository.findByEmail(authRequest.getEmail()).get()).revoked(false).expired(false).build();
            tokenRepository.save(token);
            return jwtToken;
        } else {
            throw new UsernameNotFoundException("Authentication failed Invalid User !!!!");
        }
    }

    private void revokeAllUserTokens(User user) {
        var validUserToken = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserToken.isEmpty()) {
            return;
        }
        validUserToken.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }
}