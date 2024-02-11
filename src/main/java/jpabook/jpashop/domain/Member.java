package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id") //테이블에서 쉽게 구분하기 위해 따로 이름 설정해줘야 함.
    private Long id;

    //@NotEmpty
    private String name;

    @Embedded  //내장타입
    private Address address;

    //@JsonIgnore 응답에서 무시됨. 권장x
    @OneToMany(mappedBy = "member")  //읽기 전용이 된다. 여기에 값을 변경해도 fk값이 변경되지x
    private List<Order> orders = new ArrayList<>(); //컬렉션은 생성자보단 필드에서 바로 초기화하는게 안전.
}
