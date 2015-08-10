package com.kimo.lib.alexei;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Main class that configures operations over images. You need to pass an {@link android.widget.ImageView} or a {@link android.graphics.Bitmap}
 * and the {@link com.kimo.lib.alexei.Calculus} that you be applied over the given image. Due to this, retrieving the result requires a callback as an
 * {@link com.kimo.lib.alexei.Answer}
 */
public class Alexei {

    private static Alexei singleton = null;
    private Context mContext;
    private Bitmap mImage;

    private Alexei(Context context) {
        mContext = context;
    }

    /**
     *
     * Method that access Alexei.
     *
     * @param context that Alexei will perform its operations
     * @return the singleton instance of Alexei
     */
    public static Alexei with(Context context) {
        if (singleton == null) {
            synchronized (Alexei.class) {
                if (singleton == null)
                    singleton = new Alexei(context);
            }
        }

        return singleton;
    }

    /**
     * Creates a {@link com.kimo.lib.alexei.CalculusBuilder} object that will perform the operations
     * using a given ImageView
     * @param image that will be passed over to {@link com.kimo.lib.alexei.CalculusBuilder}
     * @return a calculus builder object
     */
    public CalculusBuilder analyze(ImageView image) {

        if(image == null)
            throw new IllegalArgumentException("Image must not be null");

        mImage = Utils.getBitmapFromImageView(image);

        return new CalculusBuilder(mImage);
    }

    public CalculusBuilder analyze(Bitmap image) {

        if(image == null)
            throw new IllegalArgumentException("Image must not be null");

        mImage = image;

        return new CalculusBuilder(mImage);
    }
}
