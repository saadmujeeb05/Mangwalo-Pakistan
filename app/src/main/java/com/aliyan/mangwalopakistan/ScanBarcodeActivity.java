package com.aliyan.mangwalopakistan;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by Aliyan on 5/1/2017.
 */
public class ScanBarcodeActivity extends Activity{
    SurfaceView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanbarcode);

        camera = (SurfaceView) findViewById(R.id.camera);
        createCameraSource();
    }

    public static final int bla = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case bla: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ScanBarcodeActivity.this,"Camera permission granted",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ScanBarcodeActivity.this,"Camera permission denied",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(400, 500).setAutoFocusEnabled(true).build();

        camera.getHolder().addCallback(new SurfaceHolder.Callback() {


            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ScanBarcodeActivity.this,android.Manifest.permission.CAMERA)) {
                            Toast.makeText(ScanBarcodeActivity.this,"CAMERA SERVICE REQUIRED",Toast.LENGTH_LONG).show();
                        }
                        else {
                            ActivityCompat.requestPermissions(ScanBarcodeActivity.this,new String[]{android.Manifest.permission.CAMERA},bla);

                        }
                         return;
                    }
                    cameraSource.start(camera.getHolder());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder,int format, int width, int height){

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder){
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release(){

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections){
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size() > 0){
                    Intent intent =  new Intent();
                    intent.putExtra("barcode",barcodes.valueAt(0));
                    setResult(CommonStatusCodes.SUCCESS,intent);
                    finish();
                }
            }



        });



    }


}
