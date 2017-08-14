package id.ac.its.sikemastc.activity.verifikasi_qr_code;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import id.ac.its.sikemastc.utilities.NetworkUtils;

/**
 * Created by nurro on 7/28/2017.
 */

public class QRCodeScanner {
    private static String link;
    private static ArrayList<String[]> ruang;

    public QRCodeScanner() {
        this.link = NetworkUtils.LIST_RUANG;
        this.ruang = new ArrayList<>();
        updateRuang();
    }

    public static void updateRuang() {
        link = NetworkUtils.LIST_RUANG;
        ruang = new ArrayList<>();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        String line = null;
        try {
            URLConnection url = new URL(link).openConnection();

            HttpURLConnection con = (HttpURLConnection)url;

            InputStream inputStream = new BufferedInputStream(con.getInputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            while((line = br.readLine()) != null){
                sb.append(line+"\n");
            }
            inputStream.close();
            String result = sb.toString();

            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++){
                if (jsonArray.getJSONObject(i).getString("md5") != null) {
                    ruang.add(new String[]{jsonArray.getJSONObject(i).getString("id"),
                            jsonArray.getJSONObject(i).getString("kode_ruangan"),
                            jsonArray.getJSONObject(i).getString("md5")});
                }
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static String decryptQRCode(String qrCode) {
        updateRuang();
        String result = "Gagal";
        if (qrCode.length() % 4 == 0) {
            try {
                byte[] digest = Base64.decode(qrCode, Base64.DEFAULT);
                String text = new String(digest, "UTF-8");
                Log.d("QR Code", text);
                Log.d("Cek Ruang", Integer.toString(ruang.size()));
                for (int i = 0; i < ruang.size(); i++) {
                    if (ruang.get(i)[2].equals(text)) {
                        result = ruang.get(i)[1];
                    }
                }

            }
            catch (Exception ex) {

            }
        }

        return result;
    }
}
