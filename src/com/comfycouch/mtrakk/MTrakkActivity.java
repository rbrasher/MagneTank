package com.comfycouch.mtrakk;

import org.andengine.engine.Engine;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.IResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import Managers.ResourceManager;
import Managers.SFXManager;
import Managers.SceneManager;
import Menus.MainMenu;
import Menus.MainMenu.MainMenuScreens;
import Menus.ManagedMenuScene;
import Menus.SplashScreens;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.comfycouch.mtrakk.R;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

/**
 * This activity class builds upon the standard AndEngine BaseGameActivity
 * with the addition of ads, some advanced resolution-scaling performed in
 * the onCreateEngineOptions() method, and shared-preference methods to save
 * and restore options and scores
 * 
 * @author ron
 *
 */

public class MTrakkActivity extends BaseGameActivity {
	
	//variables that hold references to the Layout and AdView
	public static FrameLayout mFrameLayout;
	public static AdView adView;
	
	//a method to show the ads.
	public void showAds() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				adView.setVisibility(View.VISIBLE);
				final AdRequest adRequest = new AdRequest();
				adView.loadAd(adRequest);
			}
		});
	}
	
	//Create and set the AdView
	@Override
	protected void onSetContentView() {
		final FrameLayout frameLayout = new FrameLayout(this);
		final FrameLayout.LayoutParams frameLayoutLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		//TODO: Replace string of X's with your AdMob code
		adView = new AdView(this, AdSize.BANNER, "ca-app-pub-4625947242372265/5867629436");
		adView.refreshDrawableState();
		adView.setVisibility(AdView.VISIBLE);
		final FrameLayout.LayoutParams adViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM);
		this.mRenderSurfaceView = new RenderSurfaceView(this);
		mRenderSurfaceView.setRenderer(mEngine, this);
		final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams = new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());
		frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
		frameLayout.addView(adView, adViewLayoutParams);
		this.setContentView(frameLayout, frameLayoutLayoutParams);
		mFrameLayout = frameLayout;
	}
	
	
	//CONSTANTS
	
	//Define a minimum and maximum window resolution (to prevent
	//cramped or overlapping elements)
	static float MIN_WIDTH_PIXELS = 800f, MIN_HEIGHT_PIXELS = 480f;
	static float MAX_WIDTH_PIXELS = 1600f, MAX_HEIGHT_PIXELS = 960f;
	
	//This design device is the Samsung Galaxy Note (SGH-I717)
	//The resolution of the window with which you are developing
	static float DESIGN_WINDOW_WIDTH_PIXELS = 1280f;
	static float DESIGN_WINDOW_HEIGHT_PIXELS = 800f;
	
	//The physical size of the window with which you are developing.
	//calculated by dividing the window's x & y pixels by the x & y DPI.
	static float DESIGN_WINDOW_WIDTH_INCHES = 4.472441f;
	static float DESIGN_WINDOW_HEIGHT_INCHES = 2.805118f;
	
	//VARIABLES
	public float cameraWidth;
	public float cameraHeight;
	public float actualWindowWidthInches;
	public float actualWindowHeightInches;
	public MTrakkSmoothCamera mCamera;
	
	//ENGINE
	
	//create and return a Switchable Fixed-Step Engine
	@Override
	public Engine onCreateEngine(final EngineOptions pEngineOptions) {
		return new SwitchableFixedStepEngine(pEngineOptions, 50, false);
	}
	
	//Handle the Back Button
	@Override
	public void onBackPressed() {
		//if the ResourceManager has been setup...
		if(ResourceManager.getInstance().engine != null) {
			//if a layer is shown
			if(SceneManager.getInstance().mIsLayerShown)
				//hide the layer
				SceneManager.getInstance().mCurrentLayer.onHideLayer();
			//or if a game level is show
			else if(SceneManager.getInstance().mCurrentScene.getClass().getGenericSuperclass().equals(ManagedMenuScene.class) & !SceneManager.getInstance().mCurrentScene.getClass().equals(MainMenu.class))
				//Show the main menu
				SceneManager.getInstance().showMainMenu();
			//or if the main menu is already shown...
			else
				//if the Main Menu is on the Level Select Screen...
				if(MainMenu.getInstance().mCurrentScreen == MainMenuScreens.LevelSelector)
					//go to the title screen
					MainMenu.getInstance().goToTitleScreen();
				//or if the Main Menu is already on the title screen
				else
					//exit the game.
					System.exit(0);
		}
	}
	
	
	/**
	 * Create Engine options
	 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		//Override the onMeasure method of the ResolutionPolicy to set the
		//camera's size. This way usually gives better results compared
		//to the DisplayMetrics.getWidth method because it uses the
		//window instead of the display. This should also be better for if
		//the game is placed in a layout where simply measuring the display
		//would give entirely wrong results
		
		FillResolutionPolicy EngineFillResolutionPolicy = new FillResolutionPolicy() {
			@Override
			public void onMeasure(final IResolutionPolicy.Callback pResolutionPolicyCallback, final int pWidthMeasureSpec, final int pHeightMeasureSpec) {
				super.onMeasure(pResolutionPolicyCallback, pWidthMeasureSpec, pHeightMeasureSpec);
				
				final int measuredWidth = MeasureSpec.getSize(pWidthMeasureSpec);
				final int measuredHeight = MeasureSpec.getSize(pHeightMeasureSpec);
				
				//log the pixel values needed for setting up the design window's values
				Log.v("AndEngine", "Design window width & height (in pixels): " + measuredWidth + ", " + measuredHeight);
				Log.v("AndEngine", "Design window width & height (in inches): " + measuredWidth / getResources().getDisplayMetrics().xdpi + ", " + measuredHeight / getResources().getDisplayMetrics().ydpi);
				
				//determine the device's physical window size
				actualWindowWidthInches = measuredWidth / getResources().getDisplayMetrics().xdpi;
				actualWindowHeightInches = measuredHeight / getResources().getDisplayMetrics().ydpi;
				
				//get an initial width for the camera, and bound it to the minimum or maximum values.
				float actualScaledWidthInPixels = DESIGN_WINDOW_WIDTH_PIXELS * (actualWindowWidthInches / DESIGN_WINDOW_WIDTH_INCHES);
				float boundScaledWidthInPixels = Math.round(Math.max(Math.min(actualScaledWidthInPixels, MAX_WIDTH_PIXELS), MIN_WIDTH_PIXELS));
				
				//get the height for the camera based on the width and the height/width ratio of the device
				float boundScaledHeightInPixels = boundScaledWidthInPixels * (actualWindowHeightInches / actualWindowWidthInches);
				//if the height is outside of the set bounds, scale the width to match it.
				if(boundScaledHeightInPixels > MAX_HEIGHT_PIXELS) {
					float boundAdjustmentRatio = MAX_HEIGHT_PIXELS / boundScaledHeightInPixels;
					boundScaledWidthInPixels *= boundAdjustmentRatio;
					boundScaledHeightInPixels *= boundAdjustmentRatio;
				} else if(boundScaledHeightInPixels < MIN_HEIGHT_PIXELS) {
					float boundAdjustmentRatio = MIN_HEIGHT_PIXELS / boundScaledHeightInPixels;
					boundScaledWidthInPixels *= boundAdjustmentRatio;
					boundScaledHeightInPixels *= boundAdjustmentRatio;
				}
				//set the height and width variables
				cameraHeight = boundScaledHeightInPixels;
				cameraWidth = boundScaledWidthInPixels;
				
				//apply the height and width variables
				mCamera.set(0f, 0f, cameraWidth, cameraHeight);
			}
		};
		
		//create the Camera and EngineOptions
		mCamera = new MTrakkSmoothCamera(0, 0, 320, 240, 4000f, 2000f, 0.5f);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, EngineFillResolutionPolicy, mCamera);
		
		//enable sounds
		engineOptions.getAudioOptions().setNeedsSound(true);
		//enable music
		engineOptions.getAudioOptions().setNeedsMusic(true);
		//turn on dithering to smooth texture gradients
		engineOptions.getRenderOptions().setDithering(true);
		//turn on multi sampling to smooth the alias of hard-edged elements
		engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
		//set the wake lock options to prevent the engine from dumping textures when focus changes
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		
		//return our engineOptions
		return engineOptions;
	}
	
	// ==========================================================
	// ACCESS TO SHARED RESOURCES
	// ==========================================================
	
	//the name of the shared preferences used by this game
	public static final String SHARED_PREFS_MAIN = "MTrakkSettings";
	//the quality (Boolean) setting. True = High Quality
	public static final String SHARED_PREFS_HIGH_QUALITY_GRAPHICS = "quality";
	//how many stars (Integer) the player got in each level
	public static final String SHARED_PREFS_LEVEL_STARS = "level.stars.";
	//The high score (Integer) that the player has gotten for each level
	public static final String SHARED_PREFS_LEVEL_HIGHSCORE = "level.highscore.";
	//the highest level (Integer) reached by the player
	public static final String SHARED_PREFS_LEVEL_MAX_REACHED = "levels.reached.";
	//the number (Integer) of times that the application has been started
	public static final String SHARED_PREFS_ACTIVITY_START_COUNT = "count";
	//the player has rated the game. TRUE = player has agreed to rate it
	public static final String SHARED_PREFS_RATING_SUCCESS = "rating";
	//the muted state of the music. True = muted.
	public static final String SHARED_PREFS_MUSIC_MUTED = "mute.music";
	//the muted state of the sound effects. True = muted.
	public static final String SHARED_PREFS_SOUNDS_MUTED = "mute.sounds";
	
	//the number of times that the application has started, stored as a local value
	public int numTimesActivityOpened;
	
	//methods to read/write Integers in the Shared Preferences.
	public static int writeIntToSharedPreferences(final String pStr, final int pValue) {
		//the apply() method requires API level 9 in the manifest
		ResourceManager.getActivity().getSharedPreferences(SHARED_PREFS_MAIN, 0).edit().putInt(pStr, pValue).apply();
		return pValue;
	}
	
	public static int getIntFromSharedPreferences(final String pStr) {
		return ResourceManager.getActivity().getSharedPreferences(SHARED_PREFS_MAIN, 0).getInt(pStr, 0);
	}
	
	//Methods to write/read Booleans in the Shared Preferences
	public static void writeBooleanToSharedPreferences(final String pStr, final boolean pValue) {
		ResourceManager.getActivity().getSharedPreferences(SHARED_PREFS_MAIN, 0).edit().putBoolean(pStr, pValue).apply();
	}
	
	public static boolean getBooleanFromSharedPreferences(final String pStr) {
		return ResourceManager.getActivity().getSharedPreferences(SHARED_PREFS_MAIN, 0).getBoolean(pStr, false);
	}
	
	//a convenience method for accessing how many stars the player achieved on a certain level
	public static int getLevelStars(final int pLevelNumber) {
		return getIntFromSharedPreferences(SHARED_PREFS_LEVEL_STARS + String.valueOf(pLevelNumber));
	}
	
	// =================================================================================
	// CREATE RESOURCES
	// =================================================================================
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		// setup the ResourceManager
		ResourceManager.setup(this, (SwitchableFixedStepEngine) this.getEngine(), this.getApplicationContext(), cameraWidth, cameraHeight, cameraWidth / DESIGN_WINDOW_WIDTH_PIXELS, cameraHeight / DESIGN_WINDOW_HEIGHT_PIXELS);
		
		//retrieve and increment the number of times that the application has started
		numTimesActivityOpened = getIntFromSharedPreferences(SHARED_PREFS_ACTIVITY_START_COUNT) + 1;
		writeIntToSharedPreferences(SHARED_PREFS_ACTIVITY_START_COUNT, numTimesActivityOpened);
		
		//retrieve the quality level setting
		final boolean DeviceLimited = getBooleanFromSharedPreferences(SHARED_PREFS_HIGH_QUALITY_GRAPHICS);
		ResourceManager.getInstance().useHighQuality = DeviceLimited;
		
		//tell the callback that the resources have loaded.
		//we'll be handling the actual loading of resources at specific points throughout the application flow.
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	// ==================================================================================
	// CREATE SCENE
	// ==================================================================================
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		//on every third time that the application is started...
		if(numTimesActivityOpened % 3 == 0) {
			//if a market rating has not already been given, ask for one.
			if(getIntFromSharedPreferences(SHARED_PREFS_RATING_SUCCESS) < 1)
				this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AlertDialog.Builder  builder = new AlertDialog.Builder(MTrakkActivity.this);
						builder.setTitle("Like what you see?");
						builder.setIcon(R.drawable.icon);
						builder.setMessage("Would you like to rate this game?")
						.setPositiveButton("Of course!", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int id) {
								//start the market and navigate to the application's page.
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse("market://details?id=com.comfycouch.MTrakk"));
								startActivity(intent);
								writeIntToSharedPreferences(SHARED_PREFS_RATING_SUCCESS, 1);
							}
						})
						.setNegativeButton("Maybe later...", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int id) {
								
							}
						}).setCancelable(false);
						AlertDialog alert = builder.create();
						alert.show();
					}
				});
		}
		
		//load the resources necessary for the Main Menu
		ResourceManager.loadMenuResources();
		//tell the SceneManager to show the splash screens
		SceneManager.getInstance().showScene(new SplashScreens());
		//set the MainMenu to the Engine's scene.
		pOnCreateSceneCallback.onCreateSceneFinished(mEngine.getScene());
		
		//start the background music.
		SFXManager.playMusic();
		
		//if the music is muted in the settings, mute it in the game.
		if(getIntFromSharedPreferences(SHARED_PREFS_MUSIC_MUTED) > 0)
			SFXManager.setMusicMuted(true);
		
		//if the sound effects are muted in the settings, mute them in the game
		if(getIntFromSharedPreferences(SHARED_PREFS_SOUNDS_MUTED) > 0)
			SFXManager.setSoundMuted(true);
	}
	
	// ============================================================================
	// POPULATE SCENE
	// ============================================================================
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
		//our SceneManager will handle the population of the scenes, so we do nothing here
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	// ============================================================================
	// LIFECYCLE METHODS
	// ============================================================================
	
	//when the game is already loaded and paused, pause the music
	@Override
	protected void onPause() {
		super.onPause();
		if(this.isGameLoaded())
			SFXManager.pauseMusic();
	}
	
	//When the game is resumed, tell the system that it should perform a
	//garbage collection to clean up previous applications.
	//If the game is already loaded resume the music
	@Override
	protected void onResume() {
		super.onResume();
		System.gc();
		if(this.isGameLoaded())
			SFXManager.resumeMusic();
	}
	
	//Some devices do not exit the game when the activity is destroyed.
	//This ensures that the game is closed
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}


}
