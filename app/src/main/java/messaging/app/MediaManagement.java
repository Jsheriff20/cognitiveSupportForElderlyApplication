package messaging.app;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaManagement {

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
}

