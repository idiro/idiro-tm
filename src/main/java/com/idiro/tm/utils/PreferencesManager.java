package com.idiro.tm.utils;


import java.util.List;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.idiro.Log;
import com.idiro.tm.ProcessManager;
import com.idiro.tm.task.Process;

/**
 * Manages the preferences tree operation
 * 
 * Stores the method operating on all the preferences tree
 * 
 * @author etienne
 *
 */
public class PreferencesManager {

	/**
	 * Logger.
	 */
	private Logger logger = Logger.getLogger(PreferencesManager.class);


	/**
	 * Preferences node
	 */
	private Preferences prefs = Preferences.systemNodeForPackage(PreferencesManager.class);

	/**
	 * Reset all the task preferences
	 */
	public void resetAll(){

		try {
			//TODO remove all the preferences before doing this (except the library!!!)
			//Preferences.systemRoot().removeNode();
			//Preferences.userRoot().removeNode();
			//Special case: which are not tasks
			
			List<String> classNames = ProcessManager.getInstance().getNonAbstractProcessClasses();
			logger.info("number of Task to reset "+classNames.size());
			for (String className : classNames) {
				logger.info("Reset "+className);
				// This is the class to load
				Class<?> c = Class.forName(className);
				try {
					Process t = (Process) c.newInstance();
					t.resetPreferences();

				} catch (InstantiationException e) {
					logger.error(e.getMessage());
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage());
				}
			}
			//Log directory
			(new Log()).reset();
			
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
	}

}
