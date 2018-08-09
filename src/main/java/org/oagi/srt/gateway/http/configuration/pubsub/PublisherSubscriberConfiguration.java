package org.oagi.srt.gateway.http.configuration.pubsub;

import org.oagi.srt.gateway.http.api.bie_management.service.BieCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class PublisherSubscriberConfiguration {

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private BieCopyService bieCopyMessageListener;

    @Autowired
    private RedisTemplate redisTemplate;

    @Bean
    MessageListenerAdapter bieCopyMessageListener() {
        MessageListenerAdapter listenerAdapter =
                new MessageListenerAdapter(bieCopyMessageListener, "handleCopyRequest");
        listenerAdapter.setSerializer(redisTemplate.getValueSerializer());
        return listenerAdapter;
    }

    @Bean
    RedisMessageListenerContainer messageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(bieCopyMessageListener(),
                new ChannelTopic("bie-copy"));
        return container;
    }

}
