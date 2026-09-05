package com.emdtech.aurea.security;

import com.emdtech.aurea.entity.AppUser;
import com.emdtech.aurea.repository.AppUserRepository;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Profile;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Component;


@Component
@Profile("dev")
public class SecurityUserInitializer
        implements CommandLineRunner {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final String adminPassword;

    private final String ventasPassword;


    public SecurityUserInitializer(

            AppUserRepository appUserRepository,

            PasswordEncoder passwordEncoder,

            @Value("${AUREA_ADMIN_PASSWORD}")
            String adminPassword,

            @Value("${AUREA_VENTAS_PASSWORD}")
            String ventasPassword) {

        this.appUserRepository =
                appUserRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.adminPassword =
                adminPassword;

        this.ventasPassword =
                ventasPassword;
    }


    @Override
    public void run(String... args) {

        crearUsuarioSiNoExiste(
                "admin",
                adminPassword,
                UserRole.ADMIN
        );

        crearUsuarioSiNoExiste(
                "ventas",
                ventasPassword,
                UserRole.VENTAS
        );
    }


    private void crearUsuarioSiNoExiste(
            String username,
            String rawPassword,
            UserRole role) {

        if (appUserRepository
                .existsByUsername(username)) {

            return;
        }


        AppUser user =
                new AppUser();

        user.setUsername(username);

        user.setPasswordHash(
                passwordEncoder.encode(
                        rawPassword
                )
        );

        user.setRole(role);

        user.setEnabled(true);


        appUserRepository.save(user);
    }
}