package messaging.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.util.Size;

import java.io.File;
import java.io.IOException;

public class MediaManagement {

    Context mContext;

    public MediaManagement(Context context) {
        this.mContext = context;
    }

    public MediaManagement() {
    }

    public Bitmap FlipBitmap(Bitmap myBitmap, String flipType) {

        Matrix matrix = new Matrix();
        switch (flipType) {
            case "Horizontally":
                matrix.postScale(-1, 1, myBitmap.getWidth() / 2f,
                        myBitmap.getHeight() / 2f);
                break;
            case "Vertically":
                matrix.postScale(1, -1, myBitmap.getWidth() / 2f,
                        myBitmap.getHeight() / 2f);
                break;
        }

        return Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),
                myBitmap.getHeight(), matrix, true);
    }


    public Size getOptimalPreviewSize(Size[] sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Size size : sizes) {
            double ratio = (double) size.getWidth() / size.getHeight();
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.getHeight() - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.getHeight() - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }


    public static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }


    public static String degreesToExif(int orientation) {
        if (orientation == 90) {
            return String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);
        } else if (orientation == 180) {
            return String.valueOf(0);
        } else if (orientation == 270) {
            return String.valueOf(0);
        }
        return String.valueOf(0);
    }


    public void deleteMediaFile(String path, Context context) {
        File file = new File(path);
        boolean deleted = file.delete();

        return;
    }


    public Bitmap adjustBitmapImage(int exifOrientation, Bitmap myBitmap) {

        switch (exifOrientation) {
            case 2:
                myBitmap = rotateBitmap(myBitmap, 0);
                myBitmap = FlipBitmap(myBitmap, "Horizontally");
                break;
            case 3:
                myBitmap = rotateBitmap(myBitmap, 180);
                break;
            case 4:
                myBitmap = rotateBitmap(myBitmap, 270);
                myBitmap = FlipBitmap(myBitmap, "Horizontally");
                break;
            case 5:
                myBitmap = rotateBitmap(myBitmap, 90);
                myBitmap = FlipBitmap(myBitmap, "Horizontally");
                break;
            case 6:
                myBitmap = rotateBitmap(myBitmap, 90);
                break;
            case 7:
                myBitmap = rotateBitmap(myBitmap, 90);
                myBitmap = FlipBitmap(myBitmap, "Vertically");
                break;
            case 8:
                myBitmap = rotateBitmap(myBitmap, 270);
                break;
            default:
                break;
        }

        return myBitmap;
    }


    public File createVideoFileName(File videoFolder) throws IOException {
        File videoFile = File.createTempFile("tempFile", ".mp4", videoFolder);
        Log.d("test", "getAbsolutePath: " + videoFile.getAbsolutePath());
        return videoFile;
    }


    public File createImageFileName(File imageFolder) throws IOException {

        File imageFile = File.createTempFile("tempFile", ".jpg", imageFolder);
        return imageFile;
    }


    public File[] createMediaFolders() {
        //create folders for media files
        File directory = mContext.getFilesDir();

        File videoFolder = new File(directory, "capturesFromElderlyApp");
        File imageFolder = new File(directory, "capturesFromElderlyApp");

        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
        }
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }

        File[] returnArray = {videoFolder, imageFolder};
        return returnArray;
    }
}

