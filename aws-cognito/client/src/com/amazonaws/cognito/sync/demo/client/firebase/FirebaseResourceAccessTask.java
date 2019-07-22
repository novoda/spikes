/**
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0
 * <p>
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.cognito.sync.demo.client.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class FirebaseResourceAccessTask implements ObservableOnSubscribe<String> {

    @Override
    public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("test_reading");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myRef.removeEventListener(this);
                emitter.onNext(dataSnapshot.getValue().toString());
                emitter.onComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                DatabaseException databaseException = databaseError.toException();
                Log.e("FirebaseResourceAccess", "accessing resource failed!", databaseException);
                emitter.onError(databaseException);
            }
        });
    }
}
