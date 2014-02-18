package Menus;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import Managers.ResourceManager;

/**
 * The LevelSelectorButton class visually shows the player the state of a
 * level, locked or unlocked, and the number of stars achieved if the
 * level is unlocked.
 * 
 * @author ron
 *
 */

public class LevelSelectorButton extends Sprite {

	// ==========================================
	// VARIABLES
	// ==========================================
	public final int mLevelIndex;
	public final int mWorldIndex;
	public boolean mIsLocked = true;
	private final Text mButtonText;
	private final Sprite mLockedSprite;
	private final TiledSprite mStarsEnt;
	
	private boolean mIsTouched = false;
	private boolean mIsLarge = false;
	private boolean mIsClicked = false;
	
	public LevelSelectorButton(final int pLevelIndex, final int pWorldIndex, final float pX, final float pY, final float pWidth, final float pHeight, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		this.mLevelIndex = pLevelIndex;
		this.mWorldIndex = pWorldIndex;
		
		this.mButtonText = new Text(0f, 0f, ResourceManager.fontDefault32Bold, String.valueOf(this.mLevelIndex), ResourceManager.getActivity().getVertexBufferObjectManager());
		this.mButtonText.setPosition((this.getWidth() / 3f), (this.getHeight() / 2f));
		this.mButtonText.setColor(0.7f, 0.7f, 0.7f);
		
		this.mLockedSprite = new Sprite(this.getWidth() / 2f, this.getHeight() / 2f, ResourceManager.menuLevelLockedTR, ResourceManager.getActivity().getVertexBufferObjectManager());
		
		this.mStarsEnt = new TiledSprite((this.getWidth() / 3f) * 2f, (this.getHeight() / 2f), ResourceManager.menuLevelStarTTR, ResourceManager.getActivity().getVertexBufferObjectManager());
		
		this.mIsLocked = (this.mLevelIndex > (MagneTankActivity.getIntFromSharedPreferences(MagneTankActivity.SHARED_PREFS_LEVEL_MAX_REACHED) + 1));
	}
	
}
