package gt.chn.tarjetas.compartido.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracionCors {

@Bean
public WebMvcConfigurer configurarCors() {
    return new WebMvcConfigurer() {

        @Override
        public void addCorsMappings(CorsRegistry registro) {

            registro.addMapping("/api/**")
                    .allowedOrigins("http://localhost:4200")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*");
        }
    };
}

}