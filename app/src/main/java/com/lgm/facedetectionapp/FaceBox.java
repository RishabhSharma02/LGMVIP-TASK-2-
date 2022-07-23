package com.lgm.facedetectionapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.mlkit.vision.face.Face;

public class FaceBox extends GraphicOverlay.Graphic {
  private static final float sw= 10.0f;
  private final Paint boxPaint;
  private volatile Face face;
  public FaceBox(GraphicOverlay overlay) {
    super(overlay);
    boxPaint = new Paint();
    boxPaint.setColor(Color.BLUE);
    boxPaint.setStyle(Paint.Style.STROKE);
    boxPaint.setStrokeWidth(sw);
  }
  public void updateFace(Face face) {
    this.face = face;
    postInvalidate();
  }
  @Override
  public void draw(Canvas canvas) {
    Face face = this.face;
    if (face == null) {
      return;
    }
    float x = trX(face.getBoundingBox().centerX());
    float y = trY(face.getBoundingBox().centerY());
    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
    float left = x - xOffset;
    float top = y - yOffset;
    float right = x + xOffset;
    float bottom = y + yOffset;
    canvas.drawRect(left, top, right, bottom, boxPaint);
  }
}
