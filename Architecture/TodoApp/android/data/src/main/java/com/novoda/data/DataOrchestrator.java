package com.novoda.data;

public interface DataOrchestrator<T,V> {

    V startWith();

    V onConfirmed(T value);

    V onError();

}
