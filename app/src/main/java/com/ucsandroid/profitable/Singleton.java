package com.ucsandroid.profitable;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.ucsandroid.profitable.serverclasses.Location;


public class Singleton {

    static final int TYPE_TABLE = 1;
    static final int TYPE_BAR = 2;
    static final int TYPE_TAKEOUT = 3;

	private final String TAG = "Profit Tag";
	private static Singleton instance = null;
	private static Context mContext;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private ArrayList<Location> mTables;	    //local storage of orders
    private ArrayList<Location> mBars;	        //local storage of orders
    private ArrayList<Location> mTakeouts;	    //local storage of orders

    private int currentLocationPosition = -1;
    private int currentLocationType = -1;

	/**
	 * To initialize the class. It must be called before call the method getInstance()
	 * @param ctx The Context used
	 */
	public static void initialize(Context ctx) {
		mContext = ctx;
	}

	/**
	 * Check if the class has been initialized
	 * @return true  if the class has been initialized
	 *         false Otherwise
	 */
	public static boolean hasBeenInitialized() {
		return mContext != null;
	}

	/**
	 * The private constructor. Here you can use the context to initialize your variables.
	 */
	private Singleton() {
	}
	/**
	 * The main method used to get the instance
	 */
	public static synchronized Singleton getInstance() {
		if (mContext == null) {
			throw new IllegalArgumentException("Impossible to get the instance. This class must be initialized before");
		}

		if (instance == null) {
			instance = new Singleton();
		}

		return instance;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}



	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext);
		}

		return mRequestQueue;
	}


	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(mRequestQueue,
					new ImageLoader.ImageCache() {
						private final LruCache<String, Bitmap>
								cache = new LruCache<String, Bitmap>(20);

						@Override
						public Bitmap getBitmap(String url) {
							return cache.get(url);
						}

						@Override
						public void putBitmap(String url, Bitmap bitmap) {
							cache.put(url, bitmap);
						}
					});
		}

		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}



    public boolean hasLocationData(){
        return (mTables != null || mBars != null || mTakeouts != null);
    }

    /**
     * A complete location structure of the entire restaurant gets passed into here
     * @param locations
     * @throws JSONException
     */
	public void setLocations(JSONArray locations) throws JSONException {


        for(int a = 0; a < locations.length(); a++){

            if(locations.getJSONObject(a).getInt("locationCategoryId") == TYPE_TABLE){
                setTables(locations.getJSONObject(a).getJSONArray("locations"));
            }
            else if(locations.getJSONObject(a).getInt("locationCategoryId") == TYPE_BAR){
                setBars(locations.getJSONObject(a).getJSONArray("locations"));
            }
            else if(locations.getJSONObject(a).getInt("locationCategoryId") == TYPE_TAKEOUT){
                setTakeouts(locations.getJSONObject(a).getJSONArray("locations"));
            }
        }
	}


    //General location details
    public void setLocationType(int locationType){
        currentLocationType = locationType;
    }

    public int getCurrentLocationType(){
        return currentLocationType;
    }

    public void setCurrentLocationPosition(int position){
        currentLocationPosition = position;
    }

    public int getCurrentLocationPosition(){
        return currentLocationPosition;
    }

    public Location getCurrentLocation(){

        if(currentLocationType == TYPE_TABLE){
            return mTables.get(currentLocationPosition);
        }
        else if(currentLocationType == TYPE_BAR){
            return mBars.get(currentLocationPosition);
        }
        else if(currentLocationType == TYPE_TAKEOUT){
            return mTakeouts.get(currentLocationPosition);
        }
        else{
            System.out.println("invalid location type: "+currentLocationType);
            return null;
        }
    }


    //Table calls
    public void setTables(JSONArray locations) throws JSONException {

        System.out.println("locations: "+locations);

        mTables = new ArrayList<>();
        for(int a = 0; a < locations.length(); a++){
            Gson gson = new Gson();
            Location location = gson.fromJson(locations.getJSONObject(a).toString(), Location.class);
            System.out.println("location: "+location.getName());
            mTables.add(location);
            //mTables.add(new Location(locations.getJSONObject(a)));
        }
    }

    public ArrayList<Location> getTables(){
        return mTables;
    }


    //Bar calls
    public ArrayList<Location> getBars(){
        return mBars;
    }


    public void setBars(JSONArray locations) throws JSONException {
        mBars = new ArrayList<>();
        for(int a = 0; a < locations.length(); a++){
            Gson gson = new Gson();
            Location location = gson.fromJson(locations.getJSONObject(a).toString(), Location.class);
            System.out.println("location: "+location.getName());
            mBars.add(location);
            //mBars.add(new Location(locations.getJSONObject(a)));
        }
    }



    //Takeout calls
    public void setTakeouts(JSONArray locations) throws JSONException {
        mTakeouts = new ArrayList<>();
        mTakeouts.add(new Location());              //plus button holder
        for(int a = 0; a < locations.length(); a++){
            Gson gson = new Gson();
            Location location = gson.fromJson(locations.getJSONObject(a).toString(), Location.class);
            System.out.println("location: "+location.getName());
            mTakeouts.add(location);
            //mTakeouts.add(new Location(locations.getJSONObject(a)));
        }
    }

    public ArrayList<Location> getTakeouts(){
        return mTakeouts;
    }


}
