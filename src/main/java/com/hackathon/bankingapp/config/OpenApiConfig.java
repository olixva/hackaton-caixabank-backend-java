package com.hackathon.bankingapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Hackathon Banking App",
                version = "1.0",
                description = "Documentacion del reto de backend de la hackaton organizada por CaixaBank realizado por Antonio Oliva",
                contact = @Contact(
                        name = "Antonio Oliva",
                        email = "antonio.oliva.carceles@gmail.com"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Authentication based on JWT",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}