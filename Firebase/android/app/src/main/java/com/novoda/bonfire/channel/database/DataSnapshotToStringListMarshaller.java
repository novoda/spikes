package com.novoda.bonfire.channel.database;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

class DataSnapshotToStringListMarshaller implements Func1<DataSnapshot, List<String>> {
    @Override
    public List<String> call(DataSnapshot dataSnapshot) {
        List<String> keys = new ArrayList<>();
        if (dataSnapshot.hasChildren()) {
            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
            for (DataSnapshot child : children) {
                keys.add(child.getKey());
            }
        }
        return keys;
    }
}
