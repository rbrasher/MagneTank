package Managers;

/**
 * This ResourceManager adds the ability to use a set of lower-quality textures
 * if desired. It also includes methods for determining an accurate Font texture
 * size to prevent wasting valuable texture memory.
 * 
 *** @author Brian Broyles - IFL Game Studio
 **/
public class ResourceManager extends Object {

	// ====================================================
	// CONSTANTS
	// ====================================================
	private static final ResourceManager INSTANCE = new ResourceManager();
	private static final TextureOptions mNormalTextureOption = TextureOptions.BILINEAR;
	private static final TextureOptions mStretchableBeamTextureOption = new TextureOptions(
			GLES20.GL_LINEAR, GLES20.GL_LINEAR, GLES20.GL_CLAMP_TO_EDGE,
			GLES20.GL_REPEAT, false);;
	private static final TextureOptions mTransparentTextureOption = TextureOptions.BILINEAR;
	private static final TextureOptions mTransparentRepeatingTextureOption = TextureOptions.REPEATING_BILINEAR;

	// ====================================================
	// VARIABLES
	// ====================================================
	// We include these objects in the resource manager for
	// easy accessibility across our project.
	public SwitchableFixedStepEngine engine;
	public Context context;
	public MagneTankActivity activity;
	public float cameraWidth;
	public float cameraHeight;
	public float cameraScaleFactorX;
	public float cameraScaleFactorY;

	// The resource variables listed should be kept public, allowing us easy
	// access
	// to them when creating new Sprite and Text objects and to play sound
	// files.
	// ======================== Game Resources ================= //
	public static TextureRegion gameWoodenDynamicTR;
	public static TextureRegion gameMetalBeamDynamicTR;
	public static TextureRegion gameMetalBeamStaticTR;

	public static TiledTextureRegion gameCrateSmallTTR;
	public static TiledTextureRegion gameCrateMediumHorizontalTTR;
	public static TiledTextureRegion gameCrateMediumVerticalTTR;
	public static TiledTextureRegion gameCrateLargeTTR;

	public static TextureRegion gameSkyBackgroundTR;
	public static TextureRegion gameParallaxBackdrop1TR;
	public static TextureRegion gameParallaxBackdrop2TR;
	public static TextureRegion gameParallaxBackdrop3TR;
	public static TextureRegion gameParallaxBackdrop4TR;
	public static TextureRegion gameCloud1TR;
	public static TextureRegion gameCloud2TR;

	public static TextureRegion gameGroundTopLayerTR;
	public static TextureRegion gameGroundBottomLayerTR;

	public static TextureRegion gamePowerBarBackgroundTR;
	public static TextureRegion gamePowerBarLineTR;
	public static TextureRegion gamePowerBarLensTR;
	public static TextureRegion gamePauseButtonTR;

	public static TextureRegion gameMagneTankBodyTR;
	public static TextureRegion gameMagneTankTurretTR;
	public static TextureRegion gameMagneTankWheelTR;
	public static TextureRegion gameMagneTankWheelShadowTR;
	public static TextureRegion gameMagneTankTurretConnectionTR;
	public static TextureRegion gameMagneTankShadowTR;

	public static TextureRegion gameMagOrbCWTR;
	public static TextureRegion gameMagOrbCCWTR;

	public static TextureRegion gameMechRatTR;
	public static TextureRegion gameMechRatWheelTR;

	public static TextureRegion gameWoodenParticleTR;
	public static TextureRegion gameSmokeTR;
	public static TextureRegion gameWhiteSmokeTR;
	public static TextureRegion gameGear1TR;
	public static TextureRegion gameGear2TR;
	public static TextureRegion gameGear3TR;
	public static TextureRegion gameTrailingLinesTR;
	public static TextureRegion gameTrailingDotTR;
	public static TiledTextureRegion gameExplosionTTR;

	public static TextureRegion gameLevelLayerBGTR;
	public static TextureRegion gameLevelLayerButtonLevelSelectTR;
	public static TextureRegion gameLevelLayerButtonNextLevelTR;
	public static TextureRegion gameLevelLayerButtonRestartLevelTR;
	public static TextureRegion gameLevelLayerStarsBGTR;
	public static TiledTextureRegion gameLevelLayerStarsTTR;

	// ======================== Menu Resources ================= //
	public static TextureRegion menuBackgroundTR;
	public static TextureRegion menuMainTitleTR;
	public static TiledTextureRegion menuMainButtonsTTR;

