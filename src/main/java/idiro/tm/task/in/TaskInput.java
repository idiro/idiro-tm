package idiro.tm.task.in;

import org.apache.log4j.Logger;

/**
 * Parent of all the class input proposed by this software
 *  
 *  Generic class which save for each input if it is an optional or not,
 *  the default value and the label of the option
 *  
 * @author Etienne Dumoulin
 * @param <T> The type of the input
 */
public abstract class TaskInput<T> {

	/**
	 * Type of T, supported for TaskInput
	 * @author etienne
	 *
	 */
	public enum typeT{
		BOOLEAN,
		INT,
		FLOAT,
		DOUBLE,
		STRING,
	}
	
	/**
     * The logger
     */
	private static Logger logger = Logger.getLogger(TaskInput.class);
	
	
	/**
	 * A description of the option.
	 */
	protected String label;
	
	
	/**
	 * The value of the option
	 */
	protected T defaultValue;
	
	
	/**
	 * If the option is optional. The default is true.
	 */
	protected boolean optional;
	
	/**
	 * Type of the option
	 */
	protected typeT type;
	
	/**
	 * @param label
	 */
	public TaskInput(String label) {
		init(label, null, false);
	}
	
	/**
	 * @param defaultValue
	 * @param label
	 */
	public TaskInput(String label,T defaultValue) {
		init(label, defaultValue, true);
	}
	
	/**
	 * @param defaultValue
	 * @param label
	 * @param optional
	 */
	public TaskInput( String label, T defaultValue, boolean optional) {
		init(label, defaultValue, optional);
	}


	/**
	 * Initialise the item
	 * 
	 * @param label label to set
	 * @param defaultValue defaultValue to set
	 * @param optional optional boolean to set
	 */
	public void init(String label, T defaultValue, boolean optional){
		TaskInput.logger.debug("initializes "+this.getClass()+"label: "+label);
		if( defaultValue instanceof  Boolean){
			type = typeT.BOOLEAN;
		}else if(defaultValue instanceof  Integer){
			type = typeT.INT;
		}else if(defaultValue instanceof Float){
			type = typeT.FLOAT;
		}else if(defaultValue instanceof Double){
			type = typeT.DOUBLE;
		}else if(defaultValue instanceof String){
			type = typeT.STRING;
		}else{
			logger.error("Preference type is not suported");
		}
		
		this.defaultValue = defaultValue;
		this.label = label;
		this.optional = optional;
	}
	
	
	/**
	 * Get the description of the item
	 * 
	 * @return a description
	 */
	public String getDescription(){
		StringBuffer description = new StringBuffer();
		if(!optional){
			description.append("MANDATORY - ");
		}else{
			description.append("OPTIONAL - ");
		}
		String[] classDef = defaultValue.getClass().toString().split("\\.");
		description.append(classDef[classDef.length-1]+" - "+label);
		return description.toString();
	}
	
	/**
	 * Returns the value. or the defaultValue if the value is not available
	 * 
	 * @return the value
	 */
	public abstract T get();
	

	/**
	 * 
	 * Return true if the value is the default one
	 */
	public boolean isDefaultValue(){
		return get().equals(defaultValue);
	}
	
	
	/**
	 * @return the defaultValue
	 */
	public T getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	protected void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}
	/**
	 * @param optional the optional to set
	 */
	protected void setOptional(boolean optional) {
		this.optional = optional;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	protected void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the type
	 */
	public typeT getType() {
		return type;
	}
	
}
