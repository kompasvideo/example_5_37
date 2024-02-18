package ru.andreybaryshnikov.notificationservice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    //@Value("${kafka.server}")
    private static String SERVER = "http://my-prometheus-prometheus-pushgateway.monitoring.svc.cluster.local:9091";
    @Value("${appl.groupid}")
    private String groupId;

    private ConsumerFactory<String, String> consumerFactoryString() {
        System.out.println("-----------");
        System.out.println("-----------");
        System.out.println("----------- " + SERVER + " -----------");
        System.out.println("-----------");
        System.out.println("-----------");


        Map<String, Object> props = new HashMap<>();
        props.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            SERVER);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            groupId);
        props.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class);
        props.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryString() {
        System.out.println("-----------  1" );
        System.out.println("-----------  1");
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        System.out.println("----------- 2");
        System.out.println("----------- 2");

        factory.setConsumerFactory(consumerFactoryString());
        System.out.println("----------- 3");
        System.out.println("----------- 3");
        return factory;
    }
}
