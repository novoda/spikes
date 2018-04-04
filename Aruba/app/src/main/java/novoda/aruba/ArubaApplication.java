package novoda.aruba;

import android.app.Application;

import com.arubanetworks.meridian.Meridian;

public class ArubaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Meridian.configure(this);
    }

}
