package com.github.pgutkowski.fda.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.validation.Validator
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
class WebFluxConfiguration : WebFluxConfigurer {

    @Autowired
    private lateinit var defaultValidator: Validator

    override fun getValidator(): Validator? {
        return defaultValidator
    }
}