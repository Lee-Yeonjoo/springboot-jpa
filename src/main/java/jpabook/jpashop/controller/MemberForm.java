package jpabook.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm { //화면에서 사용자에게 받는 입력값들에 딱 맞는 폼 클래스를 만든다. 받은 폼을 컨트롤러에서 정제. -> member에 set하기 등.

    @NotEmpty(message="회원 이름은 필수 입니다") //이름만 필수로 받기 위한 어노테이션
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
