package novoda.android.typewriter.loader;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import novoda.android.typewriter.content.TypedResolver;

public class TypedLoader<T> extends AsyncTaskLoader<List<T>> {

    private final Class<T> what;
    Uri uri;
    String[] projection;
    String selection;
    String[] selectionArgs;
    String sortOrder;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String[] getProjection() {
        return projection;
    }

    public void setProjection(String[] projection) {
        this.projection = projection;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public TypedLoader(Context context, Class<T> what, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context);
        this.what = what;
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    public TypedLoader(Context context, Class<T> what) {
        super(context);
        this.what = what;
    }

    @Override
    public List<T> loadInBackground() {
        return new TypedResolver(getContext().getContentResolver()).query(uri, projection, selection, selectionArgs, sortOrder, what);
    }
}
