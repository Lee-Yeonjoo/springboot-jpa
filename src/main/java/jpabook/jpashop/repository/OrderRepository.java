package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
//import jpabook.jpashop.api.OrderSimpleApiController; 리포지토리에 컨트롤러 의존관계가 생기면 안된다!
import jpabook.jpashop.api.OrderApiController;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /*public List<Order> findAll(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName()
                            + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }*/

    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        //static import로 코드를 줄일 수 있다.
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        //생성자에 써주면 코드 지울 수 있음.
        JPAQueryFactory query = new JPAQueryFactory(em);

        return query.select(order) //이게 jpql로 바뀌어서 실행된다.
                .from(order)
                .join(order.member, member) //order의 member를 조인하고, alias로 member 설정
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName())) //주문 상태가 같으면
                .limit(1000)
                .fetch(); //컴파일 시점에 오타가 잡히는 장점이 있다.
    }

    private BooleanExpression nameLike(String memberName) {  //동적쿼리에 쓰이는 메소드
        if (!StringUtils.hasText(memberName)) {
            return null;
        }
        return QMember.member.name.like(memberName);
    }

    private BooleanExpression statusEq(OrderStatus statusCond) {
        if (statusCond == null) {
            return null;
        }
        return QOrder.order.status.eq(statusCond);
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class //멤버와 딜리버리를 오더와 함께 가져온다. 진짜 객체를 가져옴. 프록시x
        ).getResultList();
    }


    public List<Order> findAllWithItem() {
        return em.createQuery(
                        "select distinct o from Order o" + //하이버네이트6부터는 distinct가 자동 적용.
                                " join fetch o.member m" +
                                " join fetch o.delivery d" +
                                " join fetch o.orderItems oi" +  //orderItem이 order당 2개씩이라서 order가 총 4개가 되어 데이터 뻥튀기가 된다.
                                " join fetch oi.item i", Order.class)
                .setFirstResult(0)
                .setMaxResults(100) //컬렉션 조회에서 페치조인을 쓰면 메모리에서 페이징 처리를 한다. 잘못하면 out of memory 에러 날 수도.
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).setFirstResult(offset)  //toOne관계는 페이징 가능하다
                .setMaxResults(limit)
                .getResultList();
    }
}
