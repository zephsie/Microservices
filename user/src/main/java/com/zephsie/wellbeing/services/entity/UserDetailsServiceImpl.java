package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.repositories.UserRepository;
import com.zephsie.wellbeing.security.UserDetailsImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsImp loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s).map(UserDetailsImp::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + s + " not found"));
    }
}
