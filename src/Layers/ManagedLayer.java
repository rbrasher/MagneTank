package Layers;

import org.andengine.entity.scene.CameraScene;

/**
 * 
 * @author ron
 *
 */

public abstract class ManagedLayer extends CameraScene {
	public static final float mSLIDE_PIXELS_PER_SECONDS = 3000f;
	//is set TRUE if the layer is loaded
	public boolean mHasLoaded = false;
	//Set by the constructor, if true, the layer will be unloaded after being hidden
	public boolean mUnloadOnHidden;
	
	//convenience constructor. Creates a layer that does not unload when hidden
	public ManagedLayer() {
		this(false);
	}
	
	//Constructor. Sets whether the layer will unload when hidden and ensures that there is no background on the layer
	public ManagedLayer(boolean pUnloadOnHidden) {
		mUnloadOnHidden = pUnloadOnHidden;
		this.setBackgroundEnabled(false);
	}
	
	//if the layer is not loaded, load it. Ensure that the layer is not paused
	public void onShowManagedLayer() {
		if(!mHasLoaded) {
			mHasLoaded = true;
			onLoadLayer();
		}
		this.setIgnoreUpdate(false);
		onShowLayer();
	}
	
	//pause the layer, hide it, and unload it if it needs to be unloaded
	public void onHideManagedLayer() {
		this.setIgnoreUpdate(true);
		onHideLayer();
		if(mUnloadOnHidden) {
			onUnloadLayer();
		}
	}
	
	//methods to override in sub classes
	public abstract void onLoadLayer();
	public abstract void onShowLayer();
	public abstract void onHideLayer();
	public abstract void onUnloadLayer();
}
