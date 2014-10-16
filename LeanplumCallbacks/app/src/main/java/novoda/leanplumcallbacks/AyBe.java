package novoda.leanplumcallbacks;

import com.leanplum.Leanplum;
import com.leanplum.annotations.Variable;

public class AyBe {
    @Variable(group = "Test", name = "Var1")
    public static int abTesting = 10;

    public int getAbTesting() {
        return abTesting;
    }

    public void start(AbTestingParamsLoadedCallback callback) {
        Leanplum.addVariablesChangedHandler(WeakStartCallback.newInstance(callback));
    }
}
