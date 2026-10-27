package com.example.coffeeshop.repository;

import com.example.coffeeshop.entity.AppUser;
import com.example.coffeeshop.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appuser = userRepository
                .findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(("User not found")));

        return User.builder()
                .username(appuser.getUsername())
                .password(appuser.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_"+appuser.getRole().name()))
                .disabled(!appuser.isEnabled())
                .build();
    }
}