package Menus;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import Input.GrowButton;
import Input.GrowToggleButton;
import Layers.OptionsLayer;
import Managers.ResourceManager;
import Managers.SFXManager;
import Managers.SceneManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.comfycouch.mtrakk.MTrakkActivity;
import com.comfycouch.mtrakk.MTrakkSmoothCamera;
import com.comfycouch.mtrakk.R;

/**
 * The MainMenu Scene holds two Entities, one representing the title screen
 * and one representing the level-select screen. The movement between the 
 * two screens is achieved using entity modifiers. During the first
 * showing of the MainMenu, a loading screen is shown while the game 
 * resources are loaded
 * 
 * @author ron
 *
 */

public class MainMenu extends ManagedMenuScene {

	// ============================================
	// ENUMS
	// ============================================
	public enum MainMenuScreens {
		LevelSelector, Title
	}
	
	// ============================================
	// CONSTANTS
	// ============================================
	private static final MainMenu INSTANCE = new MainMenu();
	private static final float mCameraHeight = ResourceManager.getInstance().cameraHeight;
	private static final float mCameraWidth = ResourceManager.getInstance().cameraWidth;
	private static final float mHalfCameraHeight = ResourceManager.getInstance().cameraHeight / 2;
	private static final float mHalfCameraWidth = ResourceManager.getInstance().cameraWidth / 2;
	
	public static MainMenu getInstance() {
		return INSTANCE;
	}
	
	// =============================================
	// VARIABLES
	// =============================================
	public MainMenuScreens mCurrentScreen = MainMenuScreens.Title;
	private Entity mHomeMenuScreen;
	private Entity mLevelSelectScreen;
	private Sprite mMTrakkBGSprite;
	private LevelSelector mLevelSelector;
	
	public MainMenu() {
		super(0.001f);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
	}
	
	// =============================================
	// METHODS
	// =============================================
	public void goToTitleScreen() {
		this.mCurrentScreen = MainMenuScreens.Title;
		this.mHomeMenuScreen.registerEntityModifier(new MoveModifier(0.25f, this.mHomeMenuScreen.getX(), this.mHomeMenuScreen.getY(), 0f, 0f));
		this.mLevelSelectScreen.registerEntityModifier(new MoveModifier(0.25f, this.mLevelSelectScreen.getX(), this.mLevelSelectScreen.getY(), mCameraWidth, 0f));
		this.mMTrakkBGSprite.registerEntityModifier(new MoveModifier(0.25f, this.mMTrakkBGSprite.getX(), this.mMTrakkBGSprite.getY(), (this.mMTrakkBGSprite.getWidth() * this.mMTrakkBGSprite.getScaleX()) / 2f, (this.mMTrakkBGSprite.getHeight() * this.mMTrakkBGSprite.getScaleY()) / 2f));
	}
	
	@Override
	public void onHideScene() {

	}

	@Override
	public Scene onLoadingScreenLoadAndShown() {
		ResourceManager.loadMenuResources();
		MTrakkSmoothCamera.setupForMenus();
		
		final Scene MenuLoadingScene = new Scene();
		
		this.mMTrakkBGSprite = new Sprite(0f, 0f, ResourceManager.menuBackgroundTR, ResourceManager.getActivity().getVertexBufferObjectManager());
		this.mMTrakkBGSprite.setScale(ResourceManager.getInstance().cameraHeight / ResourceManager.menuBackgroundTR.getHeight());
		this.mMTrakkBGSprite.setPosition((this.mMTrakkBGSprite.getWidth() * this.mMTrakkBGSprite.getScaleX()) / 2f, (this.mMTrakkBGSprite.getHeight() * this.mMTrakkBGSprite.getScaleY()) / 2f);
		this.mMTrakkBGSprite.setZIndex(-999);
		MenuLoadingScene.attachChild(this.mMTrakkBGSprite);
		MenuLoadingScene.attachChild(new Text(mHalfCameraWidth, mCameraHeight, ResourceManager.fontDefaultMTrakk48, "Loading...", ResourceManager.getActivity().getVertexBufferObjectManager()));
		
		return MenuLoadingScene;
	}

