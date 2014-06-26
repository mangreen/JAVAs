package com.iii.walkmap.indoor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.iii.walkmap.customPanel.PointsDetailPanel;
import com.iii.walkmap.database.BookmarkDBManager;
import com.iii.walkmap.database.IndoorMapDBManager;
import com.iii.walkmap.searchable.IndoorPoiSearch;

public class IndoorMap extends Activity{
	/*This for debug*/
	private static final String TAG = "Map" ; 
	// We can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static final int LONGPRESS = 3;
//	private static final int CLICK = 4;	// Tony COMMENTED @ 1st merge
	private static int touchMode = NONE;

	//indoor map dir
	private static final String INDOOR_MAP_DIR = "/sdcard/walkmap/indoormap/";
	
	//connect for database searching
	private static final String CONNECT_OUT = "out";
	private static final String CONNECT_MASS = "mass";
	
	private String nowBuildID = "iii";
	private int nowFloor = 1;

	/*These matrices will be used to move and zoom image*/
	private Matrix matrix = new Matrix();
//    private Matrix originalMatrix = new Matrix();	// Tony COMMENTED @ 1st merge
    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist;
    private int scaleID = 2;
    private static float mapScale[] = {0.5f, 0.75f, 1f, 1.5f, 2f};
	
	/*UI Parameter claim*/
	private ImageView mapImageView;
	private IndoorDrawView drawView;
	private GestureDetector gestureDetector;
	
	private Button btnFloorList;
	private Button btnDownstair;
	private Button btnUpStair;
	private Button btnZoomIn;
	private Button btnZoomOut;
	private RelativeLayout layoutIndoor;
	private Bitmap bmpMap;
	//private int id=0;
	private int displayWidth;
	private int displayHeight;
	private float scaleWidth=1;
	private float scaleHeight=1;
	
	/*set bitmap topleft and downright point*/
	private Storey mainStorey = new Storey();
	private Storey tempStorey = new Storey();
	public static BitMapPoint topleft;
	public static BitMapPoint bottomright;
	private int maxFloor = 0;
	private int minBasement = 0;
	 
	// Related data adapter of floorlist
	private Cursor floorCursor;
	private SimpleCursorAdapter floorAdapter;
	
	public static final int REQUEST_POINT_DETAIL = 2;
	public static final int RESULT_PDETAIL_HAS_CENTER = 1;
	public static final int RESULT_PDETAIL_CLEAR_CUSTOMPIN_OVERLAY = 2;
	public static final int RESULT_PDETAIL_ADD_BOOKMARK = 3;
	public static final int RESULT_PDETAIL_SET_START = 4;
	public static final int RESULT_PDETAIL_SET_END = 5;
	public static final int BOOKMARK_MODE_NORMAL = 1;
	public static final int BOOKMARK_MODE_SET_START = 2;
	public static final int BOOKMARK_MODE_SET_END = 3;
	public static final int OVERLAY_CUSTOM_PIN = -1;
	public static final String INTENT_INT_1 = "intent_key_integer_1";
	
	private static final int MENUGROUP_MAIN = R.id.menu_group_main;
	private static final int MENUGROUP_DIRECTION_SETUP = R.id.menu_group_direction_setup;
	protected static final int MENUITEM_SEARCH = R.id.pmap_menu_search;
	protected static final int MENUITEM_DIRECTION = R.id.pmap_menu_direction;
	protected static final int MENUITEM_LAYER = R.id.pmap_menu_layer;
	protected static final int MENUITEM_BOOKMARK = R.id.pmap_menu_bookmark;
	protected static final int MENUITEM_CLEANMAP = R.id.pmap_menu_clean_map;
	protected static final int MENUITEM_ABOUT = R.id.pmap_menu_about;
	protected static final int MENUITEM_DSETUP_REVERSE = R.id.direction_setup_menu_reverse;
	protected static final int MENUITEM_DSETUP_CLEAR = R.id.direction_setup_menu_clear;
	protected static final int MENUITEM_DSETUP_MT_OPTION = R.id.direction_setup_menu_mass_opt;
	
	
	//private static final int iii_13f = R.drawable.iii_13f;
	//private static final int iii_1f = R.drawable.iii_1f;
	//private static int drawableID = 0;
	
	/* Create DB resources */
	// DB master
	private IndoorMapDBManager idrmapDB;
	private BookmarkDBManager bookmarkDB;
	
	// Related data adapter
	private Cursor bookmarkCursor;
	private SimpleCursorAdapter bookmarkAdapter;

	public static final String INDOOR_SERVER_URL="http://140.92.13.231:8080/LBSServer/IndoorLBS.jsp";
	//public static final String INDOOR_SERVER_URL="http://140.92.13.128:8080/LBSServer/IndoorLBS.jsp";
	//public static final String INDOOR_SERVER_URL="http://10.0.2.2:8080/LBSServer/IndoorLBS.jsp";
	public static String indoorPoi="";
	public static int indoorPoiAmount = 0;
	public static boolean indoorPOIStatus[];
	public static String poi[];
	public static boolean indoorPOIReadyFlag = false;
	
	public static boolean REQUEST_POI_SEARCH = false;
	public static int poi_long=0;
	public static int poi_lat=0;
	public static int poi_floor=0;
	public static String poi_name="";
	
