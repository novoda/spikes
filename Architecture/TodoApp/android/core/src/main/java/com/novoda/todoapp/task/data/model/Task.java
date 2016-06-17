package com.novoda.todoapp.task.data.model;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

@AutoValue
public abstract class Task {

    public static Builder builder() {
        return new AutoValue_Task.Builder()
                .title(Optional.<String>absent())
                .description(Optional.<String>absent())
                .isCompleted(false);
    }

    public abstract Id id();

    public abstract Optional<String> title();

    public abstract Optional<String> description();

    public abstract boolean isCompleted();

    public abstract Builder toBuilder();

    public Task complete() {
        return toBuilder()
                .isCompleted(true)
                .build();
    }

    public Task activate() {
        return toBuilder()
                .isCompleted(false)
                .build();
    }

    public Optional<String> titleOrDescription() {
        return title().or(description());
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(Id id);

        public abstract Builder isCompleted(boolean isCompleted);

        public Builder title(String title) {
            Preconditions.checkState(!title.isEmpty(), "An empty title is not valid, please use Optional.absent()");
            return title(Optional.of(title));
        }

        public Builder description(String description) {
            Preconditions.checkState(!description.isEmpty(), "An empty description is not valid, please use Optional.absent()");
            return description(Optional.of(description));
        }

        public abstract Builder title(Optional<String> title);

        public abstract Builder description(Optional<String> description);

        public Task build() {
            Task task = autoBuild();
            Preconditions.checkState(
                    task.title().or(task.description()).isPresent(),
                    "Please provide one of (Title, Description)"
            );
            return task;
        }

        abstract Task autoBuild();

    }

}
