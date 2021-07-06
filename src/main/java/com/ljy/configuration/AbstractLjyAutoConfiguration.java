package com.ljy.configuration;

import com.ljy.Event.RegisterEndEvent;
import com.ljy.Event.RegisterEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public abstract class AbstractLjyAutoConfiguration implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
ApplicationContext context;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bind();
    }

    private void bind() {
        context.publishEvent(new RegisterEvent(context));
        register();
        context.publishEvent(new RegisterEndEvent(context));
    }


 abstract void register() ;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context=applicationContext;
    }
}
