package com.rnmediaeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringDef;
import android.util.Log;
import android.util.Base64;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RNMediaEditorModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext _reactContext;
  private static final String TAG = "RNMediaEditorModule";

  public static final int TYPE_IMAGE = 1;
  public static final int TYPE_VIDEO = 2;

  public static Promise _promise;

  public RNMediaEditorModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this._reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNMediaEditor";
  }

  @ReactMethod
  public void embedText(final ReadableMap options, Promise promise) {
    String type = options.getString("type");
    if (type.equals("image")) {
      Log.d("Example", "RNMediaeditor embed text on image called");
      embedTextOnImage(options, promise);
    } else if (type.equals("video")) {
      embedTextOnVideo(options, promise);
    }
  }

  private void embedTextOnImage(final ReadableMap options, final Promise promise) {
    // decode input base64 string to bitmap
    String rawData = options.getString("data");
    byte[] decodedBytes = Base64.decode(rawData, Base64.DEFAULT);

    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    Bitmap.Config bitmapConfig = bitmap.getConfig();
    // set default config if config is none
    if (bitmapConfig == null) {
      bitmapConfig = Bitmap.Config.ARGB_8888;
    }

    bitmap = bitmap.copy(bitmapConfig, true);
    Canvas canvas = new Canvas(bitmap);

    // embed first text on bitmap
    ReadableMap firstText = options.getMap("firstText");

    String backgroundColor = firstText.getString("backgroundColor");
    float backgroundOpacity = (float) (firstText.getDouble("backgroundOpacity"));

    // draw text container container
    Paint containerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    containerPaint.setColor(Color.parseColor(backgroundColor));
    containerPaint.setStyle(Paint.Style.FILL);
    int opacity = (int) (255 * backgroundOpacity);
    containerPaint.setAlpha(opacity);

    String fontColor = firstText.getString("textColor");
    int fontSize = firstText.getInt("fontSize");
    String text = firstText.getString("text");

    // draw text Paint
    Paint textPaint = new Paint();
    textPaint.setColor(Color.parseColor(fontColor));
    textPaint.setTextSize(fontSize);
    textPaint.setTypeface(Typeface.DEFAULT_BOLD);
    int top = firstText.getInt("top");
    int left = firstText.getInt("left");


    boolean isVertical = firstText.getBoolean("vertical");
    if (isVertical) {
      Rect textSize = new Rect();
      textPaint.getTextBounds(text, 0, text.length(), textSize);

      // draw paint in canvas
      canvas.drawRect(left, top, left + textSize.width() + fontSize * 2, top + textSize.height() * 2, containerPaint); // left, top, right, bottom
      canvas.drawText(text, left + fontSize, top + textSize.height() + textSize.height()/2, textPaint);

    } else {

    Rect textSize = new Rect();
    textPaint.getTextBounds(text, 0, text.length(), textSize);

      // draw paint in canvas
      canvas.drawRect(left, top, left + textSize.width() + fontSize * 2, top + textSize.height() * 2, containerPaint); // left, top, right, bottom
      canvas.drawText(text, left + fontSize, top + textSize.height() + textSize.height()/2, textPaint);

    }



    // embed first text on bitmap
    ReadableMap secondText = options.getMap("secondText");

    String backgroundColor2 = secondText.getString("backgroundColor");
    float backgroundOpacity2 = (float) (secondText.getDouble("backgroundOpacity"));

    // draw text container container
    Paint containerPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    containerPaint2.setColor(Color.parseColor(backgroundColor2));
    containerPaint2.setStyle(Paint.Style.FILL);
    int opacity2 = (int) (255 * backgroundOpacity2);
    containerPaint2.setAlpha(opacity2);

    String fontColor2 = secondText.getString("textColor");
    int fontSize2 = secondText.getInt("fontSize");
    String text2 = secondText.getString("text");

    // draw text Paint
    Paint textPaint2 = new Paint();
    textPaint2.setColor(Color.parseColor(fontColor));
    textPaint2.setTextSize(fontSize);
    textPaint2.setTypeface(Typeface.DEFAULT_BOLD);
    int top2 = secondText.getInt("top");
    int left2 = secondText.getInt("left");


    boolean isVertical2 = secondText.getBoolean("vertical");
    if (isVertical2) {

      Rect textSize = new Rect();
      textPaint2.getTextBounds(text2, 0, text2.length(), textSize);

      // draw paint in canvas
      canvas.drawRect(left2, top2, left2 + textSize.width() + fontSize2 * 2, top2 + textSize.height() * 2, containerPaint2); // left, top, right, bottom
      canvas.drawText(text2, left2 + fontSize2, top2 + textSize.height() + textSize.height()/2, textPaint2);

    } else {

      Rect textSize = new Rect();
      textPaint2.getTextBounds(text2, 0, text2.length(), textSize);

      // draw paint in canvas
      canvas.drawRect(left2, top2, left2 + textSize.width() + fontSize2 * 2, top2 + textSize.height() * 2, containerPaint2); // left, top, right, bottom
      canvas.drawText(text2, left2 + fontSize2, top2 + textSize.height() + textSize.height()/2, textPaint2);
    }


    // output
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
    byte[] byteArray = byteArrayOutputStream.toByteArray();

    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);


    WritableMap map = Arguments.createMap();

    map.putString("data", encoded);
    map.putString("message", "success");
    promise.resolve(map);
  }


  @Nullable
  private Throwable writeDataToFile(byte[] data, File file) {
    try {
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(data);
      fos.close();
    } catch (FileNotFoundException e) {
      return e;
    } catch (IOException e) {
      return e;
    }

    return null;
  }

  @Nullable
  private File getOutputFile(int type) {
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

    // Create storage dir if it does not exist
    if (!storageDir.exists()) {
      if (!storageDir.mkdirs()) {
        Log.e(TAG, "Failed to create directory:" + storageDir.getAbsolutePath());
        return null;
      }
    }

    // media file name
    String fileName = String.format("%s", new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));


    if (type == TYPE_IMAGE) {
      fileName = String.format("IMG_%s.jpg", fileName);
    } else if (type == TYPE_VIDEO) {
      fileName = String.format("VID_%s.mp4", fileName);
    } else {
      Log.e(TAG, "Unsupported media type:" + type);
      return null;
    }
    Log.d("example", String.format("%s%s%s", storageDir.getPath(), File.separator, fileName));

    return new File(String.format("%s%s%s", storageDir.getPath(), File.separator, fileName));
  }


  public void embedTextOnVideo(ReadableMap options, Promise promise) {
    this._promise = promise;

    FFmpeg ffmpeg = FFmpeg.getInstance(_reactContext);
    try {
      ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

        @Override
        public void onStart() {
        }

        @Override
        public void onFailure() {
        }

        @Override
        public void onSuccess() {
        }

        @Override
        public void onFinish() {
        }
      });
    } catch (FFmpegNotSupportedException e) {
      // Handle if FFmpeg is not supported by device
    }

    String path = options.getString("path");
    ReadableMap firstText = options.getMap("firstText");
    String text = firstText.getString("text");
    String fontColor = firstText.getString("textColor");
    String backgroundColor = firstText.getString("backgroundColor");
    int fontSize = firstText.getInt("fontSize");
    float backgroundOpaciy = (float)firstText.getDouble("backgroundOpacity");

    ReadableMap secondText = options.getMap("secondText");
    String text2 = secondText.getString("text");
    String fontColor2 = secondText.getString("textColor");
    String backgroundColor2 = secondText.getString("backgroundColor");
    int fontSize2 = secondText.getInt("fontSize");
    float backgroundOpaciy2 = (float)secondText.getDouble("backgroundOpacity");

    File out = getOutputFile(TYPE_VIDEO);

    String[] cmd = new String[] {
            "-i", path, "-c:v", "libx264", "-preset", "ultrafast", "-filter_complex",
            "drawtext=fontfile=/system/fonts/Roboto-Regular.ttf:text=" +
            text + ":x=(w-text_w)/2:y=(h-text_h-line_h)/2" +":fontcolor=" + fontColor + ":fontsize=" + fontSize +
            ":box=1:boxcolor="+backgroundColor+"@"+backgroundOpaciy+":boxborderw="+(fontSize/2) + "," +
            "drawtext=fontfile=/system/fonts/Roboto-Regular.ttf:text=" +
            text2 + ":x=(w-text_w)/2:y=(h-text_h-line_h)/4" +":fontcolor=" + fontColor2 + ":fontsize=" + fontSize2 +
            ":box=1:boxcolor="+backgroundColor2+"@"+backgroundOpaciy2+":boxborderw="+(fontSize2/2),
            out.getAbsolutePath()
    };

    try {
      // to execute "ffmpeg -version" command you just need to pass "-version"
      ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

        @Override
        public void onStart() {
          Log.d("example", "start ffmpeg");
        }

        @Override
        public void onProgress(String message) {
          Log.d("example", "onProgress: " + message);
        }

        @Override
        public void onFailure(String message) {
          Log.e("example", "Error ffmpeg executing with message:\n\t" + message);
        }

        @Override
        public void onSuccess(String message) {
          Log.d("example", "Successfully output file with message:\n\t");
          RNMediaEditorModule._promise.resolve("saved video output");
        }

        @Override
        public void onFinish() {
        }
      });
    } catch (FFmpegCommandAlreadyRunningException e) {
      // Handle if FFmpeg is already running
    }
  }

}
