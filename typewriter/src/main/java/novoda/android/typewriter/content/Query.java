package novoda.android.typewriter.content;

import android.net.Uri;

public class Query {
    final Uri uri;

    String selection, sortOrder;
    String[] selectionArgs;

    public Query(Uri uri) {
        this.uri = uri;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public Uri getUri() {
        return uri;
    }
}
