package novoda.android.typewriter;

public interface Marshaller<T, W> {
    T marshall(W from, Class<T> as);
}
