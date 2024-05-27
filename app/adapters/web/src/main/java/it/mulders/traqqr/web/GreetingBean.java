package it.mulders.traqqr.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

@Named
@SessionScoped
public class GreetingBean implements Serializable {
    private static final Logger logger = Logger.getLogger(GreetingBean.class.getName());

    public GreetingBean() {
        logger.info("Creating greeting bean with ID %d".formatted(hashCode()));
    }

    public String getGreeting() {
        return "Hello World!";
    }
}
