package it.mulders.traqqr.domain.shared.spi;

import java.security.PrivilegedAction;

/**
 * Service Providing Interface for components that allow to perform a piece of code using different security
 * credentials than those of the current user.
 */
public interface SecurityWrapper {
    <T> T execute(final PrivilegedAction<T> action);
}
