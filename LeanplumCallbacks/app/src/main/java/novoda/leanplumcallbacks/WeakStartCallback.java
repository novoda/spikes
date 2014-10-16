package novoda.leanplumcallbacks;

import com.leanplum.callbacks.VariablesChangedCallback;

import java.lang.ref.WeakReference;

class WeakStartCallback extends VariablesChangedCallback {

    private final WeakReference<AbTestingParamsLoadedCallback> weakCallback;

    /**
     * calls onParamsLoaded() if the referenced class (most likely Activity) is still valid
     */
    public static WeakStartCallback newInstance(AbTestingParamsLoadedCallback callback) {
        return new WeakStartCallback(new WeakReference<AbTestingParamsLoadedCallback>(callback));
    }

    WeakStartCallback(WeakReference<AbTestingParamsLoadedCallback> weakCallback) {
        this.weakCallback = weakCallback;
    }

    @Override
    public void variablesChanged() {
        AbTestingParamsLoadedCallback callback = weakCallback.get();
        if (callback != null) {
            callback.onParamsLoaded();
        }
    }
}
