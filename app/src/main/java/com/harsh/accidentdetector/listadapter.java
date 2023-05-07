package com.harsh.accidentdetector;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class listadapter extends RecyclerView.Adapter<listadapter.MyViewHolder> {

    private Context context;
    private List<User> userList;
    public listadapter(Context context) {
        this.context = context;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public listadapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_contacts, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listadapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvFirstName.setText(this.userList.get(position).Name);
        holder.tvLastName.setText(this.userList.get(position).Number);
        holder.delete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             deleteall(position);

             loadUserList();
             setUserList(userList);

         }
     });
    }

    @Override
    public int getItemCount() {
        return  this.userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvFirstName;
        TextView tvLastName;
        ImageView delete;

        public MyViewHolder(View view) {
            super(view);
            tvFirstName = view.findViewById(R.id.textView);
            tvLastName = view.findViewById(R.id.textView2);
delete=view.findViewById(R.id.deletelogo);
        }
    }

    private void deleteall(int position){
        User user=userList.get(position);
        AppDatabase daa=AppDatabase.getDbInstance(this.context);
        daa.userDao().delete(user);

    }
    private void loadUserList() {
        AppDatabase db = AppDatabase.getDbInstance(this.context);
        userList =db.userDao().getAllUsers();
//        db.userDao().update();
    }

}

