package net.bonysoft.magicmirror;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.novoda.notils.caster.Views;
import com.novoda.notils.logger.simple.Log;

import net.bonysoft.magicmirror.facerecognition.CameraSourcePreview;
import net.bonysoft.magicmirror.facerecognition.FaceCameraSource;
import net.bonysoft.magicmirror.facerecognition.FaceDetectionUnavailableException;
import net.bonysoft.magicmirror.facerecognition.FaceExpression;
import net.bonysoft.magicmirror.facerecognition.FaceReactionSource;
import net.bonysoft.magicmirror.facerecognition.FaceStatusView;
import net.bonysoft.magicmirror.facerecognition.FaceTracker;
import net.bonysoft.magicmirror.facerecognition.KeyToFaceMappings;
import net.bonysoft.magicmirror.facerecognition.KeyboardFaceSource;
import net.bonysoft.magicmirror.facerecognition.LookingEyes;
import net.bonysoft.magicmirror.sfx.FacialExpressionEffects;
import net.bonysoft.magicmirror.sfx.GlowView;
import net.bonysoft.magicmirror.sfx.ParticlesLayout;
import net.bonysoft.magicmirror.sfx.SfxMappings;

public class FaceRecognitionActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 0;
    private final DeviceInformation deviceInformation = new DeviceInformation();

    private FaceReactionSource faceSource;
    private CameraSourcePreview preview;

    private SystemUIHider systemUIHider;
    private FaceStatusView faceStatus;
    private LookingEyes lookingEyes;
    private GlowView glowView;
    private ParticlesLayout particlesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        lookingEyes = (LookingEyes) findViewById(R.id.looking_eyes);
        faceStatus = (FaceStatusView) findViewById(R.id.status);
        preview = (CameraSourcePreview) findViewById(R.id.preview);
        glowView = Views.findById(this, R.id.glow_background);
        particlesView = Views.findById(this, R.id.particles);
        particlesView.initialise();

        systemUIHider = new SystemUIHider(findViewById(android.R.id.content));
        keepScreenOn();

        boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        if (!hasCamera) {
            Toast.makeText(this, R.string.no_camera_available_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (isUsingCamera()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST
                );
            } else {
                tryToCreateCameraSource();
            }
            displayErrorIfPlayServicesMissing();
        } else {
            createKeyboardSource();
        }
    }

    private boolean isUsingCamera() {
        return !deviceInformation.isEmulator();
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void createKeyboardSource() {
        KeyToFaceMappings mappings = KeyToFaceMappings.newInstance();
        faceSource = new KeyboardFaceSource(faceListener, mappings);
    }

    private void tryToCreateCameraSource() {
        try {
            faceSource = FaceCameraSource.createFrom(this, faceListener, preview);
        } catch (FaceDetectionUnavailableException e) {
            Toast.makeText(this, R.string.face_detection_not_available_error, Toast.LENGTH_LONG).show();
        }
    }

    private void displayErrorIfPlayServicesMissing() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, 101);
            dlg.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        systemUIHider.hideSystemUi();

        if (faceSourceHasBeenDefined()) {
            faceSource.start();
        }
    }

    private boolean faceSourceHasBeenDefined() {
        return faceSource != null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (isPermissionGranted(grantResults)) {
                tryToCreateCameraSource();
            } else {
                Log.e("User denied CAMERA permission");
                finish();
            }
        }
    }

    private boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
        lookingEyes.hide();
        systemUIHider.showSystemUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceSourceHasBeenDefined()) {
            faceSource.release();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (faceSource.onKeyDown(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (faceSource.onKeyUp(keyCode)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private final FaceTracker.FaceListener faceListener = new FaceTracker.FaceListener() {

        private final SfxMappings mappings = SfxMappings.newInstance();

        @Override
        public void onNewFace(final FaceExpression expression) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FacialExpressionEffects effects = mappings.forExpression(expression);
                    glowView.transitionToColor(effects.glowColorRes());
                    if (effects.hasParticle()) {
                        particlesView.startParticles(effects.getParticle());
                    } else {
                        particlesView.stopParticles();
                    }
                    if (expression.isMissing()) {
                        lookingEyes.show();
                        faceStatus.hide();
                    } else {
                        lookingEyes.hide();
                        faceStatus.setExpression(expression);
                        faceStatus.show();
                    }

                }
            });
        }
    };
}
