package com.betolara1.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EurekaApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void contextLoads() {
		// Se o contexto do Spring não subir (e.g., erro no application.properties), 
		// o teste já falha ao tentar rodar.
		// O assertNotNull apenas garante que a injeção do ApplicationContext funcionou.
		assertNotNull(context, "O ApplicationContext do Spring deve ter sido carregado com sucesso!");
	}

}
