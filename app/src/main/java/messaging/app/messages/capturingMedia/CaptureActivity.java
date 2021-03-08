package messaging.app.messages.capturingMedia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import java.util.Arrays;

import messaging.app.MediaManagement;
import messaging.app.R;
import messaging.app.messages.MessagesActivity;
import messaging.app.messages.sendingMedia.AddMessageToMediaActivity;
import messaging.app.register.RegisterProfileImageActivity;

import static android.widget.Toast.LENGTH_SHORT;

public class CaptureActivity extends AppCompatActivity {

    //TODO:
    //remove saved files once sent
    // function built
    //add the ability to use flash
    //add manual focus
    // add the ability to zoom
    //add front facing camera

    //state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 101;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT = 102;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_RESULT = 103;


    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private ImageButton btnCaptureImage;
    private ImageButton btnCaptureVideo;
    private ImageButton btnStopVideo;
    private ImageButton btnCancel;
    private ImageButton btnLoadMessageActivity;
    public TextureView cameraView;
    public VideoView capturedVideoView;
    private ImageView capturedImageView;

    private CameraDevice mCameraDevice;
    private String mCameraID;
    private HandlerThread mBackgroundHandlerThread;
    public Handler mBackgroundHandler;
    public Size mPreviewSize;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private boolean mIsRecording = false;
    private int mTotalRotation;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    public String mImageFilePath = "";
    public String mVideoFilePath = "";
    public File mVideoFolder = null;
    public File mImageFolder = null;


    private Size mImageSize;
    private ImageReader mImageReader;
    private CameraCaptureSession mCaptureSession;

    private String mTypeOfMediaCaptured;
    private MediaManagement mediaManagement = new MediaManagement();

