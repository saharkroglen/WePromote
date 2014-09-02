package com.wepromote.adapters;

import java.util.List;
import com.wepromote.R;
import com.wepromote.common.DrawerMenuItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuDrawerAdapter extends ArrayAdapter<DrawerMenuItem> {
	
	public MenuDrawerAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);

	}

	public MenuDrawerAdapter(Context context, int resource, List<DrawerMenuItem> items) {
		super(context, resource, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		if (v == null) {

			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.item_drawer_menu, null);
		}

		DrawerMenuItem p = getItem(position);

		if (p != null) {
			

			TextView tt = (TextView) v.findViewById(R.id.txtListItem);
			if (tt != null) {
				 tt.setText(p.getName());
			}
			
			ImageView icon = (ImageView)v.findViewById(R.id.imgMenuIcon);
			icon.setImageResource(getItem(position).getIconResourceID());			 
		}

		return v;

	}
	
//	public void handleItemSelectionEvent(int position)
//	{
//		getItem(position).doAction();
//	}

}
