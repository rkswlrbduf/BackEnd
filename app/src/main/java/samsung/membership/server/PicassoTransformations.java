package samsung.membership.server;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by KyuYeol on 2017-08-05.
 */

public class PicassoTransformations {

    public static int targetWidth = 200;

    public static Transformation resizeTransformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "resizeTransformation#" + System.currentTimeMillis();
        }
    };
}