package com.mindcoders.phial.keyvalue;

/**
 * Created by rost on 10/22/17.
 */

public class KVEntry {
    private final String category;
    private final String name;
    private final String value;

    public KVEntry(String category, String name, String value) {
        this.name = name;
        this.value = value;
        this.category = category;
    }
}