package com.example.socialMediaApp.Service;
import com.example.socialMediaApp.Dto.UserDto;
import com.example.socialMediaApp.Model.User;
import com.example.socialMediaApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    public UserRepository userRepository;
    public PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) { //can use this or commented out method below (I am learning Optionals)
        return userRepository.findById(id);
    }

    /*public User findById(Long id) {
        if (id == null) {
            throw new RuntimeException("No Id found");
        }
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException ("User not found"));
    }*/

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) { //can use this or commented out method below (I have both for learning purposes)
        return userRepository.findById(id)
                .map(user -> {
                    user.setUserName(updatedUser.getUserName());
                    user.setBio(updatedUser.getBio());
                    user.setLocation(updatedUser.getLocation());
                    user.setProfilePicture(updatedUser.getProfilePicture());
                    user.setCoverPhoto(updatedUser.getCoverPhoto());
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /*public User updateUser(Long id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUserName(updatedUser.getUserName());
            user.setBio(updatedUser.getBio());
            user.setLocation(updatedUser.getLocation());
            user.setProfilePicture(updatedUser.getProfilePicture());
            user.setCoverPhoto(updatedUser.getCoverPhoto());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }*/

    public User addFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        user.getFriends().add(friend);
        return userRepository.save(user);
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        user.getFriends().remove(friend);
        return userRepository.save(user);
    }

    public List<User> getAllFriends() {
        return userRepository.findAll().stream()
                .flatMap(user -> user.getFriends().stream())
                .collect(Collectors.toList());
    }
}
