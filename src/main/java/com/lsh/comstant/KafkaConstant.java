package com.lsh.comstant;

import lombok.Data;

/**
 * @Author: LiuShihao
 * @Date: 2022/12/27 10:13
 * @Desc:
 */
@Data
public class KafkaConstant {

    public static final String KAFKA_HOST = "192.168.153.131:9092";

    public static final String TOPIC_TEST = "test";

    public static final String TOPIC_TEST_USER = "test-user";

    public static final String ORIGIN = "originTopic";

    public static final String NEW = "copyTopic";
    public static final String SUM_A = "suma";
    public static final String SUM_B = "sumb";

}
