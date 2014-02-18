package com.ronb.magnetank;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.IUpdateHandler;

import Managers.ResourceManager;

/**
 * This class extends the SmoothCamera object but includes the ability to
 * pan to the enemy base for a specified amount of time as well as track
 * the MagneTank object.
 * 
 * @author ron
 *
 */

public class MagneTankSmoothCamera extends SmoothCamera {
	
	private MagneTank mMagneTankEntity;
	private float mBaseX = 0f;
	private float mBaseY = 0f;
	private float mTargetCenterX;
	private float mTargetCenterY;

	public MagneTankSmoothCamera(float pX, float pY, float pWidth,
			float pHeight, float pMaxVelocityX, float pMaxVelocityY,
			float pMaxZoomFactorChange) {
		
		super(pX, pY, pWidth, pHeight, pMaxVelocityX, pMaxVelocityY, pMaxZoomFactorChange);
	}
	
	public void setMagneTankEntity(MagneTank pMagneTankBodyEntity) {
		mMagneTankEntity = pMagneTankBodyEntity;
		this.setChaseEntity(mMagneTankEntity.mVehicleSprite);
	}
	
	public void setBasePosition(final float pX, final float pY) {
		mBaseX = pX;
		mBaseY = pY;
	}
	
	public void goToMagneTank() {
		this.setChaseEntity(mMagneTankEntity.mVehicleSprite);
	}
	
	public void goToBase() {
		this.setChaseEntity(null);
		this.setCenter(mBaseX, mBaseY);
	}
	
	public static void setupForMenus() {
		final MagneTankSmoothCamera ThisCam = ((MagneTankSmoothCamera)ResourceManager.getEngine().getCamera());
		ThisCam.setBoundsEnabled(false);
		ThisCam.setChaseEntity(null);
		ThisCam.setZoomFactorDirect(1f);
		ThisCam.setZoomFactor(1f);
		ThisCam.setCenterDirect(ResourceManager.getInstance().cameraWidth / 2f, ResourceManager.getInstance().cameraHeight / 2f);
		ThisCam.setCenter(ResourceManager.getInstance().cameraWidth / 2f, ResourceManager.getInstance().cameraHeight / 2f);
		ThisCam.clearUpdateHandlers();
	}
	
	public void goToBaseForSeconds(final float pSeconds) {
		goToBase();
		final MagneTankSmoothCamera ThisCam = this;
		ThisCam.registerUpdateHandler(new IUpdateHandler() {

			float timeElapsed = 0f;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				timeElapsed += pSecondsElapsed;
				if(MagneTankEntity.mGameLevel.mIsThereBaseMovement)
					timeElapsed = 0f;
				if(timeElapsed >= pSeconds) {
					ThisCam.setChaseEntity(mMagneTank.mVehicleSprite);
					ThisCam.unregisterUpdateHandler(this);
				}
			}

			@Override
			public void reset() {

			}
			
		});
	}
	
	@Override
	public void setCenter(final float pCenterX, final float pCenterY) {
		this.mTargetCenterX = pCenterX;
		this.mTargetCenterY = pCenterY;
	}
	
	public void setCenterDirect(final float pCenterX, final float pCenterY) {
		super.setCenterDirect(pCenterX, pCenterY);
		this.mTargetCenterX = pCenterX;
		this.mTargetCenterY = pCenterY;
	}
	
	@Override
	public void onUpdate(final float pSecondsElapsed) {
		super.onUpdate(pSecondsElapsed);
		
		//update center
		final float currentCenterX = this.getCenterX();
		final float currentCenterY = this.getCenterY();
		
		final float targetCenterX = this.mTargetCenterX;
		final float targetCenterY = this.mTargetCenterY;
		
		if(currentCenterX != targetCenterX || currentCenterY != targetCenterY) {
			final float diffX = targetCenterX - currentCenterX;
			final float dX = diffX / 4;
			
			final float diffY = targetCenterY - currentCenterY;
			final float dY = diffY / 4;
			
			super.setCenter(currentCenterX + dX, currentCenterY + dY);
		}
		
		//update zoom
		final float targetZoomFactor = this.getTargetZoomFactor();
		this.setZoomFactorDirect(targetZoomFactor);
	}

}
