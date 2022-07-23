
package com.lgm.facedetectionapp;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.camera2.CameraCharacteristics;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;
public class GraphicOverlay extends View {
    private final Object object= new Object();
    private int Width;
    private float sf = 1.0f;
    private int Height;
    private float ScaleFactor = 1.0f;
    private int facing = CameraCharacteristics.LENS_FACING_BACK;
    private Set<Graphic> graphic = new HashSet<>();
    public abstract static class Graphic {
        private GraphicOverlay overlay;

        public Graphic(GraphicOverlay overlay) {

            this.overlay = overlay;
        }
        public abstract void draw(Canvas canvas);
        public float scaleX(float horizontal) {
            return horizontal * overlay.sf;
        }
        public float scaleY(float vertical) {
            return vertical * overlay.ScaleFactor;
        }
        public Context getApplicationContext() {
            return overlay.getContext().getApplicationContext();
        }
        public float trX(float x) {
            if (overlay.facing == CameraCharacteristics.LENS_FACING_FRONT) {
                return overlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }
        public float trY(float y) {
            return scaleY(y);
        }

        public void postInvalidate() {
            overlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void clear() {
        synchronized (object) {
            graphic.clear();
        }
        postInvalidate();
    }
    public void add(Graphic graphic) {
        synchronized (object) {
            this.graphic.add(graphic);
        }
        postInvalidate();
    }
    public void remove(Graphic graphic) {
        synchronized (object) {
            this.graphic.remove(graphic);
        }
        postInvalidate();
    }
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (object) {
            this.Width = previewWidth;
            this.Height = previewHeight;
            this.facing = facing;
        }
        postInvalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (object) {
            if ((Width != 0) && (Height != 0)) {
                sf = (float) canvas.getWidth() / (float) Width;
                ScaleFactor = (float) canvas.getHeight() / (float) Height;
            }

            for (Graphic graphic : graphic) {
                graphic.draw(canvas);
            }
        }
    }
}
