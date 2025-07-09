package demo.JPA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class JpaApplication {

	// 로그 출력을 위한 Logger 객체 생성
	private static final Logger log = LoggerFactory.getLogger(JpaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JpaApplication.class, args);
	}

	
}


/*
@SpringBootApplication은 3개의 어노테이션을 담음

@Configuration : 빈(객체)에 대해 정의함
@EnableAutoConfiguration : 클래스패스에 있는 라이브러리 보고 자동으로 설정파일 set, defalut방식(정의x -> defalut로 하기)
@ComponentScan : runtime에, 페키지내에 @Component, @Controller, @Service, @Repository 찾아서 빈에 들옥해줘

 */