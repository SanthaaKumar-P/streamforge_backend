package com.streamforge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class FileStorageConfig
        implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        registry
                .addResourceHandler(
                        "/reports/**"
                )
                .addResourceLocations(
                        "file:uploads/reports/"
                );
    }
}