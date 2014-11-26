package novoda.android.typewriter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.List;

import novoda.android.typewriter.cursor.CursorList;

public class TypedActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        long now = System.currentTimeMillis();
        while (cursor.moveToNext()) {
            Log.i("TEST", cursor.getString(cursor.getColumnIndexOrThrow("display_name")));
        }
        Log.i("TEST", "NORMAL: " + (System.currentTimeMillis() - now));

        cursor.moveToFirst();

        List<Contact2> typedCursor = new CursorList<Contact2>(cursor, Contact2.class);
        now = System.currentTimeMillis();
        for (Contact2 c : typedCursor) {
            Log.i("TEST", "second " + c.display_name + " " + c.last_time_contacted);
        }
        Log.i("TEST", "better: " + (System.currentTimeMillis() - now));

    }

    public static class Contact2 {
        public Contact2() {
        }

        public String last_time_contacted;
        public String display_name;

    }
}
