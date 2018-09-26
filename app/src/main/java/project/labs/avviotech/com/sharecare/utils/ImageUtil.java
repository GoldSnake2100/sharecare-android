package project.labs.avviotech.com.sharecare.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ExifInterface;
import android.widget.ImageView;

import com.esafirm.imagepicker.features.imageloader.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import project.labs.avviotech.com.sharecare.R;

/**
 * Created by NJX on 3/10/2018.
 */

public class ImageUtil {

    public static Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        float left = (firstImage.getWidth() - secondImage.getWidth()) / 2;
        float top = (firstImage.getHeight() - secondImage.getHeight()) / 2 - 25;
        canvas.drawBitmap(secondImage, left, top, null);
        return result;
    }

    public static Bitmap resizeMapIcons(Context context, String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public static void setCircularImage(Context context, String url, ImageView imageView) {

        String requestUrlForImage = url;

        if (url.contains("drawable://") || url.contains("file://")) {
            requestUrlForImage = url;
        } else if (url.contains("content://")) {
            requestUrlForImage = url.replace("/photo", "");
            if (requestUrlForImage.contains("display_photo")) {
                requestUrlForImage = "drawable://" + R.drawable.default_user;
            }
        } else {

        }

        if (requestUrlForImage.isEmpty()) {
            imageView.setImageResource(R.drawable.default_user);
        } else {
            Picasso.with(context).load(requestUrlForImage).error(R.drawable.default_user).transform(new CircleTransform()).into(imageView);
        }

    }

    public static File getFileRezie(File imgFileOrig) {
        try {
            if (imgFileOrig == null)
                return null;

            if (!imgFileOrig.exists())
                return null;

            int oneKb = 1 * 1024;
            int limitSize = 100 * oneKb;

            if (imgFileOrig.length() <= limitSize)
                return imgFileOrig;

            ExifInterface exif = new ExifInterface(
                    imgFileOrig.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            File dirTemp = new File(Constant.DIR_LOCAL_IMAGE_TEMP);
            if (!dirTemp.exists())
                dirTemp.mkdirs();

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(imgFileOrig);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > Constant.IMAGE_MAX_SIZE || o.outWidth > Constant.IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(Constant.IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            // we save the file, at least until we have made use of it
            File f = new File(Constant.DIR_LOCAL_IMAGE_TEMP + File.separator + imgFileOrig.getName());
            f.createNewFile();

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(imgFileOrig);
            Bitmap b = BitmapFactory.decodeStream(fis, null, o2);

            fis.close();

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            // compress to the format you want, JPEG, PNG...
            // 70 is the 0-100 quality percentage
            b.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            // write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());

            // remember close de FileOutput
            fo.close();

            b.recycle();

            return f;

        } catch (Exception e) {

        }

        return null;
    }

}
