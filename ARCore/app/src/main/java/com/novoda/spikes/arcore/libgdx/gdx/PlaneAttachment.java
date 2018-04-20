/*
Copyright 2017 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.novoda.spikes.arcore.libgdx.gdx;

import com.google.ar.core.Anchor;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;

/**
 * Attaches an object of type T to a plane using an anchor. This associates the object both with a
 * detectec plane and an ARCore anchor in space. This keeps the object oriented consistently with
 * the anchor and the plane.
 */
public class PlaneAttachment<T> {
  private final Plane plane;
  private final Anchor anchor;
  private final T data;

  // Allocate temporary storage to avoid multiple allocations per frame.
  private final float[] mPoseTranslation = new float[3];
  private final float[] mPoseRotation = new float[4];

  public PlaneAttachment(Plane plane, Anchor anchor, T data) {
    this.plane = plane;
    this.anchor = anchor;
    this.data = data;
  }

  public boolean isTracking() {
    return /*true if*/ plane.getTrackingState() == TrackingState.TRACKING
        && anchor.getTrackingState() == TrackingState.TRACKING;
  }

  public Pose getPose() {
    Pose pose = anchor.getPose();
    pose.getTranslation(mPoseTranslation, 0);
    pose.getRotationQuaternion(mPoseRotation, 0);
    mPoseTranslation[1] = plane.getCenterPose().ty();
    return new Pose(mPoseTranslation, mPoseRotation);
  }

  public Anchor getAnchor() {
    return anchor;
  }

  public T getData() {
    return data;
  }
}
