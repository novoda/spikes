package com.novoda.todoapp.task.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Id implements Comparable<Id> {

    public static Id from(String value) {
        return new AutoValue_Id(value);
    }

    Id() {
        // AutoValue best practices https://github.com/google/auto/blob/master/value/userguide/practices.md
    }

    public abstract String value();

    @Override
    public String toString() {
        return "Id=" + value();
    }

    @Override
    public int compareTo(Id o) {
        return value().compareTo(o.value());
    }

}
