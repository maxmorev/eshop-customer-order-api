package ru.maxmorev.eshop.customer.order.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cart")
public class ShoppingCartConfig {
    private int maxItemsAmount;
}
