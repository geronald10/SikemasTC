package id.ac.its.sikemastc.activity.verifikasi_tandatangan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import id.ac.its.sikemastc.R;

/**
 * Created by novitarpl on 5/16/2017.
 */

public class TambahDataSetTandaTangan extends AppCompatActivity {
    File file;
    LinearLayout mContent;
    signature mSignature;  // fungsi untuk menampilkan dialog pop up
    View view;
    Button btn_tambah_tandatangan_cancel, btn_tambah_tandatangan_clear, btn_tambah_tandatangan_simpan;
    Dialog dialog;
    Bitmap bitmap;
    int clickcount=0;
    Toolbar toolbar;
    String StoredPath;
    private String userTerlogin;
    private TextView userLogin;

    private static final String TAG = "Tambah Data Set Tanda Tangan";
    String DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/signatureverification/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tambah_data_set_tandatangan);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        userTerlogin = intent.getStringExtra("user_terlogin"); //nrp-nama
        Log.v("log_tag", "user ter login di tambahdatasettandatangan -> " + userTerlogin);

        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }


        // Dialog Function
        dialog = new Dialog(TambahDataSetTandaTangan.this);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tambah_data_set_tandatangan);
        dialog.setCancelable(true); // agar tidak muncul dialognya
        dialog_action();

    }


    public void dialog_action() {
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout_tambah_tandatangan);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);

        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btn_tambah_tandatangan_cancel = (Button) dialog.findViewById(R.id.btn_tambah_tandatangan_cancel);
        btn_tambah_tandatangan_clear = (Button) dialog.findViewById(R.id.btn_tambah_tandatangan_clear);
        btn_tambah_tandatangan_simpan = (Button) dialog.findViewById(R.id.btn_tambah_tandatangan_simpan);
        btn_tambah_tandatangan_simpan.setEnabled(false);



        view = mContent;

        btn_tambah_tandatangan_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                btn_tambah_tandatangan_simpan.setEnabled(false);
            }
        });

        btn_tambah_tandatangan_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                //recreate();
            }
        });

        btn_tambah_tandatangan_simpan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickcount=clickcount+1;
                Log.v("log_tag", "clickcount->" +clickcount);
                if (clickcount >5)
                {
                    Toast.makeText(getApplicationContext(), "Dataset Tanda Tangan Sudah Ada", Toast.LENGTH_SHORT).show();
                    btn_tambah_tandatangan_simpan.setEnabled(true);
                }
                if(clickcount<=5)
                {

                    Log.v("log_tag", "Panel Saved");
                    view.setDrawingCacheEnabled(true);
                    mSignature.save(view, StoredPath);
                    Toast.makeText(getApplicationContext(), "Sukses menyimpan Dataset Tanda Tangan ke- " + clickcount, Toast.LENGTH_SHORT).show();
                    // Calling the same class


                }
                mSignature.clear();
                //recreate();


            }
        });
        dialog.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());

            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                StoredPath = DIRECTORY + userTerlogin+ "-" + clickcount+ ".png";
                Log.v("log_tag", "user ter login: " + userTerlogin);
                Log.v("log_tag", "store path ->" + StoredPath);

                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }


        }

        public void clear() {
            path.reset();
            invalidate();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            btn_tambah_tandatangan_simpan.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    // debug("Ignored touch event: " + event.toString());
                    //return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }


        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

    }



}
