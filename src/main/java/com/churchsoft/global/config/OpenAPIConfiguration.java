package com.churchsoft.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
    title = "GCCI Church Suite API",
    version = "1.0",
            description = "This API documentation is for Fount Technology, designed for GCCI to collect Church Member Info.",
            contact = @Contact(
      name = "Email Developer",  
      email = "gatidavid2012@gmail.com"
    ),
    license = @License(
      name = "Mesh, All Rights Reserved",
      url = "https://fountlife.com/2020/12/29/privacy-policy/"))
)
public class OpenAPIConfiguration {

}
