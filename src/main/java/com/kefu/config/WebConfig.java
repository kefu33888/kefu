package com.kefu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/index").setViewName("forward:/index.html");
        registry.addViewController("/console/share").setViewName("forward:/index.html");
        registry.addViewController("/admin/share").setViewName("forward:/index.html");
        registry.addViewController("/console/chat").setViewName("forward:/index.html");
        registry.addViewController("/admin/chat").setViewName("forward:/index.html");
        // Support legacy/chat routes used by frontend share links
        registry.addViewController("/xy-chat").setViewName("forward:/index.html");
        registry.addViewController("/zz-chat").setViewName("forward:/index.html");
        registry.addViewController("/px-chat").setViewName("forward:/index.html");
        registry.addViewController("/px2-chat").setViewName("forward:/index.html");
        registry.addViewController("/pz-chat").setViewName("forward:/index.html");
        registry.addViewController("/pz2-chat").setViewName("forward:/index.html");
        registry.addViewController("/jym-chat").setViewName("forward:/index.html");
        registry.addViewController("/wx-chat").setViewName("forward:/index.html");
        registry.addViewController("/yl-chat").setViewName("forward:/index.html");
        registry.addViewController("/kjs-chat").setViewName("forward:/index.html");
        registry.addViewController("/qd-chat").setViewName("forward:/index.html");
        registry.addViewController("/kefu").setViewName("forward:/index.html");
    }
}
