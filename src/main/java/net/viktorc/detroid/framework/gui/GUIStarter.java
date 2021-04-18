package net.viktorc.detroid.framework.gui;

import net.viktorc.detroid.framework.uci.UCIEngine;
import net.viktorc.detroid.framework.validation.ControllerEngine;

public interface GUIStarter{
	public void startGUI();
	public void setControllers(ControllerEngine c, UCIEngine u);
}
