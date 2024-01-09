package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) { //바인딩 리솔트는 폼에서 오류가 날시 리솔트에 담겨서 실행된다.

        if (result.hasErrors()) { //리솔트에 오류가 있을시 다음 화면을 보여줌.
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members); //이 코드처럼 member엔티티 화면에 그대로 뿌리기 보다는 dto로 변환에서 화면에 꼭 필요한 데이터들만 가지고 뿌리는게 좋다. 간단한 경우에만 dto변환없이 바로 하는게 괜찮음.
        //단, api를 만들때는 이유를 불문하고 절대 엔티티를 웹으로 넘기면 안된다!! -> 엔티티에 필드를 추가, 변경하면 api스펙이 변해버림. 그리고 패스워드같은 거도 노출됨.
        //단, api를 만들때는 이유를 불문하고 절대 엔티티를 웹으로 넘기면 안된다!! -> 엔티티에 필드를 추가, 변경하면 api스펙이 변해버림. 그리고 패스워드같은 거도 노출됨.
        return "members/memberList";
    }
}
