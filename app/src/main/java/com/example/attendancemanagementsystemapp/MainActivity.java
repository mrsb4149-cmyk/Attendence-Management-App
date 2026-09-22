package com.example.attendancemanagementsystemapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    TextView txtDate;
    TextView txtPresent;
    TextView txtAbsent;

    EditText edtSearch;

    Button btnDate;
    Button btnAddPerson;

    DatabaseHelper databaseHelper;

    ArrayList<Person> list;

    ArrayList<Person> allPersons;

    PersonAdapter adapter;

    String selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        txtDate = findViewById(R.id.txtDate);
        txtPresent = findViewById(R.id.txtPresent);
        txtAbsent = findViewById(R.id.txtAbsent);
        edtSearch = findViewById(R.id.edtSearch);
        btnDate = findViewById(R.id.btnDate);
        btnAddPerson = findViewById(R.id.btnAddPerson);
        list = new ArrayList<>();
        allPersons = new ArrayList<>();
        adapter = new PersonAdapter(
                this,
                list,
                databaseHelper,
                selectedDate
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(
                adapter
        );
        loadPersons();
        btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPersonDialog();
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int count,
                                      int after) {
                searchPerson(
                        s.toString()
                );

            }
        });
    }

    private void searchPerson(String text) {
        list.clear();

        for (Person person : allPersons) {

            if (person.name
                    .toLowerCase()
                    .contains(
                            text.toLowerCase()
                    )) {

                list.add(person);
            }
        }

        adapter.notifyDataSetChanged();

        updateSummary();

    }

    private void showDatePicker() {
        Calendar calendar =
                Calendar.getInstance();

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (DatePicker view,
                         int year,
                         int month,
                         int dayOfMonth) -> {

                            selectedDate =
                                    String.format(
                                            Locale.getDefault(),
                                            "%04d-%02d-%02d",
                                            year,
                                            month + 1,
                                            dayOfMonth
                                    );

                            txtDate.setText(
                                    "Date: " + selectedDate
                            );

                            adapter.setDate(
                                    selectedDate
                            );

                            updateSummary();
                        },
                        calendar.get(
                                Calendar.YEAR
                        ),
                        calendar.get(
                                Calendar.MONTH
                        ),
                        calendar.get(
                                Calendar.DAY_OF_MONTH
                        )
                );

        dialog.show();

    }

    private void showAddPersonDialog() {
        View view =
                getLayoutInflater()
                        .inflate(
                                R.layout.dialog_add_person,
                                null
                        );

        EditText edtName =
                view.findViewById(
                        R.id.edtName
                );

        EditText edtMobile =
                view.findViewById(
                        R.id.edtMobile
                );

        new AlertDialog.Builder(this)
                .setTitle("Add Person")
                .setView(view)
                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            String name =
                                    edtName.getText()
                                            .toString()
                                            .trim();

                            String mobile =
                                    edtMobile.getText()
                                            .toString()
                                            .trim();

                            if (name.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Enter name",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            boolean result =
                                    databaseHelper.addPerson(
                                            name,
                                            mobile
                                    );

                            if (result) {

                                Toast.makeText(
                                        this,
                                        "Person added",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadPersons();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Error",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();

    }

    private void loadPersons() {
        list.clear();

        allPersons.clear();

        android.database.Cursor cursor =
                databaseHelper.getPersons();

        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "id"
                            )
                    );

            String name =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "name"
                            )
                    );

            String mobile =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "mobile"
                            )
                    );

            Person person =
                    new Person(
                            id,
                            name,
                            mobile,
                            ""
                    );

            list.add(person);

            allPersons.add(person);
        }

        cursor.close();

        adapter.notifyDataSetChanged();

        updateSummary();

    }

    private void updateSummary() {
        int present = 0;
        int absent = 0;

        for (Person person : list) {

            String status =
                    databaseHelper.getAttendance(
                            person.id,
                            selectedDate
                    );

            if (status.equals("Present")) {

                present++;

            } else if (status.equals("Absent")) {

                absent++;
            }
        }

        txtPresent.setText(
                "Present: " + present
        );

        txtAbsent.setText(
                "Absent: " + absent
        );
    }

    }
