package it.mulders.traqqr.web.event;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.NotificationOptions;
import jakarta.enterprise.util.TypeLiteral;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class DummyEvent<T> implements Event<T> {
    private final List<T> firedEvents = new ArrayList<>();

    @Override
    public void fire(T event) {
        firedEvents.add(event);
    }

    @Override
    public <U extends T> CompletionStage<U> fireAsync(U event) {
        return null;
    }

    @Override
    public <U extends T> CompletionStage<U> fireAsync(U event, NotificationOptions options) {
        return null;
    }


    @Override
    public Event<T> select(Annotation... qualifiers) {
        return null;
    }

    @Override
    public <U extends T> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        return null;
    }

    @Override
    public <U extends T> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
        return null;
    }

    public List<T> getFiredEvents() {
        return firedEvents;
    }
}
