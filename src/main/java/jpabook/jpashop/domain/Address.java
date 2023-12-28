package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter //setter를 제공x. 값 타입은 변경 불가하게 만든다.
public class Address {

    private  String city;
    private String street;
    private String zipcode;

    protected Address() { //JPA스펙상 기본생성자를 넣어야 한다. 대신 protected로 하는게 좋음.

    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode =zipcode;
    }
}
