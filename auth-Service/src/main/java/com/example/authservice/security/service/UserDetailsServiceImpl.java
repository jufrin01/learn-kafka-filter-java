package com.example.authservice.security.service;

//import com.service.beauth.constant.Constant;
//import com.service.beauth.entity.User;
//import com.service.beauth.repository.UserRepository;
import com.example.authservice.constant.Constant;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(Objects.isNull(user)) {
            throw new UsernameNotFoundException(Constant.Message.NOT_FOUND_DATA_MESSAGE);
        }
        return UserDetailsImpl.build(user);
    }
}
