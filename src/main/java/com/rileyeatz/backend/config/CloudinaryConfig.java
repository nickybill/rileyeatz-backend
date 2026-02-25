package com.rileyeatz.backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dsdwiytyq",
                "api_key", "231432169295861",
                "dct3c-pP0dytGZ_iY5LZQaT5qo4"
        ));
    }
}