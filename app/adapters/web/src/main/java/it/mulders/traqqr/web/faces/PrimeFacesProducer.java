package it.mulders.traqqr.web.faces;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import org.primefaces.PrimeFaces;

/**
 * A producer method to provide {@link jakarta.faces.context.FacesContext} instances. This should make it easier to test code that depends on that class.
 */
@ApplicationScoped
public class PrimeFacesProducer {
    @RequestScoped
    @Produces
    public PrimeFaces getPrimeFaces() {
        return PrimeFaces.current();
    }
}
