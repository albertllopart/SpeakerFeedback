package edu.upc.citm.android.speakerfeedback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.EventListener;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {

    private EditText edit_name;
    List<String> rooms;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration roomRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        edit_name = findViewById(R.id.password_edit);
    }

    public void onJoinRoom(View view) {
        String name = edit_name.getText().toString();

        if(rooms.contains(name)) {
           // if(db.collection(rooms))
        }



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
}
