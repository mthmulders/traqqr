package it.mulders.traqqr.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class GreetingBean implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(GreetingBean.class);

    public GreetingBean() {
        log.info("Creating greeting bean; id={}", hashCode());
    }

    public String getGreeting() {
        return "Hello World!";
    }
}
