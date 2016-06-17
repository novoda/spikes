package com.novoda.todoapp.task.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Id {

    public static Id from(String value) {
        return new AutoValue_Id(value);
    }

    Id() {
        // AutoValue best practices https://github.com/google/auto/blob/master/value/userguide/practices.md
    }

    public abstract String value();

}
