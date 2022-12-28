package com.lsh.consumergroup;

import com.lsh.comstant.KafkaConstant;
import com.lsh.selfpartition.SelfPartitioner;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * 类说明：kafak生产者:发送多条消息
 */
public class KafkaProducerGroup {

    public static void main(String[] args) {
        // 设置属性
        Properties properties = new Properties();
        // 指定连接的kafka服务器的地址
        properties.put("bootstrap.servers", KafkaConstant.KAFKA_HOST);
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", StringSerializer.class);

        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, SelfPartitioner.class);

        // 构建kafka生产者对象
        KafkaProducer<String,String> producer  = new KafkaProducer<String, String>(properties);
        try {
            for(int i =0;i<10 ;i++){
                ProducerRecord<String,String> record;
                try {
                    // 构建消息
                    record = new ProducerRecord<String,String>(KafkaConstant.TOPIC_TEST, null,"zhangsan"+i);
                    // 发送消息
                    Future<RecordMetadata> future =producer.send(record);
                    RecordMetadata recordMetadata = future.get();
                    if(null!=recordMetadata){
                        System.out.println(i+","+"offset:"+recordMetadata.offset()+","
                                +"partition:"+recordMetadata.partition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            // 释放连接
            producer.close();
        }
    }


}
