package com.lsh.stream;

import com.lsh.comstant.KafkaConstant;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/28 15:14
 * @Desc:
 */
public class WordCountStream {
    public static void main(String[] args) {
        Properties prop =new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"wordcountstream");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.153.131:9092");
        //提交时间设置为2秒
        prop.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,2000);
//        prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest" );  //earliest  latest  none  默认latest
        //prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false"); //true(自动提交)  false(手动提交)
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        //从topic中一条一条取数据
        KTable<String, Long> count = builder.stream(KafkaConstant.ORIGIN)
                .flatMapValues(
                        //返回压扁后的数据
                        (value) -> {
                            //对数据按空格进行切割，返回List集合
                            String[] split = value.toString().split(" ");
                            List<String> strings = Arrays.asList(split);
                            return strings;
                        })  //null hello,null world,null hello,null java
                .map((k, v) -> {
                    return new KeyValue<String, String>(v,"1");
                }).groupByKey().count();
        count.toStream().foreach((k,v)->{
            //为了测试方便，我们将kv输出到控制台
            System.out.println("key:"+k+"   "+"value:"+v);
        });
        count.toStream().map((x,y)->{
            return new KeyValue<String,String>(x,y.toString());
        }).to(KafkaConstant.NEW);


        final Topology topo=builder.build();
        final KafkaStreams streams = new KafkaStreams(topo, prop);

        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("stream"){
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            streams.start();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
