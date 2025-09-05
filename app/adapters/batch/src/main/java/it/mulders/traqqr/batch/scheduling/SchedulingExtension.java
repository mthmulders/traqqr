package it.mulders.traqqr.batch.scheduling;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessManagedBean;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A CDI extension that invokes {@link @Scheduled}-annotated methods according to the schedule that the annotation
 * specifies.
 * Heavily inspired by the (now unmaintained) <a href="https://github.com/mirkosertic/cdicron">CDICron</a> codebase,
 * but simplified for this project.
 */
public class SchedulingExtension implements Extension {
    private static final Logger logger = LoggerFactory.getLogger(SchedulingExtension.class);

    private Bean<Scheduler> schedulerBean;
    private final Set<BeanMethodInvocationWithSchedule> methodsToSchedule = new HashSet<>();

    public void onProcessBean(@Observes ProcessManagedBean<?> event, BeanManager beanManager) {
        var beanClass = event.getBean().getBeanClass();

        // See if this is the actual implementation of the scheduler
        if (Scheduler.class.isAssignableFrom(beanClass)) {
            registerSchedulerBeanImplementation(beanClass, event);
            return;
        }

        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Scheduled.class)) {
                registerScheduledMethod(method, event, beanManager);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void registerSchedulerBeanImplementation(Class<?> beanClass, ProcessManagedBean<?> event) {
        logger.info("Registering scheduler; implementation={}", beanClass.getName());
        schedulerBean = (Bean<Scheduler>) event.getBean();
    }

    private void registerScheduledMethod(Method method, ProcessManagedBean<?> event, BeanManager beanManager) {
        logger.info("Processing scheduled method; method={}", method);
        var scheduled = method.getAnnotation(Scheduled.class);
        var schedule = Schedule.fromScheduled(scheduled);
        var runnable = new BeanMethodInvocationRunnable(event.getBean(), beanManager, method);

        methodsToSchedule.add(new BeanMethodInvocationWithSchedule(runnable, schedule));
    }

    public void onAfterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        if (schedulerBean == null) {
            logger.error("No scheduler found");
            throw new IllegalStateException("No Scheduler implementation found");
        }

        // Instantiate the scheduler
        var scheduler = instantiateScheduler(beanManager);
        methodsToSchedule.forEach(it -> scheduler.schedule(it.schedule, it.runnable));
    }

    private Scheduler instantiateScheduler(BeanManager beanManager) {
        var creationalContext = beanManager.createCreationalContext(schedulerBean);
        try {
            return (Scheduler) beanManager.getReference(schedulerBean, schedulerBean.getBeanClass(), creationalContext);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new RuntimeException("Failed to instantiate scheduler", e);
        }
    }

    private record BeanMethodInvocationWithSchedule(BeanMethodInvocationRunnable runnable, Schedule schedule) {}
}
