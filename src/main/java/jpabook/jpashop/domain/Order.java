package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //유지보수를 위한 생성메소드를 쓰도록 하기 위해 생성자 쓰는 걸 막아줌.
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    //@JsonIgnore //순환 참조로 인한 에러 해결을 위함. 순환 참조를 끊어준다. 한쪽에만 하면 된다.
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

    //==생성 메서드==// 주문 생성할 때 이걸 호출. new Order 하는게 아님.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//

    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();

        /*
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice
         */
    }
}
