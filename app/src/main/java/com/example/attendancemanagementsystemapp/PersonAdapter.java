package com.example.attendancemanagementsystemapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

    Context context;
    ArrayList<Person> list;
    DatabaseHelper databaseHelper;
    String selectedDate;

    public PersonAdapter(
            Context context,
            ArrayList<Person> list,
            DatabaseHelper databaseHelper,
            String selectedDate) {

        this.context = context;
        this.list = list;
        this.databaseHelper = databaseHelper;
        this.selectedDate = selectedDate;
    }

    public void setDate(String date) {

        selectedDate = date;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_person,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Person person = list.get(position);

        holder.txtName.setText(
                person.name
        );

        String status =
                databaseHelper.getAttendance(
                        person.id,
                        selectedDate
                );

        holder.txtStatus.setText(
                "Status: " + status
        );

        holder.btnPresent.setOnClickListener(v -> {

            databaseHelper.markAttendance(
                    person.id,
                    selectedDate,
                    "Present"
            );

            holder.txtStatus.setText(
                    "Status: Present"
            );

            Toast.makeText(
                    context,
                    person.name + " - Present",
                    Toast.LENGTH_SHORT
            ).show();
        });

        holder.btnAbsent.setOnClickListener(v -> {

            databaseHelper.markAttendance(
                    person.id,
                    selectedDate,
                    "Absent"
            );

            holder.txtStatus.setText(
                    "Status: Absent"
            );

            Toast.makeText(
                    context,
                    person.name + " - Absent",
                    Toast.LENGTH_SHORT
            ).show();
        });

        holder.btnLate.setOnClickListener(v -> {

            databaseHelper.markAttendance(
                    person.id,
                    selectedDate,
                    "Late"
            );

            holder.txtStatus.setText(
                    "Status: Late"
            );

            Toast.makeText(
                    context,
                    person.name + " - Late",
                    Toast.LENGTH_SHORT
            ).show();
        });

        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Person")
                    .setMessage(
                            "Delete " + person.name + "?"
                    )
                    .setPositiveButton(
                            "Yes",
                            (dialog, which) -> {

                                databaseHelper.deletePerson(
                                        person.id
                                );

                                int currentPosition =
                                        holder.getBindingAdapterPosition();

                                if (currentPosition !=
                                        RecyclerView.NO_POSITION) {

                                    list.remove(
                                            currentPosition
                                    );

                                    notifyItemRemoved(
                                            currentPosition
                                    );
                                }

                                Toast.makeText(
                                        context,
                                        "Person deleted",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                    )
                    .setNegativeButton(
                            "No",
                            null
                    )
                    .show();
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtStatus;

        Button btnPresent;
        Button btnAbsent;
        Button btnLate;
        Button btnDelete;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtName =
                    itemView.findViewById(
                            R.id.txtName
                    );

            txtStatus =
                    itemView.findViewById(
                            R.id.txtStatus
                    );

            btnPresent =
                    itemView.findViewById(
                            R.id.btnPresent
                    );

            btnAbsent =
                    itemView.findViewById(
                            R.id.btnAbsent
                    );

            btnLate =
                    itemView.findViewById(
                            R.id.btnLate
                    );

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDelete
                    );
        }
    }
}
