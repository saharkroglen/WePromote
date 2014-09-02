package com.wepromote.common;

public class DrawerMenuItem {

	private String mName;
	private int mIconID;
	private int mID;
	
//	private OnDoActionListener mDoActionListener;
//	public interface OnDoActionListener {
//		void doAction();
//	}

//	public void setOnDoActionListener(OnDoActionListener listener) {
//		mDoActionListener = listener;
//	}
	
	public DrawerMenuItem(int menuID, String name,int iconResID)
	{
		mName = name;
		mIconID = iconResID;
		mID = menuID;
	}
	
	public int getIconResourceID()
	{
		return mIconID;
	}
	public int getMenuID()
	{
		return mID;
	}
	public String getName()
	{
		return mName;
	}
	
//	public void doAction()
//	{
//		mDoActionListener.doAction();
//	}
}