	private class openBookmarkDBTask extends AsyncTask<BookmarkDBManager, Void, BookmarkDBManager>
	{
		@Override
		protected BookmarkDBManager doInBackground(BookmarkDBManager... db) {
			try {
				Log.d(TAG, "openDBTask: Opening Bookmark data bases");
				db[0].open();
				db[0].setAvailable(true);
				return db[0];
			} catch (SQLiteException e) {
				Log.e(TAG, e.getMessage());
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(BookmarkDBManager db) {
			if(db != null)
			{
				bookmarkCursor = db.SPOINT_select();
				
				if(bookmarkCursor != null)
				{
					startManagingCursor(bookmarkCursor);
					
					bookmarkAdapter = new SimpleCursorAdapter(IndoorMap.this, 
							R.layout.simple_info, 
							bookmarkCursor, 
							new String[] {BookmarkDBManager.SPOINT_FIELD_NAME, BookmarkDBManager.SPOINT_FIELD_INFO}, 
							new int[] {R.id.simple_info_title, R.id.simple_info_info});					
				}
			}
			super.onPostExecute(db);
		}
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        initDB();
        
        
		
        /*Loading main.xml Layout*/
        setContentView(R.layout.idr_main);
        
        /*Get screen dpi*/
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        displayWidth = dm.widthPixels;
        
        /*Display Height subtract Button Height*/
        displayHeight = dm.heightPixels - 80;
        
        /*Initial Parameters*/
        //bmpMap = BitmapFactory.decodeResource(getResources(), iii_13f);
        topleft = null;
    	bottomright = null;
        mainStorey = getStorey(nowBuildID,nowFloor);
        
        indoorPOIStatus = new boolean[IndoorMap.this.getResources().getStringArray(R.array.indoor_layer_menu).length];
        poiLayerInit();
        
        setBitmap(mainStorey);
        mapImageView = (ImageView)findViewById(R.id.mapImageView);
        mapImageView.setOnTouchListener(imageTouch);
//        mapImageView.setImageBitmap(bmpMap);		// Tony COMMENTED @ 20110111
    
        drawView = new IndoorDrawView(this);
        //drawView.setAnchorPoint(topleft, bottomright, mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
        drawView.setAnchorPoint(mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
        //drawView.setOnTouchListener(drawTouch);
        gestureDetector = new GestureDetector(onGestureListener);
        //setContentView(drawView);
        
        //drawImageView.setOnTouchListener(this); 
        layoutIndoor = (RelativeLayout)findViewById(R.id.layoutIndoor);
        layoutIndoor.addView(drawView, 1);
        
        btnFloorList = (Button)findViewById(R.id.btnFloorList);
    	btnDownstair = (Button)findViewById(R.id.btnDownstair);
    	btnUpStair = (Button)findViewById(R.id.btnUpstair);
        
    	if(minBasement == mainStorey.floor){
    		btnDownstair.setEnabled(false);
    	}
    	if(maxFloor == mainStorey.floor){
    		btnUpStair.setEnabled(false);
    	}
    	
        btnZoomIn = (Button)findViewById(R.id.btnZoomIn);
        btnZoomOut = (Button)findViewById(R.id.btnZoomOut);
        
        btnFloorList.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				//moveToCenter(121554344, 25058814, 1, "iii");
				openFloorListDialog();
				//Log.d(TAG, "IndoorPOI: "+indoorPoi);
			}
        });

