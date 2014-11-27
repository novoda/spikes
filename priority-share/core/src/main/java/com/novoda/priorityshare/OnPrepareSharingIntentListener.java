package com.novoda.priorityshare;

import android.content.Intent;

public interface OnPrepareSharingIntentListener {

    /**
     * Called before an intent is sent out to a priority sharing app.
     * This allows to tweak the contents of the Intent before it's fired.
     * <p/>
     * For example, a listener implementation could enforce length limits
     * for a sharing message if a certain target app is selected.
     * <p/>
     * The intent will contain the following informations:
     * <table>
     * <tr><td></td><td><b>Where it's stored</b></td></tr>
     * <tr><td>Target package</td><td><code>intent.getComponent().getPackageName()</code></td></tr>
     * <tr><td>Subject</td><td><code>intent.getCharSequenceExtra(Intent.EXTRA_SUBJECT)</code></td></tr>
     * <tr><td>Text</td><td><code>intent.getCharSequenceExtra(Intent.EXTRA_TEXT)</code></td></tr>
     * <tr><td>URI</td><td><code>intent.getDataUri()</code></td></tr>
     * </table>
     * <p/>
     * <b>NOTE:</b> do <i>NOT</i> change the URI or the target packages. Only change the Subject
     * and/or Text fields, as those are the ones the user would be allowed to edit anyway.
     *
     * @param intent the intent that is about to be sent.
     * @return the intent after any transformation.
     */
    Intent onPrepareSharingIntent(Intent intent);

}
