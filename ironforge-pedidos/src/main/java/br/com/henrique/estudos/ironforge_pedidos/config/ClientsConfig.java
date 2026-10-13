package br.com.henrique.estudos.ironforge_pedidos.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        basePackages = "br.com.henrique.estudos.ironforge_pedidos.client",
        defaultConfiguration = FeignClientConfig.class
)
public class ClientsConfig {
}