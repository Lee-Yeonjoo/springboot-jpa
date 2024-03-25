package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //중요! jpa의 데이터 변경이나 로직들은 트랜잭션 안에서 실행되어야 한다.
@RequiredArgsConstructor //final인 필드만 생성자 주입으로 만들어줌
public class MemberService {

    private final MemberRepository memberRepository; //spring data jpa 인터페이스만 만들었는데 여기에 알아서 구현체가 주입된다.

    //생성자 주입 권장. 생성자가 하나면 @Autowired 안써도 됨
    /*public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }*/

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName()); //findByName은 따로 만든다.
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) { //단건 조회
        return memberRepository.findById(memberId).get();
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id).get();  //영속 상태 -> 변경이 db에 잘 반영됨.
        member.setName(name);
    } //command와 query를 철저히 분리하자. update는 커맨드니까 반환하지 않아야함. -> 유지보수에 좋다.
}
