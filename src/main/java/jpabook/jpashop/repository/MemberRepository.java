package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //select m from Member m where m.name = ? By 뒤에 오는 Name을 보고 쿼리를 이렇게 짜준다.
    List<Member> findByName(String name);
}
