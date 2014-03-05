package Managers;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;

import Layers.ManagedLayer;
import Layers.OptionsLayer;
import Menus.MainMenu;

import com.comfycouch.mtrakk.ManagedScene;

/**
 * SceneManager class
 * 
 * @author ron
 *
 */

public class SceneManager extends Object {
	private static final SceneManager INSTANCE = new SceneManager();
	
	// ==========================================
	// VARIABLES
	// ==========================================
	
	//these variables reference the current scene and next scene when switching scenes
	public ManagedScene mCurrentScene;
	private ManagedScene mNextScene;
	
	//keep a reference to the engine
	private Engine mEngine = ResourceManager.getInstance().engine;
	//used by mLoadingScreenHandler, this variable ensures that the loading screen is shown for one frame prior to loading resources
	private int mNumFramesPassed = -1;
	//keeps the mLoadingScreenHandler from being registered with the engine if it has already been registered
	private boolean mLoadingScreenHandlerRegistered = false;
	// an update handler that shows the loading screen of mNextScene before calling it to load
	private IUpdateHandler mLoadingScreenHandler = new IUpdateHandler() {

		@Override
		public void onUpdate(float pSecondsElapsed) {
			//increment the mNumFramesPassed
			mNumFramesPassed++;
			//increment amount of time that the loading screen has been visible
			mNextScene.elapsedLoadingScreenTime += pSecondsElapsed;
			//on the first frame after the loading screen has been shown
			if(mNumFramesPassed == 1) {
				//hide and unload the previous scene if it exists
				if(mCurrentScene != null) {
					mCurrentScene.onHideManagedScene();
					mCurrentScene.onUnloadManagedScene();
				}
				//load the new scene
				mNextScene.onLoadManagedScene();
			}
			//on the first frame after the scene has been completely loaded and the loading screen has been shown for its minimum limit
			if(mNumFramesPassed > 1 && mNextScene.elapsedLoadingScreenTime >= mNextScene.minLoadingScreenTime && mNextScene.isLoaded) {
				//remove the loading screen that was set as a child in the showScene() method.
				mNextScene.clearChildScene();
				//tell the new scene to unload it loading screen
				mNextScene.onLoadingScreenUnloadAndHidden();
				//tell the new scene that it is shown
				mNextScene.onShowManagedScene();
				//set the new scene to the current scene
				mCurrentScene = mNextScene;
				//reset the handler & loading screen variables to be ready for another use
				mNextScene.elapsedLoadingScreenTime = 0f;
				mNumFramesPassed = -1;
				mEngine.unregisterUpdateHandler(this);
				mLoadingScreenHandlerRegistered = false;
			}
		}

		@Override
		public void reset() {

		}
		
	};
	
	//set to TRUE in the showLayer() method if the camera had a HUD before the layer was shown
	private boolean mCameraHadHud = false;
	//boolean to reflect whether there is a layer currently shown on the screen
	public boolean mIsLayerShown = false;
	//an empty place holder scene that we use to apply the modal properties of the layer to the currently shown scene
	private Scene mPlaceholderModalScene;
	//hold a reference to the current managed layer (if one exists)
	public ManagedLayer mCurrentLayer;
	
	// ==========================================
	// CONSTRUCTOR
	// ==========================================
	public SceneManager() {
		
	}
	
	public static SceneManager getInstance() {
		return INSTANCE;
	}
	
	// ==========================================
	// PUBLIC METHODS
	// ==========================================
	
	//convenience method to quickly show the Main Menu
	public void showMainMenu() {
		showScene(MainMenu.getInstance());
	}
	
