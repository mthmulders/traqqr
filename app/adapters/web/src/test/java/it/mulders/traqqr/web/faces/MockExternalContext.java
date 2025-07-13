package it.mulders.traqqr.web.faces;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.Flash;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MockExternalContext extends ExternalContext {
    private final Flash flash = new MockFlash();

    @Override
    public void dispatch(String path) throws IOException {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public String encodeActionURL(String url) {
        return "";
    }

    @Override
    public String encodeNamespace(String name) {
        return "";
    }

    @Override
    public String encodeResourceURL(String url) {
        return "";
    }

    @Override
    public String encodeWebsocketURL(String url) {
        return "";
    }

    @Override
    public Map<String, Object> getApplicationMap() {
        return new HashMap<>();
    }

    @Override
    public String getAuthType() {
        return "";
    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public String getInitParameter(String name) {
        return "";
    }

    @Override
    public Map<String, String> getInitParameterMap() {
        return new HashMap<>();
    }

    @Override
    public String getRemoteUser() {
        return "";
    }

    @Override
    public Object getRequest() {
        return null;
    }

    @Override
    public String getRequestContextPath() {
        return "";
    }

    @Override
    public Map<String, Object> getRequestCookieMap() {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> getRequestHeaderMap() {
        return new HashMap<>();
    }

    @Override
    public Map<String, String[]> getRequestHeaderValuesMap() {
        return new HashMap<>();
    }

    @Override
    public Locale getRequestLocale() {
        return null;
    }

    @Override
    public Iterator<Locale> getRequestLocales() {
        return null;
    }

    @Override
    public Map<String, Object> getRequestMap() {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> getRequestParameterMap() {
        return new HashMap<>();
    }

    @Override
    public Iterator<String> getRequestParameterNames() {
        return null;
    }

    @Override
    public Map<String, String[]> getRequestParameterValuesMap() {
        return new HashMap<>();
    }

    @Override
    public String getRequestPathInfo() {
        return "";
    }

    @Override
    public String getRequestServletPath() {
        return "";
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return null;
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        return new HashSet<>();
    }

    @Override
    public Object getResponse() {
        return null;
    }

    @Override
    public Object getSession(boolean create) {
        return null;
    }

    @Override
    public Map<String, Object> getSessionMap() {
        return new HashMap<>();
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public void log(String message) {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public void log(String message, Throwable exception) {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public void redirect(String url) throws IOException {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public void release() {
        // Only used in tests, no implementation necessary.
    }

    @Override
    public Flash getFlash() {
        return flash;
    }
}
