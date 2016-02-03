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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.idiro.tm.task.Task;
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
