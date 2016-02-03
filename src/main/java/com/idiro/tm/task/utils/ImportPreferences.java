/** 
 *  Copyright © 2016 Red Sqirl, Ltd. All rights reserved.
 *  Red Sqirl, Clarendon House, 34 Clarendon St., Dublin 2. Ireland
 *
 *  This file is part of Utility for command line programmes
 *
 *  User agrees that use of this software is governed by: 
 *  (1) the applicable user limitations and specified terms and conditions of 
 *      the license agreement which has been entered into with Red Sqirl; and 
 *  (2) the proprietary and restricted rights notices included in this software.
 *  
 *  WARNING: THE PROPRIETARY INFORMATION OF Utility for command line programmes IS PROTECTED BY IRISH AND 
 *  INTERNATIONAL LAW.  UNAUTHORISED REPRODUCTION, DISTRIBUTION OR ANY PORTION
 *  OF IT, MAY RESULT IN CIVIL AND/OR CRIMINAL PENALTIES.
 *  
 *  If you have received this software in error please contact Red Sqirl at 
 *  support@redsqirl.com
 */

package com.idiro.tm.task.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.prefs.Preferences;

import com.idiro.tm.task.Task;
import com.idiro.tm.task.in.Option;


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
