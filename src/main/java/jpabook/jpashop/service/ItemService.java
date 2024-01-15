package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService { //거의 아이템 리포지토리에 일을 위임하고 있음 -> 이럴 땐 아이템 서비스를 굳이 만들지 않고, 컨트롤러에서 바로 아이템 리포지토리를 접근해도 됨.

    private final ItemRepository itemRepository;

    @Transactional
    /*public void saveItem(Item item) {
        itemRepository.save(item);
    }*/
    public Long saveItem(Item item) {
        itemRepository.save(item);
        return item.getId();
    }

    @Transactional //트랜잭션이 커밋되면 flush를 날리고, 알아서 db 업데이트 -> 변경 감지
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId); //영속성 엔티티를 찾음.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity); //이렇게만 하면 알아서 db에 반영. 이게 병합보다 나은 방법. em.merge함수와 유사.
        //return findItem;
    } //머지를 쓰지 말고 이 방법인 변경 감지를 쓰자!

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
