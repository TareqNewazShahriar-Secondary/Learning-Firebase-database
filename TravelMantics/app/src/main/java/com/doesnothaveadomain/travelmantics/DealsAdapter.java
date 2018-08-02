package com.doesnothaveadomain.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealViewHolder>
{
	ArrayList<TravelDeal> deals;
	
	public DealsAdapter(Activity callerActivity)
	{
		deals = new ArrayList<>();
		
		//FirebaseUtil.openFirebaseReference(FirebaseUtil.TRAVELDEALS_PATH, callerActivity);
		
		ChildEventListener childEventListner = new ChildEventListener()
		{
			@Override
			public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
				TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
				//Log.d("Deal: ", travelDeal.getTitle());
				travelDeal.setId(dataSnapshot.getKey());
				deals.add(travelDeal);
				notifyItemInserted(deals.size() - 1);
			}
			
			@Override
			public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
			
			}
			
			@Override
			public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
			{
			
			}
			
			@Override
			public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
			
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{
			
			}
		};
		
		FirebaseUtil.mDbRef.addChildEventListener(childEventListner);
	}
	
	@NonNull
	@Override
	public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		View itemView  = LayoutInflater.from(context).inflate(R.layout.recyclerview_row, parent, false);
		
		return new DealViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull DealViewHolder holder, int position)
	{
		TravelDeal deal = deals.get(position);
		holder.bind(deal);
	}
	
	@Override
	public int getItemCount()
	{
		return deals.size();
	}
	
	
	public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		TextView textViewTitle, textViewDescription, textViewPrice;
		ImageView imageViewImg;
		
		public DealViewHolder(View itemView)
		{
			super(itemView);
			
			textViewTitle = itemView.findViewById(R.id.textViewTitle);
			textViewDescription = itemView.findViewById(R.id.textViewDescription);
			textViewPrice  = itemView.findViewById(R.id.textViewPrice);
			itemView.setOnClickListener(this);
		}
		
		public void bind(TravelDeal deal)
		{
			textViewTitle.setText(deal.getTitle());
			textViewDescription.setText(deal.getDescription());
			textViewPrice.setText(deal.getPrice());
		}
		
		@Override
		public void onClick(View view)
		{
			int position = getAdapterPosition();
			Log.d("click", String.valueOf(position));
			TravelDeal selectedDeal = deals.get(position);
			Intent intent = new Intent(view.getContext(), DealDetailActivity.class);
			intent.putExtra("deal", selectedDeal);
			view.getContext().startActivity(intent);
		}
		
	}
}
