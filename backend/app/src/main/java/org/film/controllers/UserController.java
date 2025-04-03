package org.film.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.film.dto.ChangePasswordRequest;
import org.film.dto.ChangeUsernameRequest;
import org.film.dto.DeleteAccountRequest;
import org.film.dto.LoginRequest;
import org.film.dto.RegisterRequest;
import org.film.model.User;
import org.film.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityContextRepository securityContextRepository;
    
    @PostMapping("/public/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUserName(),
                    loginRequest.getUserPassword()
                )
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            String role = authentication.getAuthorities().stream()
                            .findFirst().map(GrantedAuthority::getAuthority).orElse("unknown");

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("username", authentication.getName());
            responseBody.put("role", role);
            responseBody.put("message", "Logged in successfully");

            return ResponseEntity.ok(responseBody);
        
        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is disabled");
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
    
    @PostMapping("/public/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
    
        String encodedPassword = passwordEncoder.encode(registerRequest.getUserPassword());
        
        User newUser = new User();
        newUser.setUserName(registerRequest.getUserName());
        newUser.setUserPassword(encodedPassword);
        newUser.setUserRole("user");
        
        userRepository.save(newUser);
        
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/public/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/auth/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest passwordRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        
        if (!passwordRequest.getNewPassword().equals(passwordRequest.getRepeatedPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
        
        if (passwordEncoder.matches(passwordRequest.getNewPassword(), user.getUserPassword())) {
            return ResponseEntity.badRequest().body("New password cannot be the same as the old password");
        }
        
        user.setUserPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
        
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                user.getUserName(),
                user.getUserPassword(),
                auth.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        return ResponseEntity.ok("Password changed successfully");
    }

    
    @PostMapping("/auth/change-username")
    public ResponseEntity<?> changeUsername(@Valid @RequestBody ChangeUsernameRequest usernameRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        String currentUsername = auth.getName();
        
        if (usernameRequest.getNewUsername().equalsIgnoreCase(currentUsername)) {
            return ResponseEntity.badRequest().body("Usernames cannot be the same");
        }
        
        if (userRepository.findByUserName(usernameRequest.getNewUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        
        Optional<User> userOpt = userRepository.findByUserName(currentUsername);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        
        user.setUserName(usernameRequest.getNewUsername());
        userRepository.save(user);
        
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
            user.getUserName(), 
            user.getUserPassword(), 
            auth.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("newUsername", usernameRequest.getNewUsername());
        responseBody.put("message", "Username changed successfully");
        
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/auth/delete-account")
    public ResponseEntity<?> deleteAccount(@Valid @RequestBody DeleteAccountRequest deleteRequest,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();

        if (!passwordEncoder.matches(deleteRequest.getPassword(), user.getUserPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password");
        }

        userRepository.delete(user);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return ResponseEntity.ok("Account deleted successfully");
    }

    @GetMapping("/admin/search-user")
    public ResponseEntity<List<Map<String, Object>>> searchUser(@RequestParam("query") String query) {
        List<User> users = userRepository.findByUserNameContainingIgnoreCase(query);
        List<Map<String, Object>> results = users.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getUserId());
            map.put("name", user.getUserName());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/admin/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("userName", user.getUserName());
        result.put("userRole", user.getUserRole());
        result.put("enabled", user.getEnabled());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/disable-user/{id}")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        user.setEnabled(false);
        userRepository.save(user);
        return ResponseEntity.ok("User disabled successfully");
    }

    @PostMapping("/admin/enable-user/{id}")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok("User enabled successfully");
    }
}
