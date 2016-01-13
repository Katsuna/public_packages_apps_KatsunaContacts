package gr.crystalogic.oldmen.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class ImageHelper {

    public static Bitmap getMaskedBitmap(Resources res, Bitmap source, int maskResId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = source;
        bitmap.setHasAlpha(true);

        Bitmap mask = BitmapFactory.decodeResource(res, maskResId);
        bitmap = Bitmap.createScaledBitmap(bitmap, mask.getWidth(), mask.getHeight(), false);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mask, 0, 0, paint);
        mask.recycle();
        return bitmap;
    }
}
