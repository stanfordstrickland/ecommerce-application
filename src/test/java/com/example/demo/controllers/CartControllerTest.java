package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

    }

    @Test
    public void testAddToCartHappyPath() {
        User user = createUser();
        when(itemRepository.findById(244L)).thenReturn(Optional.ofNullable(user.getCart().getItems().get(0)));
        when(userRepository.findByUsername("goofy")).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(244L);
        modifyCartRequest.setUsername("goofy");
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);
        assertNotNull(cartResponse);
        assertEquals(200, cartResponse.getStatusCodeValue());

        Cart cart = cartResponse.getBody();
        assertNotNull(cartResponse);
        assertEquals(666L, Objects.requireNonNull(cart).getId().longValue());
        assertEquals(2, cart.getItems().size());
        assertEquals(35.98, cart.getTotal().doubleValue());
    }

    @Test
    public void testAddToCartUnhappyPath() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(244L);
        modifyCartRequest.setUsername("goofy");
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);
        assertEquals(404, cartResponse.getStatusCodeValue());

        User user = createUser();
        when(userRepository.findByUsername("goofy")).thenReturn(user);
        cartResponse = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, cartResponse.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartHappyPath() {
        User user = createUser();
        when(itemRepository.findById(244L)).thenReturn(Optional.ofNullable(user.getCart().getItems().get(0)));
        when(userRepository.findByUsername("goofy")).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(244L);
        modifyCartRequest.setUsername("goofy");
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponse = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(cartResponse);
        assertEquals(200, cartResponse.getStatusCodeValue());

        Cart cart = cartResponse.getBody();
        assertNotNull(cartResponse);
        assertEquals(666L, Objects.requireNonNull(cart).getId().longValue());
        assertEquals(0, cart.getItems().size());
        assertEquals(0., cart.getTotal().doubleValue());
    }

    @Test
    public void testRemoveFromCartUnhappyPath() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(244L);
        modifyCartRequest.setUsername("goofy");
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponse = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, cartResponse.getStatusCodeValue());

        User user = createUser();
        when(userRepository.findByUsername("goofy")).thenReturn(user);
        cartResponse = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, cartResponse.getStatusCodeValue());
    }

    private User createUser() {
        User user = new User();
        user.setId(999L);
        user.setUsername("goofy");
        user.setPassword("goofy_password");

        Item item = new Item();
        item.setId(244L);
        item.setName("Christmas Tree Bauble");
        item.setDescription("Red and Sparkly Bauble");
        item.setPrice(BigDecimal.valueOf(17.99));

        Cart cart = new Cart();
        cart.setId(666L);
        cart.addItem(item);
        cart.setUser(user);
        user.setCart(cart);

        return user;
    }
}
