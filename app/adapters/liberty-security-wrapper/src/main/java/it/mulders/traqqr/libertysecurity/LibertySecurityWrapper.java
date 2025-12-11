package it.mulders.traqqr.libertysecurity;

import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.websphere.security.auth.callback.WSCallbackHandlerImpl;
import it.mulders.traqqr.domain.shared.spi.SecurityWrapper;
import java.security.PrivilegedAction;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibertySecurityWrapper implements SecurityWrapper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String username;
    private final CallbackHandler callbackHandler;

    public LibertySecurityWrapper(String username, String password) {
        this.username = username;
        this.callbackHandler = new WSCallbackHandlerImpl(username, password);
    }

    public <T> T execute(final PrivilegedAction<T> action) {
        var ctx = createLoginContext();
        try {
            ctx.login();
        } catch (LoginException le) {
            logger.error("Programmatic login failed; username={}", username, le);
            throw new RuntimeException("Programmatic login failed", le);
        }

        var subject = ctx.getSubject();

        return (T) WSSubject.doAs(subject, action);
    }

    private LoginContext createLoginContext() {
        try {
            return new LoginContext("ClientContainer", callbackHandler);
        } catch (LoginException le) {
            logger.error("Could not create login context; username={}", username, le);
            throw new RuntimeException("Could not create login context", le);
        }
    }
}
