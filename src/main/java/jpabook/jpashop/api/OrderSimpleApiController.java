package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//주문 조회하고, 주문한 회원과 배송 정보 연관이 걸려있음. -> 얘넨 XtoOne 관계의 대상. X = Order, One이 member, delivery
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/sample-orders")
    public List<Order> ordersV1() { //엔티티 그대로 반환하는 경우
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        //하이버네이트 모듈의 강제 지연로딩을 끄고, 직접 코드를 써서 강제 초기화
        for (Order order : all) {
            order.getMember().getName(); //getMember까지는 프록시고, getName()에서 진짜 객체를 가져옴 -> Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }

        return all;
    }
}
