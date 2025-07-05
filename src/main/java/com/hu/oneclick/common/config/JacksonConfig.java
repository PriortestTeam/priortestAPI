package com.hu.oneclick.common.config;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hu.oneclick.common.advice.LargeNumberSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import java.io.IOException;
/**
 * @author qingyang
 */
@Configuration

public class JacksonConfig {
    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // Custom null value serializer
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString("");
            }
        });
        // Handling long and BigInteger serialization
        SimpleModule simpleModule = new SimpleModule();
        // Add default long -> string serialization
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(java.math.BigInteger.class, new LargeNumberSerializer();
        // Register custom serializers for Long and BigInteger
        simpleModule.addSerializer(Long.class, new LargeNumberSerializer();
        simpleModule.addSerializer(Long.TYPE, new LargeNumberSerializer();
        // Register the module
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }
}
}
}
