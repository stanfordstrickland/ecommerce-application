package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmitHappyPath() {
        User user = createUser();
        when(userRepository.findByUsername("goofy")).thenReturn(user);

        ResponseEntity<UserOrder> userOrderResponse = orderController.submit("goofy");
        assertNotNull(userOrderResponse);
        assertEquals(200, userOrderResponse.getStatusCodeValue());

        UserOrder userOrder = userOrderResponse.getBody();
        assertNotNull(userOrder);
        assertNull(userOrder.getId());
        assertEquals(17.99, userOrder.getTotal().doubleValue());

        User returnedUser = userOrder.getUser();
        assertNotNull(returnedUser);
        assertEquals(999, returnedUser.getId());
        assertEquals("goofy", returnedUser.getUsername());
        assertEquals("goofy_password", returnedUser.getPassword());

        List<Item> items = userOrder.getItems();
        assertEquals(1, items.size());
        Item item = items.get(0);
        assertEquals(244L, item.getId().longValue());
        assertEquals("Christmas Tree Bauble", item.getName());
        assertEquals("Red and Sparkly Bauble", item.getDescription());
        assertEquals(17.99, item.getPrice().doubleValue());
    }

    @Test
    public void testSubmitUnhappyPath() {
        ResponseEntity<UserOrder> orderResponse = orderController.submit("I_Don't_Exist");
        assertEquals(404, orderResponse.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUserHappyPath() {
        User user = createUser();
        when(userRepository.findByUsername("goofy")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(List.of(UserOrder.createFromCart(user.getCart())));

        ResponseEntity<List<UserOrder>> listUserOrderResponse = orderController.getOrdersForUser("goofy");
        assertNotNull(listUserOrderResponse);
        assertEquals(200, listUserOrderResponse.getStatusCodeValue());

        List<UserOrder> userOrders = listUserOrderResponse.getBody();
        assertNotNull(userOrders);
        assertEquals(1, userOrders.size());

        UserOrder userOrder = userOrders.get(0);
        assertNotNull(userOrder);
        assertNull(userOrder.getId());
        assertEquals(17.99, userOrder.getTotal().doubleValue());

        User returnedUser = userOrder.getUser();
        assertNotNull(returnedUser);
        assertEquals(999, returnedUser.getId());
        assertEquals("goofy", returnedUser.getUsername());
        assertEquals("goofy_password", returnedUser.getPassword());

        List<Item> items = userOrder.getItems();
        assertEquals(1, items.size());
        Item item = items.get(0);
        assertEquals(244L, item.getId().longValue());
        assertEquals("Christmas Tree Bauble", item.getName());
        assertEquals("Red and Sparkly Bauble", item.getDescription());
        assertEquals(17.99, item.getPrice().doubleValue());
    }

    @Test
    public void testGetOrdersForUserUnhappyPath() {
        ResponseEntity<List<UserOrder>> orderListResponse =  orderController.getOrdersForUser("I_Don't_Exist");
        assertEquals(404, orderListResponse.getStatusCodeValue());
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
