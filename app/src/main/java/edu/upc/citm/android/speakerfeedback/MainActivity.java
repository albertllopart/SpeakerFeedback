package edu.upc.citm.android.speakerfeedback;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //MainActivity
    private static final int REGISTER_USER = 0;
    private static final int JOIN_ROOM = 1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference roomRef;

    private TextView textview;
    private String userId;
    private String roomId;

    private List<Poll> polls = new ArrayList<>();
    private Map<String, Poll> polls_map = new HashMap<>();

    private RecyclerView polls_view;
    private Adapter adapter;

    private List<String> options = new ArrayList<>();


    public void OnClickBarra(View view) {
       Intent intent = new Intent(this, UserListActivity.class);
       intent.putExtra("roomId", roomId);
       startActivity(intent);
    }

    private void startFirestoreListenerService(){
        Intent intent =  new Intent(this, FirestoreListenerService.class);
        intent.putExtra("room",roomId);
        startService(intent);
    }

    private void stopFirestoreListenerService(){
        Intent intent = new Intent(this, FirestoreListenerService.class);
        stopService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = findViewById(R.id.textview);

        polls_view = findViewById(R.id.polls_view);
        adapter = new Adapter();

        polls_view.setLayoutManager(new LinearLayoutManager(this));
        polls_view.setAdapter(adapter);

        getOrRegisterUser();
    }

    @Override
    protected void onDestroy() {
        onDestroyUser();
        stopFirestoreListenerService();
        super.onDestroy();
    }

    private void onRoomClose(){
        stopFirestoreListenerService();
        finish();
    }

    private EventListener<DocumentSnapshot> roomListener = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
            if (e != null)
            {
                Log.e("SpeakerFeedback", "Error al rebre rooms", e);
                return;
            }

            if (!documentSnapshot.contains("open") || !documentSnapshot.getBoolean("open")) {
                onRoomClose();
            }

            String name = documentSnapshot.getString("name");
            setTitle(name);
        }
    };

    private EventListener<QuerySnapshot> usersListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e != null)
            {
                Log.e("SpeakerFeedback", "Error al rebre usuaris dins d'un room", e);
                return;
            }

            textview.setText(String.format("Numuser: %d", documentSnapshots.size()));

            String nomsUsuaris = "";
            for (DocumentSnapshot doc : documentSnapshots)
            {
                nomsUsuaris += doc.getString("name") + "\n";
            }

            //textview.setText(nomsUsuaris);
        }
    };

    private EventListener<QuerySnapshot> pollslistener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e != null)
            {
                Log.e("SpeakerFeedback", "Error al rebre la llista de polls", e);
                return;
            }

            polls.clear();
            for(DocumentSnapshot doc: documentSnapshots)
            {
                Poll poll = doc.toObject(Poll.class);
                poll.setPoll_id(doc.getId());
                polls.add(poll);
                polls_map.put(doc.getId(), poll);
            }
            Log.i("SpeakerFeedback", String.format("He carregat %d polls", polls.size()));
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void setUpSnapshotListeners() {
        roomRef = db.collection("rooms").document(roomId);
        roomRef.addSnapshotListener(this, roomListener);
        roomRef.collection("polls").orderBy("start", Query.Direction.DESCENDING)
                .addSnapshotListener(this, pollslistener);
        db.collection("users").whereEqualTo("room", roomId)
                .addSnapshotListener(this, usersListener);
    }


    private void getOrRegisterUser() {
        // Busquem a les preferències de l'app l'ID de l'usuari per saber si ja s'havia registrat
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        userId = prefs.getString("userId", null);
        if (userId == null) {
            // Hem de registrar l'usuari, demanem el nom
            Intent intent = new Intent(this, RegisterUserActivity.class);
            startActivityForResult(intent, REGISTER_USER);
            Toast.makeText(this, "Encara t'has de registrar", Toast.LENGTH_SHORT).show();

        } else {
            // Ja està registrat, mostrem el id al Log
            Log.i("SpeakerFeedback", "userId = " + userId);
            getOrJoinRoom();

        }
    }

    private void getOrJoinRoom(){
        Intent intent = new Intent(this, RoomListActivity.class);
        startActivityForResult(intent, JOIN_ROOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REGISTER_USER:
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    registerUser(name);
                } else {
                    Toast.makeText(this, "Has de registrar un nom", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case JOIN_ROOM:
                if (resultCode == RESULT_OK){
                    String name = data.getStringExtra("name");
                    joinRoom(name);
                } else {
                    Toast.makeText(this, "No has escollit el room...", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void joinRoom(String name){
        roomId = name;
        roomRef = db.collection("rooms").document(roomId);
        db.collection("users").document(userId).update(
                "room", roomId,
                "last_active", new Date());
        startFirestoreListenerService();
        setUpSnapshotListeners();
    }

    private void registerUser(String name) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", name);
        db.collection("users").add(fields).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                // textview.setText(documentReference.getId());
                userId = documentReference.getId();
                SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
                prefs.edit()
                        .putString("userId", userId)
                        .commit();
                Log.i("SpeakerFeedback", "New user: userId = " + userId);
                getOrJoinRoom();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("SpeakerFeedback", "Error creant objecte", e);
                Toast.makeText(MainActivity.this,
                        "No s'ha pogut registrar l'usuari, intenta-ho més tard", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private void onDestroyUser(){
        if (userId != null) {
            db.collection("users").document(userId).update("room", FieldValue.delete());
        }
    }

    private void OnClickPollLabel(int pos) {

        Poll pollid = polls.get(pos);
        if( !pollid.isOpen()){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(pollid.getQuestion());
        String[]options = new String[pollid.getOptions().size()];

        for (int i = 0; i < pollid.getOptions().size(); i++){
            options[i] = pollid.getOptions().get(i);
        }
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("pollid", polls.get(0).getPoll_id());
                map.put("option", i);
                db.collection("rooms").document(roomId).collection("votes").document(userId).set(map);
            }
        });
        builder.create().show();

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView card_view;
        private TextView label_view;
        private TextView question_view;
        private TextView options_view;

        public ViewHolder(View itemView)
        {
            super(itemView);
            card_view     = itemView.findViewById(R.id.card_view);
            label_view    = itemView.findViewById(R.id.label_view);
            question_view = itemView.findViewById(R.id.question_view);
            options_view  = itemView.findViewById(R.id.options_view);
            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(polls.get(pos).isOpen())

                        OnClickPollLabel(pos);
                }
            });

        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.poll_view, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Poll poll = polls.get(position);
            if (position == 0) {
                holder.label_view.setVisibility(View.VISIBLE);
                if (poll.isOpen()) {
                    holder.label_view.setText("Active");
                } else {
                    holder.label_view.setText("Previous");
                }
            } else {
                if (!poll.isOpen() && polls.get(position-1).isOpen()) {
                    holder.label_view.setVisibility(View.VISIBLE);
                    holder.label_view.setText("Previous");
                } else {
                    holder.label_view.setVisibility(View.GONE);
                }
            }
            holder.card_view.setCardElevation(poll.isOpen() ? 10.0f : 0.0f);
            if (!poll.isOpen()) {
                holder.card_view.setCardBackgroundColor(0xFFE0E0E0);
            }
            holder.question_view.setText(poll.getQuestion());
            holder.options_view.setText(poll.getOptionsString());
        }

        @Override
        public int getItemCount() {
            return polls.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.exit_menu_item:
            {
                stopFirestoreListenerService();
                finish();
            }
        }

        return true;
    }
}
