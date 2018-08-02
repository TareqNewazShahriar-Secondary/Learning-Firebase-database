package com.doesnothaveadomain.travelmantics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class DealDetailActivity extends AppCompatActivity
{
	EditText txtTitle;
	EditText txtDescription;
	EditText txtPrice;
	ImageView img;
	TravelDeal deal;
	int REQUEST_ID = 23;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal_detail);
		
		//FirebaseUtil.openFirebaseReference(FirebaseUtil.TRAVELDEALS_PATH, this);
		
		txtTitle = findViewById(R.id.txtTitle);
		txtDescription  = findViewById(R.id.txtDescription);
		txtPrice = findViewById(R.id.txtPrice);
		img = findViewById(R.id.imageView);
		
		final Intent intent = getIntent();
		TravelDeal deal = (TravelDeal) intent.getSerializableExtra("deal");
		if(deal == null)
			deal = new TravelDeal();
		this.deal = deal;
		
		txtTitle.setText(deal.getTitle());
		txtDescription.setText(deal.getDescription());
		txtPrice.setText(deal.getPrice());
		new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(deal.getImgUrl());
		
		Button btnImage = findViewById(R.id.btnImage);
		btnImage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intentObj = new Intent(Intent.ACTION_GET_CONTENT);
				intentObj.setType("image/jpeg");
				intentObj.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
				startActivityForResult(intentObj.createChooser(intentObj, "Choose a Picture"), REQUEST_ID);
			}
		});
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_ID && resultCode == RESULT_OK)
		{
			Uri imgUri = data.getData();
			final StorageReference reference = FirebaseUtil.mStorageRef.child(imgUri.getLastPathSegment());
			reference.putFile(imgUri).addOnSuccessListener(this,
					new OnSuccessListener<UploadTask.TaskSnapshot>()
					{
						@Override
						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
						{
							reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
								@Override
								public void onSuccess(Uri uri)
								{
									final Uri downloadUrl = uri;
									
									String url = downloadUrl.toString();
									deal.setImgUrl(url);
									new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(url);
									Toast.makeText(DealDetailActivity.this, "Image uploaded.", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
		}
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
		finish();
		
		//Intent intent = new Intent(this, ListActivity.class);
		//startActivity(intent);
	}
}
