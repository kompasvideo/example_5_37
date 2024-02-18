package ru.andreybaryshnikov.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    //@Value("${kafka.server}")
    private static String SERVER = "http://my-prometheus-prometheus-pushgateway.monitoring.svc.cluster.local:9091";
    //@Value("${notification.partitions}")
    private int notificPartitions = 3;
    @Value("${notification.topicName}")
    private String notificTopic = "NotificationTopic";


    private ProducerFactory<String, String> producerFactoryString() {
        System.out.println("-----------");
        System.out.println("-----------");
        System.out.println("----------- " + SERVER + " -----------");
        System.out.println("-----------");
        System.out.println("-----------");
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            SERVER);
        configProps.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        configProps.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString() {
        System.out.println("----------- kafkaTemplateString -----------");
        System.out.println("-----------");
        return new KafkaTemplate<>(producerFactoryString());
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        System.out.println("----------- kafkaAdmin -----------");
        System.out.println("-----------");
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        System.out.println("----------- topic1 -----------");
        System.out.println("-----------");
        return new NewTopic(notificTopic, notificPartitions, (short) 1);
    }

    @Bean
    public NewTopic topic2() {
        System.out.println("----------- topic2 -----------");
        System.out.println("-----------");
        return new NewTopic("topic2", 1, (short) 1);
    }

//    public final String topicName;
//
//    public KafkaConfig(@Value("${application.kafka.topic}") String topicName) {
//        this.topicName = topicName;
//    }
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        return JacksonUtils.enhancedObjectMapper();
//    }
//
//    @Bean
//    public ProducerFactory<String, StringValue> producerFactory(
//        KafkaProperties kafkaProperties, ObjectMapper mapper) {
//        var props = kafkaProperties.buildProducerProperties();
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        var kafkaProducerFactory = new DefaultKafkaProducerFactory<String, StringValue>(props);
//        kafkaProducerFactory.setValueSerializer(new JsonSerializer<>(mapper));
//        return kafkaProducerFactory;
//    }
//
//    @Bean
//    public KafkaTemplate<String, StringValue> kafkaTemplate(
//        ProducerFactory<String, StringValue> producerFactory) {
//        return new KafkaTemplate<>(producerFactory);
//    }
//
//    @Bean
//    public NewTopic topic() {
//        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
//    }
//
//    @Bean
//    public DataSender dataSender(NewTopic topic, KafkaTemplate<String, StringValue> kafkaTemplate) {
//        return new DataSenderKafka(
//            topic.name(),
//            kafkaTemplate,
//            stringValue -> log.info("asked, value:{}", stringValue));
//    }
//
//    @Bean
//    public StringValueSource stringValueSource(DataSender dataSender) {
//        return new StringValueSource(dataSender);
//    }
}
