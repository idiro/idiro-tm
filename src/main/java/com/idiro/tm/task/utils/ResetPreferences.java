package com.idiro.tm.task.utils;

import com.idiro.tm.task.Task;
import com.idiro.tm.utils.PreferencesManager;

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
