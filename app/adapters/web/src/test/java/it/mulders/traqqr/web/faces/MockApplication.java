package it.mulders.traqqr.web.faces;

import jakarta.faces.FacesException;
import jakarta.faces.application.Application;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.application.StateManager;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIComponent;
import jakarta.faces.convert.Converter;
import jakarta.faces.event.ActionListener;
import jakarta.faces.validator.Validator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

public class MockApplication extends Application {
    @Override
    public ActionListener getActionListener() {
        return null;
    }

    @Override
    public void setActionListener(ActionListener listener) {}

    @Override
    public Locale getDefaultLocale() {
        return null;
    }

    @Override
    public void setDefaultLocale(Locale locale) {}

    @Override
    public String getDefaultRenderKitId() {
        return "";
    }

    @Override
    public void setDefaultRenderKitId(String renderKitId) {}

    @Override
    public String getMessageBundle() {
        return "";
    }

    @Override
    public void setMessageBundle(String bundle) {}

    @Override
    public NavigationHandler getNavigationHandler() {
        return null;
    }

    @Override
    public void setNavigationHandler(NavigationHandler handler) {}

    @Override
    public ViewHandler getViewHandler() {
        return null;
    }

    @Override
    public void setViewHandler(ViewHandler handler) {}

    @Override
    public StateManager getStateManager() {
        return null;
    }

    @Override
    public void setStateManager(StateManager manager) {}

    @Override
    public void addComponent(String componentType, String componentClass) {}

    @Override
    public UIComponent createComponent(String componentType) throws FacesException {
        return null;
    }

    @Override
    public Iterator<String> getComponentTypes() {
        return null;
    }

    @Override
    public void addConverter(String converterId, String converterClass) {}

    @Override
    public void addConverter(Class<?> targetClass, String converterClass) {}

    @Override
    public Converter createConverter(String converterId) {
        return null;
    }

    @Override
    public Converter createConverter(Class<?> targetClass) {
        return null;
    }

    @Override
    public Iterator<String> getConverterIds() {
        return null;
    }

    @Override
    public Iterator<Class<?>> getConverterTypes() {
        return null;
    }

    @Override
    public Iterator<Locale> getSupportedLocales() {
        return null;
    }

    @Override
    public void setSupportedLocales(Collection<Locale> locales) {}

    @Override
    public void addValidator(String validatorId, String validatorClass) {}

    @Override
    public Validator createValidator(String validatorId) throws FacesException {
        return null;
    }

    @Override
    public Iterator<String> getValidatorIds() {
        return null;
    }
}
