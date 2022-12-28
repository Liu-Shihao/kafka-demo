package com.lsh.stream;

import com.lsh.comstant.KafkaConstant;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/28 16:04
 * @Desc:
 */
public class SumStream {
    public static void main(String[] args) {
        Properties prop =new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"sum");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.153.131:9092");
        //提交时间设置为2秒
        prop.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,2000);
//        prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest" );  //earliest  latest  none  默认latest
        //prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false"); //true(自动提交)  false(手动提交)
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());

        //创建流构造器
        StreamsBuilder builder = new StreamsBuilder();
        KStream<Object, Object> source = builder.stream(KafkaConstant.SUM_A);
        KTable<String, String> sum1 = source.map((key, value) ->
                new KeyValue<String, String>("sum", value.toString())
        ).groupByKey().reduce((x, y) -> {
            try {
                System.out.println("x: " + x + "    " + "y: "+y);
                Integer sum = Integer.valueOf(x) + Integer.valueOf(y);
                System.out.println("sum: "+sum);
                return sum.toString();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        });
        sum1.toStream().to(KafkaConstant.SUM_B);
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
