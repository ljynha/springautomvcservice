package com.ljy.configuration;

import com.ljy.Annotation.RegisterService;
import com.ljy.Registers.LjyRegisterService;
import com.ljy.Registers.ServiceSpecial;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.List;


import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;


public class LjyAutoConfiguration extends AbstractLjyAutoConfiguration implements BeanDefinitionRegistryPostProcessor {
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
    private static final Log logger = LogFactory.getLog(LjyAutoConfiguration.class);
   @Autowired
    LjyRegisterService ljyRegisterService;
    @Override
    void register() {
   ljyRegisterService.register();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            String [] bdnames=registry.getBeanDefinitionNames();
            for (String name: bdnames){
                AbstractBeanDefinition bd= (AnnotatedGenericBeanDefinition) registry.getBeanDefinition(name);
                if(checkhasRegisterService(bd,this.metadataReaderFactory)){
                    AnnotationMetadata annotationMetadata = ((AnnotatedGenericBeanDefinition) bd).getMetadata();
                    AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RegisterService.class.getName()));
                    registryservice(attributes,bd.getBeanClass(),registry);
                }

            }

    }

    private void registryservice(AnnotationAttributes attributes, Class<?> beanClass, BeanDefinitionRegistry registry) {
      String port=attributes.getString("port");
      String address=attributes.getString("address");
      registerservice(port,address,beanClass,registry);
    }

    private void registerservice(String port, String address, Class<?> beanClass, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(ServiceSpecial.class);
        builder.addPropertyValue("port",port)
        .addPropertyValue("address",address)
        .addPropertyValue("servicename",beanClass.getName());
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setBeanClassName(beanClass.getName());
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);

    }

    private boolean checkhasRegisterService(BeanDefinition beanDef, MetadataReaderFactory metadataReaderFactory) {
        String className = beanDef.getBeanClassName();
        if (className == null || beanDef.getFactoryMethodName() != null) {
            return false;
        }

        AnnotationMetadata metadata;
        if (beanDef instanceof AnnotatedBeanDefinition &&
                className.equals(((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName())) {
            // Can reuse the pre-parsed metadata from the given BeanDefinition...
            //拿出注解信息,  metadata包含类的注解，和类型信息
            metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
        } else if (beanDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) beanDef).hasBeanClass()) {
            // Check already loaded Class if present...
            // since we possibly can't even load the class file for this Class.
            Class<?> beanClass = ((AbstractBeanDefinition) beanDef).getBeanClass();
            metadata = new StandardAnnotationMetadata(beanClass, true);
        } else {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
                metadata = metadataReader.getAnnotationMetadata();
            } catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not find class file for introspecting configuration annotations: " +
                            className, ex);
                }
                  return  false;
            }

        }
        if(isRegisterServiceConfiguration(metadata)){
           return true;
        }
return  false;
    }

    private boolean isRegisterServiceConfiguration(AnnotationMetadata metadata) {
        if (metadata.isInterface()) {
            return false;
        }
        return metadata.hasAnnotation(RegisterService.class.getName());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
          //this.ljyRegisterService.getInstances()
        List<ServiceSpecial> lists= (List<ServiceSpecial>) beanFactory.getBean(ServiceSpecial.class);
        this.ljyRegisterService.setInstances(lists);
    }
}
