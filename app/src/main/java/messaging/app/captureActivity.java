package messaging.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class captureActivity extends AppCompatActivity {


    //state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private ImageButton btnCaptureImage;
    private ImageButton btnCaptureVideo;
    private ImageButton btnStopVideo;
    private TextureView cameraView;

    private CameraDevice cameraDevice;
    private String cameraID;
    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;
    private Size previewSize;
    private CaptureRequest.Builder captureRequestBuilder;
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 101;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT = 102;

    private boolean isRecording = false;
    private int totalRotation;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private String mTempFileName = null;
    private String mTempFilePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_activity);

        cameraView = (TextureView) findViewById(R.id.cameraView);
        btnCaptureVideo = (ImageButton) findViewById(R.id.btnTakeVideo);
        btnCaptureImage = (ImageButton) findViewById(R.id.btnTakePhoto);
        btnStopVideo = (ImageButton) findViewById(R.id.btnStopVideo);

        //hide unnecessary items
        btnStopVideo.setVisibility(View.INVISIBLE);


        toggleRecordingOnClick();

    }


    @Override
    protected void onResume() {
        super.onResume();

        startBackgroundThread();
        if (cameraView.isAvailable()) {
            setupCamera(cameraView.getWidth(), cameraView.getHeight());

            connectCamera();
        } else {
            cameraView.setSurfaceTextureListener(cameraViewListener);
        }
    }


    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();

    }


    private void toggleRecordingOnClick(){

        //if user has clicked to start recording a video
        btnCaptureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording){
                    //check permissions are valid
                    if(checkWriteToStoragePermissions() != false) {
                        isRecording = true;
                        btnStopVideo.setVisibility(View.VISIBLE);
                        btnCaptureVideo.setVisibility(View.INVISIBLE);
                        btnCaptureImage.setVisibility(View.INVISIBLE);

                        //start recording a video
                        startRecording();
                        mMediaRecorder.start();

                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "An error has occurred", LENGTH_SHORT).show();
                }
            }
        });


        btnStopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording) {
                    isRecording = false;
                    btnStopVideo.setVisibility(View.INVISIBLE);
                    btnCaptureVideo.setVisibility(View.VISIBLE);
                    btnCaptureImage.setVisibility(View.VISIBLE);

                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    startPreview();
                }
                else{
                    Toast.makeText(getApplicationContext(), "An error has occurred", LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setupMediaRecorder() throws IOException {
        mMediaRecorder = new MediaRecorder();

        //TODO:
        //need to send the file and then delete it from the storage once sent

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mTempFilePath);
        mMediaRecorder.setVideoEncodingBitRate(1000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        //TODO:
        //NEED to decide on H264 or HEVC
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setOrientationHint(totalRotation);
        mMediaRecorder.prepare();
    }


    private void createVideo() throws IOException{

        //store data in the cache until it is needed.
        File outputDir = this.getCacheDir();
        mTempFileName = "tempRecordedVideo";
        File outputFile = File.createTempFile(mTempFileName, ".mp4", outputDir);
        mTempFilePath = outputFile.getAbsolutePath();
    }


    private boolean checkWriteToStoragePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                try {
                    createVideo();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "App requires permission to access storage", LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        }
        else{
            try {
                createVideo();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    //Hide the phones control options when in app but allow user to swipe up to view them
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();

        if (hasFocus) {
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
            connectCamera();
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
            if(isRecording){
                try {
                    createVideo();
                    startRecording();
                    mMediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                startPreview();
            }
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


    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {

                //set camera ID
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                //select the first rear camera as suggested via google documentation
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
//                cameraID = cameraId;
                cameraID = cameraManager.getCameraIdList()[0];


                //check the device's orientation and change cameras orientation
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);

                //check if rotation is portrait (if it is it will be true)
                boolean inPortraitMode = totalRotation == 90 || totalRotation == 270;
                int rotatedWidth = width;
                int rotatedHeight = height;

                //change display if in portrait mode
                if (inPortraitMode) {
                    rotatedHeight = width;
                    rotatedWidth = height;
                }


                Log.d("Test", "cameraView.getHeight()" + height);
                Log.d("Test", "cameraView.getWidth()" + width);

                // select the best preview resolution
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                previewSize = selectOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mVideoSize = selectOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);

                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void connectCamera() {
        //create a manger of the camera service
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    cameraManager.openCamera(cameraID, cameraDeviceStateCallback, backgroundHandler);
                }
                else{
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                        Toast.makeText(this, "Please enable Camera Access", LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);
                }

            }
            else{
                cameraManager.openCamera(cameraID, cameraDeviceStateCallback, backgroundHandler);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void startRecording(){

        try {
            setupMediaRecorder();

            SurfaceTexture surfaceTexture = cameraView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface cameraPreviewSurface = new Surface(surfaceTexture);

            Surface recordingSurface = mMediaRecorder.getSurface();
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            captureRequestBuilder.addTarget(cameraPreviewSurface);
            captureRequestBuilder.addTarget(recordingSurface);

            cameraDevice.createCaptureSession(Arrays.asList(cameraPreviewSurface, recordingSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(
                                        captureRequestBuilder.build(),
                                        null,
                                        null
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, null);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }


    private void startPreview(){
        SurfaceTexture surfaceTexture = cameraView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface cameraPreviewSurface = new Surface(surfaceTexture);

        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(cameraPreviewSurface);

            //create camera session
            cameraDevice.createCaptureSession(Arrays.asList(cameraPreviewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {

                    //keep updating the camera preview
                    try {
                        session.setRepeatingRequest(captureRequestBuilder.build(),
                                null, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(getApplicationContext(), "Camera preview failed", LENGTH_SHORT).show();
                }
            }, null);

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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION_RESULT:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Camera will not work without access to camera", LENGTH_SHORT).show();
                }
            case REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Camera will not work without access to storage", LENGTH_SHORT).show();
                }
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


    private static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum(
                    (long) lhs.getWidth() * lhs.getHeight() /
                            (long) rhs.getWidth() * rhs.getHeight());
        }
    }


    private static Size selectOptimalSize(Size[] options, int width, int height){
        List<Size> possibleOptions = new ArrayList<Size>();
        for(Size option: options){
            Log.d("Test", "possibleOptions " + option.getHeight() + " " + option.getWidth());
            if(option.getHeight() == option.getWidth() *  height/width &&
            option.getWidth() >= width && option.getHeight() >= height){
                possibleOptions.add(option);
            }
        }

        if(!possibleOptions.isEmpty()){
            return Collections.min(possibleOptions, new CompareSizeByArea());

        }
        else{

            return findSimilarRatio(options, width, height);
        }
    }


    private static Size findSimilarAreaSize(Size[] options, int width, int height){
        int area = width * height;

        int bestDifference = 1000000000;
        Size mostSimilarArea = null;

        for (Size size : options){
            int currentDifference =  Math.abs(area - Math.abs(size.getHeight() * size.getWidth()));

            if (currentDifference <= bestDifference ){
                bestDifference = currentDifference;
                mostSimilarArea = size;
            }
        }
        return mostSimilarArea;
    }


    private static Size findSimilarRatio(Size[] options, int width, int height){
        double targetHeightToWidthRatio = (double) width/height;
        Log.d("Test", "target " + targetHeightToWidthRatio);


        double bestRatioDifference = 1000000000f;
        Size mostSimilar = null;

        //TODO
        //might need to change from (size.getHeight() == height) to (size.getWidth() == width)
        //when the screen is rotated
        for (Size size : options) {
            if (size.getHeight() == height) {
                double currentDifference = targetHeightToWidthRatio - (size.getWidth() / size.getHeight());

                if (currentDifference <= bestRatioDifference) {
                    bestRatioDifference = currentDifference;
                    mostSimilar = size;
                }
                Log.d("Test", "best " + bestRatioDifference);
            }
        }

        //if finding the most similar ratio fails try finding the most similar area
        mostSimilar = findSimilarAreaSize(options, width, height);

        return mostSimilar;
    }
}


