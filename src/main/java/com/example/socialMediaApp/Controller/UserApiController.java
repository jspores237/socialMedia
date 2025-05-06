package com.example.socialMediaApp.Controller;
import com.example.socialMediaApp.Dto.UserDto;
import com.example.socialMediaApp.Model.User;
import com.example.socialMediaApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserApiController {

    UserService userService;

    @Autowired
    public UserApiController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserDto> userDtos = users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(this::convertToDto) //.map() -----> If a value is present, applies the function to it and returns an Optional of the result. Here, return all values if response entity is ok (should just be 1 since searching by an id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) { //@RequestBody converts the JSON in the request body to a User object. We need it when we create or modify data, not while just accessing it
        try {
            //Call the service method to register/save the new user
            User user = convertToEntity(userDto);
            User savedUser = userService.registerUser(user);
            //Return a 201 Created status with the saved user object in the response body
            //This includes the generated ID and any other fields modified during saving
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); //If any exception occurs (like duplicate email), return a 400 Bad Request with the error message as the response body
        }
    }

    @PutMapping("/{id}") //we are modifying (Put) the /{id} endpoint
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        try {
            User user = convertToEntity(userDto);
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<UserDto>> getUserFriends(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    Set<UserDto> friendDtos = user.getFriends().stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toSet());
                    return ResponseEntity.ok(friendDtos);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            User updatedUser = userService.addFriend(userId, friendId);
            return ResponseEntity.ok(convertToDto(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            User updatedUser = userService.removeFriend(userId, friendId);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Entity to DTO
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setCoverPhoto(user.getCoverPhoto());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    //DTO to Entity
    private User convertToEntity(UserDto dto) {
        User user = new User();
        if (dto.getId() != null) {
            user.setId(dto.getId());
        }
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setProfilePicture(dto.getProfilePicture());
        user.setCoverPhoto(dto.getCoverPhoto());
        user.setBio(dto.getBio());
        user.setLocation(dto.getLocation());
        return user;
    }
}
