package com.novoda.todoapp.statistics.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Statistics {

    public static Builder builder() {
        return new AutoValue_Statistics.Builder()
                .activeTasks(0)
                .completedTasks(0);
    }

    public abstract int activeTasks();

    public abstract int completedTasks();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder activeTasks(int activeTasks);

        public abstract Builder completedTasks(int completedTasks);

        public abstract Statistics build();

    }

}
