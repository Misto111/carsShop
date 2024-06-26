package bg.technologies.carshop.service.impl;

import bg.technologies.carshop.model.dto.UserRegistrationDTO;
import bg.technologies.carshop.model.entity.UserEntity;
import bg.technologies.carshop.model.events.UserRegisteredEvent;
import bg.technologies.carshop.repository.UserRepository;
import bg.technologies.carshop.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher appEventPublisher;
    private final UserDetailsService carshopUserDetailsService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ApplicationEventPublisher appEventPublisher, UserDetailsService carshopUserDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appEventPublisher = appEventPublisher;
        this.carshopUserDetailsService = carshopUserDetailsService;
    }

    @Override
    public void registerUser(UserRegistrationDTO userRegistrationDTO) {

        userRepository.save(map(userRegistrationDTO));

        appEventPublisher.publishEvent(new UserRegisteredEvent(
                "UserService", userRegistrationDTO.email(),
                userRegistrationDTO.fullName()));
    }

    @Override
    public void createUserIfNotExist(String email, String names) {

    }

    @Override
    public Authentication login(String email) {
        UserDetails userDetails = carshopUserDetailsService.loadUserByUsername(email);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }


    private  UserEntity map(UserRegistrationDTO userRegistrationDTO) {

        return new UserEntity()
                .setActive(false)
                .setFirstName(userRegistrationDTO.firstName())
                .setLastName(userRegistrationDTO.lastName())
                .setEmail(userRegistrationDTO.email())
                .setPassword(passwordEncoder.encode(userRegistrationDTO.password()));

    }
}
