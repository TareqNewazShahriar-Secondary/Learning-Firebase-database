package com.doesnothaveadomain.travelmantics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity
{
	EditText txtTitle;
	EditText txtDescription;
	EditText txtPrice;
	TravelDeal deal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert);
		
		FirebaseUtil.openFirebaseReference(FirebaseUtil.TRAVELDEALS_PATH, this);
		
		txtTitle = findViewById(R.id.txtTitle);
		txtDescription  = findViewById(R.id.txtDescription);
		txtPrice = findViewById(R.id.txtPrice);
		
		Intent intent = getIntent();
		TravelDeal deal = (TravelDeal) intent.getSerializableExtra("deal");
		if(deal == null)
			deal = new TravelDeal();
		this.deal = deal;
		
		txtTitle.setText(deal.getTitle());
		txtDescription.setText(deal.getDescription());
		txtPrice.setText(deal.getPrice());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.save_menu: 
				SaveDeal();
				Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
				finish();
				return true;
			case R.id.delete_menu:
				Delete();
				Toast.makeText(this, "Record deleted.", Toast.LENGTH_SHORT).show();
				finish();
				return true;
			case R.id.cancel_menu:
					finish();
					return true;
            default:
            	return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);
		
		menu.findItem(R.id.save_menu).setVisible(FirebaseUtil.isAdmin);
		menu.findItem(R.id.delete_menu).setVisible(FirebaseUtil.isAdmin);
		
		return true;
	}
	
	private void SaveDeal()
	{
		deal.setTitle(txtTitle.getText().toString());
		deal.setDescription(txtDescription.getText().toString());
		deal.setPrice(txtPrice.getText().toString());
		if(deal.getId() == null)
			FirebaseUtil.mDbRef.push().setValue(deal);
		else
			FirebaseUtil.mDbRef.child(deal.getId()).setValue(deal);
	}
	
	private void Delete()
	{
		if(deal == null || deal.getId() == null)
		{
			Toast.makeText(this, "Not an existing record.", Toast.LENGTH_SHORT).show();
			return;
		}
		FirebaseUtil.mDbRef.child(deal.getId()).removeValue();
	}
	
	private void clean()
	{
		txtTitle.setText("");
		txtDescription.setText("");
		txtPrice.setText("");
		
		txtTitle.requestFocus();
	}
	
	private void BackToList()
	{
		Intent intent = new Intent(this, ListActivity.class);
		startActivity(intent);
	}
}