    private boolean mCaptureForProfileImage;
    private String mReplyingToUUID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);


        mCaptureForProfileImage = getIntent().getBooleanExtra("captureForProfileImage", false);
        if (getIntent().getStringExtra("replyingTo") != null) {
            mReplyingToUUID = getIntent().getStringExtra("replyingTo");
        }

        //assign variables to each view element
        cameraView = (TextureView) findViewById(R.id.cameraView);
        btnCaptureVideo = (ImageButton) findViewById(R.id.btnTakeVideo);
        btnCaptureImage = (ImageButton) findViewById(R.id.btnTakePhoto);
        btnStopVideo = (ImageButton) findViewById(R.id.btnStopVideo);
        btnCancel = (ImageButton) findViewById(R.id.btnCancel);
        btnLoadMessageActivity = (ImageButton) findViewById(R.id.btnEnterMessageActivity);
        capturedVideoView = (VideoView) findViewById(R.id.capturedVideoView);
        capturedImageView = (ImageView) findViewById(R.id.capturedImageView);

        //hide unnecessary items
        btnStopVideo.setVisibility(View.INVISIBLE);
        capturedVideoView.setVisibility(View.INVISIBLE);
        capturedImageView.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);
        btnLoadMessageActivity.setVisibility(View.INVISIBLE);

        //if capturing for profile image hide additional features
        //also initiate onclick listeners
        if (mCaptureForProfileImage) {
            btnCaptureVideo.setVisibility(View.INVISIBLE);
            confirmProfileImageSelectionOnClick();
        } else {
            //create listeners
            setLoadMessageActivityOnClick();
            setVideoViewListener();
            toggleRecordingOnClick();
        }

        //hide the navigation controls
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //create directories for files
        createMediaFolders();

        //create events
        captureImageOnClick();
        cancelMediaOnClick();

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CaptureActivity.this, MessagesActivity.class);
        CaptureActivity.this.startActivity(intent);
    }

    @Override
    protected void onResume() {
        //start background thread and initiate all services
        startBackgroundThread();
        if (cameraView.isAvailable()) {
            setupCamera(cameraView.getWidth(), cameraView.getHeight());

            //check that the rotation is correct, if not fix it
            transformImage(cameraView.getWidth(), cameraView.getHeight());
            connectCamera();
        } else {
            cameraView.setSurfaceTextureListener(cameraViewListener);
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        //stop all services
        closeCamera();
        stopBackgroundThread();
        super.onPause();

    }


    public void toggleRecordingOnClick() {

        //if user has clicked to start recording a video
        btnCaptureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsRecording) {
                    //check permissions are valid
                    if (checkWriteToStoragePermissions() != false) {
                        mIsRecording = true;
                        btnStopVideo.setVisibility(View.VISIBLE);
                        btnCaptureVideo.setVisibility(View.INVISIBLE);
                        btnCaptureImage.setVisibility(View.INVISIBLE);

                        //start recording a video
                        startRecording();
                        mMediaRecorder.start();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "An error has occurred", LENGTH_SHORT).show();
                }
            }
        });


        btnStopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if video is record, if so stop and adjust view for user
                if (mIsRecording) {
                    mIsRecording = false;
                    btnStopVideo.setVisibility(View.INVISIBLE);
                    btnCaptureVideo.setVisibility(View.VISIBLE);
                    btnCaptureImage.setVisibility(View.VISIBLE);

                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    startPreview();


                    previewCapturedMedia("Video");

                } else {
                    Toast.makeText(getApplicationContext(), "An error has occurred", LENGTH_SHORT).show();
                }
            }
        });
    }


    public void setVideoViewListener() {
        capturedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }


    public void captureImageOnClick() {

        //if user has clicked to start recording a video
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkWriteToStoragePermissions() != false) {
                    lockFocus();
                }

            }
        });

    }


    private void cancelMediaOnClick() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hide unwanted view elements
                btnCancel.setVisibility(View.INVISIBLE);
                btnLoadMessageActivity.setVisibility(View.INVISIBLE);
                capturedImageView.setVisibility(View.INVISIBLE);
                capturedVideoView.setVisibility(View.INVISIBLE);

                //clear media
                capturedVideoView.setVideoURI(null);
                capturedImageView.setImageResource(0);

                lockOrientation(false);

                //display view elements
                btnCaptureImage.setVisibility(View.VISIBLE);
                btnCaptureVideo.setVisibility(View.VISIBLE);
                cameraView.setVisibility(View.VISIBLE);


                //start background thread and initiate all services
                startBackgroundThread();
                if (cameraView.isAvailable()) {
                    setupCamera(cameraView.getWidth(), cameraView.getHeight());

                    //check that the rotation is correct, if not fix it
                    transformImage(cameraView.getWidth(), cameraView.getHeight());
                    connectCamera();
                } else {
                    cameraView.setSurfaceTextureListener(cameraViewListener);
                }

                switch (mTypeOfMediaCaptured) {
                    case "Image":
                        deleteMediaFile(mImageFilePath);
                        break;
                    case "Video":
                        deleteMediaFile(mVideoFilePath);
                        break;
                }
                mTypeOfMediaCaptured = null;
            }
        });
    }


    private void confirmProfileImageSelectionOnClick() {
        btnLoadMessageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterProfileImageActivity.class);
                intent.putExtras(getIntent().getExtras());
                intent.putExtra("profileImage", mImageFilePath);
                intent.putExtra("typeOfMediaCaptured", mTypeOfMediaCaptured);
                intent.putExtra("profileImageRotation", mTotalRotation);

                startActivity(intent);
            }
        });
    }


    private void setLoadMessageActivityOnClick() {
        btnLoadMessageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMessageToMediaActivity.class);
                intent.putExtra("typeOfMediaCaptured", mTypeOfMediaCaptured);

                if (mReplyingToUUID != null) {
                    intent.putExtra("replyingTo", mReplyingToUUID);
                }

                int deviceOrientationMode = getWindowManager().getDefaultDisplay().getRotation();

                switch (mTypeOfMediaCaptured) {
                    case "Image":
                        intent.putExtra("mediaPath", mImageFilePath);
                        break;

                    case "Video":
                        intent.putExtra("mediaPath", mVideoFilePath);
                        break;
                }
                intent.putExtra("deviceOrientationMode", deviceOrientationMode);

                startActivity(intent);

            }
        });
    }


    public TextureView.SurfaceTextureListener cameraViewListener = new TextureView.SurfaceTextureListener() {
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


    public final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //add the imageSave runnable to the background thread
            mBackgroundHandler.post(new ImageSaver(reader.acquireLatestImage()));
        }
    };


    private void setupMediaRecorder() throws IOException {

        //setup video and audio for media recorder
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideoFilePath);
        mMediaRecorder.setVideoEncodingBitRate(1000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setAudioSamplingRate(44100);
        mMediaRecorder.setAudioEncodingBitRate(96000); //change to 128000 if needed
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.HEVC);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        int landscape = 1;
        boolean inLandscapeMode = ((int) getWindowManager().getDefaultDisplay().getRotation() == landscape);
        if (inLandscapeMode) {
            mMediaRecorder.setOrientationHint(0);
        } else {
            mMediaRecorder.setOrientationHint(mTotalRotation);
        }
        mMediaRecorder.prepare();
    }


    private boolean checkWriteToStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    //if permissions are granted create the video file
                    createVideoFileName();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //if permissions are denied say why permission is needed
                    Toast.makeText(this, "App requires permission to access storage", LENGTH_SHORT).show();
                }
                //request for permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        } else {
            try {
                //if permissions are granted create the video file
                createVideoFileName();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public void deleteMediaFile(String path) {
        File file = new File(path);
        boolean deleted = file.delete();

        if (!deleted) {
            Toast.makeText(this, "Error Deleting file", LENGTH_SHORT).show();
        }
        return;
    }


    public void createMediaFolders() {
        //create folders for media files
        File movieDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        mVideoFolder = new File(movieDir, "capturesFromElderlyApp");
        mImageFolder = new File(imageDir, "capturesFromElderlyApp");

        if (!mVideoFolder.exists()) {
            mVideoFolder.mkdirs();
        }
        if (!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }
    }


    public File createVideoFileName() throws IOException {
        File videoFile = File.createTempFile("tempFile", ".mp4", mVideoFolder);
        mVideoFilePath = videoFile.getAbsolutePath();
        return videoFile;
    }


    public File createImageFileName() throws IOException {

        File imageFile = File.createTempFile("tempFile", ".jpg", mImageFolder);
        mImageFilePath = imageFile.getAbsolutePath();
        return imageFile;
    }


    private CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            mMediaRecorder = new MediaRecorder();

            //if user wants to record create a new file and start the video and audio recoding
            if (mIsRecording) {
                try {
                    createVideoFileName();
                    startRecording();
                    mMediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //if user is ending video recording start the preview
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


    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            startCaptureImageRequest();
        }
    };

    private static String degreesToExif(int orientation) {
        if (orientation == 90) {
            return String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);
        } else if (orientation == 180) {
            return String.valueOf(0);
        } else if (orientation == 270) {
            return String.valueOf(0);
        }
        return String.valueOf(0);
    }


    private void previewCapturedMedia(String typeOfCapturedMedia) {
        File mediaFile;
        mTypeOfMediaCaptured = typeOfCapturedMedia;

        //hide unwanted view elements
        btnCaptureImage.setVisibility(View.INVISIBLE);
        btnCaptureVideo.setVisibility(View.INVISIBLE);
        cameraView.setVisibility(View.INVISIBLE);

        //display view elements
        btnCancel.setVisibility(View.VISIBLE);
        btnLoadMessageActivity.setVisibility(View.VISIBLE);


        lockOrientation(true);

        //display the captured media
        switch (typeOfCapturedMedia) {
            case "Image":
                //get image file
                mediaFile = new File(mImageFilePath);

                //update image view
                if (mediaFile.exists()) {

                    try {

                        ExifInterface exif = null;
                        //display the media in the correct rotation
                        exif = new ExifInterface(mediaFile.getPath());
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());


                        Bitmap adjustedBitmapImage = mediaManagement.adjustBitmapImage(exifOrientation, myBitmap);


                        capturedImageView.setImageBitmap(adjustedBitmapImage);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Could not find image", LENGTH_SHORT).show();
                }

                //display image
                capturedImageView.setVisibility(View.VISIBLE);
                break;

            case "Video":
                //get image file
                mediaFile = new File(mVideoFilePath);

                //update image view
                if (mediaFile.exists()) {

                    capturedVideoView.setVideoPath(mVideoFilePath);
                    capturedVideoView.start();


                } else {
                    Toast.makeText(getApplicationContext(), "Could not find video", LENGTH_SHORT).show();
                }

                //display image
                capturedVideoView.setVisibility(View.VISIBLE);


                break;
        }
    }


    public void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {

                //set camera ID
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                //select the first rear camera as suggested via google documentation
                mCameraID = cameraManager.getCameraIdList()[0];


                //check the device's orientation and change cameras orientation
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);

                //check if rotation is portrait (if it is it will be true)
                boolean inPortraitMode = mTotalRotation == 90 || mTotalRotation == 270;
                int rotatedWidth = width;
                int rotatedHeight = height;

                //change display if in portrait mode
                if (inPortraitMode) {
                    rotatedHeight = width;
                    rotatedWidth = height;
                }

                // select the best preview resolution
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = mediaManagement.selectOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mVideoSize = mediaManagement.selectOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
                mImageSize = mediaManagement.selectOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotatedWidth, rotatedHeight);

                mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public void connectCamera() {
        //create a manger of the camera service
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //open the camera if permissions are granted
                    cameraManager.openCamera(mCameraID, cameraDeviceStateCallback, mBackgroundHandler);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        //if permissions are denied say why permission is needed
                        Toast.makeText(this, "Please enable Camera Access", LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION_RESULT);
                }

            } else {
                cameraManager.openCamera(mCameraID, cameraDeviceStateCallback, mBackgroundHandler);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void startRecording() {

        try {
            setupMediaRecorder();

            //setup target surface
            SurfaceTexture surfaceTexture = cameraView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface cameraPreviewSurface = new Surface(surfaceTexture);

            //setup capture request builder
            Surface recordingSurface = mMediaRecorder.getSurface();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            mCaptureRequestBuilder.addTarget(cameraPreviewSurface);
            mCaptureRequestBuilder.addTarget(recordingSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(cameraPreviewSurface, recordingSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mCaptureSession = session;
                            try {
                                session.setRepeatingRequest(
                                        mCaptureRequestBuilder.build(),
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


    private void startCaptureImageRequest() {
        try {
            //setup capture request builder
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mCaptureRequestBuilder.addTarget(mImageReader.getSurface());

            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_STATE_FOCUSED_LOCKED);

            CameraCaptureSession.CaptureCallback imageCaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                    try {
                        createImageFileName();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            //capture image
            mCaptureSession.capture(mCaptureRequestBuilder.build(), imageCaptureCallback, null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public class ImageSaver implements Runnable {

        private final Image mImage;

        public ImageSaver(Image image) {
            mImage = image;
        }


        @Override
        public void run() {
            //get bytes from image
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);


            //save image's bytes as a file
            FileOutputStream fileOutputStream = null;
            try {

                fileOutputStream = new FileOutputStream(mImageFilePath);
                fileOutputStream.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(mImageFilePath);
                    exif.setAttribute(ExifInterface.TAG_ORIENTATION, degreesToExif(mTotalRotation));
                    exif.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mImage.close();
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            //display the captured image using the main/ UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //display the captured image
                    previewCapturedMedia("Image");
                }
            });

        }
    }


    private void startPreview() {
        //setup target surface
        SurfaceTexture surfaceTexture = cameraView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface cameraPreviewSurface = new Surface(surfaceTexture);

        try {
            //setup capture request builder
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(cameraPreviewSurface);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(mTotalRotation));


            //create camera session
            mCameraDevice.createCaptureSession(Arrays.asList(cameraPreviewSurface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {

                            mCaptureSession = session;

                            //keep updating the camera preview
                            try {
                                mCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),
                                        null, mBackgroundHandler);
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


    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //when a permission request result is received if the user selects deny alert them of the issue.
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_RESULT:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera will not work without access to camera", LENGTH_SHORT).show();
                }
                break;
            case REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera will not work without access to storage", LENGTH_SHORT).show();
                }
                break;
            case REQUEST_RECORD_AUDIO_PERMISSION_RESULT:
                if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Application will not be able to record audio", LENGTH_SHORT).show();
                }
                break;
        }

    }


    //setup background thread for the camera to use so it is not blocking main thread
    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("CameraForElderlyApplication");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }


    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);

        //output the orientation of the device in degrees
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }


    public void lockFocus() {

        //focus on the subject
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);

        //capture image
        try {
            mCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public void transformImage(int width, int height) {
        if (mPreviewSize == null || cameraView == null) {
            return;
        }

        Matrix matrix = new Matrix();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();

        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / mPreviewSize.getWidth(), (float) height / mPreviewSize.getHeight());

            matrix.postScale(scale, scale, centerX, centerY);

            //calculate what rotation to use
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }

        //adjust the cameraView for fix
        cameraView.setTransform(matrix);

    }


    private void lockOrientation(boolean lock) {

        if (lock) {
            int landscape = 1;
            boolean inLandscapeMode = ((int) getWindowManager().getDefaultDisplay().getRotation() == landscape);
            if (inLandscapeMode) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
}