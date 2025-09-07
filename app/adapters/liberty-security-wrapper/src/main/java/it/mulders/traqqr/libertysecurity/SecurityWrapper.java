package it.mulders.traqqr.libertysecurity;

import java.security.PrivilegedAction;

public interface SecurityWrapper {
    <T> T execute(final PrivilegedAction<T> action);
}
