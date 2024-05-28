package com.hu.oneclick.common.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.hu.oneclick.relation.enums.SwaggerDisplayEnum;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.Annotations;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.schema.ApiModelProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration //声明该类为配置类
@EnableSwagger2 //声明启动Swagger2
@Slf4j
public class SwaggerConfig implements ModelPropertyBuilderPlugin {

    @Bean
    public Docket customDocket() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("Authorization").description("user Token")//Token 以及Authorization 为自定义的参数，session保存的名字是哪个就可以写成那个
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build(); //header中的ticket参数非必填，传空也可以
        ParameterBuilder ticketParTwo = new ParameterBuilder();
        ticketParTwo.name("emailId").description("emailId")//Token 以及Authorization 为自定义的参数，session保存的名字是哪个就可以写成那个
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(ticketPar.build());
        pars.add(ticketParTwo.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 扫描所有有注解的api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .apis(RequestHandlerSelectors.basePackage("com.hu.oneclick.controller"))//扫描的包路径
                .build()
                .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("oneclick")//文档说明
                .version("1.0.0")//文档版本说明
                .build();
    }

    @Override
    public void apply(ModelPropertyContext context) {
//        //如果不支持swagger的话，直接返回
//        if (!swaggerEnable) {
//            return;
//        }

        //获取当前字段的类型
        final Optional<BeanPropertyDefinition> definition = context.getBeanPropertyDefinition();
        definition.ifPresent(d -> descForEnumFields(context));
    }

    /**
     * 为枚举字段设置注释
     */
    private void descForEnumFields(ModelPropertyContext context) {
        Optional<ApiModelProperty> optional = Optional.empty();
        // 找到 @ApiModelProperty 注解修饰的枚举类
        if (context.getAnnotatedElement().isPresent()) {
            optional = optional.or(() -> ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
        }
        if (context.getBeanPropertyDefinition().isPresent()) {
            optional = optional.or(() -> Annotations.findPropertyAnnotation(
                    context.getBeanPropertyDefinition().get(),
                    ApiModelProperty.class));
        }

        //没有@ApiModelProperty 或者 notes 属性没有值，直接返回
        if (optional.isEmpty() || StrUtil.isEmpty(optional.get().notes())) {
            return;
        }

        //@ApiModelProperties中的notes指定的class类型
        Class rawPrimaryType;
        try {
            rawPrimaryType = Class.forName((optional.get()).notes());
        } catch (ClassNotFoundException e) {
            //如果指定的类型无法转化，直接忽略
            return;
        }

        Object[] subItemRecords = null;
        SwaggerDisplayEnum swaggerDisplayEnum = AnnotationUtils
                .findAnnotation(rawPrimaryType, SwaggerDisplayEnum.class);
        // 判断是否存在 @SwaggerDisplayEnum 注解，并且 rawPrimaryType 是枚举
        if (null != swaggerDisplayEnum && Enum.class.isAssignableFrom(rawPrimaryType)) {
            // 拿到枚举的所有的值
            subItemRecords = rawPrimaryType.getEnumConstants();
        }
        if (null == subItemRecords) {
            return;
        }

        final List<String> displayValues =
                Arrays.stream(subItemRecords)
                        .filter(Objects::nonNull)
                        // 调用枚举类的 toString 方法
                        .map(Object::toString)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        String joinText = " (" + String.join(";", displayValues) + ")";
        try {
            // 拿到字段上原先的描述
            Field mField = context.getSpecificationBuilder().getClass().getDeclaredField("description");
            mField.setAccessible(true);
            // context 中的 builder 对象保存了字段的信息
            joinText = mField.get(context.getSpecificationBuilder()) + joinText;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        // 设置新的字段说明
        context.getSpecificationBuilder().description(joinText);
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }

}
