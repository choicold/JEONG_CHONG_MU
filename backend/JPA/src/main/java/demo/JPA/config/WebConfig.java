package demo.JPA.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // ▼▼▼▼▼ [수정] 여기에 ngrok 주소를 추가합니다. ▼▼▼▼▼
                .allowedOrigins(
                        "http://localhost:8080",
                        "http://127.0.0.1:5500",
                        "null",
                        "https://stable-finally-jaybird.ngrok-free.app" // ngrok 주소 추가
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}