	//Initiates the process of switching the current managed scene for a new managed scene
	public void showScene(final ManagedScene pManagedScene) {
		//reset the camera. This is automatically overridden by any calls to alter the camera from within a managed scene's onShowScene() method
		mEngine.getCamera().set(-ResourceManager.getInstance().cameraWidth / 2f, -ResourceManager.getInstance().cameraHeight / 2f, ResourceManager.getInstance().cameraWidth / 2f, ResourceManager.getInstance().cameraHeight / 2f);
		//if the new managed scene has a loading screen
		if(pManagedScene.hasLoadingScreen) {
			//set the loading screen as a modal child to the new managed scene
			pManagedScene.setChildScene(pManagedScene.onLoadingScreenLoadAndShown(), true, true, true);
			//this if/else block assures that the LoadingScreen Update Handler is only registered if necessary
			if(mLoadingScreenHandlerRegistered) {
				mNumFramesPassed = -1;
				mNextScene.clearChildScene();
				mNextScene.onLoadingScreenUnloadAndHidden();
			} else {
				mEngine.registerUpdateHandler(mLoadingScreenHandler);
				mLoadingScreenHandlerRegistered = true;
			}
			//set pManagedScene to mNextScene which is used by the loading screen update handler
			mNextScene = pManagedScene;
			//set the new scene as the engine's scene
			mEngine.setScene(pManagedScene);
			//exit the method and let the LoadingScreen Update Handler finish the switching.
			return;
		}
		//if the new managed scene does not have a loading screen
		//set pManagedScene to mNextScene and apply the new scene to the engine
		mNextScene = pManagedScene;
		mEngine.setScene(pManagedScene);
		//if a previous managed scene exists, hide and unload it
		if(mCurrentScene != null) {
			mCurrentScene.onHideManagedScene();
			mCurrentScene.onUnloadManagedScene();
		}
		//load and show the new managed scene, and set it as the current scene
		mNextScene.onLoadManagedScene();
		mNextScene.onShowManagedScene();
		mCurrentScene = mNextScene;
	}
	
	//convenience method to quickly show the Options Layer
	public void showOptionsLayer(final boolean pSuspendCurrentSceneUpdates) {
		showLayer(OptionsLayer.getInstance(), false, pSuspendCurrentSceneUpdates, true);
	}
	
	//shows a layer by placing it as a child to the Camera's HUD
	public void showLayer(final ManagedLayer pLayer, final boolean pSuspendSceneDrawing, final boolean pSuspendSceneUpdates, final boolean pSuspendSceneTouchEvents) {
		//if the camera already has a HUD, we will use it
		if(mEngine.getCamera().hasHUD()) {
			mCameraHadHud = true;
		} else {
			//otherwise, create one to use
			mCameraHadHud = false;
			HUD placeholderHud = new HUD();
			mEngine.getCamera().setHUD(placeholderHud);
		}
		//if the managed layer needs modal properties, set them
		if(pSuspendSceneDrawing || pSuspendSceneUpdates || pSuspendSceneTouchEvents) {
			//apply the managed layer directly to the camera's HUD
			mEngine.getCamera().getHUD().setChildScene(pLayer, pSuspendSceneDrawing, pSuspendSceneDrawing, pSuspendSceneTouchEvents);
			mEngine.getCamera().getHUD().setOnSceneTouchListenerBindingOnActionDownEnabled(true);
			//create the placeholder scene if it needs to be created
			if(mPlaceholderModalScene == null) {
				mPlaceholderModalScene = new Scene();
				mPlaceholderModalScene.setBackgroundEnabled(false);
			}
			//apply the place holder to the current scene
			mCurrentScene.setChildScene(mPlaceholderModalScene, pSuspendSceneDrawing, pSuspendSceneUpdates, pSuspendSceneTouchEvents);
		} else {
			//if the managed layer does not need to be modal, simply set it to the HUD
			mEngine.getCamera().getHUD().setChildScene(pLayer);
		}
		//set the camera for the managed layer so that it binds to the camera if the camera is moved/scaled/rotated
		pLayer.setCamera(mEngine.getCamera());
		//scale the layer according to screen size
		//	pLayer.setScale(ResourceManager.getInstance().cameraScaleFactorX, ResourceManager.getInstance().cameraScaleFactorY);
		//let the layer know that it is being shown
		pLayer.onShowManagedLayer();
		//reflect that a layer is show
		mIsLayerShown = true;
		//set the current layer to pLayer
		mCurrentLayer = pLayer;
	}
	
	//hides the open layer if one is open
	public void hideLayer() {
		if(mIsLayerShown) {
			//clear the HUD's child scene to remove modal properties
			mEngine.getCamera().getHUD().clearChildScene();
			//if we had to use a place-holder scene, clear it
			if(mCurrentScene.hasChildScene())
				if(mCurrentScene.getChildScene() == mPlaceholderModalScene)
					mCurrentScene.clearChildScene();
			//if the camera did not have a HUD before we showed the layer, remove the place-holder HUD
			if(!mCameraHadHud)
				mEngine.getCamera().setHUD(null);
			//reflect that a layer is no longer shown
			mIsLayerShown = false;
			//remove the reference to the layer
			mCurrentLayer = null;
		}
	}
}
