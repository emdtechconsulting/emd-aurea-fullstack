package com.emdtech.aurea.security;

import com.emdtech.aurea.entity.AppUser;
import com.emdtech.aurea.repository.AppUserRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;


@Service
public class DatabaseUserDetailsService
        implements UserDetailsService {

    private final AppUserRepository appUserRepository;


    public DatabaseUserDetailsService(
            AppUserRepository appUserRepository) {

        this.appUserRepository =
                appUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        AppUser appUser =
                appUserRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Usuario no encontrado: "
                                                + username
                                )
                        );


        return User
                .withUsername(
                        appUser.getUsername()
                )
                .password(
                        appUser.getPasswordHash()
                )
                .roles(
                        appUser.getRole().name()
                )
                .disabled(
                        !appUser.isEnabled()
                )
                .build();
    }
}