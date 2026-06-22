package application.controllers;

import application.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import application.services.UserService;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

     public UserController(UserService userService) {
         this.userService = userService;
     }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
         UserDto dto = userService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestParam String name,
                                              @RequestParam String email,
                                              @RequestParam Integer age) {
        UserDto dto = userService.create(name, email, age);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
         return ResponseEntity.ok(userService.update(id, userDto.name(), userDto.email(), userDto.age()));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
         return ResponseEntity.ok(userService.getAll());
    }
}
