package it.mulders.traqqr.web.prime;

import it.mulders.traqqr.web.faces.FacesContextMock;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DialogFrameworkOptions;

public class PrimeFacesMock extends PrimeFaces {
    public PrimeFacesMock() {
        setCurrent(this);
    }

    @Override
    protected FacesContext getFacesContext() {
        return new FacesContextMock();
    }

    @Override
    public Dialog dialog() {
        return new Dialog() {
            @Override
            public void openDynamic(String outcome) {}

            @Override
            public void openDynamic(String outcome, DialogFrameworkOptions options, Map<String, List<String>> params) {}

            @Override
            public void openDynamic(String outcome, Map<String, Object> options, Map<String, List<String>> params) {}

            @Override
            public void closeDynamic(Object data) {}

            @Override
            public void showMessageDynamic(FacesMessage message) {}

            @Override
            public void showMessageDynamic(FacesMessage message, boolean escape) {}
        };
    }

    @Override
    public Ajax ajax() {
        return new Ajax() {
            @Override
            public void addCallbackParam(String name, Object value) {}

            @Override
            public void update(Collection<String> expressions) {}

            @Override
            public void update(String... expressions) {}

            @Override
            public void update(UIComponent... components) {}

            @Override
            public void ignoreAutoUpdate() {}
        };
    }

    @Override
    public MultiViewState multiViewState() {
        return new MultiViewState() {
            @Override
            public void clearAll() {}

            @Override
            public void clearAll(boolean reset) {}

            @Override
            public void clearAll(boolean reset, Consumer<String> clientIdConsumer) {}

            @Override
            public void clearAll(String viewId, boolean reset) {}

            @Override
            public void clearAll(String viewId, boolean reset, Consumer<String> clientIdConsumer) {}

            @Override
            public void clear(String viewId, String clientId) {}

            @Override
            public void clear(String viewId, String clientId, boolean reset) {}

            @Override
            public <T> T get(String viewId, String clientId, boolean create, Supplier<T> supplier) {
                return null;
            }

            @Override
            public <T> T get(String viewId, String clientId, Supplier<T> supplier) {
                return null;
            }

            @Override
            public <T> T get(String viewId, String clientId) {
                return null;
            }
        };
    }
}
