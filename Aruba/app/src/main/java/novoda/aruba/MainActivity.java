package novoda.aruba;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.arubanetworks.meridian.editor.EditorKey;
import com.arubanetworks.meridian.maps.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating a key representing your app in the Meridian Editor
        EditorKey appKey = new EditorKey("5085468668573440");
        // Creating a key representing a map in the Meridian Editor
        EditorKey mapKey = EditorKey.forMap("5708115733494264", appKey.getId());
        // Creating a key representing a placemark
        EditorKey placemarkKey = EditorKey.forPlacemark("375634485771", mapKey);

        setUpMapView(appKey, mapKey);

    }

    private void setUpMapView(EditorKey appKey, EditorKey mapKey) {
        mapView = findViewById(R.id.map);
        mapView.setAppKey(appKey);
        mapView.setMapKey(mapKey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // you must always pass your activity's pause/resume events to the MapView to ensure resources are managed properly.
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
