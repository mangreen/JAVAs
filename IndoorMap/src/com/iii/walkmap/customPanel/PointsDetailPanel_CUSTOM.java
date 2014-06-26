package com.iii.walkmap.customPanel;


import com.iii.walkmap.indoor.IndoorMap;
import com.iii.walkmap.indoor.R;

import android.content.Intent;
import android.util.Log;

//import com.iiiesti.pedestrian.pmap.PMap;
//import com.iiiesti.pedestrian.pmap.R;


public class PointsDetailPanel_CUSTOM extends PointsDetailPanel {

	@Override
	protected String[] getMiscOperationTags() {
		return this.getResources().getStringArray(R.array.point_operations_for_custom_pin);
	}

	@Override
	protected void onMiscOperation(int operationId) {
		Log.d(D_TAG, Integer.toString(operationId));
		
		switch(operationId)
		{
		
		case 1:
			setResult(IndoorMap.RESULT_PDETAIL_CLEAR_CUSTOMPIN_OVERLAY, new Intent().putExtra(IndoorMap.INTENT_INT_1, IndoorMap.OVERLAY_CUSTOM_PIN));
			PointsDetailPanel_CUSTOM.this.finish();
			break;
		
		}
	}
}
