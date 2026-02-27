package com.betolara1.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;



// O @ComponentScan é necessário para o Spring encontrar os componentes do
// pacote jwt-package, que é onde estão as
// classes de segurança (filtros, etc). Sem ele, o Spring não consegue injetar
// as dependências e dá erro 401.
// Ele basicamente diz: "Ei Spring, procura por componentes (como @Service,
// @Repository, etc) nos pacotes com.betolara1.user e com.betolara1.jwt-package"
// Se o jwt-package estivesse dentro do user, não seria necessário. Mas como ele
// é um módulo separado, precisamos dizer explicitamente onde ele está.

@SpringBootApplication(scanBasePackages = {"com.betolara1.user", "com.betolara1.jwt_package"})
@ComponentScan(basePackages = { "com.betolara1.user", "com.betolara1.jwt-package" })
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
