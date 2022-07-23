package com.lgm.facedetectionapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView iv;
    private Button fb;
    private Bitmap img;
    private GraphicOverlay g_ov;
    private Integer wd;
    private Integer mhieght;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.img_vw);
        fb= findViewById(R.id.fbtn);
        fb.setTextColor(Color.BLACK);
        g_ov = findViewById(R.id.graphic_overlay);
        fb.setOnClickListener(view -> FaceDetection());
        Spinner dropdown = findViewById(R.id.spinner);
        String[] values = new String[]{"Image 1","Image 2","Image 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, values);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
    }
    private void FaceDetection() {
        InputImage image = InputImage.fromBitmap(img, 0);
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build();
        fb.setEnabled(false);
        FaceDetector detector = FaceDetection.getClient(options);
        Task<List<Face>> listTask = detector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {
                                fb.setEnabled(true);
                                Detect(faces);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                fb.setEnabled(true);
                                e.printStackTrace();
                            }
                        });

    }

    private void Detect(List<Face> faces) {
        if (faces.size() == 0) {
            showToast();
            return;
        }
        g_ov.clear();
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.get(i);
            FaceBox faceGraphic = new FaceBox(g_ov);
            g_ov.add(faceGraphic);
            faceGraphic.updateFace(face);
        }
    }

    private void showToast() {
        Toast.makeText(getApplicationContext(), "No face found in this image", Toast.LENGTH_SHORT).show();
    }
    private Integer getImageMaxWidth() {
        if (wd == null) {

            wd = iv.getWidth();
        }

        return wd;
    }
    private Integer getImageMaxHeight() {
        if (mhieght == null) {
            mhieght =
                    iv.getHeight();
        }

        return mhieght;
    }
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int finalwidth;
        int finalhight;
        int maxWidth = getImageMaxWidth();
        int maxHeight = getImageMaxHeight();
        finalwidth = maxWidth;
        finalhight = maxHeight;
        return new Pair<>(finalwidth, finalhight);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        g_ov.clear();
        switch (position) {
            case 0:
                img = getBitmapFromAsset(this, "Photo 1.jpg");
                break;
            case 1:
                img= getBitmapFromAsset(this, "Photo 2.jpg");
                break;
            case 2:
                img = getBitmapFromAsset(this, "Photo 3.jpg");
                break;
        }
        if (img  != null) {
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();
            int wd = targetedSize.first;
            int ht= targetedSize.second;
            float scaleFactor =
                    Math.max(
                            (float) img.getWidth() / (float) wd,
                            (float) img.getHeight() / (float) ht);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            img,
                            (int) (img.getWidth() / scaleFactor),
                            (int) (img.getHeight() / scaleFactor),
                            true);

            iv.setImageBitmap(resizedBitmap);
            img = resizedBitmap;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bm = null;
        try {
            is = assetManager.open(filePath);
            bm = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bm;
    }
}
