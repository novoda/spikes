package com.novoda.wallpaper.droidcon;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.halfninja.wallpaper.parallax.gl.Capabilities;
import uk.co.halfninja.wallpaper.parallax.gl.Quad;
import uk.co.halfninja.wallpaper.parallax.gl.Texture;
import uk.co.halfninja.wallpaper.parallax.gl.TextureLoader;
import uk.co.halfninja.wallpaper.parallax.gl.Utils;

import static android.content.ContentValues.TAG;
import static javax.microedition.khronos.opengles.GL10.*;

class LondonParallaxWallpaperRenderer implements GLSurfaceView.Renderer {

    private static final String[] PORTRAIT_LAYERS_FILES_NAMES = {
            "phone_wallpaper_blue.png",
            "phone_wallpaper_green.png",
            "phone_wallpaper_sepia.png"
    };

    private static final String[] LANDSCAPE_LAYERS_FILES_NAMES = {
            "phone_wallpaper_blue.png",
            "phone_wallpaper_green.png",
            "phone_wallpaper_sepia.png"
    };

    private float offset = 0.0f;
    private int surfaceHeight;
    private int surfaceWidth;

    private final Capabilities capabilities = new Capabilities();
    private final TextureLoader textureLoader;
    private List<Quad> portraitLayers = new ArrayList<>(PORTRAIT_LAYERS_FILES_NAMES.length);
    private List<Quad> landscapeLayers = new ArrayList<>(LANDSCAPE_LAYERS_FILES_NAMES.length);
    private List<Quad> currentLayers = new ArrayList<>();

    private GL10 gl;

    LondonParallaxWallpaperRenderer(AssetManager assets) {
        this.textureLoader = new TextureLoader(capabilities, assets);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig cfg) {
        this.gl = gl;
        capabilities.reload(gl);

        try {
            reloadLayers();
        } catch (IOException e) {
            Log.e(TAG, "Error loading textures", e);
        }
    }

    void reloadLayers() throws IOException {
        if (gl != null && layersNotAlreadyLoaded()) {
            portraitLayers.clear();
            landscapeLayers.clear();
            textureLoader.clear(gl);
            for (String bitmapPath : LANDSCAPE_LAYERS_FILES_NAMES) {
                loadLayerTo(bitmapPath, landscapeLayers);
            }
            for (String bitmapPath : PORTRAIT_LAYERS_FILES_NAMES) {
                loadLayerTo(bitmapPath, portraitLayers);
            }
        }
    }

    private boolean layersNotAlreadyLoaded() {
        return portraitLayers.isEmpty() && landscapeLayers.isEmpty();
    }

    private void loadLayerTo(String bitmapPath, List<Quad> layerList) throws IOException {
        Quad quad = new Quad();
        Texture tex = textureLoader.loadTextureFromFile(gl, bitmapPath);
        quad.setTexture(tex);
        layerList.add(0, quad);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawLayers(gl);
    }

    private void drawLayers(GL10 gl) {
        for (Quad quad : currentLayers) {
            quad.setX(offset * (surfaceWidth - quad.getWidth()));
            quad.draw(gl);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        if (w == surfaceWidth && h == surfaceHeight) {
            return;
        }
        surfaceWidth = w;
        surfaceHeight = h;
        Utils.pixelProjection(gl, w, h);
        gl.glEnable(GL_TEXTURE_2D);
        gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        resizeLayers();
        setCurrentLayers();
    }

    void resizeLayers() {
        if (portraitLayers.isEmpty()) {
            return;
        }
        float portraitRatio = getPortraitRatio();
        resizeLayers(portraitRatio, portraitLayers);

        float landscapeRatio = getLandscapeRatio();
        resizeLayers(landscapeRatio, landscapeLayers);
    }

    private float getPortraitRatio() {
        int bitmapHeight = portraitLayers.get(0).getTexture().getBitmapHeight();
        return (float) surfaceHeight / bitmapHeight;
    }

    private float getLandscapeRatio() {
        int bitmapHeight = landscapeLayers.get(0).getTexture().getBitmapHeight();
        return (float) surfaceHeight / bitmapHeight;
    }

    private void resizeLayers(float landscapeRatio, List<Quad> layers) {
        for (Quad quad : layers) {
            resizeLayer(quad, landscapeRatio);
        }
    }

    private void resizeLayer(Quad quad, float ratio) {
        quad.setHeight(quad.getTexture().getBitmapHeight() * ratio);
        quad.setWidth(quad.getTexture().getBitmapWidth() * ratio);
    }

    private void setCurrentLayers() {
        currentLayers.clear();
        if (surfaceHeight > surfaceWidth) {
            currentLayers.addAll(portraitLayers);
        } else {
            currentLayers.addAll(landscapeLayers);
        }
    }

    void setOffset(float xOffset) {
        offset = xOffset;
    }

    void loadAssetsFor(TimeOfDay timeOfDay) {
        // TODO for a follow up PR
    }
}
