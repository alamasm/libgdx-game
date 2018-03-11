package com.pektusin.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.pektusin.game.Util.AdHandler;

public class AndroidLauncher extends AndroidApplication implements AdHandler {
	protected final String AD_UNIT_ID = "ca-app-pub-8858324957206553/7203998428";
	InterstitialAd ad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ad = new InterstitialAd(this);
		ad.setAdUnitId(AD_UNIT_ID);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useImmersiveMode = true;

		initialize(new BallGame(this), config);
	}

	public void showInterstitial() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (ad.isLoaded()) {
						ad.show();
					} else {
						loadInterstitial();
					}
				}
			});
		} catch (Exception e) {
		}
	}

	public void loadInterstitial() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AdRequest interstitialRequest = new AdRequest.Builder().build();
					ad.loadAd(interstitialRequest);
				}
			});
		} catch (Exception e) {
		}
	}
}

