package com.doesnothaveadomain.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealDetailActivity extends AppCompatActivity
{
	EditText txtTitle;
	EditText txtDescription;
	EditText txtPrice;
	ImageView imgView;
	TravelDeal deal;
	int REQUEST_ID = 23;
	Uri imgUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal_detail);
		
		//FirebaseUtil.openFirebaseReference(FirebaseUtil.TRAVELDEALS_PATH, this);
		
		txtTitle = findViewById(R.id.txtTitle);
		txtDescription  = findViewById(R.id.txtDescription);
		txtPrice = findViewById(R.id.txtPrice);
		imgView = findViewById(R.id.imageView);
		
		final Intent intent = getIntent();
		TravelDeal deal = (TravelDeal) intent.getSerializableExtra("deal");
		if(deal == null)
			deal = new TravelDeal();
		this.deal = deal;
		
		txtTitle.setText(deal.getTitle());
		txtDescription.setText(deal.getDescription());
		txtPrice.setText(deal.getPrice());
		//new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(deal.getImgUrl());
		showImage(imgView, deal.getImgUrl());
		
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
				return true;
			case R.id.delete_menu:
				Delete();
				Toast.makeText(this, "Record deleted.", Toast.LENGTH_SHORT).show();
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
			imgUri = data.getData();
			showImage(imgView, imgUri.toString());
		}
	}
	
	private void SaveDeal()
	{
		findViewById(R.id.save_menu).setEnabled(false);
		
		deal.setTitle(txtTitle.getText().toString());
		deal.setDescription(txtDescription.getText().toString());
		deal.setPrice(txtPrice.getText().toString());
		
		if(imgUri != null)
		{
			deleteImageFromFirebaseDb(deal.getImgName()); // delete existing
			UploadImage();
		}
		else
		{
			insertToDb();
			BackToList("Deal saved.");
		}
		
	}
	
	private void Delete()
	{
		if(deal == null || deal.getId() == null)
		{
			Toast.makeText(this, "Not an existing record.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		FirebaseUtil.mDbRef.child(deal.getId()).removeValue();
		
		deleteImageFromFirebaseDb(deal.getImgName());
	}
	
	private void UploadImage()
	{
		final StorageReference reference = FirebaseUtil.mStorageRef.child(imgUri.getLastPathSegment());
		reference.putFile(imgUri).addOnSuccessListener(this,
				new OnSuccessListener<UploadTask.TaskSnapshot>()
				{
					@Override
					public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot)
					{
						final UploadTask.TaskSnapshot snapshot  = taskSnapshot;
						deal.setImgName(taskSnapshot.getMetadata().getPath());
						
						reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
							@Override
							public void onSuccess(Uri uri)
							{
								String urlStr = uri.toString();
								deal.setImgUrl(urlStr);
								Log.d("url", urlStr);

//									String url = uri.toString();
//									new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(url);
								//showImage(uri.toString());
								findViewById(R.id.save_menu).setEnabled(true);
								
								insertToDb();
								
								BackToList("Deal saved with image.");
							}
						}).addOnFailureListener(new OnFailureListener()
						{
							@Override
							public void onFailure(@NonNull Exception e)
							{
								findViewById(R.id.save_menu).setEnabled(true);
								Log.e("upload err", e.getMessage());
								Toast.makeText(DealDetailActivity.this, "Image upload error: " + e.getMessage(), Toast.LENGTH_LONG).show();
							}
						});
					}
				});
	}
	
	private void deleteImageFromFirebaseDb(String imgPath)
	{
		if(imgPath != null && !imgPath.isEmpty())
		{
			StorageReference reference = FirebaseUtil.mStorage.getReference().child(imgPath);
			reference.delete().addOnSuccessListener(new OnSuccessListener<Void>()
			{
				@Override
				public void onSuccess(Void aVoid)
				{
					Log.d("delete pic", "pic deleted.");
				}
			}).addOnFailureListener(new OnFailureListener()
			{
				@Override
				public void onFailure(@NonNull Exception e)
				{
					Log.e("del err", e.getMessage());
					Toast.makeText(DealDetailActivity.this, "Image delete error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			});
		}
	}
	
	private void insertToDb()
	{
		if(deal.getId() == null)
			FirebaseUtil.mDbRef.push().setValue(deal);
		else
			FirebaseUtil.mDbRef.child(deal.getId()).setValue(deal);
	}
	
	private void clean()
	{
		txtTitle.setText("");
		txtDescription.setText("");
		txtPrice.setText("");
		
		txtTitle.requestFocus();
	}
	
	private void BackToList(String msg)
	{
		if(msg != null && !msg.isEmpty())
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
		finish();
		
		//Intent intent = new Intent(this, ListActivity.class);
		//startActivity(intent);
	}
	
	public static void showImage(ImageView imgView, String urlString)
	{
		if(urlString != null && !urlString.isEmpty())
		{
			DisplayMetrics displayMatrics = Resources.getSystem().getDisplayMetrics();
			
			Picasso.get().setIndicatorsEnabled(true);
			Picasso.get()
					.load(urlString)
					.placeholder(R.drawable.fui_idp_button_background_phone)
					.error(R.drawable.ic_launcher_background)
					//.resize(displayMatrics.widthPixels, displayMatrics.widthPixels*2/3)
					//.centerCrop()
					.into(imgView);
		}
	}
}
