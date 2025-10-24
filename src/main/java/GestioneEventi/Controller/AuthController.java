package GestioneEventi.Controller;

import GestioneEventi.DTO.AuthRequest;
import GestioneEventi.DTO.AuthResponse;
import GestioneEventi.DTO.RegisterRequest;
import GestioneEventi.Entities.User;
import GestioneEventi.Service.JwtService;
import GestioneEventi.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = new User(request.getUsername(), request.getEmail(),
                    request.getPassword(), request.getRole());

            User savedUser = userService.registerUser(user);

            String token = jwtService.generateToken(savedUser);

            AuthResponse response = new AuthResponse(token, savedUser.getUsername(),
                    savedUser.getEmail(), savedUser.getRole(),
                    savedUser.getId());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            AuthResponse response = new AuthResponse(token, user.getUsername(),
                    user.getEmail(), user.getRole(),
                    user.getId());

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }
}