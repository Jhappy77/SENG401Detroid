package net.viktorc.detroid.framework.gui;

import net.viktorc.detroid.framework.uci.UCIEngine;
import net.viktorc.detroid.framework.validation.ControllerEngine;
import javafx.application.Application;

public class StandardGUIStarter implements GUIStarter {
	@Override
	public void startGUI() {
		Application.launch(GUI.class);
		// This GUI uses JavaFX. But using GUIStarter as a bridge pattern,
		// you could create a new GUI that uses something else!
	}

	@Override
	public void setControllers(ControllerEngine c, UCIEngine u) {
		GUI.setEngines(c, u); // GUI was premade by Detroid
		// GUI is what we're abstracting behind the GUIStarter using Bridge
	}
}
