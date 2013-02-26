package idiro.tm.gui;

import java.util.List;

import idiro.tm.gui.minimal.MainWindow;
import idiro.tm.task.Task;

/**
 * Draw a window with a minimal interface
 * 
 * @author etienne
 *
 */
public class GuiMinimal extends Task{


	@Override
	public String getDescription() {
		return "run tasks command from a gui interface";
	}

	@Override
	public boolean init(){
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean run() {
		// TODO Auto-generated method stub
		MainWindow mw = new MainWindow();
		return true;
	}


	@Override
	protected boolean finalCheck() {
		return true;
	}

}
