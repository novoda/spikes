package com.novoda.todoapp.task.newtask.presenter;

import com.novoda.todoapp.task.data.model.Id;

import java.util.UUID;

public class IdGenerator {
    public Id generate() {
        return Id.from(UUID.randomUUID().toString());
    }
}
