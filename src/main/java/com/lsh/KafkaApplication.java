package com.lsh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/9 16:49
 * @Desc:
 */
@Slf4j
@SpringBootApplication
public class KafkaApplication {

    public static void main(String[] args) {
        log.info("************* KafkaApplication Start *************");
        SpringApplication.run(KafkaApplication.class,args);
        log.info("************* KafkaApplication End *************");
    }



}
