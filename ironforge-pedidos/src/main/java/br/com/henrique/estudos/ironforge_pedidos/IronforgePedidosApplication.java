package br.com.henrique.estudos.ironforge_pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
public class IronforgePedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(IronforgePedidosApplication.class, args);
	}

}
