package com.lorena.services;

import com.lorena.models.USER_ROLE;
import com.lorena.models.User;
import com.lorena.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user!=null){
            throw new UsernameNotFoundException("El usuario no fue encontrado con el email: "+ username);
        }

        USER_ROLE role=user.getRole();

        if(role==null)role=USER_ROLE.ROLE_CUSTOMER;



        return null;
    }
}