	@Override
	public void onLoadingScreenUnloadAndHidden() {
		this.mMTrakkBGSprite.detachSelf();
	}

	@Override
	public void onLoadScene() {
		//load the game resources
		ResourceManager.loadGameResources();
		
		this.mLevelSelector = new LevelSelector(mHalfCameraWidth, mHalfCameraHeight, MTrakkActivity.getIntFromSharedPreferences(MTrakkActivity.SHARED_PREFS_LEVEL_MAX_REACHED) + 1, 1, ResourceManager.menuLevelIconTR, ResourceManager.fontDefault32Bold, this);
		
		this.mHomeMenuScreen = new Entity(0f, mCameraHeight) {
			boolean hasLoaded = false;
			
			@Override
			protected void onManagedUpdate(final float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if(!this.hasLoaded) {
					this.hasLoaded = true;
					this.registerEntityModifier(new MoveModifier(0.25f, 0f, mCameraHeight, 0f, 0f));
				}
			}
		};
		
		this.mLevelSelectScreen = new Entity(mCameraWidth, 0f);
		
		final TiledTextureRegion MainMenuButtons = ResourceManager.menuMainButtonsTTR;
		final float ButtonSpacing = 25f * ResourceManager.getInstance().cameraScaleFactorY;
		final GrowButton PlayButton = new GrowButton((mCameraWidth * 0.75f), mHalfCameraHeight + MainMenuButtons.getHeight() + ButtonSpacing, MainMenuButtons.getTextureRegion(0)) {
			@Override
			public void onClick() {
				if(MainMenu.this.mCurrentScreen == MainMenuScreens.Title) {
					MainMenu.this.mCurrentScreen = MainMenuScreens.LevelSelector;
					MainMenu.this.mHomeMenuScreen.registerEntityModifier(new MoveModifier(0.25f, MainMenu.this.mHomeMenuScreen.getX(), MainMenu.this.mHomeMenuScreen.getY(), -mCameraWidth, 0f));
					MainMenu.this.mLevelSelectScreen.registerEntityModifier(new MoveModifier(0.25f, MainMenu.this.mLevelSelectScreen.getX(), MainMenu.this.mLevelSelectScreen.getY(), 0f, 0f));
					MainMenu.this.mMTrakkBGSprite.registerEntityModifier(new MoveModifier(0.25f, MainMenu.this.mMTrakkBGSprite.getX(), MainMenu.this.mMTrakkBGSprite.getY(), ((MainMenu.this.mMTrakkBGSprite.getWidth() * MainMenu.this.mMTrakkBGSprite.getScaleX()) / 2f) - (150f * MainMenu.this.mMTrakkBGSprite.getScaleX()), (MainMenu.this.mMTrakkBGSprite.getHeight() * MainMenu.this.mMTrakkBGSprite.getScaleY()) / 2f));
				}
			}
		};
		
		this.mHomeMenuScreen.attachChild(PlayButton);
		this.registerTouchArea(PlayButton);
		
		final GrowButton OptionsButton = new GrowButton(PlayButton.getX(), PlayButton.getY() - MainMenuButtons.getHeight() - ButtonSpacing, MainMenuButtons.getTextureRegion(1)) {
			@Override
			public void onClick() {
				SceneManager.getInstance().showLayer(OptionsLayer.getInstance(), false, false, true);
			}
		};
		
		this.mHomeMenuScreen.attachChild(OptionsButton);
		this.registerTouchArea(OptionsButton);
		
		//if we were using an upgrade button, it would go here.
		
		final GrowButton AboutButton = new GrowButton(OptionsButton.getX(), OptionsButton.getY() - MainMenuButtons.getHeight() - ButtonSpacing, MainMenuButtons.getTextureRegion(3)) {
			@Override
			public void onClick() {
				ResourceManager.getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						final AlertDialog.Builder builder = new AlertDialog.Builder(ResourceManager.getActivity())
							.setIcon(R.drawable.icon)
							.setTitle("MTrakk")
							.setMessage(Html.fromHtml("This game was designed and created by RonB for Comfy Couch Media. By using this game or its source-code, you agree that Comfy Couch Media is not responsible for any damage that happens to your device."
									+ "<br><br><b><u>Contact Information:</u></b>" + "<br><a href=\"mailto:tony@comfycouchmedia.com\">Email Comfy Couch Media</a>"
									+ "<br><br><b>Twitter</b><br><a href=\"https://twitter.com/RonBnAZ\">Follow RonBnAZ</a>"
									+ "<br><br><a href=\"https://twitter.com/ComfyCouchMedia\">Follow ComfyCouchMedia</a>"))
									.setPositiveButton("Back", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int id) {
											
										}
									});
						final AlertDialog alert = builder.create();
						alert.show();
						((TextView) alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
					}
					
				});
			}
		};
		
		this.mHomeMenuScreen.attachChild(AboutButton);
		this.registerTouchArea(AboutButton);
		
		final Sprite MainTitleText = new Sprite(0f, 0f, ResourceManager.menuMainTitleTR, ResourceManager.getActivity().getVertexBufferObjectManager());
		MainTitleText.setPosition(mCameraWidth / 2f, mCameraHeight - (MainTitleText.getHeight() / 2f));
		this.mHomeMenuScreen.attachChild(MainTitleText);
		
		this.mLevelSelectScreen.attachChild(this.mLevelSelector);
		
		final GrowButton BackToTitleButton = new GrowButton(0f, 0f, ResourceManager.menuArrow1TR) {
			@Override
			public void onClick() {
				if(MainMenu.this.mCurrentScreen == MainMenuScreens.LevelSelector) {
					MainMenu.this.goToTitleScreen();
				}
			}
		};
		
		BackToTitleButton.setFlippedHorizontal(true);
		BackToTitleButton.setScales(2f / ResourceManager.getInstance().cameraScaleFactorY, (2f / ResourceManager.getInstance().cameraScaleFactorY) * 1.2f);
		BackToTitleButton.setPosition((BackToTitleButton.getWidth() * BackToTitleButton.getScaleX()) / 2f, mHalfCameraHeight);
		this.mLevelSelectScreen.attachChild(BackToTitleButton);
		this.registerTouchArea(BackToTitleButton);
		
		this.attachChild(this.mHomeMenuScreen);
		this.attachChild(this.mLevelSelectScreen);
		
		final GrowToggleButton MusicToggleButton = new GrowToggleButton(ResourceManager.MusicToggleTTR.getWidth() / 2f, ResourceManager.MusicToggleTTR.getHeight() / 2f, ResourceManager.MusicToggleTTR, !SFXManager.isMusicMuted()) {
			@Override
			public boolean checkState() {
				return !SFXManager.isMusicMuted();
			}
			
			@Override
			public void onClick() {
				SFXManager.toggleMusicMuted();
			}
		};
		
		final GrowToggleButton SoundToggleButton = new GrowToggleButton(MusicToggleButton.getX() + 75f, MusicToggleButton.getY(), ResourceManager.SoundToggleTTR, !SFXManager.isSoundMuted()) {
			@Override
			public boolean checkState() {
				return !SFXManager.isSoundMuted();
			}
			
			@Override
			public void onClick() {
				SFXManager.toggleSoundMuted();
			}
		};
		
		this.attachChild(MusicToggleButton);
		this.attachChild(SoundToggleButton);
		this.registerTouchArea(MusicToggleButton);
		this.registerTouchArea(SoundToggleButton);
	}

	@Override
	public void onShowScene() {
		this.RefreshLevelStars();
		MTrakkSmoothCamera.setupForMenus();
		if(!this.mMTrakkBGSprite.hasParent()) {
			this.attachChild(this.mMTrakkBGSprite);
			this.sortChildren();
		}
	}

	@Override
	public void onUnloadScene() {

	}
	
	public void RefreshLevelStars() {
		this.mLevelSelector.refreshAllButtonStars();
	}

}
