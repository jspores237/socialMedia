package com.example.socialMediaApp.Controller;
import com.example.socialMediaApp.Dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.socialMediaApp.Model.User;
import com.example.socialMediaApp.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserViewController {

    UserService userService;

    @Autowired
    public UserViewController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/{id}")
    public String getUserPage(@PathVariable Long id, Model model) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("isOwnProfile", false);
            model.addAttribute("isFriend", false);
            model.addAttribute("posts", new ArrayList<>());
        return "userHomepage";
    } else {
        return "error"; //Might want to make a more specific template for this (user not found)
        }
    }

    @GetMapping("/allUsers")
    public String getAllUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "allUsers";
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

}
