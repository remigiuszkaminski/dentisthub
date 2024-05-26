package ug.edu.medithub.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ug.edu.medithub.api.models.FoundUser;
import ug.edu.medithub.api.models.User;
import ug.edu.medithub.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.security.Key;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<FoundUser> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<FoundUser> foundUsers = new java.util.ArrayList<>();
        for (User user : users) {
            FoundUser foundUser = new FoundUser(user.getEmail(), user.getUsername(), user.getProfilePicture(), user.getDescription(), user.getId());
            foundUsers.add(foundUser);
        }
        return foundUsers;
    }


    public ResponseEntity<String> addUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return new ResponseEntity<>("User registered succesfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<LoginResponse> loginUser(User user) {
        try {
            List<User> users = userRepository.findByUsername(user.getUsername());
            if (users.isEmpty()) {
                return new ResponseEntity<>(new LoginResponse("User login failed", null, null, false, null), HttpStatus.UNAUTHORIZED);
            }
            User foundUser = users.get(0);

            if (passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
                return ResponseEntity.ok().body(new LoginResponse("User logged in successfully", foundUser, generateJwtToken(foundUser), true, foundUser.getId()));
            } else {
                return new ResponseEntity<>(new LoginResponse("User login failed", null, null, false, null), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new LoginResponse("User login failed", null, null, false, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String test(String token) {
        if (isValidToken(token)) {
            return "Token is valid";
        } else {
            return "Token is invalid";
        }
    }

    private boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public FoundUser getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> new FoundUser(value.getEmail(), value.getUsername(), value.getProfilePicture(), value.getDescription(), value.getId())).orElse(null);
    }


    public ResponseEntity<String> updateUser(String id, FoundUser user, String authorizationHeader) {
        try {
            Optional<User> foundUser = userRepository.findById(id);
            if (foundUser.isEmpty()) {
                return new ResponseEntity<>("User update failed", HttpStatus.NOT_FOUND);
            }


            if (checkIfAuthorized(id, authorizationHeader)) {
                return new ResponseEntity<>("User update failed", HttpStatus.UNAUTHORIZED);
            }
            User userToUpdate = foundUser.get();
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setProfilePicture(user.getProfilePicture());
            userToUpdate.setDescription(user.getDescription());
            userRepository.save(userToUpdate);
            return new ResponseEntity<>("User updated succesfully", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("User update failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String extractToken(String authorizationHeader) {
        String[] parts = authorizationHeader.split(" ");
        if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
            return parts[1];
        } else {
            throw new IllegalArgumentException("Invalid or missing JWT token");
        }
    }

    public boolean checkIfAuthorized(String id, String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        String token_id = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return !token_id.equals(id);
    }

}




