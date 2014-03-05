package com.comfycouch.mtrakk;

import org.andengine.entity.scene.Scene;

import Managers.ResourceManager;


public abstract class ManagedScene extends Scene {
	//tells the Scene Manager that the managed scene either has or doesn't have a loading screen
	public final boolean hasLoadingScreen;
	//the minimum length of time (in seconds) that the loading screen should be displayed
	public final float minLoadingScreenTime;
	//keeps track of how long the loading screen has been visible. Set by the SceneManager.
	public float elapsedLoadingScreenTime = 0f;
	//is set TRUE if the scene is loaded
	public boolean isLoaded = false;
	//convenience constructor that disabled the loading screen.
	public boolean willHandle_isLoaded = true;
	
	public ManagedScene() {
		this(0f);
	}
	
	//Constructor that sets the minimum length of the loading screen and sets hasLoadingScreen accordingly.
	public ManagedScene(final float pLoadingScreenMinimumSecondsShown) {
		minLoadingScreenTime = pLoadingScreenMinimumSecondsShown;
		hasLoadingScreen = (minLoadingScreenTime > 0f);
	}
	
	//called by the Scene Manager. It calls onLoadScene if loading is needed, sets the isLoaded status, and pauses the scene while it's not shown.
	public void onLoadManagedScene() {
		if(!isLoaded) {
			onLoadScene();
			if(willHandle_isLoaded) {
				isLoaded = true;
				this.setIgnoreUpdate(true);
			}
		}
	}
	
	//called by the Scene Manager. It calls onUnloadScene if the scene had been previously loaded and sets the isLoaded status.
	public void onUnloadManagedScene() {
		if(isLoaded) {
			isLoaded = false;
			ResourceManager.getInstance().engine.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					onUnloadScene();
				}
				
			});
		}
	}
	
	//called by the Scene Manager. It un-pauses the scene before showing it.
	public void onShowManagedScene() {
		this.setIgnoreUpdate(false);
		onShowScene();
	}
	
	//called by the Scene Manager. It pauses the scene before hiding it.
	public void onHideManagedScene() {
		this.setIgnoreUpdate(true);
		onHideScene();
	}
	
	//Methods to override in the sub-classes
	public abstract Scene onLoadingScreenLoadAndShown();
	public abstract void onLoadingScreenUnloadAndHidden();
	public abstract void onLoadScene();
	public abstract void onShowScene();
	public abstract void onHideScene();
	public abstract void onUnloadScene();
}
