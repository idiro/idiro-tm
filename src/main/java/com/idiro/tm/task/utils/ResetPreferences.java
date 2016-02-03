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
