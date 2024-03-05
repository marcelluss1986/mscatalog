package com.mdss.mscatalog.services;

import com.mdss.mscatalog.dto.EmailDTO;
import com.mdss.mscatalog.dto.NewPasswordDTO;
import com.mdss.mscatalog.entities.PasswordRecover;
import com.mdss.mscatalog.entities.User;
import com.mdss.mscatalog.repositories.PasswordRecoverRepository;
import com.mdss.mscatalog.repositories.UserRepository;
import com.mdss.mscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private EmailService emailService;


    public void createRecoverToken(EmailDTO body){
        User user = userRepository.findByEmail(body.getEmail());
        if(user == null){
            throw new ResourceNotFoundException("E-mail not found!");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        entity = passwordRecoverRepository.save(entity);

        String bodyMessage = "Click here to password redefinition\n\n"
                + recoverUri + token + ". Expiration in " + tokenMinutes + " minutes.";

        emailService.sendEmail(body.getEmail(), "Recover password", bodyMessage);
    }


    @Transactional
    public void saveNewPassword(NewPasswordDTO newPassword) {
        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(newPassword.getToken(), Instant.now());
        if (result.size() == 0){
            throw new ResourceNotFoundException("Invalid token");
        }
        User user = userRepository.findByEmail(result.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
        user = userRepository.save(user);
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }
}
