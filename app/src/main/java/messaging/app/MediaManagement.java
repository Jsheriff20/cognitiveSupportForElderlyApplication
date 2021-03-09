package messaging.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class MediaManagement {

    public Bitmap FlipBitmap(Bitmap myBitmap, String flipType) {

        Matrix matrix = new Matrix();
        switch (flipType){
            case "Horizontally":
                matrix.postScale(-1, 1, myBitmap.getWidth() / 2f, myBitmap.getHeight() / 2f);
                break;
            case "Vertically":
                matrix.postScale(1,-1, myBitmap.getWidth() / 2f, myBitmap.getHeight() / 2f);
                break;
        }

        return Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
    }

    public static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum(
                    (long) (lhs.getWidth() * lhs.getHeight()) /
                            (long) (rhs.getWidth() * rhs.getHeight()));
        }
    }


    public static Size selectOptimalSize(Size[] options, int width, int height){
        List<Size> possibleOptions = new ArrayList<Size>();
        for(Size option: options){

            //check that the dimensions are suitable
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


    public static Size findSimilarAreaSize(Size[] options, int width, int height){
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


    public static Size findSimilarRatio(Size[] options, int width, int height){
        double targetHeightToWidthRatio = (double) width/height;


        double bestRatioDifference = 1000000000f;
        Size mostSimilar = null;

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



    public static Bitmap RotateBitmap(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }


    public void deleteMediaFile(String path, Context context){
        File file = new File(path);
        boolean deleted = file.delete();

        if(!deleted){
            Toast.makeText(context, "Error Deleting file", LENGTH_SHORT).show();
        }
        return;
    }



    public Bitmap adjustBitmapImage(int exifOrientation, Bitmap myBitmap) {

        switch (exifOrientation)
        {
            case 2:
                myBitmap = RotateBitmap(myBitmap, 0);
                myBitmap = FlipBitmap(myBitmap, "Horizontally");
                break;
            case 3:
                myBitmap = RotateBitmap(myBitmap, 180);
                break;
            case 4:
                myBitmap = RotateBitmap(myBitmap, 270);
                myBitmap = FlipBitmap(myBitmap, "Horizontally");
                break;
            case 5:
                myBitmap = RotateBitmap(myBitmap, 90);
                myBitmap = FlipBitmap(myBitmap, "Horizontally");
                break;
            case 6:
                myBitmap = RotateBitmap(myBitmap, 90);
                break;
            case 7:
                myBitmap = RotateBitmap(myBitmap, 90);
                myBitmap = FlipBitmap(myBitmap, "Vertically");
                break;
            case 8:
                myBitmap = RotateBitmap(myBitmap, 270);
                break;
            default:
                break;
        }

        return myBitmap;
    }

}

