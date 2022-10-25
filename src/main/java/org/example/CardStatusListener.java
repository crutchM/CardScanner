package org.example;

public interface CardStatusListener {

    void cardIsAttached(String id);

    void cardIsRemoved(String id);
}

