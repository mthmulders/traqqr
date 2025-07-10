package it.mulders.traqqr.web.faces;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.FacesListener;
import jakarta.faces.render.Renderer;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DummyUIComponent extends UIComponent {
    private final String clientId;

    public DummyUIComponent(final String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public String getClientId(FacesContext context) {
        return clientId;
    }

    @Override
    public String getFamily() {
        return "";
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void setId(String id) {}

    @Override
    public UIComponent getParent() {
        return null;
    }

    @Override
    public void setParent(UIComponent parent) {}

    @Override
    public boolean isRendered() {
        return false;
    }

    @Override
    public void setRendered(boolean rendered) {}

    @Override
    public String getRendererType() {
        return "";
    }

    @Override
    public void setRendererType(String rendererType) {}

    @Override
    public boolean getRendersChildren() {
        return false;
    }

    @Override
    public List<UIComponent> getChildren() {
        return List.of();
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public UIComponent findComponent(String expr) {
        return null;
    }

    @Override
    public Map<String, UIComponent> getFacets() {
        return Map.of();
    }

    @Override
    public UIComponent getFacet(String name) {
        return null;
    }

    @Override
    public Iterator<UIComponent> getFacetsAndChildren() {
        return null;
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {}

    @Override
    public void decode(FacesContext context) {}

    @Override
    public void encodeBegin(FacesContext context) throws IOException {}

    @Override
    public void encodeChildren(FacesContext context) throws IOException {}

    @Override
    public void encodeEnd(FacesContext context) throws IOException {}

    @Override
    protected void addFacesListener(FacesListener listener) {}

    @Override
    protected FacesListener[] getFacesListeners(Class clazz) {
        return new FacesListener[0];
    }

    @Override
    protected void removeFacesListener(FacesListener listener) {}

    @Override
    public void queueEvent(FacesEvent event) {}

    @Override
    public void processRestoreState(FacesContext context, Object state) {}

    @Override
    public void processDecodes(FacesContext context) {}

    @Override
    public void processValidators(FacesContext context) {}

    @Override
    public void processUpdates(FacesContext context) {}

    @Override
    public Object processSaveState(FacesContext context) {
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return null;
    }

    @Override
    protected Renderer getRenderer(FacesContext context) {
        return null;
    }

    @Override
    public Object saveState(FacesContext context) {
        return null;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {}

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean newTransientValue) {}
}
