package org.spring.springboot;

import io.shardingsphere.core.constant.properties.ShardingPropertiesConstant;
import org.spring.springboot.service.impl.DemoServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by weichao on 18/10/22.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class,args);
    }
}
