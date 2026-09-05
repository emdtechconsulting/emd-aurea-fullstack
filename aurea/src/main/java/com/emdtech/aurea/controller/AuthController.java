package com.emdtech.aurea.controller;

import com.emdtech.aurea.dto.LoginRequest;
import com.emdtech.aurea.dto.LoginResponse;
import com.emdtech.aurea.entity.AppUser;
import com.emdtech.aurea.repository.AppUserRepository;
import com.emdtech.aurea.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Autenticación",
        description = "Operaciones relacionadas con el acceso de usuarios"
)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final long expirationSeconds;


    public AuthController(
            AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            JwtService jwtService,
            @Value("${aurea.jwt.expiration-seconds}")
            long expirationSeconds) {

        this.authenticationManager =
                authenticationManager;

        this.appUserRepository =
                appUserRepository;
                
        this.jwtService =
                jwtService;

        this.expirationSeconds =
                expirationSeconds;
    }


    @Operation(
            summary = "Iniciar sesión",
            description = "Valida usuario y contraseña y devuelve un JWT."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody
            LoginRequest request) {


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );


        AppUser user =
                appUserRepository
                        .findByUsername(
                                request.getUsername()
                        )
                        .orElseThrow();


        String token =
                jwtService.generarToken(
                        user.getUsername(),
                        user.getRole()
                );


        LoginResponse response =
                new LoginResponse(
                        user.getUsername(),
                        user.getRole().name(),
                        "Autenticación correcta",
                        token,
                        "Bearer",
                        expirationSeconds
                );


        return ResponseEntity.ok(response);
    }
}