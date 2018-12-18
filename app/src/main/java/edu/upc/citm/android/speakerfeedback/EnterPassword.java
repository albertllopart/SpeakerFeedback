package edu.upc.citm.android.speakerfeedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EnterPassword extends AppCompatActivity {

    private EditText edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        edit_text = findViewById(R.id.password_edit);
    }

    public void onEnterPassword(View view) {

        String password = edit_text.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
        finish();
    }
}
