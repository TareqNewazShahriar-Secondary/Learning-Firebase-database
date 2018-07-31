package com.doesnothaveadomain.travelmantics;

import android.support.annotation.NonNull;

import com.google.android.gms.common.stats.StatisticalEventTrackerProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil
{
	public static String travelDealsPath = "traveldeals";
	
	public static FirebaseDatabase mFirebaseDb;
	public static DatabaseReference mDbRef;
	private static FirebaseUtil mFirebaseUtil;
	private static FirebaseAuth mFirebaseAuth;
	private static FirebaseAuth.AuthStateListener mAuthStateListner;
	public static ArrayList<TravelDeal> mTravelDeals;
	
	private FirebaseUtil() { }
	
	public static void openFirebaseReference(String dbPath)
	{
		if(mFirebaseUtil == null)
		{
			mFirebaseUtil = new FirebaseUtil();
			mFirebaseDb = FirebaseDatabase.getInstance();
			mFirebaseAuth = FirebaseAuth.getInstance();
			mAuthStateListner = new FirebaseAuth.AuthStateListener()
			{
				@Override
				public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
				{
					
				}
			};
		}
		mTravelDeals = new ArrayList<>();
		mDbRef = mFirebaseDb.getReference().child(dbPath);
	}
}
