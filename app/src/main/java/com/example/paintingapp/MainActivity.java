package com.example.paintingapp;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton currentPaint, drawButton, baru, erase, save;
    private DrawingView drawView;
    private float brushSize;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // @param API
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                drawView = (DrawingView) findViewById(R.id.drawing);
                drawButton = (ImageButton) findViewById(R.id.draw_btn);
                baru = (ImageButton) findViewById(R.id.new_btn);
                erase = (ImageButton) findViewById(R.id.erase_btn);
                save = (ImageButton) findViewById(R.id.save_btn);

                LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
                currentPaint = (ImageButton) paintLayout.getChildAt(0);

                currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
                drawButton.setOnClickListener(this);
                erase.setOnClickListener(this);
                baru.setOnClickListener(this);
                save.setOnClickListener(this);
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void paintClicked(View view) {
        if (view != currentPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currentPaint = (ImageButton) view;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.draw_btn) {
            drawView.setUpDrawing();
            brushSize = drawView.getBrushSize();

        }

        if (v.getId() == R.id.erase_btn) {
            drawView.setErase(true);
            drawView.setBrushSize(brushSize);
        }

        if (v.getId() == R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New Drawing");
            newDialog.setMessage("Start new drawing");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        }

        if (v.getId() == R.id.save_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save Drawing");
            saveDialog.setMessage("Save Drawing to device Gallery");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(),
                            drawView.getDrawingCache(), UUID.randomUUID().toString() + ".png", "drawing");
                    if (imgSaved != null) {
                        Context context;
                        CharSequence text;
                        Toast savedToast = Toast.makeText(getApplicationContext(), "Drawing saved to Gallery",
                                Toast.LENGTH_SHORT);
                        savedToast.show();
                    } else {
                        Toast unSaved = Toast.makeText(getApplicationContext(), "Image can not saved",
                                Toast.LENGTH_SHORT);
                        unSaved.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }

        if (v.getId() == R.id.increase_brushSize) {
            brushSize += 2;
            drawView.setBrushSize(brushSize);

            Toast ToastBrushSize = Toast.makeText(getApplicationContext(), "BrushSize " + brushSize,
                    Toast.LENGTH_SHORT);
            ToastBrushSize.show();
        }

        if (v.getId() == R.id.decrease_brushSize) {
            brushSize -= 2;
            drawView.setBrushSize(brushSize);

            Toast ToastBrushSize = Toast.makeText(getApplicationContext(), "BrushSize " + brushSize,
                    Toast.LENGTH_SHORT);
            ToastBrushSize.show();
        }
    }
}