package com.onlinemuseum.controller;

import com.onlinemuseum.domain.entity.User;
import com.onlinemuseum.jwt.JwtRequest;
import com.onlinemuseum.jwt.JwtResponse;
import com.onlinemuseum.registration.RegistrationRequest;
import com.onlinemuseum.registration.RegistrationService;
import com.onlinemuseum.service.CustomUserService;
import com.onlinemuseum.utility.JWTUtility;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@AllArgsConstructor

@RequestMapping(path = "/api/v1/user")
public class UserController {

    @PreAuthorize("hasAnyAuthority('user')")
    @GetMapping("/")
    public String home(Principal principal) {
        return principal.getName();
    }
}

