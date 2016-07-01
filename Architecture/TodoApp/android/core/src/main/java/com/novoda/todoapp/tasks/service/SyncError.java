package com.novoda.todoapp.tasks.service;

public class SyncError extends Throwable {

    public SyncError() {
        super("Sync to server Failed");
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }

}
