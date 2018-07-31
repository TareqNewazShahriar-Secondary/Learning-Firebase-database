package com.doesnothaveadomain.travelmantics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity
{
	ArrayList<TravelDeal> deals;
	private FirebaseDatabase mFirebaseDb;
	private DatabaseReference mDbRef;
	private ChildEventListener mchildEventListner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		RecyclerView rvdeals = findViewById(R.id.recyclerViewDeals);
		final DealsAdapter adapter = new DealsAdapter();
		rvdeals.setAdapter(adapter);
		LinearLayoutManager dealsLayoutManager =
				new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		rvdeals.setLayoutManager(dealsLayoutManager);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_activity_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.insert_menu:
				Intent intent  = new Intent(this, DealActivity.class);
				startActivity(intent);
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		FirebaseUtil.detachListner();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		FirebaseUtil.attachListner();
	}
}
