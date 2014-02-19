package Menus;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import Managers.ResourceManager;

/**
 * Based on Level Selector by Jay Schroeder.
 * 
 * @author ron
 *
 */

public class LevelSelector extends Entity {
	
	// ====================================
	// CONSTANTS
	// ====================================
	
	//Layout properties
	private final int mCOLUMNS = 6;
	private final int mROWS = 4;
	
	//Tile Properties
	private final int mTILE_DIMENSION = 64;
	private final int mTILE_PADDING = 15;
	
	
	// ====================================
	// VARIABLES
	// ====================================
	private final Scene mScene;
	
	//Level selector's chapter
	private final int mWorldIndex;
	private final int mNumLevelsInWorld;
	
	//initial tile positions
	private final float mInitialX;
	private final float mInitialY;
	
	public final ArrayList<LevelSelectorButton> mLevelButtons = new ArrayList<LevelSelectorButton>();
	
	public LevelSelector(final float pX, final float pY, final int maxCompletedLevel, final int pWorldIndex, final ITextureRegion textureRegion, final Font font, final Scene pScene) {
		//Initializes the proper variables from the activity
		this.mScene = pScene;
		this.mWorldIndex = pWorldIndex;
		this.mNumLevelsInWorld = Levels.getNumLevelsInWorld(mWorldIndex);
		
		//Set the initial position in order to properly center the tiles
		this.mInitialX = -(((mTILE_DIMENSION + mTILE_PADDING) * mCOLUMNS) / 2f) + (mTILE_DIMENSION + mTILE_PADDING) / 2f;
		this.mInitialY = (((mTILE_DIMENSION + mTILE_PADDING) * mROWS) / 2f) - (mTILE_DIMENSION + mTILE_PADDING) / 2f;
		createTiles(textureRegion, font);
		this.setPosition(pX, pY);
		this.setScale(1 / ResourceManager.getInstance().cameraScaleFactorX);
	}
	
	// =======================================
	// METHODS
	// =======================================
	private void createTiles(final ITextureRegion textureRegion, final Font font) {
		//temporary coordinates
		float tempX = this.mInitialX;
		float tempY = this.mInitialY;
		
		//temporary level integer
		int currentTileLevel = 1;
		
		//loop through, adjust ROW positions
		for(int i = 0; i < mROWS; i++) {
			//loop through, adjust COLUMN positions and
			//create sprite/text objects
			for(int o = 0; o < mCOLUMNS; o++) {
				//Create the level tile/button
				final LevelSelectorButton levelButton = new LevelSelectorButton(currentTileLevel, mWorldIndex, tempX, tempY, mTILE_DIMENSION, mTILE_DIMENSION, textureRegion, ResourceManager.getEngine().getVertexBufferObjectManager());
				
				//register and attach the level tile/button to the scene
				mScene.registerTouchArea(levelButton);
				this.attachChild(levelButton);
				mLevelButtons.add(levelButton);
				
				//Increment the temporary X position of the level tile
				tempX = tempX + mTILE_DIMENSION + mTILE_PADDING;
				
				//Increment the current tile count/level
				currentTileLevel++;
				if(currentTileLevel > mNumLevelsInWorld)
					return;
			}
			
			//Reset the temporary X position back to the first column position
			tempX = mInitialX;
			//Re-position the height to the next row's position
			tempY = tempY - mTILE_DIMENSION - mTILE_PADDING;
		}
	}
	
	public void refreshAllButtonStars() {
		for(LevelSelectorButton curLvlBtn : mLevelButtons)
			curLvlBtn.refreshStars();
	}
}
