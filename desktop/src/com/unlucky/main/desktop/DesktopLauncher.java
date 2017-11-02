package com.unlucky.main.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.unlucky.main.Unlucky;

/**
 * Desktop version access
 */
public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = Unlucky.V_WIDTH * Unlucky.V_SCALE;
        config.height = Unlucky.V_HEIGHT * Unlucky.V_SCALE;
        config.resizable = false;
        config.vSyncEnabled = false;
        config.title = Unlucky.TITLE;

		new LwjglApplication(new Unlucky(), config);
	}

}
