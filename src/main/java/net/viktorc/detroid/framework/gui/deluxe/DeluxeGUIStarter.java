package net.viktorc.detroid.framework.gui.deluxe;

import net.viktorc.detroid.framework.gui.GUIStarter;
import net.viktorc.detroid.framework.uci.UCIEngine;
import net.viktorc.detroid.framework.validation.ControllerEngine;

public class DeluxeGUIStarter implements GUIStarter {
	private ControllerEngine c;
	private UCIEngine u;
	@Override
	public void startGUI() {
		// TODO Auto-generated method stub
		System.out.println("Simulate starting the Deluxe GUI!");
		// We didn't make a full GUI. But if you did, you could start it from here
	}
	@Override
	public void setControllers(ControllerEngine c, UCIEngine u) {
		this.c = c;
		this.u = u;
	}
}
