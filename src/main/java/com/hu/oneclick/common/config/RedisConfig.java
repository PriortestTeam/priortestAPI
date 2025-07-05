package com.hu.oneclick.common.config;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author qingyang
 */
@Configuration

public class RedisConfig {
    private final RedisProperties redisProperties;
    @Value("${spring.redis.host:43.139.159.146}") // Use the provided IP, default to localhost if not set
    private String redisHost;
    @Value("${spring.redis.port:6379}") // Default Redis port
    private int redisPort;
    @Value("${spring.redis.password:}") // Redis password, empty by default
    private String redisPassword;
    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }
    @Bean
    public RedissonClient redissonSingle() {
        Config config = new Config();
        config.setCodec(new StringCodec();
        //指定使用单节点部署方式
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setClientName(null)
                .setDatabase(3)
                .setIdleConnectionTimeout(10000)
                .setPingConnectionInterval(2000)
                .setConnectTimeout(5000)
                .setTimeout(5000)
                .setRetryAttempts(2)
                .setRetryInterval(1000)
                .setSubscriptionsPerConnection(2)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(10)
                .setConnectionPoolSize(16)
                .setConnectionMinimumIdleSize(2)
                .setDnsMonitoringInterval(5000);
        config.setThreads(4);
        config.setNettyThreads(4);
        config.setCodec(new JsonJacksonCodec();
        config.setTransportMode(TransportMode.NIO);
        return Redisson.create(config);
    }
}