package edu.upc.citm.android.speakerfeedback;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {

    private static final int ENTER_PASSWORD = 1;
    private EditText edit_name;
    List<String> rooms;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration roomRegistration;

    private DocumentReference roomRef;
    private String password;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        rooms = new ArrayList<>();

        edit_name = findViewById(R.id.password_edit);

    }

    public void onJoinRoom(View view) {
        roomId = edit_name.getText().toString();

        db.collection("rooms").document(roomId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    // TODO: Missatge

                    return;
                }

                if (!documentSnapshot.contains("open") || !documentSnapshot.getBoolean("open")) {

                    return;
                }

                if (!documentSnapshot.contains("password")) {

                    onRoomJoin();
                }
                else {
                    password = documentSnapshot.getString("password");
                    onEnterPassword();
                }
            }
        });

    }


    private void onEnterPassword(){
        Intent intent = new Intent(this, EnterPassword.class);
        startActivityForResult(intent, ENTER_PASSWORD);
    }

    private void onRoomJoin(){
        Intent intent = new Intent();
        intent.putExtra("name", roomId);
        setResult(RESULT_OK, intent);
        finish();
    }

    private com.google.firebase.firestore.EventListener<QuerySnapshot> roomsListener = new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e != null)
            {
                Log.e("SpeakerFeedback", "Error al rebre rooms", e);
                return;
            }

            updateList(documentSnapshots);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

       roomRegistration = db.collection("rooms").addSnapshotListener(roomsListener);
    }

    private void updateList(QuerySnapshot documentSnapshots){

        rooms.clear();
        for(DocumentSnapshot doc : documentSnapshots){

            rooms.add(doc.getString("name"));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == ENTER_PASSWORD && resultCode == RESULT_OK) {
            String user_password = intent.getStringExtra("password");
            if(password.equals(user_password)){
                onRoomJoin();
            }
            else{
                Toast.makeText(RoomListActivity.this,"INCORRECT PASSWORD", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
