package ee.tools.componentcalculator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class InventorySaveLoadDialogAdapter extends BaseAdapter
{
	ListView list_view;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	class InventoryLoadDialogItem extends LinearLayout
	{
		TextView text_view;
		Context context;
		
		public InventoryLoadDialogItem(Context context) 
		{
			super(context);
			this.context = context;
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			this.setLayoutParams(params);
			
			text_view = new TextView(context);
			this.addView(text_view);
		}
	}

	public void setListView(ListView list_view) {
		this.list_view = list_view;
	}

	public void usePreLoaded() {
		// TODO Auto-generated method stub
		
	}

	public void useUser() {
		// TODO Auto-generated method stub
		
	}

}
