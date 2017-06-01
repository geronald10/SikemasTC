package id.ac.its.sikemastc.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.HalamanUtamaDosen;
import id.ac.its.sikemastc.activity.mahasiswa.HalamanUtamaMahasiswa;
import id.ac.its.sikemastc.activity.orangtua.HalamanUtamaOrangtua;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.sync.DeleteTokenIntentService;
import id.ac.its.sikemastc.sync.MyFirebaseInstanceIDService;
import id.ac.its.sikemastc.utilities.NetworkUtils;

public class LoginActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = LoginActivity.class.getSimpleName();

    private Context mContext;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnSignIn;
    private ProgressDialog pDialog;
    private SikemasSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        // Set up the login form.
        edtEmail = (EditText) findViewById(R.id.edt_email_login);
        edtPassword = (EditText) findViewById(R.id.edt_password_login);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);

        // Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SikemasSessionManager(mContext);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            Intent intent = null;
            String loggedUserId = session.getUserDetails().get(SikemasSessionManager.KEY_USER_ROLE);
            startImmediateDeleteToken(this);
            switch (loggedUserId) {
                case "1":
                    intent = new Intent(LoginActivity.this, HalamanUtamaDosen.class);
                    break;
                case "3":
                    intent = new Intent(LoginActivity.this, HalamanUtamaOrangtua.class);
                    break;
                case "4":
                    intent = new Intent(LoginActivity.this, HalamanUtamaMahasiswa.class);
                    break;
                default:
                    return;
            }
            startActivity(intent);
            finish();
        }

        if (checkPlayServices()) {
            btnSignIn.setOnClickListener(operation);
        }
    }

    View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.btn_sign_in:
                    String email = edtEmail.getText().toString().trim();
                    String password = edtPassword.getText().toString().trim();

                    if (!email.isEmpty() && !password.isEmpty()) {
                        checkLogin(email, password);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        StringRequest strReq;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        pDialog.setMessage("Sign in ...");
        showDialog();

        strReq = new StringRequest(Request.Method.POST,
                NetworkUtils.LOGIN_SIKEMAS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("status");
                    // Check for error node in json
                    if (code.equals("1")) {
                        // user successfully logged in. Create Login session
                        JSONObject user = jsonObject.getJSONObject("user");
                        String userId = user.getString(SikemasContract.UserEntry.KEY_USER_ID);
                        String name = user.getString(SikemasContract.UserEntry.KEY_USER_NAME);
                        String email = user.getString(SikemasContract.UserEntry.KEY_USER_EMAIL);
                        String role = user.getString(SikemasContract.UserEntry.KEY_USER_ROLE);
                        if (role.equals("1")) {
                            String userCode = jsonObject.getString(SikemasContract.UserEntry.KEY_KODE_DOSEN);
                            session.createLoginSession(userId, name, email, role, userCode);
                        } else if (role.equals("3")) {
                            String userCode = "OT";
                            session.createLoginSession(userId, name, email, role, userCode);
                        } else if (role.equals("4")) {
                            String userCode = "MHS";
                            session.createLoginSession(userId, name, email, role, userCode);
                        }
                        checkUserRole(role);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        Toast.makeText(getApplicationContext(),
                                message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_email", email);
                params.put("user_password", password);

                return params;
            }
        };
        // Adding request to request queue
        strReq.setTag(tag_string_req);
        requestQueue.add(strReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void checkUserRole(String userRole) {
        switch (userRole) {
            case "1":
                Intent intentDosen = new Intent(LoginActivity.this, HalamanUtamaDosen.class);
                startActivity(intentDosen);
                break;
            case "4":
                Intent intentMahasiswa = new Intent(LoginActivity.this, HalamanUtamaMahasiswa.class);
                startActivity(intentMahasiswa);
                break;
            case "3":
                Intent intentOrangtua = new Intent(LoginActivity.this, HalamanUtamaOrangtua.class);
                startActivity(intentOrangtua);
                break;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private static void startImmediateDeleteToken(Context context) {
        Intent intentToDeleteToken = new Intent(context, DeleteTokenIntentService.class);
        context.startService(intentToDeleteToken);
    }
}