        btnDownstair.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				downStair();
			}
        });
        
        btnUpStair.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				upStair();
			}
        });
        
        /*Shrink button onClickListener*/
        btnZoomIn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				zoomIn();
			}
        });
        /*Blow-up button onClickListener*/
        btnZoomOut.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				zoomOut();
			}
        });
    }
    
    @Override
    protected void onStart() {
    	Log.v(TAG, "onStart");
    	
    	/* Tony MOVED @ 20110111 */
    	setBitmap(mainStorey);
    	mapImageView.setImageBitmap(bmpMap);
    	
    	moveToCenter(mainStorey.left+(mainStorey.right-mainStorey.left)/2, 
    			mainStorey.bottom+(mainStorey.top-mainStorey.bottom)/2,
    			mainStorey.floor,
    			mainStorey.buildID);
    	
    	super.onStart();
    }
    
	@Override
	protected void onResume() {
		Log.v(TAG, "onResume");
	    super.onResume();
	    
	    if(true == REQUEST_POI_SEARCH){
	    	Log.d(TAG, "onResume: " + poi_long +", "+ poi_lat +", "+ poi_floor +", "+ poi_name);
	    	
	    	if(moveToCenter(poi_long, poi_lat, poi_floor, nowBuildID)){
	    		drawView.setPressPoint(poi_long, poi_lat, poi_name);
	    	}else{
	    		new AlertDialog.Builder(this)
				.setTitle(R.string.indoor_no_map)
				.setIcon(R.drawable.ic_dialog_info)
				.setMessage(R.string.indoor_no_map_message)
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            })
				.show();
	    	}
	    	REQUEST_POI_SEARCH = false;
	    }
	}
    
    @Override
    protected void onPause() {
    	Log.v(TAG, "PMap: onPause");
        super.onPause();
    }
    
    @Override
    protected void onStop() {
    	Log.v(TAG, "onStop");
    	
    	/* Tony ADDED @ 20110111 */
		if(bmpMap != null)
			bmpMap.recycle();
		
    	super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	Log.v(TAG, "onDestroy");
    	closeDB();
    	super.onDestroy();
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
    	Log.v(TAG, "onConfigurationChanged");
    	
    	// [NOTE] When you declare your Activity to handle a configuration change, 
    	// you are responsible for resetting any elements for which you provide alternatives. 
    	// If you declare your Activity to handle the orientation change and have images that should change 
    	// between landscape and portrait, you must re-assign each resource to each element during onConfigurationChanged().
    	
		super.onConfigurationChanged(newConfig);
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	Log.v(TAG, "onActivityResult");
    	drawView.pressFlag = false;
    	drawView.pin.animateDone = true;
    	
    	Bundle bundle = null;
    	
    	if(intent != null){
    		bundle = intent.getExtras();
    	}
    	
    	if(resultCode != RESULT_CANCELED)
			switch(requestCode)
			{
			case REQUEST_POINT_DETAIL:
				switch(resultCode){
				
				case RESULT_PDETAIL_HAS_CENTER:
					Log.d(TAG, "RESULT_PDETAIL_HAS_CENTER");
					moveToCenter(drawView.pin.getIntLontitude(), drawView.pin.getIntLatitude(), drawView.pin.getFloor(), drawView.pin.getBuildID());
					break;
					
				case RESULT_PDETAIL_CLEAR_CUSTOMPIN_OVERLAY:
					drawView.pin.pinFlag = false;
					break;
					
				case RESULT_PDETAIL_ADD_BOOKMARK:
					if(bundle != null)
					{
						bookmarkDB.SPOINT_insert(bundle.getString(PointsDetailPanel.BUNDLE_TITLE),
							bundle.getString(PointsDetailPanel.BUNDLE_INFO), 
							(int)(bundle.getDouble(PointsDetailPanel.BUNDLE_LAT) * 1E6), 
							(int)(bundle.getDouble(PointsDetailPanel.BUNDLE_LON) * 1E6),
							(int)(bundle.getDouble(PointsDetailPanel.BUNDLE_ADDRESS_LAT, 91.0) * 1E6), 
							(int)(bundle.getDouble(PointsDetailPanel.BUNDLE_ADDRESS_LON, 181.0) * 1E6),
							bundle.getInt(PointsDetailPanel.BUNDLE_FLOOR),
							bundle.getString(PointsDetailPanel.BUNDLE_BUILD_ID));
						bundle.clear();
						Log.d(TAG, "RESULT_PDETAIL_ADD_BOOKMARK");
					}
					break;
					
				case RESULT_PDETAIL_SET_START:
					Log.d(TAG, "RESULT_PDETAIL_SET_START");
					if(bundle != null){
						/*
						directionSetup.setStart(false, 
								bundle.getString(PointsDetailPanel.BUNDLE_TITLE),
								bundle.getString(PointsDetailPanel.BUNDLE_INFO), 
								new GeoPoint((int)(bundle.getDouble(PointsDetailPanel.BUNDLE_LAT) * 1E6), 
										(int)(bundle.getDouble(PointsDetailPanel.BUNDLE_LON) * 1E6)));
						*/
						bundle.clear();
					}
					break;
				case RESULT_PDETAIL_SET_END:
					Log.d(TAG, "RESULT_PDETAIL_SET_END");
					if(bundle != null){
						/*
						directionSetup.setEnd(false, 
								bundle.getString(PointsDetailPanel.BUNDLE_TITLE),
								bundle.getString(PointsDetailPanel.BUNDLE_INFO), 
								new GeoPoint((int)(bundle.getDouble(PointsDetailPanel.BUNDLE_LAT) * 1E6), 
										(int)(bundle.getDouble(PointsDetailPanel.BUNDLE_LON) * 1E6)));
						bundle.clear();
						*/
					}
					break;
				}
				break;
			}
    	
    	super.onActivityResult(requestCode, resultCode, intent);
	}
    
    @Override
	public void onBackPressed() {
    	Log.v(TAG, "onBackPressed");
		super.onBackPressed();
	}
    
    @Override
	public boolean onSearchRequested() {
    	Log.v(TAG, "onSearchRequested");
    	searchPoi();
		return false;			// Disable the default launch
	}
    
    /* OptionsMenu */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)	// Executed once only at the menu first created
	{
		Log.v(TAG, "onCreateOptionsMenu");
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pmap_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 	// Executed each time the menu is going to be opened
	{
		Log.v(TAG, "onPrepareOptionsMenu");
		/*
		if(directionSetup.isEnabled())
		{
			menu.setGroupVisible(MENUGROUP_MAIN, false);
			menu.setGroupVisible(MENUGROUP_DIRECTION_SETUP, true);
			
			if(directionSetup.hasSetSomething())
				menu.findItem(MENUITEM_DSETUP_REVERSE).setEnabled(true);
			else
				menu.findItem(MENUITEM_DSETUP_REVERSE).setEnabled(false);	
		}
		else
		{
			menu.setGroupVisible(MENUGROUP_MAIN, true);
			menu.setGroupVisible(MENUGROUP_DIRECTION_SETUP, false);
		}
		*/
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log.v(TAG, "onOptionsItemSelected");
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId())
		{
		case MENUITEM_SEARCH:
			searchPoi();
			break;
		case MENUITEM_DIRECTION:
			/*
			directionSetup.show();
			resetMapMode();
			btnMapMode.setVisibility(View.GONE);
			*/
			break;
		case MENUITEM_LAYER:
			
			new AlertDialog.Builder(IndoorMap.this)
			.setIcon(R.drawable.ic_dialog_dialer)
            .setTitle(R.string.pmap_menu_layer)
            .setMultiChoiceItems(R.array.indoor_layer_menu, indoorPOIStatus,
            new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                	if(0 == whichButton && true == indoorPOIStatus[whichButton]){
                		Arrays.fill(indoorPOIStatus, true);
      	
                	}else if(0 == whichButton && false == indoorPOIStatus[whichButton]){
                		
                		Arrays.fill(indoorPOIStatus, false);
                		for(int i=0; i<indoorPOIStatus.length; i++){  
                			((AlertDialog)dialog).getListView().setItemChecked(i, false);
                		}      
                	}
                }
            })
            .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	drawView.invalidate();
                	//itemizedOverlaysUpdate();
                }
            })
            .setNegativeButton(R.string.btn_clear, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	Arrays.fill(indoorPOIStatus, false);
                	drawView.invalidate();
                	//itemizedOverlaysUpdate();
                }
            })
            .show();
            
			break;
		case MENUITEM_BOOKMARK:
			openBookmarkDialog(BOOKMARK_MODE_NORMAL);
			break;
		case MENUITEM_CLEANMAP:
			Arrays.fill(indoorPOIStatus, false);
			drawView.hide();
			drawView.invalidate();
			break;
			
		case MENUITEM_ABOUT:
			new AlertDialog.Builder(IndoorMap.this)
				.setTitle(R.string.pmap_about_title)
				.setIcon(R.drawable.ic_dialog_info)
				.setMessage(R.string.pmap_about_message)
				.setPositiveButton(R.string.btn_ok, null)
				.show();
			break;
			
		// For direction setup menu
		case MENUITEM_DSETUP_REVERSE:
			/*
			directionSetup.reverseStartEnd();
			*/
			break;
		case MENUITEM_DSETUP_CLEAR:
			/*
			directionSetup.dataInitialize();
			*/
			break;
		case MENUITEM_DSETUP_MT_OPTION:
			break;
		}
		
		return true;
	}
    
    /*Method of down stair*/
    private void downStair(){
    	Cursor cursor = idrmapDB.selectDownstairs(mainStorey.buildID, mainStorey.floor);
    	if(0 != cursor.getCount()){
			cursor.moveToLast();
			setStorey(mainStorey, cursor);
		}
    	setBitmap(mainStorey);
    	mapImageView.setImageBitmap(bmpMap);
    	//drawView.setAnchorPoint(topleft, bottomright, mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
    	drawView.setAnchorPoint(mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
    	drawView.invalidate();
    	
    	if(minBasement == mainStorey.floor){
    		btnDownstair.setEnabled(false);
    	}
    	if(maxFloor != mainStorey.floor){
    		btnUpStair.setEnabled(true);
    	}
    }
    
    /*Method of up stair*/
    private void upStair(){
    	Cursor cursor = idrmapDB.selectUpstairs(mainStorey.buildID, mainStorey.floor);
    	if(0 != cursor.getCount()){
			cursor.moveToFirst();
			setStorey(mainStorey, cursor);
		}
    	setBitmap(mainStorey);
    	mapImageView.setImageBitmap(bmpMap);
    	//drawView.setAnchorPoint(topleft, bottomright, mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
    	drawView.setAnchorPoint(mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
    	drawView.invalidate();
    	
    	if(maxFloor == mainStorey.floor){
    		btnUpStair.setEnabled(false);
    	}
    	if(minBasement != mainStorey.floor){
    		btnDownstair.setEnabled(true);
    	}
    }
    
    /*Method of shrinking images*/
    private void zoomOut(){

    	// Tony COMMENTED @ 1st merge
//    	int bmpWidth = bmpMap.getWidth();
//    	int bmpHeight = bmpMap.getHeight();
//    	
//    	/*Set image shrinking scale*/
//    	double scale = 0.5;
//    	
//    	/*Calculate shrinking scale*/
//    	scaleWidth = (float)(scaleWidth*scale);
//    	scaleHeight = (float)(scaleHeight*scale);
    	
    	/*Product Bitmap after reSize*/
    	//Matrix bmpMatrix = new Matrix();
    	//bmpMatrix.postScale(scaleWidth, scaleHeight);
    	//Bitmap resizeBmp = Bitmap.createBitmap(bmpMap, 0, 0, bmpWidth, bmpHeight, bmpMatrix, true);
    	
    	/*
    	if(id == 0){
    		//If first push, remove original ImageView/
    		layout1.removeView(mImageView);
    	}else{
    		//If not, remove last ImageView/
    		layout1.removeView((ImageView)findViewById(id));
    	}
    	//produce new ImageView, push to Bitmap of reSize, and then push to Layout/
    	id++;
    	ImageView imageView = new ImageView(IndoorMap.this);
    	imageView.setId(id);
    	imageView.setImageBitmap(resizeBmp);
    	imageView.setScaleType(ImageView.ScaleType.MATRIX);
    	layout1.addView(imageView,0);
    	setContentView(layout1);
    	*/
    	//mapImageView.setImageBitmap(resizeBmp);
    	if(1 == scaleID){
    		scaleID = 0;
    		matrix.postScale((1f/mapScale[1]), (1f/mapScale[1]), displayWidth/2, displayHeight/2);
    		topleft.postScale((1f/mapScale[1]), (1f/mapScale[1]), displayWidth/2, displayHeight/2);
        	bottomright.postScale((1f/mapScale[1]), (1f/mapScale[1]), displayWidth/2, displayHeight/2);
        	
        	matrix.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		topleft.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		bottomright.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		mapImageView.setImageMatrix(matrix);
        	btnZoomOut.setEnabled(false);
    	}else if(2 == scaleID){
    		scaleID = 1;
    		
    		matrix.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		topleft.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		bottomright.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		mapImageView.setImageMatrix(matrix);
    	}else if(3 == scaleID){
    		scaleID = 2;
    		matrix.postScale(1f/mapScale[3], 1f/mapScale[3], displayWidth/2, displayHeight/2);
    		topleft.postScale(1f/mapScale[3], 1f/mapScale[3], displayWidth/2, displayHeight/2);
        	bottomright.postScale(1f/mapScale[3], 1f/mapScale[3], displayWidth/2, displayHeight/2);
        	
        	matrix.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		topleft.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		bottomright.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		mapImageView.setImageMatrix(matrix);
    	}else if(4 == scaleID){
    		scaleID = 3;
    		matrix.postScale(1f/mapScale[4], 1f/mapScale[4], displayWidth/2, displayHeight/2);
    		topleft.postScale(1f/mapScale[4], 1f/mapScale[4], displayWidth/2, displayHeight/2);
        	bottomright.postScale(1f/mapScale[4], 1f/mapScale[4], displayWidth/2, displayHeight/2);
        	
        	matrix.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		topleft.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		bottomright.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    		mapImageView.setImageMatrix(matrix);
        	btnZoomIn.setEnabled(true);
    	}

    	//drawView.setAnchorPoint(topleft, bottomright);

    	//Log.d(TAG, "dx:" + (event.getX()-start.x) + ", " + ", dy:" + (event.getY()-start.y));
	    Log.d(TAG, "top(" + topleft.x + ", " + topleft.y +"), down(" + bottomright.x + ", " + bottomright.y);
    	//Matrix pmatrix = new Matrix();
    	//pmatrix.postTranslate(50f, 50f);
    	//mImageView.setImageMatrix(pmatrix);
    	
    	/*enable blow-up button*/
    	//btnZoomOut.setEnabled(true);
    }
    
    /*Method of blow-up images*/
    private void zoomIn(){

    	int bmpWidth = bmpMap.getWidth();
    	int bmpHeight = bmpMap.getHeight();
    	
    	/*Set blow-up scale*/
    	double scale = 2;
    	
    	/*Calculate blow-up scale*/
    	scaleWidth = (float)(scaleWidth*scale);
    	scaleHeight = (float)(scaleHeight*scale);
    	
    	/*Product Bitmap after reSize*/
    	//Matrix bmpMatrix = new Matrix();
    	//bmpMatrix.postScale(scaleWidth, scaleHeight);
    	//Bitmap resizeBmp = Bitmap.createBitmap(bmpMap, 0, 0, bmpWidth, bmpHeight, bmpMatrix, true);
    	
    	/*
    	if(id == 0){
    		//If first push, remove original ImageView
    		layout1.removeView(mImageView);
    	}else{
    		//If not, remove last ImageView
    		layout1.removeView((ImageView)findViewById(id));
    	}
    	//produce new ImageView, push to Bitmap of reSize, and then push to Layout
    	id++;
    	ImageView imageView = new ImageView(IndoorMap.this);
    	imageView.setId(id);
    	imageView.setImageBitmap(resizeBmp);
    	imageView.setScaleType(ImageView.ScaleType.MATRIX);
    	layout1.addView(imageView,0);
    	setContentView(layout1);
    	*/
    	
    	//mapImageView.setImageBitmap(resizeBmp);

    	if(0 == scaleID){
    		scaleID = 1;
    		matrix.postScale(1f/mapScale[0], 1f/mapScale[0], displayWidth/2, displayHeight/2);
    		topleft.postScale(1f/mapScale[0], 1f/mapScale[0], displayWidth/2, displayHeight/2);
        	bottomright.postScale(1f/mapScale[0], 1f/mapScale[0], displayWidth/2, displayHeight/2);
        	
        	matrix.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	    topleft.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	    bottomright.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
        	btnZoomOut.setEnabled(true);
    	}else if(1 == scaleID){
    		scaleID = 2;
    		matrix.postScale(1f/mapScale[1], 1f/mapScale[1], displayWidth/2, displayHeight/2);
    		topleft.postScale(1f/mapScale[1], 1f/mapScale[1], displayWidth/2, displayHeight/2);
        	bottomright.postScale(1f/mapScale[1], 1f/mapScale[1], displayWidth/2, displayHeight/2);
        	
        	matrix.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	    topleft.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	    bottomright.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	}else if(2 == scaleID){
    		scaleID = 3;
    		
    		matrix.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	    topleft.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	    bottomright.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	}else if(3 == scaleID){
    		scaleID = 4;
    		matrix.postScale(1f/mapScale[3], 1f/mapScale[3], displayWidth/2, displayHeight/2);
    		topleft.postScale(1f/mapScale[3], 1f/mapScale[3], displayWidth/2, displayHeight/2);
        	bottomright.postScale(1f/mapScale[3], 1f/mapScale[3], displayWidth/2, displayHeight/2);
        	
        	matrix.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	    topleft.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
    	    bottomright.postScale(mapScale[scaleID], mapScale[scaleID], displayWidth/2, displayHeight/2);
        	btnZoomIn.setEnabled(false);
    	}
    	
    	mapImageView.setImageMatrix(matrix);
    	//drawView.setAnchorPoint(topleft, bottomright);
    	//Log.d(TAG, "dx:" + (event.getX()-start.x) + ", " + ", dy:" + (event.getY()-start.y));
	    Log.d(TAG, "top(" + topleft.x + ", " + topleft.y +"), down(" + bottomright.x + ", " + bottomright.y);
    	
    	/*If blow-up over the display size, disable blow-up button*/
    	if(scaleWidth*scale*bmpWidth > displayWidth || scaleHeight*scale*bmpHeight > displayHeight){
    		//mButton02.setEnabled(false);
    	}
    }
    
    public boolean moveToCenter(int longtitude, int latitude, int floor, String buildID){
    	boolean setBitmapFlag = true;
    	if(mainStorey.buildID != buildID || mainStorey.floor != floor){
    		
    		if(mainStorey.buildID != buildID){
    			setMaxAndMinFloor(buildID);
    		}
    		
    		tempStorey = mainStorey;
    		mainStorey = getStorey(buildID, floor);
    		
    		if(true == setBitmap(mainStorey)){
	        	mapImageView.setImageBitmap(bmpMap);
	        	//drawView.setAnchorPoint(topleft, bottomright, mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
	        	drawView.setAnchorPoint(mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
	        	if(maxFloor == mainStorey.floor){
		    		btnUpStair.setEnabled(false);
		    	}else{
		    		btnUpStair.setEnabled(true);
		    	}
		    	if(minBasement == mainStorey.floor){
		    		btnDownstair.setEnabled(false);
		    	}else{
		    		btnDownstair.setEnabled(true);
		    	}
    		}else{
    			mainStorey = tempStorey;
    			setBitmapFlag = false;
    		}
    	}
    	
    	float offsetX = displayWidth/2 - IndoorDrawView.longitudeToScreenX(longtitude);
    	float offsetY = displayHeight/2 - IndoorDrawView.latitudeToScreenY(latitude);
    	// 設置位移
		matrix.postTranslate(offsetX, offsetY);
		topleft.postTranslate(offsetX, offsetY);
	    bottomright.postTranslate(offsetX, offsetY);
	    mapImageView.setImageMatrix(matrix);
	    //drawView.setAnchorPoint(topleft, bottomright);
	    drawView.invalidate();
	    return setBitmapFlag;
    }
    
    private View.OnTouchListener imageTouch = new View.OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
	        // Handle touch events here...
	        ImageView view = (ImageView) v;
	
	        // Handle touch events here...
	        switch (event.getAction() & MotionEvent.ACTION_MASK) {
	        	//設置拖拉模式
	          	case MotionEvent.ACTION_DOWN:
	          		savedMatrix.set(matrix);
	          		start.set(event.getX(), event.getY());
	          		topleft.setTempPlace();
	          		bottomright.setTempPlace();
	          		Log.d(TAG, "mode=DRAG");
	          		touchMode = DRAG;
	          		break;

	          	case MotionEvent.ACTION_UP:
	          	case MotionEvent.ACTION_POINTER_UP:
	          		touchMode = NONE;
	          		Log.d(TAG, "mode=NONE" );
	          		break;
	          	//設置多點觸控模式
	          	case MotionEvent.ACTION_POINTER_DOWN:
	          		oldDist = spacing(event);
	          		Log.d(TAG, "oldDist=" + oldDist);
	          		if(oldDist > 10f){
	          			savedMatrix.set(matrix);
	          			midPoint(mid, event);
	          			touchMode = ZOOM;
	          			Log.d(TAG, "mode=ZOOM" );
	          		}
	          		break;
	            //若為DRAG模式，則移動圖片
	          	case MotionEvent.ACTION_MOVE:
	          		if(touchMode == DRAG){
	          			//設回起始觸控位置
	          			matrix.set(savedMatrix);
	          			topleft.restorePlace();
	          			bottomright.restorePlace();
	          			//設置位移
	          			matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
	          			topleft.postTranslate(event.getX() - start.x, event.getY() - start.y);
	          	        bottomright.postTranslate(event.getX() - start.x, event.getY() - start.y);
	          	        //drawView.setAnchorPoint(topleft, bottomright);
	          	        Log.d(TAG, "top(" + topleft.x + ", " + topleft.y +"), down(" + bottomright.x + ", " + bottomright.y);
	          		}else if(touchMode == ZOOM){//若為ZOOM模式，則多點觸控縮放
	          			float newDist = spacing(event);
	          			Log.d(TAG, "newDist=" + newDist);
	          			if (newDist > 10f) {
	          				//matrix.set(savedMatrix);
	          				//topleft.restore();
	              			//downright.restore();
	          				float scale = newDist / oldDist;
	          				//設置說縮放比例和圖中點位置
	          				//matrix.postScale(scale, scale, mid.x, mid.y);
	          				//topleft.postScale((float)scale, (float)scale, displayWidth/2, displayHeight/2);
	          		    	//downright.postScale((float)scale, (float)scale, displayWidth/2, displayHeight/2);
	          		    	//drawView.setAnchorPoint(topleft, downright);
	          				
	          		    	if(scale < 0.5f){
	          					zoomOut();
	          					return true; // indicate event was handled
	          				}else if(scale > 2f){
	          					zoomIn();
	          					return true; // indicate event was handled
	          				}
	          			}
	          		}
	          		break;
	        }
	
	        // Perform the transformation
	        view.setImageMatrix(matrix);
	        gestureDetector.onTouchEvent(event);
	        return true; // indicate event was handled
	    }
    };
    
    //Calculate moving distance
    private float spacing(MotionEvent event) {
    	float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
     
    //Calculate center point position
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    
    /*
    // Connect to the MotionEvent input
	private View.OnTouchListener drawTouch = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			//gestureDetector.onTouchEvent(event);		
			return false;
		}
	};*/
	
	// Create the gesture detector
	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {
		
		@Override
		public boolean onDown(MotionEvent e) {
			drawView.checkClickPoint(e.getX(), e.getY());
			Log.d(TAG, "click!!!!!!!!!!!");
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
				drawView.setPressPoint(e.getX(), e.getY());
				touchMode = LONGPRESS;
				Log.d(TAG, "press!!!!!!!!!!!");
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
	};
	
	private void initDB(){
		Log.d(TAG, "initDB");
		
		bookmarkCursor = null;
		bookmarkAdapter = null;
		
        bookmarkDB = new BookmarkDBManager(IndoorMap.this);
        
        new openBookmarkDBTask().execute(bookmarkDB);
		
        idrmapDB = new IndoorMapDBManager(this);
        //idrmapDB.getReadableDatabase();
        try{
        	idrmapDB.insertData();
        }catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
        //idrmapDB.getAll();
        //Cursor cursor = idrmapDB.selectMapByFloor("iii", 13);
        setMaxAndMinFloor(nowBuildID);
	}
	
	private void closeDB(){
		Log.d(TAG, "closeDB");
		
		idrmapDB.close();
		
		if(bookmarkDB.availability())
			bookmarkDB.close();
		
	}
	
	private void setMaxAndMinFloor(String buildID){
		floorCursor = idrmapDB.getAll(buildID);
        setFloorAdapter();
        floorCursor.moveToFirst();
        maxFloor = floorCursor.getInt(2);
        floorCursor.moveToLast();
        minBasement = floorCursor.getInt(2);
	}
	
	private Storey getStorey(String buildID, String connect){
		Storey tempStorey = new Storey();
		Cursor cursor = idrmapDB.selectMapByConnect(buildID, connect);
		if(0 != cursor.getCount()){
			cursor.moveToFirst();
			tempStorey.buildID = buildID;
			setStorey(tempStorey, cursor);
		}
		return tempStorey;
	}
	
	private Storey getStorey(String buildID, int floor){
		Storey tempStorey = new Storey();
		Cursor cursor = idrmapDB.selectMapByFloor(buildID, floor);
		if(0 != cursor.getCount()){
			cursor.moveToFirst();
			tempStorey.buildID = buildID;
			setStorey(tempStorey, cursor);
		}
		return tempStorey;
	}
	
	private void setStorey(Storey storey, Cursor cursor){

		storey.filename = cursor.getString(cursor.getColumnIndexOrThrow("picname"));
		storey.floor = cursor.getInt(cursor.getColumnIndexOrThrow("floor"));
		storey.left = cursor.getInt(cursor.getColumnIndexOrThrow("left_lon"));
		storey.top = cursor.getInt(cursor.getColumnIndexOrThrow("top_lat"));
		storey.right = cursor.getInt(cursor.getColumnIndexOrThrow("right_lon"));
		storey.bottom = cursor.getInt(cursor.getColumnIndexOrThrow("bottom_lat"));
		storey.floorname = cursor.getString(cursor.getColumnIndexOrThrow("floor_name"));
		storey.address = cursor.getString(cursor.getColumnIndexOrThrow("build_address"));
		/*
		if(maxFloor == storey.floor){
			storey.maxFloor = true;
		}else if(minBasement == storey.floor){
			storey.minBasement = true;
		}*/
	}
	
	private boolean setBitmap(Storey storey){
		String filepath = INDOOR_MAP_DIR + storey.filename +".jpg";
		
		/* Tony ADDED @ 20110111 */
		if(bmpMap != null)
			bmpMap.recycle();
		
        File f = new File(filepath);
        if(f.exists()){
        	bmpMap = BitmapFactory.decodeFile(filepath);
        	
        	if(null == topleft && null == bottomright){
        		Log.e(TAG, "no ANCHOR ");
            	topleft = new BitMapPoint(storey.left, storey.top, 0, 0);
                bottomright  = new BitMapPoint(storey.right, storey.bottom, bmpMap.getWidth(), bmpMap.getHeight());
                //Log.e(TAG, storey.right+", "+ storey.bottom+" ; "+bmpMap.getWidth()+", "+bmpMap.getHeight());
            }else{
            	Log.e(TAG, "have ANCHOR ");
            	topleft.setTempPlace();
                topleft.setCoordinate(storey.left, storey.top, topleft.tx, topleft.ty);
                bottomright.setCoordinate(storey.right, storey.bottom, bmpMap.getWidth()*mapScale[scaleID]+topleft.tx, bmpMap.getHeight()*mapScale[scaleID]+topleft.ty);
                //Log.e(TAG, storey.right+", "+ storey.bottom+" ; "+bmpMap.getWidth()+", "+bmpMap.getHeight());
            }
        	return true;
        }
        return false;
	}
	
	public void setFloorAdapter(){
		if(floorCursor != null){
			startManagingCursor(floorCursor);
			floorAdapter = new SimpleCursorAdapter(IndoorMap.this, 
					R.layout.simple_info, 
					floorCursor, 
					new String[] {IndoorMapDBManager.SPOINT_FIELD_FLOOR_NAME, IndoorMapDBManager.SPOINT_FIELD_INFO}, 
					new int[] {R.id.simple_info_title, R.id.simple_info_info});					
		}
	}
	
	public void openFloorListDialog(){
		
		if(floorCursor != null && floorAdapter != null)
		{
			new AlertDialog.Builder(IndoorMap.this)
			.setIcon(R.drawable.ic_dialog_floorlist)
			.setTitle(R.string.indoor_floorlist)
			.setNeutralButton(R.string.btn_cancel, null)
	        .setAdapter(floorAdapter, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					floorCursor.moveToPosition(arg1);
					
			    	if(mainStorey.floor != floorCursor.getInt(floorCursor.getColumnIndexOrThrow("floor"))){
						setStorey(mainStorey, floorCursor);
						setBitmap(mainStorey);
				    	mapImageView.setImageBitmap(bmpMap);
				    	//drawView.setAnchorPoint(topleft, bottomright, mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
				    	drawView.setAnchorPoint(mainStorey.floor, mainStorey.buildID, mainStorey.floorname);
				    	drawView.invalidate();
				    	
				    	if(maxFloor == mainStorey.floor){
				    		btnUpStair.setEnabled(false);
				    	}else{
				    		btnUpStair.setEnabled(true);
				    	}
				    	if(minBasement == mainStorey.floor){
				    		btnDownstair.setEnabled(false);
				    	}else{
				    		btnDownstair.setEnabled(true);
				    	}
					}
				}
            	
            })
			.show();
		}else{
			new AlertDialog.Builder(IndoorMap.this)
			.setIcon(R.drawable.ic_dialog_bookmark)
			.setTitle(R.string.indoor_floorlist)
			.setMessage(R.string.indoor_floorlist_none)
			.setPositiveButton(R.string.btn_ok, null)
			.show();
		}	
	}
	
	private class queryIndoorPOI extends AsyncTask<String, Void, Void>
	{
		@Override
		protected Void doInBackground(String... param) {

			HttpClient httpclient = new DefaultHttpClient();  
			HttpPost httppost = new HttpPost(INDOOR_SERVER_URL);  

			try {
				//建�?要�?交�?表單?��?  
	    		StringEntity reqEntity = new StringEntity("build="+param[0]);  
	    		//設置類�?  
	    		reqEntity.setContentType("application/x-www-form-urlencoded");  
	    		//設置请�??�数?? 
	    		httppost.setEntity(reqEntity);  
				
				HttpResponse response = httpclient.execute(httppost);
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
					
					String content = EntityUtils.toString(response.getEntity());
					
					if(-1 != content.indexOf("#")){	
						String str[] = content.split("#");
						if(str[1].length()>1){
							String strbuf[] = str[1].split("@");
							indoorPoiAmount = Integer.parseInt(strbuf[0]);
							if(strbuf[1].length()>1){
								indoorPoi = strbuf[1];
								poi = IndoorMap.indoorPoi.split(";");
							}
						}
					}
				}  
			} catch (ClientProtocolException e) {

				Log.e(TAG, e.getMessage());
			} catch (IOException e) {

				Log.e(TAG, e.getMessage());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			
			// Tony ADDED @ 20110111
			// When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
			indoorPOIReadyFlag = true;

			return null;
		}
	}
	/** Overlay management */
	private void poiLayerInit()
	{
		//TODO
		indoorPoiAmount = 0;
		indoorPOIReadyFlag = false;
		Arrays.fill(indoorPOIStatus, true);
		String params[] = {mainStorey.buildID};
		new queryIndoorPOI().execute(params);
	}
	
	public void openBookmarkDialog(final int operationMode)
	{
		if(bookmarkCursor != null && bookmarkAdapter != null)
		{
			bookmarkCursor.requery();
			
			new AlertDialog.Builder(IndoorMap.this)
			.setIcon(R.drawable.ic_dialog_bookmark)
			.setTitle(R.string.pmap_menu_bookmark)
			//.setNeutralButton(R.string.btn_cancel, null)
			.setPositiveButton(R.string.btn_cancel, null)
			.setNegativeButton("Edit Mode", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					editBookmarkDialog();
				}
			})
	        .setAdapter(bookmarkAdapter, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					bookmarkCursor.moveToPosition(arg1);
				
					if(arg1 == 0)
					{
						/*
						if(locationOverlay.getCurrentPoint() != null)
						{
							mapCtrl.animateTo(locationOverlay.getCurrentPoint());
							
							switch(operationMode)
							{
							case BOOKMARK_MODE_SET_START:
								directionSetup.setStart(true, bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_NAME),
										bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_INFO), 
										locationOverlay.getCurrentPoint());
								break;
							case BOOKMARK_MODE_SET_END:
								directionSetup.setEnd(true, bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_NAME),
										bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_INFO), 
										locationOverlay.getCurrentPoint());
								break;
							}
						}
						else*/
							Toast.makeText(IndoorMap.this, getString(R.string.pmap_bookmark_no_current_location), Toast.LENGTH_SHORT).show();
						
					}else{
						
						// Pin the custom point
						//GeoPoint location = new GeoPoint(bookmarkCursor.getInt(BookmarkDBManager.CCOLUMN_SPOINT_INT_LAT), bookmarkCursor.getInt(BookmarkDBManager.CCOLUMN_SPOINT_INT_LON));
						
						switch(operationMode)
						{
						case BOOKMARK_MODE_NORMAL:
							if(moveToCenter(bookmarkCursor.getInt(BookmarkDBManager.CCOLUMN_SPOINT_INT_LON),
											bookmarkCursor.getInt(BookmarkDBManager.CCOLUMN_SPOINT_INT_LAT), 
											bookmarkCursor.getInt(BookmarkDBManager.CCOLUMN_SPOINT_INT_FLOOR), 
											bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_BUILD_ID))){
								
								// TODO Auto-generated catch block
								poiLayerInit();
								
					    		drawView.setPressPoint(bookmarkCursor.getInt(BookmarkDBManager.CCOLUMN_SPOINT_INT_LON),
														bookmarkCursor.getInt(BookmarkDBManager.CCOLUMN_SPOINT_INT_LAT), 
														bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_NAME));
					    	}else{
					    		new AlertDialog.Builder(IndoorMap.this)
								.setTitle(R.string.indoor_no_map)
								.setIcon(R.drawable.ic_dialog_info)
								.setMessage(R.string.indoor_no_map_message)
								.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int whichButton) {
					                }
					            })
								.show();
					    	}
							break;
						
						case BOOKMARK_MODE_SET_START:
							/*
							directionSetup.setStart(false, bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_NAME),
									bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_INFO), 
									location);
							*/
							break;
						case BOOKMARK_MODE_SET_END:
							/*
							directionSetup.setEnd(false, bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_NAME),
									bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_INFO), 
									location);
							*/
							break;
						}

						//mapCtrl.animateTo(location);
						
					}
				}
            })
			.show();	
		}else{// [NOTE] According to current design, here should never be reached
			new AlertDialog.Builder(IndoorMap.this)
			.setIcon(R.drawable.ic_dialog_bookmark)
			.setTitle(R.string.pmap_menu_bookmark)
			.setMessage(R.string.pmap_bookmark_none)
			.setPositiveButton(R.string.btn_ok, null)
			.show();
		}	
	}
	
	public void editBookmarkDialog()
	{
		if(bookmarkCursor != null && bookmarkAdapter != null)
		{
			bookmarkCursor.requery();
			
			new AlertDialog.Builder(IndoorMap.this)
			.setIcon(R.drawable.ic_dialog_edit)
			.setTitle(R.string.pmap_menu_bookmark)
			.setNeutralButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					openBookmarkDialog(BOOKMARK_MODE_NORMAL);
				}
			})
	        .setAdapter(bookmarkAdapter, new DialogInterface.OnClickListener(){
	        	
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					bookmarkCursor.moveToPosition(arg1);
					
					if(arg1 == 0)
					{
						/*
						if(locationOverlay.getCurrentPoint() != null)
						{
							mapCtrl.animateTo(locationOverlay.getCurrentPoint());
							
							switch(operationMode)
							{
							case BOOKMARK_MODE_SET_START:
								directionSetup.setStart(true, bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_NAME),
										bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_INFO), 
										locationOverlay.getCurrentPoint());
								break;
							case BOOKMARK_MODE_SET_END:
								directionSetup.setEnd(true, bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_NAME),
										bookmarkCursor.getString(BookmarkDBManager.CCOLUMN_SPOINT_STR_INFO), 
										locationOverlay.getCurrentPoint());
								break;
							}
						}
						else*/
							Toast.makeText(IndoorMap.this, getString(R.string.pmap_bookmark_no_current_location), Toast.LENGTH_SHORT).show();
							
					}else{
						new AlertDialog.Builder(IndoorMap.this)
						.setIcon(R.drawable.ic_dialog_bookmark)
						.setTitle(R.string.pmap_menu_bookmark)
						.setMessage(R.string.pmap_bookmark_none)
						.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						})
						.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						})
						.show();
					}
				}
            })
			.show();	
		}else{// [NOTE] According to current design, here should never be reached
			new AlertDialog.Builder(IndoorMap.this)
			.setIcon(R.drawable.ic_dialog_bookmark)
			.setTitle(R.string.pmap_menu_bookmark)
			.setMessage(R.string.pmap_bookmark_none)
			.setPositiveButton(R.string.btn_ok, null)
			.show();
		}	
	}
	
	private void searchPoi(){
		Bundle bundle = new Bundle();
		bundle.putString(IndoorPoiSearch.BUNDLE_BUILD, nowBuildID);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchManager.startSearch(null, true, IndoorMap.this.getComponentName(), bundle, false);
	}
}