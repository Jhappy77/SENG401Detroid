package net.viktorc.detroid.framework.gui;


import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;


/**
 * A singleton class that enables users to access the Stage and switch it from anywhere
 */
public class StandardStageManager {
	
	  private Stage primaryStage; 
	  private static StandardStageManager theManager;
	  // Returns the GUI, or creates one if not done yet. Allows anywhere to access GUI
	  public static StandardStageManager getStageManager() {
		  if(theManager == null)
			  return new StandardStageManager();
		  return theManager;
	  }
	  
	  private StandardStageManager() {
		  // do nothing
	  }
	  
	  /**
	   * A new function that takes advantage of the fact that GUI now uses Singleton pattern to allow
	   * users to set the scene to whatever they want! Using Singleton Pattern, the engines and evaluators
	   * can now access the StageManager from anywhere, and they could change the scene to a different scene
	   * when they need to (EX: Make a special scene for a user winning a round of chess!)
	   * @param s
	   */
	  public void setScene(Scene s) {
		  primaryStage.setScene(s);
	  }
	  // Same idea, now we can set the stage's title from anywhere!
	  public void setTitle(String newTitle) {
		  primaryStage.setTitle(newTitle);
	  }
	  
	  public void addIcon(Image icon) {
		  primaryStage.getIcons().add(icon);
	  }
	  public void allowResize(boolean b) {
		  primaryStage.setResizable(b);
	  }
	  public void showStage() {
		  primaryStage.show();
	  }
	  // Should be called if you have a stage that you want to be accessible thru this class
	  public void setStage(Stage s) {
		  this.primaryStage = s;
	  }
}
