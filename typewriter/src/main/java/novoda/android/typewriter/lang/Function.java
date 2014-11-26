package novoda.android.typewriter.lang;

public interface Function<F, T> {
    T apply(F input);
}