package com.SmartLaundry.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalClient {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    public PayPalHttpClient client() {
        PayPalEnvironment environment = mode.equalsIgnoreCase("live")
                ? new PayPalEnvironment.Live(clientId, clientSecret)
                : new PayPalEnvironment.Sandbox(clientId, clientSecret);
        return new PayPalHttpClient(environment);
    }
}
