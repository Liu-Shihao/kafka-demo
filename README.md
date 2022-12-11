

```shell
docker exec -it kafka-kafka-1 bash
cd /opt/kafka/config
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

