package com.liangck.filemanagement.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author liangck
 * @version 1.0
 * @since 2018/1/16 16:54
 */
@EntityScan(
        basePackageClasses = {FileManagementApplication.class, Jsr310JpaConverters.class}
)
@SpringBootApplication
public class FileManagementApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManagementApplication.class);

    @PostConstruct
    private void initGlobalConfig() {
        /*try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("init fastdfs by properties error. caused by:  {}", e.getMessage());
        } catch (MyException e) {
            e.printStackTrace();
            LOGGER.error("init fastdfs by properties error. caused by: {}", e.getMessage());
        }*/
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FileManagementApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FileManagementApplication.class, args);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        System.out.println("Config is starting.");
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
