package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserHappyPathTest() {
        when(bCryptPasswordEncoder.encode("mickey_mouse_password")).thenReturn("mickey_mouse_encrypted_password");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("mickey_mouse");
        userRequest.setPassword("mickey_mouse_password");
        userRequest.setConfirmPassword("mickey_mouse_password");

        final ResponseEntity<User> userResponse = userController.createUser(userRequest);

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());

        User user = userResponse.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("mickey_mouse", user.getUsername());
        assertEquals("mickey_mouse_encrypted_password", user.getPassword());
    }

    @Test
    public void createUserUnhappyPathTest() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("pluto");
        //Test small password!

        userRequest.setPassword("a");
        userRequest.setConfirmPassword("a");

        ResponseEntity<User> userResponse = userController.createUser(userRequest);

        assertNotNull(userResponse);
        assertEquals(400, userResponse.getStatusCodeValue());

        //Test password mismatch with confirmPassword!
        userRequest.setPassword("my_password");
        userRequest.setConfirmPassword("my_confirmed_password");

        userResponse = userController.createUser(userRequest);
        assertNotNull(userResponse);
        assertEquals(400, userResponse.getStatusCodeValue());
    }

    @Test
    public void findByIdTest() {
        User user = createUser();
        when(userRepository.findById(666L)).thenReturn(Optional.of(user));
        ResponseEntity<User> userResponse = userController.findById(666L);

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());

        User returnedUser = userResponse.getBody();
        assertNotNull(returnedUser);
        assertEquals(666, returnedUser.getId());
        assertEquals("username", returnedUser.getUsername());
        assertEquals("password", returnedUser.getPassword());
    }

    @Test
    public void findByUserNameHappyPathTest() {
        User user = createUser();
        when(userRepository.findByUsername("username")).thenReturn(user);

        ResponseEntity<User> userResponse = userController.findByUserName("username");

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());

        User returnedUser = userResponse.getBody();
        assertNotNull(returnedUser);
        assertEquals(666, returnedUser.getId());
        assertEquals("username", returnedUser.getUsername());
        assertEquals("password", returnedUser.getPassword());
    }

    @Test
    public void findByUserNameUnhappyPathTest() {
        ResponseEntity<User> userResponse = userController.findByUserName("blah_blah");
        assertEquals(404, userResponse.getStatusCodeValue());
    }

    private static  User createUser() {
        User user = new User();
        user.setId(666);
        user.setUsername("username");
        user.setPassword("password");
        user.setCart(new Cart());
        return user;
    }
}

