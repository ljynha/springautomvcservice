package com.ljy.Event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

public class RegisterEvent extends ApplicationContextEvent {
    /**
     * Create a new ContextStartedEvent.
     *
     * @param source the {@code ApplicationContext} that the event is raised for
     *               (must not be {@code null})
     */
    public RegisterEvent(ApplicationContext source) {
        super(source);
    }
}
