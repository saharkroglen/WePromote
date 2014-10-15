package com.wepromote.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wepromote.MainActivity;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;
import com.wepromote.common.InvitationList.Invitation;
import com.wepromote.parse.CampaignPromoter;
import com.wepromote.parse.CampaignPromoter.enInvitationStatus;
import com.wepromote.preferences.InvitationPrefs;



public class InvitationsAdapter extends BaseAdapter implements OnClickListener{
	  private final Activity mContext;	 
	  private List<Invitation> mInvitations;

	  
	  public InvitationsAdapter(Activity context,ArrayList<Invitation> invitations) {
	    super();
	    mInvitations = new ArrayList<Invitation>(invitations);
	    this.mContext = context;
	  }

	  @Override
	  public synchronized View getView(int position, View convertView, ViewGroup parent) { 
	    LayoutInflater inflater = (LayoutInflater) mContext 
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    
	    if (convertView == null)
		{
	    	convertView = inflater.inflate(R.layout.item_invitation, parent, false);	
	    }
	
	    TextView btnConfirmInvitation = (TextView) convertView.findViewById(R.id.btnConfirm);
	    btnConfirmInvitation.setTag(getItem(position).getCampaignID());
	    btnConfirmInvitation.setOnClickListener(this);
	    
	    TextView btnDismiss = (TextView) convertView.findViewById(R.id.txtDismiss);
	    btnDismiss.setTag(getItem(position).getCampaignID());
	    btnDismiss.setOnClickListener(this);
	    
	    TextView btnSpam = (TextView) convertView.findViewById(R.id.txtSpam);
	    btnSpam.setTag(getItem(position).getCampaignID());
	    btnSpam.setOnClickListener(this);
	    
	    TextView appName = (TextView) convertView.findViewById(R.id.txtAppName);
	    String invitationTitle = String.format(mContext.getResources().getString(R.string.invitation_description),mInvitations.get(position).getMerchantName(true));
	    appName.setText(invitationTitle);
	    return convertView;
	    
	  }

	@Override
	public int getCount() {
		return mInvitations.size();
	}


	@Override
	public Invitation getItem(int position) {
		return mInvitations.get(position);
	}


	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	private void removePackageFromAdapter(String invitation)
	{
		Invitation invitationToRemove = null;
		for(Invitation invite: mInvitations)
		{
			if (invite.getCampaignID().equals(invitation))
			{				
				invitationToRemove = invite;
				break;
			}
		}	
		if (invitationToRemove != null)
		{
			mInvitations.remove(invitationToRemove);
			notifyDataSetChanged();
		}
		if(mInvitations.size() == 0)
		{
//			Utils.sendMessage(mContext,new InternalMessage(InternalMessage.MESSAGE_SELECT_MENU_ITEM,String.valueOf(MainActivity.MENU_ITEM_HOME)));
			((MainActivity)mContext).openHome();
		}
	}
	
	
	@Override
	public void onClick(View v) {
		String campaignID = v.getTag().toString();
		switch (v.getId())
		{
		case R.id.txtDismiss:
			CampaignPromoter.updateInvitationStatus(enInvitationStatus.decline, campaignID);
			break;
		case R.id.btnConfirm:
			CampaignPromoter.updateInvitationStatus(enInvitationStatus.accept, campaignID);
			break;
		case R.id.txtSpam:
			CampaignPromoter.updateInvitationStatus(enInvitationStatus.spam, campaignID);
			break;
			
		}
		removePackageFromAdapter(campaignID);
		removePackageFromPreferences(campaignID);
	}

	/**
	 * A decision is made for this package. whether it's confirm or dismiss.
	 * now remove it from preferences so it won't show again.
	 * @param pname
	 */
	private void removePackageFromPreferences(String campaignID) {
		InvitationPrefs.removePendingInvitation(campaignID);		
	}
}


