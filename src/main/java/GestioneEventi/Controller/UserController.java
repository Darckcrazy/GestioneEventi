package GestioneEventi.Controller;

import GestioneEventi.Entities.User;
import GestioneEventi.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        // Only organizers can view all users (optional feature)
        if (currentUser.getRole() != User.Role.ORGANIZZATORE_EVENTI) {
            return ResponseEntity.status(403).body(null);
        }

        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id,
                                         Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        // Users can only view their own profile, organizers can view any profile
        if (!currentUser.getId().equals(id) &&
                currentUser.getRole() != User.Role.ORGANIZZATORE_EVENTI) {
            return ResponseEntity.status(403).body("Access denied");
        }

        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}