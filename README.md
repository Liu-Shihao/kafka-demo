
# Kafka 概念介绍



# 安装Kafka
## 直接在官网下载压缩包解压安装


## 使用docker-compose部署zookeeper和kafka
 docker-compose.yml文件
```yml
version: '2'

services:
  zookeeper:
    image: zookeeper:3.6
    ports:
      - "2181:2181"
    volumes:
      - ./zookeeper/data:/data
  kafka:
    image: cppla/kafka-docker:arm
    ports:
      - "9092:9092"
    environment:
      - KAFKA_ADVERTISED_HOST_NAME=192.168.153.131
      - KAFKA_ZOOKEEPER_CONNECT=192.168.153.131:2181
      - KAFKA_ADVERTISED_PORT=9092
      - KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.153.131:9092
      - KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./kafka:/kafka
    depends_on:
      - zookeeper
  kafka-ui:
    image: provectuslabs/kafka-ui
    container_name: kafka-ui
    ports:
      - "8082:8080"
    restart: always
    environment:
      - KAFKA_CLUSTERS_0_NAME=local
      - KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=192.168.153.131:9092
      - KAFKA_CLUSTERS_0_ZOOKEEPER=192.168.153.131:2181
      - KAFKA_CLUSTERS_0_READONLY=true
    depends_on:
      - kafka
      - zookeeper
```
kafka的镜像是arm64版本。

```shell
docker-compose up -d
docker-compose down
docker-compose down ps
docker-compose logs -f
```


## kafka命令行
```shell
# 进入容器
docker exec -it kafka-kafka-1 bash
cd /opt/kafka/config
# 进入bin目录
cd /opt/kafka/bin

#查看Topic列表
./kafka-topics.sh --bootstrap-server 192.168.153.131:9092 --list
#查看Topic详情
./kafka-topics.sh --bootstrap-server 192.168.153.131:9092  --describe --topic test 

#创建Topic
./kafka-topics.sh --create --bootstrap-server 192.168.153.131:9092 --topic test --partitions 2 --replication-factor 1

./kafka-topics.sh --create --bootstrap-server 192.168.153.131:9092 --topic originTopic --partitions 3 --replication-factor 1
./kafka-topics.sh --create --bootstrap-server 192.168.153.131:9092 --topic copyTopic --partitions 3 --replication-factor 1
./kafka-topics.sh --create --bootstrap-server 192.168.153.131:9092 --topic suma --partitions 3 --replication-factor 1
./kafka-topics.sh --create --bootstrap-server 192.168.153.131:9092 --topic sumb --partitions 3 --replication-factor 1

#删除Topic
kafka-topics.sh --delete --bootstrap-server 192.168.153.131:9092 --topic test 

# 生产消息
./kafka-console-producer.sh --broker-list 192.168.153.131:9092 --topic originTopic
# 监听消息
./kafka-console-consumer.sh --bootstrap-server 192.168.153.131:9092 --topic copyTopic --from-beginning
```
