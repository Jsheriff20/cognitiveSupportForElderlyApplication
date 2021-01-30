package messaging.app;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class captureActivity extends AppCompatActivity {


    //state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0,0);
        ORIENTATIONS.append(Surface.ROTATION_90,90);
        ORIENTATIONS.append(Surface.ROTATION_180,180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private ImageButton btnCaptureImage;
    private ImageButton btnCaptureVideo;
    private TextureView cameraView;

    private CameraDevice cameraDevice;
    private String cameraID;
    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;
    private Size previewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_activity);

        cameraView = (TextureView) findViewById(R.id.cameraView);
        btnCaptureVideo = (ImageButton) findViewById(R.id.btnTakeVideo);
        btnCaptureImage = (ImageButton) findViewById(R.id.btnTakePhoto);
    }



    @Override
    protected void onResume() {
        super.onResume();

        startBackgroundThread();
        if(cameraView.isAvailable()){
            setupCamera(cameraView.getWidth(), cameraView.getHeight());
        }
        else{
            cameraView.setSurfaceTextureListener(cameraViewListener);
        }
    }



    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();

    }



    private static class CompareSizeByArea implements Comparator<Size>{
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum(
                    (long) lhs.getWidth() * lhs.getHeight()/
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }


    //Hide the phones control options when in app but allow user to swipe up to view them
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();

        if(hasFocus){
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }



    private TextureView.SurfaceTextureListener cameraViewListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };



    private CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            //clear memory and remove the use of the camera
            closeCamera();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            //clear memory and remove the use of the camera
            closeCamera();

        }
    };



    private void setupCamera(int width, int height){
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d("Test", "setupCamera: " + cameraManager.getCameraIdList());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        try {
            for(String cameraId : cameraManager.getCameraIdList()){

                //set camera ID
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT){
                    continue;
                }
                cameraID = cameraId;


                //check the device's orientation and change cameras orientation
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                int totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);

                //check if rotation is portrait (if it is it will be true)
                boolean inPortraitMode = totalRotation == 90 || totalRotation == 270;
                int rotatedWidth = width;
                int rotatedHeight = height;

                //change display if in portrait mode
                if(inPortraitMode){
                    rotatedHeight = width;
                    rotatedWidth = height;
                }

                // select the best preview resolution
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                previewSize = selectOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);

                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    private void closeCamera(){
        if(cameraDevice !=null){
            cameraDevice.close();
            cameraDevice = null;
        }
    }



    //setup background thread for the camera to use so it is not blocking main thread
    private void startBackgroundThread(){
        backgroundHandlerThread = new HandlerThread("CameraForElderlyApplication");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }



    private void stopBackgroundThread(){
        backgroundHandlerThread.quitSafely();
        try{
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation){
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        //output the orientation of the device in degrees
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }



    private static Size selectOptimalSize(Size[] options, int width, int height){
        List<Size> possibleOptions = new ArrayList<Size>();
        for(Size option: options){
            if(option.getHeight() == option.getWidth() *  height/width &&
            option.getWidth() >= width && option.getHeight() >= height){
                possibleOptions.add(option);
            }
        }

        if(!possibleOptions.isEmpty()){
            return Collections.min(possibleOptions, new CompareSizeByArea());
        }
        else{
            return options[0];
        }
    }
}


