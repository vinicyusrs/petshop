package petshop.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetShop API")
                        .description("API para gerenciamento de pets, produtos e pedidos")
                        .version("v1.0")
                        .contact(new Contact().name("Seu Nome").email("seu@email.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação PetShop")
                        .url("http://localhost:8080/swagger-ui.html"));
    }
}
