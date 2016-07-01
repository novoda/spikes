package com.novoda.todoapp.task.newtask.presenter;

import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.presenter.IdProducer;

import java.util.UUID;

public class UUIDGenerator implements IdProducer {
    public Id produce() {
        return Id.from(UUID.randomUUID().toString());
    }
}
