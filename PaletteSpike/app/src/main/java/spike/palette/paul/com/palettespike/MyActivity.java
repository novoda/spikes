package spike.palette.paul.com.palettespike;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


public class MyActivity extends Activity {

    private static final int RESULT_LOAD_IMAGE = 13370;
    private ImageView imageView;
    private TextView textViewPrimaryAccent;
    private TextView textViewSecondaryAccent;
    private TextView textViewTertiaryAccent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        imageView = (ImageView) findViewById(R.id.image_view);
        textViewPrimaryAccent = (TextView) findViewById(R.id.text_view_primary_accent);
        textViewSecondaryAccent = (TextView) findViewById(R.id.text_view_secondary_accent);
        textViewTertiaryAccent = (TextView) findViewById(R.id.text_view_tertiary_accent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_get_colors) {

            try {
                getColors();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (id == R.id.action_set_picture) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select a picture"), RESULT_LOAD_IMAGE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getColors() throws IOException {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        DominantColorCalculator dominantColorCalculator = new DominantColorCalculator(bitmap);

        textViewPrimaryAccent.setBackgroundColor(dominantColorCalculator.getColorScheme().primaryAccent);
        textViewSecondaryAccent.setBackgroundColor(dominantColorCalculator.getColorScheme().secondaryAccent);
        textViewTertiaryAccent.setBackgroundColor(dominantColorCalculator.getColorScheme().tertiaryAccent);
        textViewPrimaryAccent.setTextColor(dominantColorCalculator.getColorScheme().primaryText);
        textViewSecondaryAccent.setTextColor(dominantColorCalculator.getColorScheme().secondaryText);
        textViewTertiaryAccent.setTextColor(dominantColorCalculator.getColorScheme().secondaryText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            imageView.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        }
    }

    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }
}
