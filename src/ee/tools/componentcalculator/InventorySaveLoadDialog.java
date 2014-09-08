package ee.tools.componentcalculator;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RadioGroup;

public class InventorySaveLoadDialog extends Dialog
implements RadioGroup.OnCheckedChangeListener{

	ListView list_view;
	RadioGroup radio_group;
	InventorySaveLoadDialogAdapter adapter;
	
	static final int SAVE = 0, LOAD = 1;
	int mode;
	
	public InventorySaveLoadDialog(Context context, int mode) {
		super(context);
		this.mode = mode;
		if (mode == LOAD)
		{
			this.setContentView(R.layout.inventory_load_dialog);
			list_view = (ListView)this.findViewById(R.id.inventory_load_dialog_list_view);
			radio_group = (RadioGroup)this.findViewById(R.id.inventory_load_dialog_radio_group);
			radio_group.setOnCheckedChangeListener(this);
		
			adapter = new InventorySaveLoadDialogAdapter();
			adapter.setListView(list_view);
			list_view.setAdapter(adapter);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (mode == LOAD)
		{
			switch(checkedId)
			{
			case R.id.inventory_load_dialog_radio_button_use_preloaded:
				adapter.usePreLoaded();
				break;
			case R.id.inventory_load_dialog_radio_button_use_user:
				adapter.useUser();
				break;
			}
		}
	}
}
