package application.controllers;

import application.dto.UserDto;
import application.mappers.UserDtoAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import application.services.UserService;

import java.util.List;

@RestController
@RequestMapping("api/users")
@Tag(name = "Пользователи", description = "Управление пользователями")
public class UserController {

    private final UserService userService;
    private final UserDtoAssembler assembler;

    public UserController(UserService userService, UserDtoAssembler assembler) {
         this.userService = userService;
         this.assembler = assembler;
     }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<EntityModel<UserDto>> getUserById(@PathVariable Long id) {
         UserDto dto = userService.getById(id);
        return ResponseEntity.ok(assembler.toModel(dto));
    }

    @PostMapping
    @Operation(summary = "Создать пользователя")
    public ResponseEntity<EntityModel<UserDto>> createUser(@RequestParam String name,
                                              @RequestParam String email,
                                              @RequestParam Integer age) {
        UserDto dto = userService.create(name, email, age);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя")
    public ResponseEntity<EntityModel<UserDto>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updated = userService.update(id, userDto.name(), userDto.email(), userDto.age());
        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {
         return ResponseEntity.ok(assembler.toCollectionModel(userService.getAll()));
    }
}
