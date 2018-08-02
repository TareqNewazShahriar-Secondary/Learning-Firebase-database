package com.doesnothaveadomain.travelmantics;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ListActivity extends AppCompatActivity
{
	/*ArrayList<TravelDeal> deals;
	private FirebaseDatabase mFirebaseDb;
	private DatabaseReference mDbRef;
	private ChildEventListener mchildEventListner;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_activity_menu, menu);
		
		MenuItem item = menu.findItem(R.id.insert_menu);
		item.setVisible(FirebaseUtil.isAdmin);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.insert_menu:
				Intent intent  = new Intent(this, DealDetailActivity.class);
				startActivity(intent);
				return true;
			case R.id.signout_menu:
				AuthUI.getInstance()
						.signOut(this)
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							public void onComplete(@NonNull Task<Void> task) {
								Log.d("sign-out", "Signed-out");
								Toast.makeText(ListActivity.this, "Signed-out.", Toast.LENGTH_SHORT).show();
								FirebaseUtil.attachListner();
							}
						});
				FirebaseUtil.detachListner();
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
		
		FirebaseUtil.openFirebaseReference(FirebaseUtil.TRAVELDEALS_PATH, this);
		RecyclerView rvdeals = findViewById(R.id.recyclerViewDeals);
		final DealsAdapter adapter = new DealsAdapter(this);
		rvdeals.setAdapter(adapter);
		LinearLayoutManager dealsLayoutManager =
				new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		rvdeals.setLayoutManager(dealsLayoutManager);
		
		FirebaseUtil.attachListner();
		
		invalidateOptionsMenu();
	}
}
