package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //상속 타입. 싱글 테이블은 테이블 한개에 상속되는 애들 다 한번에 넣는 것.
@DiscriminatorColumn(name = "dtype")
@Getter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==// 재고관리는 그냥 Item엔티티가 하는게 좋다. Item이 stockQuantity데이터를 갖고 있으니까.
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quanitty) {
        int restStock = this.stockQuantity - quanitty;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
