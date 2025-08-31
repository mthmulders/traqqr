package it.mulders.traqqr.batch.scheduling;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the invocation of a method, including a first-time instantiation of the bean class that holds the method.
 */
class BeanMethodInvocationRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BeanMethodInvocationRunnable.class);

    private final Bean<?> bean;
    private final BeanManager beanManager;
    private final Method method;

    private Object beanInstance = null;

    public BeanMethodInvocationRunnable(Bean<?> bean, BeanManager beanManager, Method method) {
        this.bean = bean;
        this.beanManager = beanManager;
        this.method = method;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (beanInstance == null) {
                var creationalContext = beanManager.createCreationalContext(bean);
                beanInstance = beanManager.getReference(bean, bean.getBeanClass(), creationalContext);
            }
        }

        try {
            method.invoke(beanInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Failed to invoke method", e);
        }
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