	public static TextureRegion menuLevelIconTR;
	public static TextureRegion menuLevelLockedTR;
	public static TiledTextureRegion menuLevelStarTTR;

	public static TextureRegion menuArrow1TR;
	public static TextureRegion menuArrow2TR;

	// =================== Shared Game and Menu Resources ====== //
	public static TiledTextureRegion buttonTiledTextureRegion;
	public static TiledTextureRegion MusicToggleTTR;
	public static TiledTextureRegion SoundToggleTTR;
	public static TextureRegion cloudTextureRegion;
	public static Sound clickSound;
	public static Font fontDefault32Bold;
	public static Font fontDefault72Bold;
	public static Font fontMonospace72Bold;
	public static Font fontDefaultMagneTank48;

	// This variable will be used to revert the TextureFactory's default path
	// when we change it.
	private String mPreviousAssetBasePath = "";

	// ====================================================
	// CONSTRUCTOR
	// ====================================================
	public ResourceManager() {
	}

	// ====================================================
	// GETTERS & SETTERS
	// ====================================================
	// Retrieves a global instance of the ResourceManager
	public static ResourceManager getInstance() {
		return INSTANCE;
	}

	// ====================================================
	// PUBLIC METHODS
	// ====================================================
	// Setup the ResourceManager
	public static void setup(MagneTankActivity pActivity,
			SwitchableFixedStepEngine pEngine, Context pContext,
			float pCameraWidth, float pCameraHeight, float pCameraScaleX,
			float pCameraScaleY) {
		getInstance().activity = pActivity;
		getInstance().engine = pEngine;
		getInstance().context = pContext;
		getInstance().cameraWidth = pCameraWidth;
		getInstance().cameraHeight = pCameraHeight;
		getInstance().cameraScaleFactorX = pCameraScaleX;
		getInstance().cameraScaleFactorY = pCameraScaleY;
	}

	// Loads all game resources.
	public static void loadGameResources() {
		getInstance().loadGameTextures();
		getInstance().loadSharedResources();
		LevelWonLayer.getInstance().onLoadLayer();
		LevelPauseLayer.getInstance().onLoadLayer();
		OptionsLayer.getInstance().onLoadLayer();
	}

	// Loads all menu resources
	public static void loadMenuResources() {
		getInstance().loadMenuTextures();
		getInstance().loadSharedResources();
	}

	public static SwitchableFixedStepEngine getEngine() {
		return getInstance().engine;
	}

	public static Context getContext() {
		return getInstance().context;
	}

	public static MagneTankActivity getActivity() {
		return getInstance().activity;
	}

	public static MagneTankSmoothCamera getCamera() {
		return (MagneTankSmoothCamera) getInstance().engine.getCamera();
	}

	// ====================================================
	// PRIVATE METHODS
	// ====================================================
	// Loads resources used by both the game scenes and menu scenes
	private void loadSharedResources() {
		loadSharedTextures();
		loadFonts();
	}

