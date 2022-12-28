package com.lsh.springboot.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/11 21:14
 * @Desc:
 */
@Slf4j
@Component
public class TestConsumer {

    /**
     * 定义此消费者接收topics = "demo"的消息，与controller中的topic对应上即可
     * @param record 变量代表消息本身，可以通过ConsumerRecord<?,?>类型的record变量来打印接收的消息的各种信息
     */
//    @KafkaListener(topicPartitions = {@TopicPartition(topic = "test",partitions = {"0","1"})})
    @KafkaListener(topics = "test")
    public void listen (ConsumerRecord<String, String> record){

        String topic = record.topic();
        String value = record.value();

        log.info("============== listen : {}:{}",topic,value);

    }

}
