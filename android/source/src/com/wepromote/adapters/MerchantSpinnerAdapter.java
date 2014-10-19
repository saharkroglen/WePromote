package com.wepromote.adapters;

import java.util.ArrayList;

import com.wepromote.parse.Campaign;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MerchantSpinnerAdapter implements SpinnerAdapter{
    Context context;
    ArrayList<Campaign> mCampaignList;
    
    public MerchantSpinnerAdapter(Context context ,ArrayList<Campaign> campaigns){
        this.context =context;
        this.mCampaignList = campaigns;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mCampaignList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mCampaignList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
//Note:-Create this two method getIDFromIndex and getIndexByID 
    public String getIDFromIndex(int Index) {
        return    mCampaignList.get(Index).getMerchantID();       
    }
    
    public int getIndexByID(String ID) {
        for(int i=0;i<mCampaignList.size();i++)
        {
        	if(mCampaignList.get(i).getMerchantID() == ID){
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textview = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
        textview.setText(mCampaignList.get(position).getName());
       
        return textview;
    }

    @Override
    public int getViewTypeCount() {
        return android.R.layout.simple_spinner_item;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub
       
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub
       
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textview = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
        textview.setText(mCampaignList.get(position).getName());
       
        return textview;
    }


}
