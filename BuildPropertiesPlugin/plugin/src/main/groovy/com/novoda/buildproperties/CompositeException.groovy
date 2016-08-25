package com.novoda.buildproperties

class CompositeException extends Exception {

    public static final CompositeException EMPTY = new CompositeException(Collections.emptyList())

    private final List<Throwable> exceptions

    static CompositeException from(Throwable throwable) {
        return EMPTY.add(throwable)
    }

    private CompositeException(List<Throwable> exceptions) {
        this.exceptions = Collections.unmodifiableList(exceptions)
    }

    CompositeException add(Throwable throwable) {
        List<Throwable> newExceptions = new ArrayList<>(exceptions)
        if (throwable instanceof CompositeException) {
            newExceptions.addAll(((CompositeException) throwable).exceptions)
        } else {
            newExceptions.add(throwable)
        }
        return new CompositeException(newExceptions)
    }

    List<Throwable> getExceptions() {
        exceptions
    }

    @Override
    String getMessage() {
        return exceptions
                .collect { it.message }
                .inject('A problem occurred while evaluating entry:', { acc, val -> acc + "\n- $val" })
    }

}
