package com.onlinemuseum.controller;

import com.onlinemuseum.jwt.JwtRequest;
import com.onlinemuseum.jwt.JwtResponse;
import com.onlinemuseum.registration.RegistrationRequest;
import com.onlinemuseum.registration.RegistrationService;
import com.onlinemuseum.registration.ResetPasswordRequest;
import com.onlinemuseum.registration.ResponseMessage;
import com.onlinemuseum.service.CustomUserService;
import com.onlinemuseum.service.UserService;
import com.onlinemuseum.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {

    public static final String AUTH_HEADER = "Authorization";

    private final JWTUtility jwtUtility;
    private final AuthenticationManager authenticationManager;
    private final CustomUserService userService;
    private final RegistrationService registrationService;
    private final UserService appUserService;

    @Autowired
    public AuthenticationController(JWTUtility jwtUtility, AuthenticationManager authenticationManager, CustomUserService userService,
                                    RegistrationService registrationService, UserService appUserService) {
        this.jwtUtility = jwtUtility;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.registrationService = registrationService;
        this.appUserService = appUserService;
    }


    @PostMapping("/login")
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getEmail(),
                            jwtRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("ERROR", e);
        }

        final UserDetails userDetails =
                userService.loadUserByUsername(jwtRequest.getEmail());
        final String token = jwtUtility.generateToken(userDetails);
        return new JwtResponse(token);
    }

    @PostMapping("/signup")
    public String register(@RequestBody RegistrationRequest request) {

        return registrationService.register(request);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(AUTH_HEADER);
        final String token = authToken.substring(7);
        String username = jwtUtility.getUsernameFromToken(token);
        userService.loadUserByUsername(username);

        if (jwtUtility.canTokenBeRefreshed(token)) {
            String refreshedToken = jwtUtility.refreshToken(token);
            return ResponseEntity.ok(new JwtResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token) {

        return registrationService.confirmToken(token);
    }

    @PostMapping("/forgot_password")
    public ResponseMessage processForgotPassword(@RequestBody ResetPasswordRequest passwordResetRequest) throws Exception {
        return appUserService.updateResetPasswordToken(passwordResetRequest);
    }

    @PostMapping("/reset_password")
    public ResponseMessage processResetPassword(@RequestBody ResetPasswordRequest request) {
        return appUserService.getByResetPasswordToken(request);
    }
}
