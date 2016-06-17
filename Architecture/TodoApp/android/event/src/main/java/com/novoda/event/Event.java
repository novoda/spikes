package com.novoda.event;

import com.google.auto.value.AutoValue;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

@AutoValue
public abstract class Event<T> {

    public static <T> Event<T> idle() {
        return Event.<T>builder()
                .state(Status.IDLE)
                .build();
    }

    public static <T> Event<T> idle(Predicate<T> dataValidator) {
        return Event.<T>builder()
                .state(Status.IDLE)
                .dataValidator(dataValidator)
                .build();
    }

    public static <T> Event<T> loading() {
        return Event.<T>builder()
                .state(Status.LOADING)
                .build();
    }

    public static <T> Event<T> loading(Predicate<T> dataValidator) {
        return Event.<T>builder()
                .state(Status.LOADING)
                .dataValidator(dataValidator)
                .build();
    }

    public static <T> Event<T> error(Throwable throwable) {
        return Event.<T>builder()
                .state(Status.ERROR)
                .error(Optional.of(throwable))
                .build();
    }

    public static <T> Builder<T> builder() {
        return new AutoValue_Event.Builder<T>()
                .data(Optional.<T>absent())
                .error(Optional.<Throwable>absent())
                .dataValidator(Predicates.<T>alwaysTrue());
    }

    Event() {
        // AutoValue best practices https://github.com/google/auto/blob/master/value/userguide/practices.md
    }

    public abstract Status state();

    public abstract Optional<T> data();

    public abstract Optional<Throwable> error();

    protected abstract Predicate<T> dataValidator();

    public Optional<String> errorMessage() {
        return error().transform(
                new Function<Throwable, String>() {
                    @Override
                    public String apply(Throwable input) {
                        if (input.getMessage() == null) {
                            return "Something went wrong";
                        }
                        return input.getMessage();
                    }
                }
        );
    }

    public abstract Builder<T> toBuilder();

    public Event<T> asError(Throwable error) {
        return toBuilder()
                .state(Status.ERROR)
                .error(Optional.fromNullable(error))
                .build();
    }

    public Event<T> asIdle() {
        return toBuilder()
                .state(Status.IDLE)
                .error(Optional.<Throwable>absent())
                .build();
    }

    public Event<T> updateData(T value) {
        return toBuilder()
                .data(validateData(value))
                .build();
    }

    public Event<T> updateData(Optional<T> data) {
        return toBuilder()
                .data(data.transform(validateData()).or(Optional.<T>absent()))
                .build();
    }

    private Function<T, Optional<T>> validateData() {
        return new Function<T, Optional<T>>() {
            @Override
            public Optional<T> apply(T input) {
                return validateData(input);
            }
        };
    }

    public Event<T> removeData() {
        return toBuilder()
                .data(Optional.<T>absent())
                .build();
    }

    public Event<T> asLoading() {
        return toBuilder()
                .state(Status.LOADING)
                .error(Optional.<Throwable>absent())
                .build();
    }

    public Event<T> asLoadingWithData(T value) {
        return toBuilder()
                .state(Status.LOADING)
                .data(validateData(value))
                .error(Optional.<Throwable>absent())
                .build();
    }

    private Optional<T> validateData(T value) {
        if (dataValidator().apply(value)) {
            return Optional.of(value);
        } else {
            return Optional.absent();
        }
    }

    @AutoValue.Builder
    public abstract static class Builder<T> {

        public abstract Builder<T> state(Status state);

        public abstract Builder<T> data(Optional<T> data);

        public abstract Builder<T> error(Optional<Throwable> error);

        public abstract Builder<T> dataValidator(Predicate<T> dataValidator);

        public abstract Event<T> build();

    }

}
