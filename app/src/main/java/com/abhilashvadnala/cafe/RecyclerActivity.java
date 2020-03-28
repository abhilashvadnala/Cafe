package com.abhilashvadnala.cafe;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RecyclerActivity extends AppCompatActivity {

    public class CafeAdapter extends RecyclerView.Adapter<CafeAdapter.CafeViewHolder>{
        ArrayList<CafeBasic> cafes;
        class CafeViewHolder extends RecyclerView.ViewHolder{
            LinearLayout l;
            public CafeViewHolder(@NonNull LinearLayout l) {
                super(l);
                this.l=l;
            }
        }

        public CafeAdapter(ArrayList<CafeBasic> cafes){
            this.cafes = cafes;
            Collections.sort(this.cafes, new Comparator<CafeBasic>() {
                @Override
                public int compare(CafeBasic o1, CafeBasic o2) {
                    return Double.compare(o2.rating,o1.rating);
                }
            });

        }

        @NonNull
        @Override
        public CafeAdapter.CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cafe_item, parent, false);
            CafeViewHolder vh = new CafeViewHolder(l);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull CafeAdapter.CafeViewHolder holder, int position) {
            ((TextView)(holder.l).findViewById(R.id.name)).setText(cafes.get(position).name);
            ((TextView)(holder.l).findViewById(R.id.rating)).setText(cafes.get(position).rating+"");
            ((TextView)(holder.l).findViewById(R.id.address)).setText(cafes.get(position).address);
        }

        @Override
        public int getItemCount() {
            return cafes.size();
        }
    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setVisibility(View.GONE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getTitle().toString().equals("Map Activity")){
                    startActivity(new Intent(RecyclerActivity.this, MapActivity.class));
                    finish();
                }
                return false;
            }
        });


        //spinner.setAdapter(spinnerArrayAdapter);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new CafeAdapter((ArrayList<CafeBasic>)getIntent().getExtras().get("EXTRA_CAFE")));

    }

}
