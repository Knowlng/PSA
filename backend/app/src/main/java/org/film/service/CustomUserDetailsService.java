package org.film.service;

import java.util.Collections;

import org.film.model.User;
import org.film.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
            user.getEnabled(),
            true,
            true,
            true,
            Collections.singleton(new SimpleGrantedAuthority(user.getUserRole()))
        );
            
    }
}
