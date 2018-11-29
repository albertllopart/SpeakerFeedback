package edu.upc.citm.android.speakerfeedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PollAnswer extends AppCompatActivity {

    private TextView question;
    private Button option1;
    private Button option2;
    private String poll_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_answer);

        question = findViewById(R.id.answer_question_view);
        option1  = findViewById(R.id.btn_opt_1);
        option2  = findViewById(R.id.btn_opt_2);
        Intent intent  = getIntent();
        question.setText(intent.getStringExtra("question"));
        option1.setText(intent.getStringExtra("option1"));
        option2.setText(intent.getStringExtra("option2"));
        poll_id = intent.getStringExtra("pollid");
    }


    public void onClickOption1(View view) {
        Intent intent = new Intent();
        intent.putExtra("pollid", poll_id);
        intent.putExtra("option", 0);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickOption2(View view) {
        Intent intent = new Intent();
        intent.putExtra("pollid", poll_id);
        intent.putExtra("option", 1);
        setResult(RESULT_OK, intent);
        finish();
    }
}
