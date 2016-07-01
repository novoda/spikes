package com.novoda.todoapp.task.newtask.presenter;

import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.presenter.IdGenerator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {
    public Id generate() {
        return Id.from(UUID.randomUUID().toString());
    }
}
