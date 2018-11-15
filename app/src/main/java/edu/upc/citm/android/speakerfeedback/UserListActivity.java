package edu.upc.citm.android.speakerfeedback;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    List<String> usuaris;
    private RecyclerView user_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_holder);

        usuaris = new ArrayList<>();
        usuaris.add("pauek");
        usuaris.add("aleix");

        user_list = findViewById(R.id.user_list);
        user_list.setLayoutManager(new LinearLayoutManager(this));
        user_list.setAdapter(new UserAdapter());
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
}
