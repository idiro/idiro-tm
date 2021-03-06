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

import java.util.prefs.Preferences;

/**
 * Stores a preference which is also an option
 * 
 * @note The option is automatically set to true for the option,
 * in order to be checked in the preferences. 
 * 
 * @author Etienne Dumoulin
 * @param <T> type of the preference.
 */
public class PrefAndOpt<T> extends Preference<T>{

	private Option<T> opt;
	
	/**
	 * Set a preference
	 * 
	 * optional is set true
	 * 
	 * @param prefs node where is store the preference
	 * @param longForm The long form of the option
	 * @param defaultValue default value of the preference
	 * @param label label to describe what the preference do
	 */
	public PrefAndOpt(Preferences prefs, String longForm, String label, T defaultValue) {
		super(prefs, label,defaultValue);
		opt = new Option<T>(longForm, label, defaultValue);
	}

	/**
	 * @param prefs node where is store the preference
	 * @param longForm of the option
	 * @param defaultValue default value of the preference
	 * @param label label to describe what the preference do
	 * @param optional set the option as optional or mandatory
	 */
	public PrefAndOpt(Preferences prefs, String longForm, String label, T defaultValue,
			boolean optional) {
		super(prefs, label,defaultValue,optional);
		opt = new Option<T>(longForm, label, defaultValue);
	}
	
	/**
	 * Set a preference
	 * 
	 * optional is set true
	 * 
	 * @param prefs node where is store the preference
	 * @param longForm The long form of the option
	 * @param shortForm The short form of the option
	 * @param defaultValue default value of the preference
	 * @param label label to describe what the preference do
	 */
	public PrefAndOpt(Preferences prefs, String longForm, Character shortForm, String label, T defaultValue) {
		super(prefs, label,defaultValue);
		opt = new Option<T>(longForm, shortForm, label, defaultValue);
	}

	/**
	 * @param prefs node where is store the preference
	 * @param longForm of the option
	 * @param shortForm The short form of the option
	 * @param defaultValue default value of the preference
	 * @param label label to describe what the preference do
	 * @param optional set the option as optional or mandatory
	 */
	public PrefAndOpt(Preferences prefs, String longForm, Character shortForm, String label, T defaultValue,
			boolean optional) {
		super(prefs, label,defaultValue,optional);
		opt = new Option<T>(longForm, shortForm, label, defaultValue);
	}
	

	/**
	 * Set a preference
	 * 
	 * optional is set true
	 * 
	 * @param prefs node where is store the preference
	 * @param option option associated to the preference
	 * @param defaultValue default value of the preference
	 * @param label label to describe what the preference do
	 * 
	 */
	public PrefAndOpt(Preferences prefs, Option<T> opt, String key, String label, T defaultValue) {
		super(prefs, label,defaultValue);
		opt.setDefaultValue(defaultValue);
		this.opt = opt;
	}

	/**
	 * @param prefs node where is store the preference
	 * @param option option associated to the preference
	 * @param defaultValue default value of the preference
	 * @param label label to describe what the preference do
	 * @param optional set the option as optional or mandatory
	 */
	public PrefAndOpt(Preferences prefs, Option<T> opt,  String key, String label, T defaultValue,
			boolean optional) {
		super(prefs, label,defaultValue,optional);
		opt.setDefaultValue(defaultValue);
		this.opt = opt;
	}
	
	/**
	 * 
	 * Get the value from the Option or from Preference tree
	 * 
	 * Looks for a value in option then if not specified in preference
	 * 
	 * @return null if the type does not exist,
	 *         default value if the backing store is not available 
	 *         or there is no value store for this Preference,
	 *         the value in the other cases.
	 */
	public T get(){
		T value = null;
		if(opt.getValue() == null){
			value = super.get();
		}else{
			value = opt.get();
		}
		return value;
	}

	/**
	 * @return the opt
	 */
	public Option<T> getOpt() {
		return opt;
	}

	/**
	 * @param opt the opt to set
	 */
	public void setOpt(Option<T> opt) {
		this.opt = opt;
	}
	
	
}
