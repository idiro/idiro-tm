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

package com.idiro.tm.task.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.*;

/**
 * Argument Parser.
 * 
 * This class load the value of options parsed
 * 
 * @author Dónal Doyle
 * 
 */
public class ArgumentParser {

	/**
	 * Logger.
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Options with a key.
	 */
	private Map<String, Option> options = new HashMap<String, Option>();

	/**
	 * 
	 * @param option
	 *            Adds an option that will be checked by this parser
	 */
	public final void addOption(final Option option) {
		if (option.getShortForm() != null) {
			this.options.put("-" + option.getShortForm(), option);
		}
		this.options.put("--" + option.getLongForm(), option);
	}

	/**
	 * Parses a list of arguments. They can be of the form -shortname value
	 * -longname value --longname=value value
	 * 
	 * @param args
	 *            The args
	 * @throws Exception
	 *             An exception if there is a problem parsing
	 */
	public final boolean parseOptions(final String[] args){
		logger.debug("Enter in "+this.getClass()+" parseOptions.");
		boolean ok = true;
		int 	position = 0,
		equalsPos;
		String 	currentOptionValue = null,
		currentOptionName,
		current;

		while (position < args.length && ok) {
			current = args[position];
			logger.debug("parse the option: "+current);
			// Check if it is an option
			if (current.startsWith("--")) {
				//Treatment of long form option
				equalsPos = current.indexOf("=");
				if (equalsPos != -1) {
					currentOptionName = current.substring(0,equalsPos);
					currentOptionValue = current.substring(equalsPos + 1);
				}else{
					logger.debug("No '=' for a long form option, supposed to be boolean");
					currentOptionName = current;
				}
				Option opt = (Option) this.options.get(currentOptionName);
				if (opt == null) {
					logger.error("Unknown Option: " + currentOptionName);
					logger.error("Exit of the method");
					ok = false;
				}else{
					if(equalsPos == -1){
						if(opt.getType() == TaskInput.typeT.BOOLEAN){
							Boolean b = !( (Boolean) opt.getDefaultValue());
							currentOptionValue = b.toString();
						}else{
							logger.error("A long form option without '=' has to be boolean");
							ok = false;
						}
					}
					if(ok){
						logger.debug("option: "+ currentOptionName);
						logger.debug("value: "+currentOptionValue);
						opt.setValue(currentOptionValue);
						++position;
					}
				}

			}else if(current.startsWith("-")){
				//Treatment of short form option
				// Short boolean options can be in series -abc equivalent to -a -b -c
				if (current.length() > 2) {
					for (int i = 1; i < current.length(); i++) {
						Option opt = (Option) options.get("-" + current.charAt(i));
						if (opt == null) {
							logger.error("Unknown option: -" + current.charAt(i));
							logger.error("Exit of the method");
							ok = false;
						}else if(opt.getType() != TaskInput.typeT.BOOLEAN){
							logger.error("option: -"+ current.charAt(i)+" in options "+
									current+"has to be boolean");
							ok = false;
						}else{

						}
					}
					++position;
				}else{
					Option opt = (Option) this.options.get(current);
					if (opt == null) {
						logger.error("Unknown Option: " + current);
						logger.error("Exit of the method");
						ok = false;
					}
					if(ok){
						++position;
						if (position < args.length) {
							currentOptionValue = args[position];
							//Check if currentValue is an option
							Option optValue = (Option) this.options.get(currentOptionValue);
							if(optValue == null){
								//It is a value
								opt.setValue(currentOptionValue);
								++position;
							}else{
								//The current option has to be a boolean
								if(opt.getType() == TaskInput.typeT.BOOLEAN){
									opt.setValue(!((Boolean)opt.getDefaultValue()));
								}else{
									//Case where a value is also an option
									//Case rare enough to send a warning
									logger.warn(currentOptionValue+ " is parsed as the value of the option "+current);
									opt.setValue(currentOptionValue);
									++position;
								}
							}
						}else{
							//Has to be a boolean option 
							if(opt.getType() != TaskInput.typeT.BOOLEAN){
								logger.error("option: -"+ current+" parsed without arguments");
								ok = false;
							}else{
								opt.setValue(!((Boolean)opt.getDefaultValue()));
								++position;
							}
						}

					}
				}
			}else{
				logger.error(current+"is not an option");
				logger.error("Exit of the method");
				ok = false;
			}

		}
		logger.debug("Exit from "+this.getClass()+" parseOptions: "+ok);
		return ok;
	}
}
