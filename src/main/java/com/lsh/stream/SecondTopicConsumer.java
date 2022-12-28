package com.lsh.stream;

import com.lsh.comstant.KafkaConstant;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/28 14:34
 * @Desc:
 */
public class SecondTopicConsumer {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers","192.168.153.131:9092");
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"CopyConsumer");
        // 构建kafka消费者对象
        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(properties);
        try {
            consumer.subscribe(Collections.singletonList(KafkaConstant.NEW));
            // 调用消费者拉取消息
            while(true){
                // 设置1秒的超时时间
                ConsumerRecords<String, String> records= consumer.poll(Duration.ofSeconds(1));
                for(ConsumerRecord<String, String> record:records){
                    String key = record.key();
                    String value = record.value();
                    System.out.println("接收到消息: key = " + key + ", value = " + value);
                }
            }
        } finally {
            // 释放连接
            consumer.close();

        }
    }
}
