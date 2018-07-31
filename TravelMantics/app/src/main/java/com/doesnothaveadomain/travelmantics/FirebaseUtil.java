package com.doesnothaveadomain.travelmantics;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.stats.StatisticalEventTrackerProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class FirebaseUtil
{
	public static String travelDealsPath = "traveldeals";
	public static ArrayList<TravelDeal> mTravelDeals;
	
	public static FirebaseDatabase mFirebaseDb;
	public static DatabaseReference mDbRef;
	
	private static FirebaseUtil mFirebaseUtil;
	private static FirebaseAuth mFirebaseAuth;
	private static FirebaseAuth.AuthStateListener mAuthStateListner;
	private static Activity caller;
	private static final int RC_SIGN_IN = 123;
	
	private FirebaseUtil() { }
	
	public static void openFirebaseReference(String dbPath, final Activity callerActivity)
	{
		if(mFirebaseUtil == null)
		{
			mFirebaseUtil = new FirebaseUtil();
			mFirebaseDb = FirebaseDatabase.getInstance();
			mFirebaseAuth = FirebaseAuth.getInstance();
			caller = callerActivity;
			mAuthStateListner = new FirebaseAuth.AuthStateListener()
			{
				@Override
				public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
				{
					signin();
					Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
				}
			};
		}
		mTravelDeals = new ArrayList<>();
		mDbRef = mFirebaseDb.getReference().child(dbPath);
	}
	
	private static void signin()
	{
		// Choose authentication providers
		List<AuthUI.IdpConfig> providers = Arrays.asList(
				new AuthUI.IdpConfig.EmailBuilder().build(),
				new AuthUI.IdpConfig.PhoneBuilder().build(),
				new AuthUI.IdpConfig.GoogleBuilder().build()
		);
		
		// Create and launch sign-in intent
		caller.startActivityForResult(
				AuthUI.getInstance()
						.createSignInIntentBuilder()
						.setAvailableProviders(providers)
						.build(),
				RC_SIGN_IN);
	}
	
	public static void attachListner()
	{
		mFirebaseAuth.addAuthStateListener(mAuthStateListner);
	}
	
	public static void detachListner()
	{
		mFirebaseAuth.removeAuthStateListener(mAuthStateListner);
	}
}
