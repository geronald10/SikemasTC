package id.ac.its.sikemastc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import id.ac.its.sikemastc.R;

public class AddSetWajah extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_set_wajah);

        final Intent intent = getIntent();
        final String userInfo = intent.getStringExtra("identitas_mahasiswa");

        TextView textName = (TextView) findViewById(R.id.tv_user_detail);
        textName.setText(userInfo);

        Button btnStart = (Button) findViewById(R.id.btn_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToStart = new Intent(v.getContext(), AddSetWajahPreview.class);
                intentToStart.putExtra("Name", userInfo);
                intentToStart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentToStart.putExtra("Method", AddSetWajahPreview.TIME);

                // Add photos to "Training" folder
                if (isNameAlreadyUsed(new FileHelper().getTrainingList(), userInfo)) {
                    Toast.makeText(getApplicationContext(), "Data Set Wajah sudah dibuat", Toast.LENGTH_SHORT).show();
                } else {
                    intentToStart.putExtra("Folder", "Training");
                    startActivity(intentToStart);
                }
            }
        });
    }

    private boolean isNameAlreadyUsed(File[] list, String name) {
        boolean used = false;
        if (list != null && list.length > 0) {
            for (File person : list) {
                // The last token is the name --> Folder name = Person name
                String[] tokens = person.getAbsolutePath().split("/");
                final String foldername = tokens[tokens.length - 1];
                if (foldername.equals(name)) {
                    used = true;
                    break;
                }
            }
        }
        return used;
    }
}
