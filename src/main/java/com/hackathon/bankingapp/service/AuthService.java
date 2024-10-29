package com.hackathon.bankingapp.service;


import com.hackathon.bankingapp.dto.user.LoginResponse;
import com.hackathon.bankingapp.dto.user.LoginRequest;
import com.hackathon.bankingapp.dto.user.RegisterRequest;
import com.hackathon.bankingapp.dto.user.UserInfoResponse;
import com.hackathon.bankingapp.models.Account;
import com.hackathon.bankingapp.models.User;
import com.hackathon.bankingapp.util.EmailValidator;
import com.hackathon.bankingapp.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.getUserByIdentifier(loginRequest.getIdentifier());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));

        userService.saveUser(user);

        return LoginResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }

    public UserInfoResponse registerUser(RegisterRequest registerRequest) {
        PasswordValidator.validate(registerRequest.getPassword());
        EmailValidator.validate(registerRequest.getEmail());
        User userCreated = userService.createUser(registerRequest);
        Account account = accountService.createAccount(userCreated);

        // Link the account to the user and save the user
        userCreated.setAccount(account);
        userService.saveUser(userCreated);

        return userService.getUserInfo(userCreated);
    }

    public void logout(String token) {
        // Invalidate the token
        jwtService.logoutToken(token);
    }
}
