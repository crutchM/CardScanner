package org.example.CardScanner;

import java.io.FileNotFoundException;

public interface CardStatusListener {

    void cardIsAttached(String id) throws FileNotFoundException;

    void cardIsRemoved(String id);
}

