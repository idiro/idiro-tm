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

import org.apache.log4j.Logger;

/**
 * Class uses for command line task input
 * 
 * @author Donal Doyle
 * 
 */
public class Option<T> extends TaskInput<T>{

	/**
     * Logger.
     */
    private static Logger logger = Logger.getLogger(Option.class);
	
	/**
	 * The long form of the option. i.e. --longform
	 */
	private String longForm;

	/**
	 * The short form of the option. i.e. -shortform
	 */
	private Character shortForm;
	
	/**
	 * The value of the option
	 */
	private T value;
	

	
	
	/**
	 * Constructor.
	 * 
	 * optional is set true
	 * 
	 * @param longForm
	 *            The long form of the option. i.e. --longform
	 * @param label
	 *            Label of the option
	 * @param default value
	 *            The default value
	 */
	public Option( final String longForm,
			final String label, final T defaultValue) {
		super(label,defaultValue);
		init(longForm, null);
	}
	
	/**
	 * Constructor.
	 * 
	 * optional is set true
	 * 
	 * @param longForm
	 *            The long form of the option. i.e. --longform
	 * @param shortForm
	 *            The short form to use.
	 * @param label
	 *            Label of the option
	 * @param defaultValue
	 *            The default value
	 */
	public Option(final String longForm, final Character shortForm, 
			final String label, final T defaultValue) {
		super(label,defaultValue);
		init(longForm,shortForm);
	}
	
	/**
	 * Constructor.
	 * 
	 * 
	 * @param longForm
	 *            The long form of the option. i.e. --longform
	 * @param label
	 *            Label of the option
	 * @param defaultValue
	 *            The default value
	 * @param optional
	 * 			  Set the option as mandatory or optional
	 */
	public Option( final String longForm,
			final String label, final T defaultValue, final boolean optional) {
		super(label,defaultValue,optional);
		init(longForm, null);
	}
	
	/**
	 * Constructor.
	 * 
	 * 
	 * @param longForm
	 *            The long form of the option. i.e. --longform
	 * @param shortForm
	 *            The short form to use.
	 * @param label
	 *            Label of the option
	 * @param defaultValue
	 *            The default value
	 * @param optional
	 * 			  Set the option as mandatory or optional
	 */
	public Option(final String longForm, final Character shortForm, 
			final String label, final T defaultValue, final boolean optional) {
		super(label,defaultValue,optional);
		init(longForm,shortForm);
	}

	/**
	 * Method called by the constructor to initialise the object
	 * 
	 * @param longForm
	 *            The long form of the option. i.e. --longform
	 * @param shortForm
	 *            The short form to use.
	 */
	private void init( final String longForm,final Character shortForm) {
		this.shortForm = shortForm;
		this.longForm = longForm;
		this.value = null;
	}



	/**
	 * Returns the value. or the defaultValue if the value is null
	 * 
	 * @return the value
	 */
	public T get(){
		if(value == null){
			logger.debug("get value for option --"+longForm+", label "+label+", the value is: "+defaultValue);
			return defaultValue;
		}
		logger.debug("get value for option --"+longForm+", label "+label+", the value is: "+value);
		return value;
	}

	/**
	 * Returns the value
	 * 
	 * @return the value
	 */
	public T getValue(){
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            The value
	 */
	public void setValue(T value){
		this.value = value;
	}
	
	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            The value
	 */
	public void setValue(String value){
		
		switch(type){
		case BOOLEAN:
		    Boolean bValue =  value.toLowerCase().equals("true");
		    this.value = (T) bValue;
			break;
		case INT:
			Integer iValue = Integer.valueOf(value);
			this.value = (T) iValue;
			break;
		case FLOAT:
			Float fValue = Float.valueOf(value);
			this.value = (T) fValue;
			break;
		case DOUBLE:
			Double dValue = Double.valueOf(value);
			this.value = (T) dValue;
			break;
		case STRING:
			this.value = (T) value;
			break;
		default:
		}
		logger.debug("set value for the option "+getDescription()+": '"+value+"'");
	}
	
	/**
	 * @return the longForm
	 */
	public String getLongForm() {
		return longForm;
	}
	/**
	 * @param longForm the longForm to set
	 */
	public void setLongForm(String longForm) {
		this.longForm = longForm;
	}
	/**
	 * @return the shortForm
	 */
	public Character getShortForm() {
		return shortForm;
	}
	/**
	 * @param shortForm the shortForm to set
	 */
	public void setShortForm(Character shortForm) {
		this.shortForm = shortForm;
	}
	
}
