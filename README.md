
# 使用docker-compose部署zookeeper和kafka
## docker-compose.yml文件
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
kafka-topics.sh --list --zookeeper 192.168.153.131:2181 
#查看Topic详情
kafka-topics.sh --describe --topic test2 --zookeeper 192.168.153.131:2181

#创建Topic
kafka-topics.sh --create --zookeeper 192.168.153.131:2181 --topic test2 --partitions 2 --replication-factor 1
#删除Topic
kafka-topics.sh --delete --zookeeper 192.168.153.131:2181 --topic test 

# 生产消息
kafka-console-producer.sh --broker-list 192.168.153.131:9092 --topic test
# 监听消息
kafka-console-consumer.sh --bootstrap-server 192.168.153.131:9092 --topic test2 --from-beginning
```
