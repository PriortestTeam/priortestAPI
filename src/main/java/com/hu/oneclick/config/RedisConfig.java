package com.hu.oneclick.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qingyang
 */
@Configuration
public class RedisConfig {

    private final RedisProperties redisProperties;

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public RedissonClient redissonSingle() {
        Config config = new Config();
        config.setCodec(new StringCodec());
        //指定使用单节点部署方式
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" +redisProperties.getPort())
                .setPassword(redisProperties.getPassword())
                .setClientName(null)
                .setDatabase(3)
                .setIdleConnectionTimeout(10000)
                .setPingConnectionInterval(1000)
                .setConnectTimeout(10000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500)
                .setSubscriptionsPerConnection(5)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(50)
                .setConnectionMinimumIdleSize(32)
                .setConnectionPoolSize(64)
                .setDnsMonitoringInterval(5000);
        config.setThreads(0);
        config.setNettyThreads(0);
        config.setCodec(new JsonJacksonCodec());
        config.setTransportMode(TransportMode.NIO);
        return Redisson.create(config);
    }
}
