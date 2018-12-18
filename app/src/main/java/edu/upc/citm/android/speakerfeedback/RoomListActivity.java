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

    private EditText edit_name;
    List<String> rooms;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration roomRegistration;

    private DocumentReference roomRef;

    private boolean open = false;
    private boolean password_bool = false;
    private String password;
    private String user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        rooms = new ArrayList<>();

        edit_name = findViewById(R.id.password_edit);
    }

    public void onJoinRoom(View view) {
        String name = edit_name.getText().toString();

        if(rooms.contains(name)) {

            roomRef = db.collection("rooms").document(name);

            if (onCheckOpen(name)) {

                if (onCheckPassword(name)){
                    //TODO demanar password
                    onEnterPassword();

                    if (user_password.equals(password)){
                        onRoomJoin(name);
                    }
                    else{
                        Toast.makeText(this, "INCORRECT PASSWORD", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    //TODO entrar a la room
                    onRoomJoin(name);
                }
            }
        }



    }

    public boolean onCheckOpen(String name){

        roomRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.contains("open")) {
                    open = documentSnapshot.getBoolean("open");
                }
            }
        });

        return open;
    }

    public boolean onCheckPassword(String name){

        password_bool = false;

        roomRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains("password")) {
                    password_bool = true;
                    password = documentSnapshot.getString("password");
                }
            }
        });

        return password_bool;
    }

    private void onEnterPassword(){

        Intent intent = new Intent(this, EnterPassword.class);
        startActivity(intent);

    }

    private void onRoomJoin(String name){
        Intent intent = new Intent();
        intent.putExtra("name", name);
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
        if (resultCode == RESULT_OK)
        {
            user_password = intent.getStringExtra("password");
        }
    }
}
