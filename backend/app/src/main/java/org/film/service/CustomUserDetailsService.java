package org.film.service;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.film.model.User;
import org.film.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
  
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
      
        return new org.springframework.security.core.userdetails.User(
            user.getUserName(),
            user.getUserPassword(),
            Collections.singleton(new SimpleGrantedAuthority(user.getUserRole()))
        );
    }
}
