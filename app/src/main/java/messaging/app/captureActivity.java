package messaging.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class captureActivity extends AppCompatActivity {

    //TODO:
    //remove saved files once sent
        // function built
    //fix variable names
    // organise code
    //fix rotation issues
    //disable the ability to rotate application once media is captured
    //add the ability to use flash
    //add auto focus
    //add manual focus
    // add the ability to retake the captured media
    // add the ability to zoom
    //NEED to decide on H264 or HEVC encoding for video

    //state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 101;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT = 102;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_RESULT = 103;
    private static final int  STATE_PREVIEW = 0;
    private static final int  STATE_WAIT_LOCK = 1;


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
    private VideoView capturedVideoView;
    private ImageView capturedImageView;

    private CameraDevice cameraDevice;
    private String cameraID;
    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;
    private Size previewSize;
    private CaptureRequest.Builder captureRequestBuilder;

    private boolean isRecording = false;
    private int totalRotation;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private String mImageFilePath = "";
    private String mVideoFilePath = "";
    private File mVideoFolder = null;
    private File mImageFolder = null;


    private Size mImageSize;
    private ImageReader mImageReader;
    private CameraCaptureSession mCaptureSession;
    private int mCaptureState = STATE_PREVIEW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_activity);

        cameraView = (TextureView) findViewById(R.id.cameraView);
        btnCaptureVideo = (ImageButton) findViewById(R.id.btnTakeVideo);
        btnCaptureImage = (ImageButton) findViewById(R.id.btnTakePhoto);
        btnStopVideo = (ImageButton) findViewById(R.id.btnStopVideo);
        capturedVideoView = (VideoView) findViewById(R.id.capturedVideoView);
        capturedImageView = (ImageView) findViewById(R.id.capturedImageView);

        //hide unnecessary items
        btnStopVideo.setVisibility(View.INVISIBLE);
        capturedVideoView.setVisibility(View.INVISIBLE);
        capturedImageView.setVisibility(View.INVISIBLE);

        createVideoFolder();
        createImageFolder();

        toggleRecordingOnClick();
        captureImageOnClick();
        setVideoViewListener();

    }


    @Override
    protected void onResume() {
        super.onResume();

        startBackgroundThread();
        if (cameraView.isAvailable()) {
            setupCamera(cameraView.getWidth(), cameraView.getHeight());

            //check that the rotation is correct, if not fix it
            transformImage(cameraView.getWidth(), cameraView.getHeight());
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


                    previewCapturedMedia("Video");

                }
                else{
                    Toast.makeText(getApplicationContext(), "An error has occurred", LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setVideoViewListener(){
        capturedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }


    private void captureImageOnClick(){

        //if user has clicked to start recording a video
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(captureActivity.this, "Capture", LENGTH_SHORT).show();
                lockFocus();

            }
        });

    }


    private void setupMediaRecorder() throws IOException {

        //setup video and audio for media recorder
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideoFilePath);
        mMediaRecorder.setVideoEncodingBitRate(1000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOrientationHint(totalRotation);
        mMediaRecorder.prepare();
    }


    private void createVideoFolder() {
        File movieDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        mVideoFolder = new File(movieDir, "capturesFromElderlyApp");
        if(!mVideoFolder.exists()) {
            mVideoFolder.mkdirs();
        }
    }


    private File createVideoFileName() throws IOException {
        File videoFile = File.createTempFile("tempFile", ".mp4", mVideoFolder);
        mVideoFilePath = videoFile.getAbsolutePath();
        return videoFile;
    }


    private void createImageFolder() {
        File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mImageFolder = new File(imageDir, "capturesFromElderlyApp");
        if(!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }
    }


    private File createImageFileName() throws IOException {

        File imageFile = File.createTempFile("tempFile", ".jpg", mImageFolder);
        mImageFilePath = imageFile.getAbsolutePath();
        return imageFile;
    }


    private boolean checkWriteToStoragePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                try {
                    createVideoFileName();
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
                createVideoFileName();
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

            //check that the rotation is correct, if not fix it
            transformImage(width, height);
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
            mMediaRecorder = new MediaRecorder();
            if(isRecording){
                try {
                    createVideoFileName();
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


    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //add the imageSave runnable to the background thread
            backgroundHandler.post(new ImageSaver(reader.acquireLatestImage()));
        }
    };


    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            startCaptureImageRequest();

        }


    };


    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private void previewCapturedMedia(String typeOfCapturedMedia){
        File mediaFile;

        //hide unwanted features
        btnCaptureImage.setVisibility(View.INVISIBLE);
        btnCaptureVideo.setVisibility(View.INVISIBLE);
        cameraView.setVisibility(View.INVISIBLE);


        switch (typeOfCapturedMedia){
            case "Image":
                //get image file
                mediaFile = new File(mImageFilePath);

                //update image view
                if(mediaFile.exists()){

                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(mediaFile.getPath());
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotationInDegrees = exifToDegrees(rotation);
                        Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
                        myBitmap = RotateBitmap(myBitmap, rotationInDegrees);
                        capturedImageView.setImageBitmap(myBitmap);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }



//                    capturedVideoView.setRotation(totalRotation);

                }else{
                    Toast.makeText(getApplicationContext(), "Could not find image", LENGTH_SHORT).show();
                }

                //display image
                capturedImageView.setVisibility(View.VISIBLE);
                break;

            case "Video":
                //get image file
                mediaFile = new File(mVideoFilePath);

                //update image view
                if(mediaFile.exists()){

                    capturedVideoView.setVideoPath(mVideoFilePath);
                    capturedVideoView.start();


                }else{
                    Toast.makeText(getApplicationContext(), "Could not find video", LENGTH_SHORT).show();
                }

                //display image
                capturedVideoView.setVisibility(View.VISIBLE);


                break;
        }



    }


    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {

                //set camera ID
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                //select the first rear camera as suggested via google documentation
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

                // select the best preview resolution
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                previewSize = selectOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mVideoSize = selectOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
                mImageSize = selectOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotatedWidth, rotatedHeight);

                mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, backgroundHandler);

                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSION_RESULT);
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
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            captureRequestBuilder.addTarget(cameraPreviewSurface);
            captureRequestBuilder.addTarget(recordingSurface);

            cameraDevice.createCaptureSession(Arrays.asList(cameraPreviewSurface, recordingSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mCaptureSession = session;
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


    private void startCaptureImageRequest(){
        Log.d("Test", "startCaptureImageRequest");
        try {
            Log.d("Test", "TRYING startCaptureImageRequest");
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            Log.d("Test", "TRYING startCaptureImageRequest 2");
            CameraCaptureSession.CaptureCallback imageCaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                    try {
                        Log.d("Test", "createMediaFile image");
                        createImageFileName();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            //capture image
            mCaptureSession.capture(captureRequestBuilder.build(), imageCaptureCallback, null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private class ImageSaver implements Runnable{

        private final Image mImage;

        public ImageSaver(Image image){
            mImage = image;
        }


        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);


            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(mImageFilePath);
                fileOutputStream.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                mImage.close();
                if(fileOutputStream != null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //display the captured image
                    previewCapturedMedia("Image");
                }
            });

        }
    }


    private void startPreview(){
        SurfaceTexture surfaceTexture = cameraView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface cameraPreviewSurface = new Surface(surfaceTexture);

        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(cameraPreviewSurface);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(totalRotation));





            //create camera session
            cameraDevice.createCaptureSession(Arrays.asList(cameraPreviewSurface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {

                    mCaptureSession = session;

                    //keep updating the camera preview
                    try {
                        mCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
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
                break;
            case REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Camera will not work without access to storage", LENGTH_SHORT).show();
                }
                break;
            case REQUEST_RECORD_AUDIO_PERMISSION_RESULT:
                if(grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Application will not be able to record audio", LENGTH_SHORT).show();
                }
                break;
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
                    (long) (lhs.getWidth() * lhs.getHeight()) /
                            (long) (rhs.getWidth() * rhs.getHeight()));
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

            Log.d("test", String.valueOf(findSimilarRatio(options, width, height)));
            Log.d("test", String.valueOf(options[0]));
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


    private void lockFocus(){
        Toast.makeText(captureActivity.this, "lockFocus", LENGTH_SHORT).show();
        mCaptureState = STATE_WAIT_LOCK;

        //focus on the subject
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);

        //capture image
        try {
            mCaptureSession.capture(captureRequestBuilder.build(), mCaptureCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void deleteMediaFile(String path){
        File file = new File(path);
        boolean deleted = file.delete();
        if(!deleted){
            Toast.makeText(this, "Error Deleting file", LENGTH_SHORT).show();
        }

        return;
    }


    private void transformImage(int width, int height){
        if(previewSize == null || cameraView == null){
            return;
        }

        Matrix matrix = new Matrix();
        int rotation =  getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();

        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / previewSize.getWidth(), (float) height / previewSize.getHeight());

            matrix.postScale(scale, scale, centerX, centerY);

            //calculate what rotation to use
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }

        //adjust the cameraView for fix
        cameraView.setTransform(matrix);

    }
}


