package com.ljy.common;

import com.ljy.Registers.LjyRegisterService;
import com.ljy.configuration.LjyAutoConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

public class LjyImportRegisterclass implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(LjyAutoConfiguration.class);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
        builder=rootBeanDefinition(LjyRegisterService.class);
        AbstractBeanDefinition beanDefinition1 = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition1, registry);
    }
}