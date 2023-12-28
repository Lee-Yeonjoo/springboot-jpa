package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")  //fk 이름 설정
    private Member member; //연관관계의 주인

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //cascade는 Order를 persist할 때 같이 persist해줌.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name= "delivery_id") //일대일 매핑일 때는 둘 다 연관관계 주인이 가능하지만 주로 액세스를 많이 하는 쪽으로 설정
    private Delivery delivery;       //배송조회보다는 주문조회를 더 많이 하니까 주문에서의 delivery를 fk로

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //연관관계 메서드 -> 위치는 양방향일 때 핵심적인 엔티티가 갖고 있는게 좋다.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
