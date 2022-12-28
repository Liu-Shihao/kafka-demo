package com.lsh.stream;

import com.lsh.comstant.KafkaConstant;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/28 14:34
 * @Desc:
 */
public class FirstTopicProducer {

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put("bootstrap.servers","192.168.153.131:9092");
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", StringSerializer.class);

        KafkaProducer<String,String> producer  = new KafkaProducer<>(properties);
        try {
            ProducerRecord<String,String> record;
            while (true){
                record = new ProducerRecord<>(KafkaConstant.ORIGIN, null, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                Future<RecordMetadata> recordMetadataFuture = producer.send(record);
                RecordMetadata recordMetadata = recordMetadataFuture.get();
                if(null!=recordMetadata){
                    System.out.println("topic:"+recordMetadata.topic()+",offset:"+recordMetadata.offset()+","
                            +"partition:"+recordMetadata.partition());
                }
                Thread.sleep(1000);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 释放连接
            producer.close();
        }
    }
}
