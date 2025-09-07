package it.mulders.traqqr.batch.scheduling;

import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import java.lang.reflect.Method;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BeanMethodInvocationRunnableTest implements WithAssertions {
    @Test
    void should_initialize_bean_and_invoke_method() throws NoSuchMethodException {
        // Arrange
        var performActionMethod = DummyBean.class.getMethod("performAction");

        try (var container = SeContainerInitializer.newInstance()
                .disableDiscovery()
                .addBeanClasses(DummyBean.class)
                .initialize()) {
            var beanManager = container.getBeanManager();

            var beans = beanManager.getBeans(DummyBean.class);
            var dummyBeanDefinition = beans.iterator().next();

            // Act
            var runnable =
                    new BeanMethodInvocationRunnableForTest(dummyBeanDefinition, beanManager, performActionMethod);
            runnable.run();

            // Assert
            var dummyBean = runnable.getBeanInstance();
            assertThat(dummyBean).isNotNull().isInstanceOf(DummyBean.class);
            assertThat(dummyBean.getInvocationCount()).isEqualTo(1);
        }
    }

    @Test
    void should_not_initialize_bean_twice() throws NoSuchMethodException {
        // Arrange
        var performActionMethod = DummyBean.class.getMethod("performAction");

        try (var container = SeContainerInitializer.newInstance()
                .disableDiscovery()
                .addBeanClasses(DummyBean.class)
                .initialize()) {
            var beanManager = container.getBeanManager();

            var beans = beanManager.getBeans(DummyBean.class);
            var dummyBeanDefinition = beans.iterator().next();

            // Act
            var runnable =
                    new BeanMethodInvocationRunnableForTest(dummyBeanDefinition, beanManager, performActionMethod);
            runnable.run();
            runnable.run();

            // Assert
            var dummyBean = runnable.getBeanInstance();
            assertThat(dummyBean).isNotNull().isInstanceOf(DummyBean.class);
            assertThat(dummyBean.getInvocationCount()).isEqualTo(2);
        }
    }

    static class BeanMethodInvocationRunnableForTest extends BeanMethodInvocationRunnable {
        public BeanMethodInvocationRunnableForTest(Bean<?> bean, BeanManager beanManager, Method method) {
            super(bean, beanManager, method);
        }

        public DummyBean getBeanInstance() {
            return (DummyBean) this.beanInstance;
        }
    }
}
