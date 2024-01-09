package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;
    @Autowired EntityManager em;

    @Test
    public void 상품등록() {
        //give
        Book item = new Book();

        //when
        Long itemId = itemService.saveItem(item);

        //then
        Assertions.assertEquals(item.getId(), itemId);
    }

    @Test
    public void 상품_단건조회() {
        //give
        Book book = new Book();
        book.setName("책");
        itemService.saveItem(book);

        //when
        Item findbook = itemService.findOne(book.getId());

        //then
        Assertions.assertEquals(book, findbook);
    }
}
