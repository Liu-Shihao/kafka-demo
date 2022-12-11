package com.lsh;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/11 21:16
 * @Desc:
 */
@SpringBootTest
public class SentKafka {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Test
    public  void  test1(){
        kafkaTemplate.send("test", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
        System.out.println("done.");
    }


}
