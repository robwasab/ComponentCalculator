package ee.tools.componentcalculator;


import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InventoryListItemView extends RelativeLayout implements Checkable{

		private TextView text_view;
		private LinearLayout layout;
		
		//The adapter that this view belongs to, if there is one...
		private BaseAdapter adapter = null;
		
		ComponentViewInterface component_view;
		Schematic schematic;
		boolean checked = false;
		private String tag = "InventoryListItemView";
		
		InventoryListItemView(Context context, ComponentViewInterface component_view) 
		{
			// NOTE: Once you have made JokeView extend the appropriate ViewGroup
			// 		 un-comment this next line.
			super(context);
					
			this.component_view = component_view;
			
			//This class is the root view of our xml file joke_view.xml
			//Need to attach this instance of a relativelayout as the root view
			//of all the inflated widgets in joke_view.xml
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(
					  Context.LAYOUT_INFLATER_SERVICE);
			
			ViewGroup root = this;
			boolean attach_to_root = true;
			inflater.inflate(R.layout.inventory_list_item, root, attach_to_root);
			
			
			text_view      = (TextView) this.findViewById(R.id.text_view);	
			
			text_view.setText(component_view.toString()); 
			
			layout  = (LinearLayout) this.findViewById(R.id.inventory_schematic_holder);
			
			schematic = new Schematic(context, layout);
			
			schematic.setSeries(component_view);
			
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			schematic.setLayoutParams(params);
			
			schematic.getHolder().setFixedSize((int)component_view.getWidth(), (int)component_view.getHeight());
			
			schematic.setEnabled(false);
		
			layout.addView(schematic);
			
		}
		
		public void setAdapter(BaseAdapter adapter)
		{
			this.adapter = adapter;
		}

		public void setComponentViewInterface(ComponentViewInterface cvi)
		{
			this.component_view = cvi;
			this.schematic.setSeries(cvi);
			this.text_view.setText(cvi.toString()); 
			invalidate();
		}
		
		@Override
		public void invalidate()
		{
			super.invalidate();
			this.schematic.invalidate();
			this.text_view.invalidate();
			if (this.adapter != null) this.adapter.notifyDataSetChanged();
		}
		
		private void expandView() {
			text_view.setEllipsize(null);
			text_view.setMaxLines(Integer.MAX_VALUE);
			schematic.setVisibility(View.GONE);
			invalidate();
		}

		private void collapseView() {
			text_view.setLines(2);
			text_view.setEllipsize(TruncateAt.END);
			schematic.setVisibility(View.VISIBLE);
			invalidate();
		}

		@Override
		public void setChecked(boolean checked) {
			this.checked = checked;
			if (checked) expandView();
			else         collapseView();
		}

		@Override
		public boolean isChecked() {
			return checked;
		}

		@Override
		public void toggle() {
			this.checked ^= true;
			setChecked(this.checked);
		}
		
		public String toString()
		{
			return component_view.toString();
		}
}
