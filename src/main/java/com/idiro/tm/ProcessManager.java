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

package com.idiro.tm;

/*
 * Copyright 2009 by Idiro Technologies. 
 * All rights reserved
 */


import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.idiro.Block;
import com.idiro.BlockManager;
import com.idiro.Log;


/**
 * Entry point for processes.
 * 
 * @author etienne
 * 
 */
public final class ProcessManager extends BlockManager{

	/**
	 * The logger.
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * The single instance of process runner.
	 */
	private static ProcessManager runner = new ProcessManager();

	private boolean init = false;

	/**
	 * Constructor.
	 * 
	 */
	private ProcessManager() {

	}

	/**
	 * 
	 * @return Returns the single allowed instance of ProcessRunner
	 */
	public static ProcessManager getInstance() {
		if(!runner.init){
			runner.init = true;
			// Loads in the log settings.
			Log.init();
		}
		return runner;
	}

	/**
	 * Run a block from its name and arguments
	 * 
	 * @param values
	 * @param rootFound
	 * @return true if the task has been launched
	 */
	public boolean runBlock(final String[] values){
		boolean found = false;
		String taskName = (this.getRootPackage()+"."+ values[0]).toLowerCase(),
		className;
		// This is a dynamic task. Need to start it off.
		List<String> classeNames = getNonAbstractProcessClasses();
		Iterator<String> itClass = classeNames.iterator();
		while(!found && itClass.hasNext()) {
			className = itClass.next();
			logger.debug(className.toLowerCase()+" "+taskName);
			if (className.toLowerCase().startsWith(taskName)) {
				if (className.toLowerCase().endsWith(taskName)) {
					found = true;
					Class<?> c = null;

					Block b = null;
					try{	
						// This is the class to load
						c = Class.forName(className);
						b = (Block) c.newInstance();
					}catch(Exception e){
						logger.error(e.getMessage());
						logger.error(e.getCause());
						logger.error("The task "+c.getClass()+" newInstance return an exception: the default constructor is it implemented ?");
						logger.error("The class which has to be called can not be instancied. Exit now");
					}
					if(b != null){
						String[] taskParameters = new String[values.length - 1];
						for (int x = 0; x < taskParameters.length; x++) {
							taskParameters[x] = values[x + 1];
						}
						b.execute(taskParameters);
					}
				}
			}
		}

		return found;
	}
	
	
	/**
	 * Runs process manager
	 * 
	 * @param args
	 *            The args
	 */
	private void run(final String[] args){

		boolean ok = true;

		if (args.length == 0) {
			logger.error("You must either pass in a parameter or and an xml file");
			ok = false;
		}else{

			String[] values;
			if (args[0].endsWith("console")) {
				logger.info("Please enter arguments");
				Scanner in = new Scanner(System.in);
				values = in.nextLine().split(" ");
			} else {
				values = args;
			}

			ok = runBlock(values);
		}
		if(!ok){
			help();
		}

	}

	

	/**
	 * Gets the command for running the specified task.
	 * 
	 * @param taskClass
	 *            The class
	 * @return the command
	 */
	public static String getProcessCommand(final Class<?> taskClass) {
		String text = taskClass.getCanonicalName();
		text = text.replaceFirst(getInstance().getRootPackage().toLowerCase()+".", "");
		text = text.toLowerCase();
		return text;
	}

	/**
	 * @return the init
	 */
	public boolean isInit() {
		return init;
	}


	/**
	 * Runs a task Manager
	 * @param args
	 */
	public static void runTaskManager(String[] args){

		ProcessManager runner = ProcessManager.getInstance();
		if(runner.isInit()){
			String currentDir = (new File("")).getAbsolutePath();

			String arguments = new String();
			for(int i=0; i < args.length;++i){
				arguments += args[i] + " ";
			}

			runner.logger.info("Starting. "+runner.getClass().getPackage().toString().split(" ")[1].toLowerCase()+" " + arguments + " from " + currentDir);
			runner.run(args);

			runner.logger.info("Ending. "+ runner.getClass().getPackage().toString().split(" ")[1].toLowerCase()+" "+ arguments + " from " + currentDir);

		}

	}
	
	/**
	 * 
	 * @return the list of non abstract task classes in the project
	 */
	public List<String> getNonAbstractProcessClasses(){
		//TODO Process.class.getCanonicalName() does not work, why?
		return getNonAbstractClassesFromSuperClass(Block.class.getCanonicalName());
	}

}
