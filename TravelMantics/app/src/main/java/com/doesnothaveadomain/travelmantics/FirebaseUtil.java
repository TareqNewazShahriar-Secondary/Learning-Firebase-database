package com.doesnothaveadomain.travelmantics;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil
{
	public static String travelDealsPath = "traveldeals";
	
	public static FirebaseDatabase mFirebaseDb;
	public static DatabaseReference mDbRef;
	private static FirebaseUtil mFirebaseUtil;
	public static ArrayList<TravelDeal> mTravelDeals;
	
	private FirebaseUtil() { }
	
	public static void openFirebaseReference(String dbPath)
	{
		if(mFirebaseUtil == null)
		{
			mFirebaseUtil = new FirebaseUtil();
			mFirebaseDb = FirebaseDatabase.getInstance();
		}
		mTravelDeals = new ArrayList<>();
		mDbRef = mFirebaseDb.getReference().child(dbPath);
	}
}
