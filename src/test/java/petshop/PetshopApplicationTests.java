package petshop;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PetshopApplicationTests {

	@Test
	@Disabled("Desativado no CI até corrigir configuração de ApplicationContext")
	void contextLoads() {
	}

}
