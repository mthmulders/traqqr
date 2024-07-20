package it.mulders.traqqr.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("domainBean")
public class DomainBean {
    public String getWhatever() {
        return "World";
    }
}
