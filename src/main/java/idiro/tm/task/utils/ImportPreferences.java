package idiro.tm.task.utils;

import java.io.*;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import idiro.tm.task.Task;
import idiro.tm.task.in.ArgumentParser;
import idiro.tm.task.in.Option;

/**
 * Imports the preferences from two files (one for system, one for user) 
 * @author etienne
 *
 */
public class ImportPreferences extends Task{

	private Option<String> systemOpt,userOpt;

	private File systemFile;

	private File userFile;

	public ImportPreferences(){
		systemOpt = new Option<String>("system",'s',"The system XML tree to import","null",false);
		userOpt  = new Option<String>("user", 'u', "The user XML tree to import","null",false);
		add(systemOpt);
		add(userOpt);
	}

	@Override
	public String getDescription() {
		return "Import the preferences from an xml file";
	}

	@Override
	public boolean init(){
		
		systemFile = new File(systemOpt.getValue());
		userFile = new File(userOpt.getValue());
		
		if(!systemFile.exists() || !userFile.exists()){
			logger.error("One of the input files: "+systemFile+" "+userFile+" does not exist");
			return false;
		}
		if(!systemFile.isFile() || !userFile.isFile()){
			logger.error("One of the input files: "+systemFile+" "+userFile+" is not a file");
			return false;
		}
		if(!systemFile.canRead() || !userFile.canRead()){
			logger.error("One of the input files: "+systemFile+" "+userFile+" is not readable");
			return false;
		}
		return true;
	}

	@Override
	protected boolean run(){
		boolean ok = true;
		try{
		InputStream inSTree = new BufferedInputStream(new FileInputStream(systemFile));
		Preferences.importPreferences(inSTree);
		InputStream inUTree = new BufferedInputStream(new FileInputStream(userFile));
		Preferences.importPreferences(inUTree);
		}catch(Exception e){
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
