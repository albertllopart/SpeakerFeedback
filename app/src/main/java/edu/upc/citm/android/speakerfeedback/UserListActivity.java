package edu.upc.citm.android.speakerfeedback;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    List<String> usuaris;
    private RecyclerView user_list;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration usersRegistration;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_holder);

        onStart();

        usuaris = new ArrayList<>();

        user_list = findViewById(R.id.user_list);
        user_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter();
        user_list.setAdapter(adapter);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView user_list;

        public ViewHolder(View itemView){
            super(itemView);
            this.user_list = itemView.findViewById(R.id.user_name_view);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<ViewHolder>{
        @Override
        public int getItemCount() { return usuaris.size();}


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.usertemplate, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String user_name = usuaris.get(position);
            holder.user_list.setText(user_name);

        }
    }

    //rebre info d'usuaris a temps real
    private EventListener<QuerySnapshot> usersListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e != null)
            {
                Log.e("SpeakerFeedback", "Error al rebre usuaris dins d'un room", e);
                return;
            }

            //textview.setText(String.format("Numuser: %d", documentSnapshots.size()));

            updateList(documentSnapshots);

            //textview.setText(nomsUsuaris);
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();

        usersRegistration = db.collection("users").whereEqualTo("room", "testroom").addSnapshotListener(usersListener);
    }

    @Override
    protected void onStop() {
        usersRegistration.remove();
        super.onStop();
    }

    private void updateList(QuerySnapshot documentSnapshots)
    {
        usuaris.clear();
        for (DocumentSnapshot doc : documentSnapshots)
        {
            usuaris.add(doc.getString("name"));
        }
        adapter.notifyDataSetChanged();
    }
}
