package com.lsh.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/28 17:06
 * @Desc: 每隔2秒钟输出一次过去5秒内topicA里的wordcount，结果写入到TopicB
 */
public class WindowStream1 {
    public static void main(String[] args) {

        Properties prop =new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"WindowStream");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.153.131:9092");
        prop.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,3000);
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<Object, Object> source = builder.stream("topicA");
        KTable<Windowed<String>, Long> countKtable = source.flatMapValues(value -> Arrays.asList(value.toString().split("\\s+")))
                .map((x, y) -> {
                    return new KeyValue<String, String>(y, "1");
                }).groupByKey()
                //加5秒窗口,按步长2秒滑动  Hopping Time Window
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)).advanceBy(Duration.ofSeconds(2)))
                //.windowedBy(SessionWindows.with(Duration.ofSeconds(15).toMillis()))
                .count();
        //为了方便查看，输出到控制台
        countKtable.toStream().foreach((x,y)->{
            System.out.println("x: "+x+"  y: "+y);
        });

        countKtable.toStream().map((x,y)-> {
            return new KeyValue<String, String>(x.toString(), y.toString());
        }).to("topicB");

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
