package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

//주문 조회하고, 주문한 회원과 배송 정보 연관이 걸려있음. -> 얘넨 XtoOne 관계의 대상. X = Order, One이 member, delivery
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() { //엔티티 그대로 반환하는 경우
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        //하이버네이트 모듈의 강제 지연로딩을 끄고, 직접 코드를 써서 강제 초기화
        for (Order order : all) {
            order.getMember().getName(); //getMember까지는 프록시고, getName()에서 진짜 객체를 가져옴 -> Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }

        return all;
    }

    @GetMapping("/api/v2/simple-orders") //지연 로딩으로 인한 데이터베이스 쿼리가 너무 많이 호출되는 문제가 있다.
    public List<SimpleOrderDto> ordersV2() { //dto로 반환. 꼭 엔티티가 아닌 dto로 바꾸자!
        //ORDER 2개의 결과(row)를 낸다.
        //N + 1 -> 1 + 회원 N + 배송 N = 총 5번의 쿼리가 나간다.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());  //order만 조회(지연로딩이니까)


        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                //.map(SimpleOrderDto::new) 로 코드 줄이기 가능
                .collect(toList()); //alt+enter -> static import를 통해 코드 줄일 수 있따.

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화 = 영속성컨텍스트에 없으므로 db에 쿼리를 날린다.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }
}
