package id.ac.its.sikemastc.activity.verifikasi_tandatangan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

/**
 * Created by novitarpl on 5/18/2017.
 */

public class PencocokanTandaTangan extends AppCompatActivity {
    private int x1, x2, x3, x4;
    private int xa,ya, xb, yb, xc, yc, xd, yd, i, j;
    private int baris, kolom;
    private int tinggi_img_baru, lebar_img_baru;
    private int lebar_new, tinggi_new;
    private double lebar_new2, tinggi_new2;
    private ImageView image_source, image_source_crop;
    private Bitmap bitmap1, bitmap2, new_img1;
    private double vertical_max, horisontal_max, luas_bounding_box, Ratio, pixel_area, normalize_area;
    private String userTerlogin;
    private String idUserTerlogin;
    private String idPerkuliahan;
    private ProgressDialog progressDialog;

    private void preprocessing(Bitmap bitmap) {

        Bitmap bitmap3=bitmap;
        bitmap3.getHeight();
        bitmap3.getWidth();

        int tinggi_img1 =bitmap3.getHeight();
        int lebar_img1= bitmap3.getWidth();

        int R=0;
        Bitmap gray_1 = Bitmap.createBitmap( bitmap3.getWidth(), bitmap3.getHeight(), bitmap3.getConfig());

        for(ya=0; ya<tinggi_img1; ya++) {
            for (xa = 0; xa < lebar_img1; xa++) {
                int pixel = bitmap3.getPixel(xa, ya);
                R = Color.red(pixel);
                if (R > 128)
                    R = 255;
                else
                    R = 0;
                gray_1.setPixel(xa, ya, Color.rgb(R, R, R));
                if (R == 0) {
                    x1 = ya;
                    break;}
            }

            if (R == 0) {break;}
        }


        for( xb=0; xb<lebar_img1; xb++){
            for(yb=0; yb<tinggi_img1; yb++) {
                int pixel = bitmap3.getPixel(xb, yb);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                gray_1.setPixel(xb, yb, Color.rgb(R, R, R));

                if (R == 0) {
                    x2 = xb;
                    break; }
            }
            if (R == 0){break;}
        }

        for(xc=lebar_img1-1; xc >=0; xc--){
            for(yc=0; yc < tinggi_img1; yc++){
                int pixel = bitmap3.getPixel(xc, yc);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                gray_1.setPixel(xc, yc, Color.rgb(R, R, R));

                if (R == 0) {
                    x3 = xc;
                    break;}
            }
            if (R == 0){break;}
        }

        for(yd=tinggi_img1-1; yd>=0; yd--){
            for(xd=0; xd<lebar_img1; xd++){
                int pixel = bitmap3.getPixel(xd, yd);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                gray_1.setPixel(xd, yd, Color.rgb(R, R, R));

                if (R == 0) {
                    x4 = yd;
                    break;}
            }
            if (R == 0){break;}
        }
        lebar_new = x3-x2;
        tinggi_new = x4-x1;


        new_img1 = Bitmap.createBitmap(lebar_new,tinggi_new,Bitmap.Config.RGB_565);

        for(i=0; i<lebar_new; i++){
            //Log.v("log_tag", "lala");
            for(j=0; j<tinggi_new; j++){
                int pixel = bitmap3.getPixel(x2+i, x1+j);
                new_img1.setPixel(i,j,pixel);
            }

        }

        //ekstraksi fitur
        tinggi_img_baru = new_img1.getHeight();
        lebar_img_baru =new_img1.getWidth();
        int kolom_max=0, kolom_temp;
        Bitmap gray_img_crop = Bitmap.createBitmap( bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        for(ya=0; ya<tinggi_img_baru; ya++) {
            for (xa = 0; xa < lebar_img_baru; xa++) {
                int pixelnya = new_img1.getPixel(xa, ya);
                // Log.v("log_tag", "pixel for 1 ->" + pixel);
                R = Color.red(pixelnya);
                if (R > 128)
                    R = 255;
                else
                    R = 0;
                gray_img_crop.setPixel(xa, ya, Color.rgb(R, R, R));
            }
        }

        //max horisontal;
        for(baris=0; baris<tinggi_img_baru; baris++){
            kolom_temp=0;
            for(kolom=0; kolom<lebar_img_baru; kolom++){
                int pixel = gray_img_crop.getPixel(kolom, baris);
                // Log.v("log_tag", "warna img1 kolom ke -> " + kolom + "warna -> "+gray_img_crop.getPixel(kolom, baris));
                if(pixel == Color.BLACK){
                    kolom_temp=kolom_temp+1;
                    //   Log.v("log_tag", "kolom temp-> " + kolom_temp);
                }
            }
            if(kolom_temp > kolom_max) {
                kolom_max = kolom_temp;
                horisontal_max = baris;
            }

        }

        //max vertical;
        for(kolom=0; kolom<lebar_img_baru; kolom++){
            kolom_temp=0;
            for(baris=0; baris<tinggi_img_baru; baris++){
                int pixel = gray_img_crop.getPixel(kolom, baris);
                // Log.v("log_tag", "warna img1 kolom ke -> " + kolom + "warna -> "+gray_img_crop.getPixel(kolom, baris));
                if(pixel == Color.BLACK){
                    kolom_temp=kolom_temp+1;
                    //   Log.v("log_tag", "kolom temp-> " + kolom_temp);
                }
            }
            if(kolom_temp > kolom_max) {
                kolom_max = kolom_temp;
                vertical_max = kolom;
            }
        }

        //aspect ratio
        lebar_new2 =lebar_new;
        tinggi_new2 = tinggi_new;
        Ratio = lebar_new2/tinggi_new2;
        //  Log.v("log_tag", "ratio -> " + Ratio);

        //number black pixel in area
        pixel_area=0;
        for(baris=0; baris<tinggi_img_baru; baris++){
            for(kolom=0; kolom<lebar_img_baru; kolom++){
                int pixel = gray_img_crop.getPixel(kolom, baris);
                if(pixel == Color.BLACK){
                    pixel_area=pixel_area+1;
                }
            }
        }

        //center of mass
        //Bitmap gray_img_crop = Bitmap.createBitmap( bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        Mat imgMAT = new Mat();
        Utils.bitmapToMat(gray_img_crop, imgMAT);
        Mat grad = new Mat();
        Imgproc.cvtColor(imgMAT, grad, Imgproc.COLOR_BGR2GRAY);
        Mat img_sobel = new Mat();
        Mat img_threshold = new Mat();

        //Bitmap image;
        Imgproc.Sobel(grad, img_sobel, CvType.CV_8U, 1, 0, 3, 1, 0, Core.BORDER_DEFAULT);

        Imgproc.threshold(img_sobel, img_threshold, 0, 255, Imgproc.THRESH_OTSU + THRESH_BINARY);

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 13));

        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_CLOSE, element);

        Imgproc.cvtColor(imgMAT, imgMAT, Imgproc.COLOR_BGR2GRAY);


        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(img_threshold, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE, new org.opencv.core.Point(0, 0));

        //list moment/center of mask
        List<MatOfPoint2f> mc = new ArrayList<MatOfPoint2f>(contours.size());

        List<Moments> mu = new ArrayList<Moments>(contours.size());


        //    Log.v("log_tag", "contour size -> " + contours.size());
        Integer centroid[][]= new Integer[i][2];
        for (int i = 0; i < contours.size(); i++) {

            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );

            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            org.opencv.core.Rect rect = Imgproc.boundingRect(points);
            //    Log.v("log_tag", "rect -> " + rect);

            // get area of signature
            luas_bounding_box = rect.area();
            //    Log.v("log_tag", "area -> " + rect.area());

            // get line bounding box
            Imgproc.rectangle(imgMAT, rect.tl(), rect.br(), new Scalar(128, 128, 128), 2);

            //center of mass
            mu.add(i, Imgproc.moments(contours.get(i), false));
            Moments p = mu.get(i);
            int x = (int) (p.get_m10() / p.get_m00());
            int y = (int) (p.get_m01() / p.get_m00());

            //array untuk menyimpan data x,y bounding box

            centroid[i][0]=x;
            centroid[i][1]=y;
            // Toast.makeText(this, "centroid ke->" + i+ "nilai x ->" + centroid[i][0] + "nilai y ->" + centroid[i][1], Toast.LENGTH_LONG).show();

            // Mat rgbaImage = new Mat();
            Imgproc.circle(imgMAT, new Point(x, y), 10, new Scalar(255,215,0,255));
            //    Log.v("log_tag", "x -> " + x + ", i -> " + i);
            //    Log.v("log_tag", "y -> " + y + ",  i -> " + i);
            String file = Environment.getExternalStorageDirectory().getPath() + "/DCIM/DigitSign/"+"/Center_of_Mass.jpg";
            Imgcodecs.imwrite(file,imgMAT);
        }

        //normalize area signature
        double luas_signature_crop= lebar_new2*tinggi_new2;
        normalize_area = luas_signature_crop/luas_bounding_box;

    }
    public double calculate_skew(String image){
        Mat img = Imgcodecs.imread( image, Imgcodecs.IMREAD_GRAYSCALE );

        //Binarize it
        //Use adaptive threshold if necessary
        //Imgproc.adaptiveThreshold(img, img, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, 40);
        Imgproc.threshold( img, img, 200, 255, THRESH_BINARY );

        //Invert the colors (because objects are represented as white pixels, and the background is represented by black pixels)
        Core.bitwise_not( img, img );
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

        //We can now perform our erosion, we must declare our rectangle-shaped structuring element and call the erode function
        Imgproc.erode(img, img, element);

        //Find all white pixels
        Mat wLocMat = Mat.zeros(img.size(),img.type());
        Core.findNonZero(img, wLocMat);

        //Create an empty Mat and pass it to the function
        MatOfPoint matOfPoint = new MatOfPoint( wLocMat );

        //Translate MatOfPoint to MatOfPoint2f in order to user at a next step
        MatOfPoint2f mat2f = new MatOfPoint2f();
        matOfPoint.convertTo(mat2f, CvType.CV_32FC2);

        //Get rotated rect of white pixels
        RotatedRect rotatedRect = Imgproc.minAreaRect( mat2f );

        Point[] vertices = new Point[4];
        rotatedRect.points(vertices);
        List<MatOfPoint> boxContours = new ArrayList<>();
        boxContours.add(new MatOfPoint(vertices));
        Imgproc.drawContours( img, boxContours, 0, new Scalar(128, 128, 128), -1);

        double resultAngle = rotatedRect.angle;
        if (rotatedRect.size.width > rotatedRect.size.height)
        {
            rotatedRect.angle += 90.f;
        }

        Mat result = deskew( img, rotatedRect.angle );
        String outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/signature/skew.png";

        Imgcodecs.imwrite( outputFile, result );
        return resultAngle;
    }

    public Mat deskew(Mat src, double angle){
        Point center = new Point(src.width()/2, src.height()/2);
        Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        //1.0 means 100 % scale
        Size size = new Size(src.width(), src.height());
        Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
        return src;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tandatangan_berhasil);

        Intent intent = getIntent();
        userTerlogin = intent.getStringExtra("user_terlogin"); //nrp-nama
        idUserTerlogin = intent.getStringExtra("id_user_terlogin");
        idPerkuliahan = intent.getStringExtra("id_perkuliahan");

        Log.v("log_tag", "identitas mahasiswa di pencocokantandatangan -> " + userTerlogin);
        Log.v("log_tag", "id perkuliahan di pencocokantandatangan -> " + idPerkuliahan);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Pengecekan Tanda Tangan... ");
        progressDialog.show();



        Bitmap bitmap1;

        String ImageSourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/signatureverification/"+ userTerlogin;
        String ImageSignaturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/signature/"+ userTerlogin+".png";

        Log.v("log_tag", "image source -> " + ImageSourcePath);
        Log.v("log_tag", "image signature -> " + ImageSignaturePath);

        //FITUR DATABASE MHS
        Double[][] fitur_ttd = new Double[6][7];
        ArrayList<Double> fitur_ttd_a = new ArrayList<>();
        ArrayList<Double> fitur_ttd_b = new ArrayList<>();
        ArrayList<Double> fitur_ttd_c = new ArrayList<>();
        ArrayList<Double> fitur_ttd_d= new ArrayList<>();
        ArrayList<Double> fitur_ttd_e= new ArrayList<>();
        ArrayList<Double> fitur_ttd_f= new ArrayList<>();
        ArrayList<Double> fitur_ttd_g= new ArrayList<>();

        for(int index=0; index<5; index++)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize=8;
            String filename = userTerlogin + "-" + (index) + ".png";
            bitmap1 = BitmapFactory.decodeFile(ImageSourcePath +"/"+ filename);
            preprocessing(bitmap1);

            double skew = calculate_skew(ImageSourcePath + "/" + filename);

            fitur_ttd[index][0]=horisontal_max; fitur_ttd_a.add(horisontal_max);
            fitur_ttd[index][1]=vertical_max; fitur_ttd_b.add(vertical_max);
            fitur_ttd[index][2]=Ratio; fitur_ttd_c.add(Ratio);
            fitur_ttd[index][3]=luas_bounding_box;fitur_ttd_d.add(luas_bounding_box);
            fitur_ttd[index][4]=pixel_area; fitur_ttd_e.add(pixel_area);
            fitur_ttd[index][5]=normalize_area; fitur_ttd_f.add(normalize_area);
            fitur_ttd[index][6]=skew; fitur_ttd_g.add(skew);

            //centerOfMass(bitmap1);
            //Toast.makeText(this, "centroid ke->" + i+ "nilai x ->" + centroid[i][0] + "nilai y ->" + centroid[i][1], Toast.LENGTH_SHORT).show();
        }

        //FITUR TTD MHS
        ArrayList<Double> fitur_ttd_mhs = new ArrayList<>();
        bitmap2 = BitmapFactory.decodeFile(ImageSignaturePath);
        preprocessing(bitmap2);

        double skew = calculate_skew(ImageSignaturePath);

        fitur_ttd_mhs.add(horisontal_max); fitur_ttd_a.add(horisontal_max);
        fitur_ttd_mhs.add(vertical_max); fitur_ttd_b.add(vertical_max);
        fitur_ttd_mhs.add(Ratio); fitur_ttd_c.add(Ratio);
        fitur_ttd_mhs.add(luas_bounding_box); fitur_ttd_d.add(luas_bounding_box);
        fitur_ttd_mhs.add(pixel_area);fitur_ttd_e.add(pixel_area);
        fitur_ttd_mhs.add(normalize_area);fitur_ttd_f.add(normalize_area);
        fitur_ttd_mhs.add(skew); fitur_ttd_g.add(skew);

        //NORMALISASI
        ArrayList<Double> max= new ArrayList<>();
        ArrayList<Double> min = new ArrayList<>();
        max.add(Collections.max(fitur_ttd_a));
        min.add(Collections.min(fitur_ttd_a));
        max.add(Collections.max(fitur_ttd_b));
        min.add(Collections.min(fitur_ttd_b));
        max.add(Collections.max(fitur_ttd_c));
        min.add(Collections.min(fitur_ttd_c));
        max.add(Collections.max(fitur_ttd_d));
        min.add(Collections.min(fitur_ttd_d));
        max.add(Collections.max(fitur_ttd_e));
        min.add(Collections.min(fitur_ttd_e));
        max.add(Collections.max(fitur_ttd_f));
        min.add(Collections.min(fitur_ttd_f));
        max.add(Collections.max(fitur_ttd_g));
        min.add(Collections.min(fitur_ttd_g));

        Double[][] fitur_ttd_normalisasi = new Double[6][8];
        ArrayList<Double> fitur_ttd_mhs_normalisasi = new ArrayList<>();
        Double norm_atas, norm_bawah;

        for(int index=0; index<5; index++){
            for(int i=0; i<7; i++)
            {
                norm_atas= fitur_ttd[index][i]-min.get(i);
                norm_bawah= max.get(i)-min.get(i);
                fitur_ttd_normalisasi[index][i]=norm_atas/norm_bawah;
                if(index==1)
                {
                    norm_atas= fitur_ttd_mhs.get(i)-min.get(i);
                    norm_bawah= max.get(i)-min.get(i);
                    fitur_ttd_mhs_normalisasi.add(norm_atas/norm_bawah);
                }

            }
        }

        //EUCLIDEAN DISTANCE
        ArrayList<Double> fitur_a = new ArrayList<>();
        ArrayList<Double> fitur_b = new ArrayList<>();
        ArrayList<Double> fitur_c = new ArrayList<>();
        ArrayList<Double> fitur_d= new ArrayList<>();
        ArrayList<Double> fitur_e= new ArrayList<>();
        ArrayList<Double> fitur_f= new ArrayList<>();
        ArrayList<Double> fitur_g= new ArrayList<>();

        for(int i=0; i<5; i++)
        {
            fitur_a.add(fitur_ttd_normalisasi[i][0]-fitur_ttd_mhs_normalisasi.get(0));
            fitur_b.add(fitur_ttd_normalisasi[i][1]-fitur_ttd_mhs_normalisasi.get(1));
            fitur_c.add(fitur_ttd_normalisasi[i][2]-fitur_ttd_mhs_normalisasi.get(2));
            fitur_d.add(fitur_ttd_normalisasi[i][3]-fitur_ttd_mhs_normalisasi.get(3));
            fitur_e.add(fitur_ttd_normalisasi[i][4]-fitur_ttd_mhs_normalisasi.get(4));
            fitur_f.add(fitur_ttd_normalisasi[i][5]-fitur_ttd_mhs_normalisasi.get(5));
            fitur_g.add(fitur_ttd_normalisasi[i][6]-fitur_ttd_mhs_normalisasi.get(6));
        }

        ArrayList<Double> euclidean= new ArrayList<>();
        double f1,f2,f3,f4,f5,f6,f7;
        for(int i=0; i<5;i++)
        {
            f1= Math.pow((fitur_a.get(i)),2.0); //vertical
            f2= Math.pow((fitur_b.get(i)),2.0); //horisontal
            f3= Math.pow((fitur_c.get(i)),2.0); //ratio
            f4= Math.pow((fitur_d.get(i)),2.0); //bounding box
            f5= Math.pow((fitur_e.get(i)),2.0); //pixel area
            f6= Math.pow((fitur_f.get(i)),2.0); //normalize area
            f7= Math.pow((fitur_g.get(i)),2.0); // skew

            euclidean.add(Math.sqrt(f1+f3+f5+f7));
            Log.v("log_tag", "euclidean -> "+ i + "-> " + euclidean.get(i));
        }

        if(euclidean.get(0)<1 || euclidean.get(1)<1 || euclidean.get(2)<1 || euclidean.get(3)<1 || euclidean.get(4)<1)
        {
            progressDialog.dismiss();
            sendStatus();
            TextView verifikasi = (TextView) findViewById(R.id.tv_user_verifikasi_ttd);
            verifikasi.setText("Berhasil Presensi");
        }
        else
        {
            progressDialog.dismiss();
            TextView verifikasi = (TextView) findViewById(R.id.tv_user_verifikasi_ttd);
            verifikasi.setText("Gagal Melakukan Presensi");

            Toast.makeText(this, "Absen Tidak Berhasil", Toast.LENGTH_SHORT).show();
        }

        TextView user_login = (TextView) findViewById(R.id.tv_user_detail_ttd);
        user_login.setText(userTerlogin);

        Button finish = (Button) findViewById(R.id.btn_finish_verifikasi_ttd);
        finish.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_finish_verifikasi_ttd:
                    Intent intentToStart = new Intent(getBaseContext(), KelolaDataSetTandaTangan.class);
                    startActivityForResult(intentToStart, 1);
                    finish();
                    break;
            }
        }
    };



    private void sendStatus() {

    StringRequest stringRequest;
    RequestQueue requestQueue = Volley.newRequestQueue(this);

    stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.KIRIM_STATUS_KEHADIRAN,
            new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Showing toast message of the response
                        Toast.makeText(PencocokanTandaTangan.this, "Verifikasi Kehadiran Berhasil", Toast.LENGTH_SHORT).show();
                        Log.d("VolleyResponse", "Dapat Response Volley Berhasil Kirim Status Absensi");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("VolleyErroyResponse", "Error");
                        //Showing toast
                        Toast.makeText(PencocokanTandaTangan.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Adding parameters
                params.put("id_perkuliahan",idPerkuliahan);
                params.put("nrp", idUserTerlogin);
                params.put("status", "M");

                return params;
            }
        };
        //Adding request to the queue
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}