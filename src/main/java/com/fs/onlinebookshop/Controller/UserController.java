package com.fs.onlinebookshop.Controller;

import com.fs.onlinebookshop.Entity.AuthRequest;
import com.fs.onlinebookshop.Entity.Token;
import com.fs.onlinebookshop.Entity.User;
import com.fs.onlinebookshop.Repository.TokenRepository;
import com.fs.onlinebookshop.Repository.UserRepository;
import com.fs.onlinebookshop.Services.JwtService;
import com.fs.onlinebookshop.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user){
        return userService.registerUser(user);
    }

    @GetMapping("/my-info")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<User>viewUserInfo(@RequestHeader(name = "Authorization") String token){
        String email = jwtService.extractUsername(token.substring(7));
        return userService.viewUserInfo(email);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<String>deleteUser(@RequestHeader(name = "Authorization") String token){
        String email = jwtService.extractUsername(token.substring(7));
        return userService.deleteUser(email);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<User>updateUser(@RequestHeader(name = "Authorization") String token,@RequestBody User user){
        String email = jwtService.extractUsername(token.substring(7));
        return userService.updateUser(email,user);
    }

    @PutMapping("/changePassword")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<String>changePassword(@RequestHeader(name = "Authorization") String token,@RequestParam String newPassword){
        String email = jwtService.extractUsername(token.substring(7));
        return userService.changeUserPassword(email, newPassword);
    }

    @GetMapping("/admin/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User>findUserById(@PathVariable long userId){
        return userService.findUserById(userId);
    }

    @GetMapping("/admin/allUsers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<User>>findAllUsers(){
        return userService.findAllUsers();
    }

    @PostMapping("/login")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        return userService.authenticateAndGetToken(authRequest);
    }

}
