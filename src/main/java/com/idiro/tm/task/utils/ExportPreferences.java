package com.idiro.tm.task.utils;

import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.idiro.tm.task.Task;
import com.idiro.tm.task.in.Option;
import com.idiro.tm.task.in.PrefAndOpt;


/**
 * Exports the preferences stored into two files 
 * (one for user, one for system preference)
 * @author etienne
 *
 */
public class ExportPreferences extends Task{

	/**
     * The logger
     */
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Preferences
	 */
	private Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
	
	private PrefAndOpt<String> 	systemOpt,
								userOpt;
	

	public ExportPreferences(){
		systemOpt = new PrefAndOpt<String>(prefs, "system",'s',"The file where export the system XML tree","systemTree.xml");
		userOpt  = new PrefAndOpt<String>(prefs, "user",'u',"The file where export the system XML tree","userTree.xml");
		add(systemOpt);
		add(userOpt);
	}
			
	@Override
	public String getDescription() {
		return "Export the preferences to an XML file";
	}

	@Override
	public boolean init(){
		return true;
	}

	@Override
	protected boolean run(){
		boolean ok = true;
		try {
			OutputStream osSTree = new BufferedOutputStream(new FileOutputStream("systemTree.xml"));
			Preferences.systemRoot().exportSubtree(osSTree);
			OutputStream osUTree = new BufferedOutputStream(new FileOutputStream("userTree.xml"));
			Preferences.userRoot().exportSubtree(osUTree);
		} catch (Exception e) {
			logger.error(e.getMessage());
			ok = false;
		}
		return ok;
	}

	@Override
	protected boolean finalCheck() {
		return true;
	}

}
