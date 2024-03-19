package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {

		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5JakartaModule hibernate5JakartaModule() {
		return new Hibernate5JakartaModule();  //지연로딩인 경우 무시하라고 해준다.
		/*Hibernate5JakartaModule hibernate5JakartaModule = new Hibernate5JakartaModule();
		hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);  //지연로딩을 무시하지 않고, 애플리케이션 실행 시점에 강제로 지연로딩시킨다.
		return hibernate5JakartaModule;*/ //왜 스택 오버플로우 오류나지..
	}
}
