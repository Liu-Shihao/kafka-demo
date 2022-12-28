package com.lsh.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/28 17:10
 * @Desc: TopicA 15秒内的wordcount，结果写入TopicB
 * 比如登录某app，20分钟内不操作，会自动退出。
 * 一个典型的案例是，希望通过Session Window计算某个用户访问网站的时间。
 * 对于一个特定的用户（用Key表示）而言，当发生登录操作时，该用户（Key）的窗口即开始，
 * 当发生退出操作或者超时时，该用户（Key）的窗口即结束。窗口结束时，可计算该用户的访问时间或者点击次数等。
 */
public class WindowStream3 {
    public static void main(String[] args) {
        Properties prop =new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"WindowStream");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.153.131:9092");
        prop.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,3000);
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<Object, Object> source = builder.stream("windowdemo1");
        KTable<Windowed<String>, Long> countKtable = source.flatMapValues(value -> Arrays.asList(value.toString().split("\\s+")))
                .map((x, y) -> {
                    return new KeyValue<String, String>(y, "1");
                }).groupByKey()
                .windowedBy(SessionWindows.with(Duration.ofSeconds(15)))
                .count();

        countKtable.toStream().foreach((x,y)->{
            System.out.println("x: "+x+"  y: "+y);
        });

        countKtable.toStream().map((x,y)-> {
            return new KeyValue<String, String>(x.toString(), y.toString());
        }).to("windowDemoOut");

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
