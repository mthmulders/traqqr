package it.mulders.traqqr.web.faces;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MockFlash extends Flash {
    private final Map<String, Object> delegate = new HashMap<>();

    @Override
    public boolean isKeepMessages() {
        return false;
    }

    @Override
    public void setKeepMessages(boolean newValue) {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public boolean isRedirect() {
        return false;
    }

    @Override
    public void setRedirect(boolean newValue) {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public void putNow(String key, Object value) {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public void keep(String key) {}

    @Override
    public void doPrePhaseActions(FacesContext ctx) {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public void doPostPhaseActions(FacesContext ctx) {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return delegate.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return delegate.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<Object> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return delegate.entrySet();
    }
}
