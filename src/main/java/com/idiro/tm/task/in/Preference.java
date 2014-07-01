package com.idiro.tm.task.in;

import java.util.prefs.Preferences;

import org.apache.log4j.Logger;


/**
 * Class which put or get a specific preference from the preferences class
 * 
 * This class is generic but cannot support all the type only BOOLEAN, INT,
 * FLOAT, DOUBLE and STRING. This is due to the preferences class which 
 * supports only these type
 * 
 * @author Etienne Dumoulin
 * @param <T> type of the preference
 */
public class Preference<T> extends TaskInput<T>{
	
	/**
     * Logger.
     */
    private static Logger logger = Logger.getLogger(Preference.class);
	
    /**
     * Preferences node where the preference is stored
     */
	private Preferences prefs;
	
	/**
	 * Set to false if the preference field is not manually editable
	 */
	private boolean editable;
	
	
	/**
	 * Set a preference
	 * 
	 * optional is set true
	 * 
	 * @param prefs node where is store the preference
	 * @param defaultValue default value of the preference
	 * @param label label to describe what the preference do
	 * 
	 */
	public Preference(Preferences prefs, String label, T defaultValue) {
		super(label,defaultValue);
		this.prefs = prefs;
		setEditable(true);
	}

	/**
	 * @param prefs node where the preference is store
	 * @param defaultValue default value of the preference
	 * @param label label to describe what the preference do
	 * @param optional set the option as optional or mandatory
	 */
	public Preference(Preferences prefs, String label, T defaultValue,
			boolean optional) {
		super(label,defaultValue,optional);
		this.prefs = prefs;
		setEditable(true);
	}
	
	/**
	 * 
	 * Get the value from the Preference tree
	 * 
	 * @return null if the type does not exist,
	 *         default value if the backing store is not available 
	 *         or there is no value store for this Preference,
	 *         the value in the other cases.
	 */
	@SuppressWarnings("unchecked")
	public T get(){
		T value = null;
		switch(type){
		case BOOLEAN:
		    Boolean bValue =  prefs.getBoolean(getDescription(),(Boolean) defaultValue);
		    value = (T) bValue;
			break;
		case INT:
			Integer iValue = prefs.getInt(getDescription(),(Integer) defaultValue);
			value = (T) iValue;
			break;
		case FLOAT:
			Float fValue = prefs.getFloat(getDescription(),(Float) defaultValue);
			value = (T) fValue;
			break;
		case DOUBLE:
			Double dValue = prefs.getDouble(getDescription(),(Double) defaultValue);
			value = (T) dValue;
			break;
		case STRING:
			String sValue = prefs.get(getDescription(),(String) defaultValue);
			value = (T) sValue;
			break;
		default:
		}
		logger.debug("get value for "+prefs.absolutePath()+" : "+getDescription()+": "+value);
		return value;
	}


	/**
	 * put a new value in the Preferences tree
	 * 
	 * @param value the new value
	 */
	public void put(T value){
		logger.debug("put value for "+prefs.absolutePath()+" : "+getDescription()+": "+value);
		switch(type){
		case BOOLEAN:
		    prefs.putBoolean(getDescription(), (Boolean)value);
			break;
		case INT:
			prefs.putInt(getDescription(), (Integer)value);
			break;
		case FLOAT:
			prefs.putFloat(getDescription(), (Float)value);
			break;
		case DOUBLE:
			prefs.putDouble(getDescription(), (Double)value);
			break;
		case STRING:
			prefs.put(getDescription(),(String)value);
			break;
		default:
		}
	}
	
	/**
	 * Reset the preference
	 * 
	 * The value enter in the tree is the default value
	 */
	public void reset(){
		put(defaultValue);
	}
	
	public void remove(){
		prefs.remove(getDescription());
	}

	/**
	 * @return the prefs
	 */
	public Preferences getPrefs() {
		return prefs;
	}

	/**
	 * @param prefs the prefs to set
	 */
	public void setPrefs(Preferences prefs) {
		this.prefs = prefs;
	}

	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	
}
