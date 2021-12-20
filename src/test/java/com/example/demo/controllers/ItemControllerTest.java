package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems() {
        List<Item> items = createItemList();
        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> itemsResponse = itemController.getItems();
        assertNotNull(itemsResponse);
        assertEquals(200, itemsResponse.getStatusCodeValue());

        List<Item> returnedItems = itemsResponse.getBody();
        assertNotNull(returnedItems);

        Item itemOne = returnedItems.get(0);
        assertEquals(0L, itemOne.getId().longValue());
        assertEquals("Football", itemOne.getName());
        assertEquals("Ronaldo Signed Football", itemOne.getDescription());
        assertEquals(50.99, itemOne.getPrice().doubleValue());

        Item itemTwo = returnedItems.get(1);
        assertEquals(1L, itemTwo.getId().longValue());
        assertEquals("Rugby Ball", itemTwo.getName());
        assertEquals("Josh Adams Signed Rugby Ball", itemTwo.getDescription());
        assertEquals(39.99, itemTwo.getPrice().doubleValue());
    }

    @Test
    public void testGetItemsById() {
        List<Item> items = createItemList();
        when(itemRepository.findById(0L)).thenReturn(Optional.ofNullable(items.get(0)));

        ResponseEntity<Item> itemResponse = itemController.getItemById(0L);
        assertNotNull(itemResponse);
        assertEquals(200, itemResponse.getStatusCodeValue());

        Item returnedItem = itemResponse.getBody();
        assertNotNull(returnedItem);
        assertEquals(0L, returnedItem.getId().longValue());
        assertEquals("Football", returnedItem.getName());
        assertEquals("Ronaldo Signed Football", returnedItem.getDescription());
        assertEquals(50.99, returnedItem.getPrice().doubleValue());
    }

    @Test
    public void testGetItemsByNameHappyPath() {
        List<Item> items = createItemList();
        when(itemRepository.findByName("Rugby Ball")).thenReturn(items.subList(1, 2));

        ResponseEntity<List<Item>> itemsResponse = itemController.getItemsByName("Rugby Ball");
        assertNotNull(itemsResponse);
        assertEquals(200, itemsResponse.getStatusCodeValue());

        List<Item> returnedItems = itemsResponse.getBody();
        assertNotNull(returnedItems);
        assertEquals(1, returnedItems.size());

        Item returnedItem = returnedItems.get(0);
        assertEquals(1L, returnedItem.getId().longValue());
        assertEquals("Rugby Ball", returnedItem.getName());
        assertEquals("Josh Adams Signed Rugby Ball", returnedItem.getDescription());
        assertEquals(39.99, returnedItem.getPrice().doubleValue());
    }

    @Test
    public void testGetItemsByNameUnhappyPath() {
        ResponseEntity<List<Item>> userResponse = itemController.getItemsByName("blah_blah");
        assertEquals(404, userResponse.getStatusCodeValue());
    }

    private List<Item> createItemList() {
        List<Item> items = new ArrayList<>();
        Item itemOne = new Item();
        itemOne.setId(0L);
        itemOne.setName("Football");
        itemOne.setDescription("Ronaldo Signed Football");
        itemOne.setPrice(BigDecimal.valueOf(50.99));
        items.add(itemOne);

        Item itemTwo = new Item();
        itemTwo.setId(1L);
        itemTwo.setName("Rugby Ball");
        itemTwo.setDescription("Josh Adams Signed Rugby Ball");
        itemTwo.setPrice(BigDecimal.valueOf(39.99));
        items.add(itemTwo);

        return items;
    }
}
