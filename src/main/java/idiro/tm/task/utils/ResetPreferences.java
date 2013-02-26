package idiro.tm.task.utils;

import idiro.tm.task.Task;
import idiro.tm.utils.PreferencesManager;

/**
 * Resets preferences of all the processes
 * @author etienne
 *
 */
public class ResetPreferences extends Task{


	@Override
	public String getDescription() {
		return "Reset the preferences";
	}

	@Override
	public boolean init(){
		return true;
	}

	@Override
	protected boolean run(){
		// TODO Auto-generated method stub
		(new PreferencesManager()).resetAll();
		return true;
	}

	@Override
	protected boolean finalCheck() {
		return true;
	}

}