	// Gets the texture region and sets it to be double-sized if the quality of
	// the graphics is set to Low
	private TextureRegion getLimitableTR(String pTextureRegionPath,
			TextureOptions pTextureOptions) {
		final IBitmapTextureAtlasSource bitmapTextureAtlasSource = AssetBitmapTextureAtlasSource
				.create(activity.getAssets(),
						BitmapTextureAtlasTextureRegionFactory
								.getAssetBasePath() + pTextureRegionPath);
		final BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(),
				bitmapTextureAtlasSource.getTextureWidth(),
				bitmapTextureAtlasSource.getTextureHeight(), pTextureOptions);
		final TextureRegion textureRegion = new TextureRegion(
				bitmapTextureAtlas, 0, 0,
				bitmapTextureAtlasSource.getTextureWidth(),
				bitmapTextureAtlasSource.getTextureHeight(), false) {
			@Override
			public void updateUV() {
				if (!isUsingHighQualityGraphics()) {
					this.mU = 0f;
					this.mV = 0f;
					this.mU2 = 1f;
					this.mV2 = 1f;
				} else {
					super.updateUV();
				}
			}
		};
		bitmapTextureAtlas
				.addTextureAtlasSource(bitmapTextureAtlasSource, 0, 0);
		bitmapTextureAtlas.load();
		if (!isUsingHighQualityGraphics())
			textureRegion.setTextureSize(textureRegion.getWidth() * 2f,
					textureRegion.getHeight() * 2f);
		return textureRegion;
	}

	// Gets the tiled texture region and sets it to be double-sized if the
	// quality of the graphics is set to Low
	private TiledTextureRegion getLimitableTTR(String pTiledTextureRegionPath,
			int pColumns, int pRows, TextureOptions pTextureOptions) {
		final IBitmapTextureAtlasSource bitmapTextureAtlasSource = AssetBitmapTextureAtlasSource
				.create(activity.getAssets(),
						BitmapTextureAtlasTextureRegionFactory
								.getAssetBasePath() + pTiledTextureRegionPath);
		final BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(),
				bitmapTextureAtlasSource.getTextureWidth(),
				bitmapTextureAtlasSource.getTextureHeight(), pTextureOptions);
		final ITextureRegion[] textureRegions = new ITextureRegion[pColumns
				* pRows];

		final int tileWidth = bitmapTextureAtlas.getWidth() / pColumns;
		final int tileHeight = bitmapTextureAtlas.getHeight() / pRows;

		for (int tileColumn = 0; tileColumn < pColumns; tileColumn++) {
			for (int tileRow = 0; tileRow < pRows; tileRow++) {
				final int tileIndex = tileRow * pColumns + tileColumn;

				final int x = tileColumn * tileWidth;
				final int y = tileRow * tileHeight;
				textureRegions[tileIndex] = new TextureRegion(
						bitmapTextureAtlas, x, y, tileWidth, tileHeight, false) {
					@Override
					public void updateUV() {
						if (!isUsingHighQualityGraphics()) {
							this.mU = this.getTextureX()
									/ bitmapTextureAtlas.getWidth();
							this.mU2 = (this.getTextureX() + tileWidth)
									/ bitmapTextureAtlas.getWidth();

							this.mV = this.getTextureY()
									/ bitmapTextureAtlas.getHeight();
							this.mV2 = (this.getTextureY() + tileHeight)
									/ bitmapTextureAtlas.getHeight();
						} else {
							super.updateUV();
						}
					}
				};
				if (!isUsingHighQualityGraphics())
					textureRegions[tileIndex].setTextureSize(
							textureRegions[tileIndex].getWidth() * 2f,
							textureRegions[tileIndex].getHeight() * 2f);
			}
		}

		final TiledTextureRegion tiledTextureRegion = new TiledTextureRegion(
				bitmapTextureAtlas, false, textureRegions);
		bitmapTextureAtlas
				.addTextureAtlasSource(bitmapTextureAtlasSource, 0, 0);
		bitmapTextureAtlas.load();
		return tiledTextureRegion;
	}

	public void switchQuality() {
		final CharSequence MesageToShow = isUsingHighQualityGraphics() ? "Switch to Low Quality?"
				: "Your device may be unable to run this game at a higher quality. Performance will noticably decrease on anything but newer devices.";
		final CharSequence ButtonText = isUsingHighQualityGraphics() ? "Switch to Low Quality"
				: "Switch to High Quality";
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Switch quality?");
				builder.setIcon(R.drawable.icon);
				builder.setMessage(MesageToShow)
						.setPositiveButton(ButtonText,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										activity.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												AlertDialog.Builder builder = new AlertDialog.Builder(
														activity);
												builder.setIcon(R.drawable.icon);
												builder.setMessage(
														"The game will close and re-open shortly.")
														.setPositiveButton(
																"Continue",
																new DialogInterface.OnClickListener() {
																	public void onClick(
																			DialogInterface dialog,
																			int id) {
																		switchQualityDirect();
																	}
																})
														.setNegativeButton(
																"Cancel",
																new DialogInterface.OnClickListener() {
																	public void onClick(
																			DialogInterface dialog,
																			int id) {

																	}
																});
												AlertDialog alert = builder
														.create();
												alert.show();
											}
										});
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}

	private void switchQualityDirect() {
		activity.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				MagneTankActivity.writeBooleanToSharedPreferences(
						MagneTankActivity.SHARED_PREFS_HIGH_QUALITY_GRAPHICS,
						!isUsingHighQualityGraphics());
				AlarmManager alm = (AlarmManager) activity
						.getSystemService(Context.ALARM_SERVICE);
				alm.set(AlarmManager.RTC, System.currentTimeMillis() + 3000,
						PendingIntent.getActivity(activity, 0, new Intent(
								activity, activity.getClass()), 0));
				System.exit(0);
			}
		});
	}

	public boolean useHighQuality = true;

	public static boolean isUsingHighQualityGraphics() {
		return INSTANCE.useHighQuality;
	}

	// ============================ LOAD TEXTURES (GAME) ================= //
	private void loadGameTextures() {
		// Store the current asset base path to apply it after we've loaded our
		// textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory
				.getAssetBasePath();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("MagneTank/");

		final TextureManager textureManager = activity.getTextureManager();

		// Load the textures of the game level that will not require stretching
		// first
		if (gameMetalBeamDynamicTR == null) {
			BitmapTextureAtlas metalBeamDynamicT = new BitmapTextureAtlas(
					textureManager, 128, 64, mStretchableBeamTextureOption);
			gameMetalBeamDynamicTR = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(metalBeamDynamicT,
							ResourceManager.getActivity(),
							"MetalBeamDynamic.png", 0, 0);
			metalBeamDynamicT.load();
		}
		if (gameMetalBeamStaticTR == null) {
			BitmapTextureAtlas metalBeamStaticT = new BitmapTextureAtlas(
					textureManager, 128, 64, mStretchableBeamTextureOption);
			gameMetalBeamStaticTR = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(metalBeamStaticT,
							ResourceManager.getActivity(),
							"MetalBeamStatic.png", 0, 0);
			metalBeamStaticT.load();
		}
		if (gameWoodenDynamicTR == null) {
			BitmapTextureAtlas metalBeamDynamicT = new BitmapTextureAtlas(
					textureManager, 128, 64, mStretchableBeamTextureOption);
			gameWoodenDynamicTR = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(metalBeamDynamicT,
							ResourceManager.getActivity(),
							"WoodenBeamDynamic.png", 0, 0);
			metalBeamDynamicT.load();
		}

		// then load the ones that can be stretched (if half-sized graphics are
		// available in the assets)
		if (!useHighQuality)
			BitmapTextureAtlasTextureRegionFactory
					.setAssetBasePath("MagneTank/Limited/");

		// crates
		if (gameCrateSmallTTR == null)
			gameCrateSmallTTR = getLimitableTTR("CratesSmall.png", 2, 2,
					mTransparentTextureOption);
		if (gameCrateMediumHorizontalTTR == null)
			gameCrateMediumHorizontalTTR = getLimitableTTR(
					"CratesMediumHorizontal.png", 1, 4,
					mTransparentTextureOption);
		if (gameCrateMediumVerticalTTR == null)
			gameCrateMediumVerticalTTR = getLimitableTTR(
					"CratesMediumVertical.png", 2, 2, mTransparentTextureOption);
		if (gameCrateLargeTTR == null)
			gameCrateLargeTTR = getLimitableTTR("CratesLarge.png", 2, 2,
					mTransparentTextureOption);
		// atmosphere
		if (gameSkyBackgroundTR == null)
			gameSkyBackgroundTR = getLimitableTR("Sky01.png",
					mNormalTextureOption);
		if (gameParallaxBackdrop1TR == null)
			gameParallaxBackdrop1TR = getLimitableTR("BG_Layer1.png",
					mTransparentTextureOption);
		if (gameParallaxBackdrop2TR == null)
			gameParallaxBackdrop2TR = getLimitableTR("BG_Layer2.png",
					mTransparentTextureOption);
		if (gameParallaxBackdrop3TR == null)
			gameParallaxBackdrop3TR = getLimitableTR("BG_Layer3.png",
					mTransparentTextureOption);
		if (gameParallaxBackdrop4TR == null)
			gameParallaxBackdrop4TR = getLimitableTR("BG_Layer4.png",
					mTransparentTextureOption);
		if (gameCloud1TR == null)
			gameCloud1TR = getLimitableTR("Cloud1.png",
					mTransparentTextureOption);
		if (gameCloud2TR == null)
			gameCloud2TR = getLimitableTR("Cloud2.png",
					mTransparentTextureOption);
		// ground
		if (gameGroundTopLayerTR == null)
			gameGroundTopLayerTR = getLimitableTR("Grass_TopLayer.png",
					mTransparentRepeatingTextureOption);
		if (gameGroundBottomLayerTR == null)
			gameGroundBottomLayerTR = getLimitableTR("Ground_BottomLayer.png",
					mTransparentRepeatingTextureOption);

		if (gamePowerBarBackgroundTR == null)
			gamePowerBarBackgroundTR = getLimitableTR("PowerBarBG.png",
					mTransparentTextureOption);
		if (gamePowerBarLineTR == null)
			gamePowerBarLineTR = getLimitableTR("PowerBarLine.png",
					mTransparentTextureOption);
		if (gamePowerBarLensTR == null)
			gamePowerBarLensTR = getLimitableTR("PowerBarLens.png",
					mTransparentTextureOption);
		if (gamePauseButtonTR == null)
			gamePauseButtonTR = getLimitableTR("PauseButton.png",
					mTransparentTextureOption);

		if (gameMagneTankBodyTR == null)
			gameMagneTankBodyTR = getLimitableTR("MagneTank_Body.png",
					mTransparentTextureOption);
		if (gameMagneTankTurretTR == null)
			gameMagneTankTurretTR = getLimitableTR("MagneTank_Turret.png",
					mTransparentTextureOption);
		if (gameMagneTankWheelTR == null)
			gameMagneTankWheelTR = getLimitableTR("MagneTank_Wheel.png",
					mTransparentTextureOption);
		if (gameMagneTankWheelShadowTR == null)
			gameMagneTankWheelShadowTR = getLimitableTR(
					"MagneTank_WheelShadow.png", mTransparentTextureOption);
		if (gameMagneTankTurretConnectionTR == null)
			gameMagneTankTurretConnectionTR = getLimitableTR(
					"MagneTank_Turret_Connection.png",
					mTransparentTextureOption);
		if (gameMagneTankShadowTR == null)
			gameMagneTankShadowTR = getLimitableTR("MagneTank_Shadow.png",
					mTransparentTextureOption);

		if (gameMagOrbCWTR == null)
			gameMagOrbCWTR = getLimitableTR("MagOrbCW.png",
					mTransparentTextureOption);
		if (gameMagOrbCCWTR == null)
			gameMagOrbCCWTR = getLimitableTR("MagOrbCCW.png",
					mTransparentTextureOption);

		if (gameMechRatTR == null)
			gameMechRatTR = getLimitableTR("MechRat.png",
					mTransparentTextureOption);
		if (gameMechRatWheelTR == null)
			gameMechRatWheelTR = getLimitableTR("MechRat_Wheel.png",
					mTransparentTextureOption);

		if (gameWoodenParticleTR == null)
			gameWoodenParticleTR = getLimitableTR("WoodenParticle.png",
					mTransparentTextureOption);
		if (gameSmokeTR == null)
			gameSmokeTR = getLimitableTR("Smoke.png", mTransparentTextureOption);
		if (gameWhiteSmokeTR == null)
			gameWhiteSmokeTR = getLimitableTR("WhiteSmoke.png",
					mTransparentTextureOption);
		if (gameGear1TR == null)
			gameGear1TR = getLimitableTR("Gear01.png",
					mTransparentTextureOption);
		if (gameGear2TR == null)
			gameGear2TR = getLimitableTR("Gear02.png",
					mTransparentTextureOption);
		if (gameGear3TR == null)
			gameGear3TR = getLimitableTR("Gear03.png",
					mTransparentTextureOption);
		if (gameTrailingLinesTR == null)
			gameTrailingLinesTR = getLimitableTR("TrailLines.png",
					mTransparentTextureOption);
		if (gameTrailingDotTR == null)
			gameTrailingDotTR = getLimitableTR("TrailDot.png",
					mTransparentTextureOption);
		if (gameExplosionTTR == null)
			gameExplosionTTR = getLimitableTTR("Explosion.jpg", 4, 3,
					mNormalTextureOption);

		if (gameLevelLayerBGTR == null)
			gameLevelLayerBGTR = getLimitableTR("LevelLayerBG.png",
					mTransparentTextureOption);
		if (gameLevelLayerButtonLevelSelectTR == null)
			gameLevelLayerButtonLevelSelectTR = getLimitableTR(
					"LevelLayerButtonLevelSelect.png",
					mTransparentTextureOption);
		if (gameLevelLayerButtonNextLevelTR == null)
			gameLevelLayerButtonNextLevelTR = getLimitableTR(
					"LevelLayerButtonNext.png", mTransparentTextureOption);
		if (gameLevelLayerButtonRestartLevelTR == null)
			gameLevelLayerButtonRestartLevelTR = getLimitableTR(
					"LevelLayerButtonRestart.png", mTransparentTextureOption);
		if (gameLevelLayerStarsBGTR == null)
			gameLevelLayerStarsBGTR = getLimitableTR("LevelLayerStarsBG.png",
					mTransparentTextureOption);
		if (gameLevelLayerStarsTTR == null)
			gameLevelLayerStarsTTR = getLimitableTTR("LevelLayerStars.png", 1,
					4, mTransparentTextureOption);

		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath(mPreviousAssetBasePath);
	}

}
