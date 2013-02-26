package idiro.tm.task.in;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * List of task input
 * 
 * Add functionalities to a list of TaskInput
 * @author etienne
 *
 */
public class TaskInputList extends LinkedList<TaskInput>{

	/**
	 * Returns a description of all the arguments options.
	 * 
	 * @return A description of the options available.
	 */
	public String getOptionsString() {

		List<Option> optionList = getAllOptions(); 

		if (optionList != null) {

			StringBuilder builder = new StringBuilder();

			for (Option option : optionList) {
				builder.append(option.getDescription() + "\n");
			}
			return builder.toString();
		}
		return "";
	}

	/**
	 * Returns a description of all the arguments options.
	 * 
	 * @return A description of the options available.
	 */
	public String getPreferencesString() {

		List<Preference> prefList = getAllPreferences();
		if (prefList != null) {

			StringBuilder builder = new StringBuilder();

			for (Preference pref : prefList) {
				builder.append(pref.getDescription() + "\n");
			}
			return builder.toString();
		}
		return "";
	}


	/**
	 * Returns the options stored in this list
	 * @return The options
	 */
	public List<Option> getOptionList() {
		List optionList = new ArrayList<Option>();
		Iterator<TaskInput> it = iterator();
		TaskInput curr;
		while(it.hasNext()){
			curr = it.next();
			if(curr instanceof Option){
				optionList.add((Option)curr);
			}
		}
		return optionList;
	}


	/**
	 * Returns the preferences stored in this list except the PrefAndOpt
	 * 
	 * @return The preferences except the PrefAndOpt
	 */
	public List<Preference> getPrefList() {
		List prefList = new ArrayList<Preference>();
		Iterator<TaskInput> it = iterator();
		TaskInput curr;
		while(it.hasNext()){
			curr = it.next();
			if(curr instanceof Preference && 
					!( curr instanceof PrefAndOpt)){
				prefList.add((Preference)curr);
			}
		}
		return prefList;
	}

	/**
	 * Returns the prefAndOpt objects stored in this list
	 * 
	 * @return The PrefAndOpt
	 */
	public List<PrefAndOpt> getPrefAndOptList() {
		List prefOptfList = new ArrayList<PrefAndOpt>();
		Iterator<TaskInput> it = iterator();
		TaskInput curr;
		while(it.hasNext()){
			curr = it.next();
			if(curr instanceof PrefAndOpt ){
				prefOptfList.add((PrefAndOpt)curr);
			}
		}
		return prefOptfList;
	}

	/**
	 * Returns the options stored in the prefAndOpt objects in this list
	 * 
	 * @return The option from PrefAndOpt
	 */
	public List<Option> getOptionsFromPrefAndOpt(){
		List<Option> options = new ArrayList<Option>();
		Iterator<PrefAndOpt> it = getPrefAndOptList().iterator();
		while(it.hasNext()){
			options.add(it.next().getOpt());
		}
		return options;
	}

	/**
	 * Returns all the options stored directly or indirectly
	 * @return all the options
	 */
	public List<Option> getAllOptions(){
		List<Option> options = getOptionList();
		options.addAll(getOptionsFromPrefAndOpt());
		return options;
	}

	/**
	 * Returns all the preferences stored in this list
	 * @return all the preferences
	 */
	public List<Preference> getAllPreferences(){
		List<Preference> prefList = getPrefList();
		prefList.addAll(getPrefAndOptList());
		return prefList;
	}
	
	/**
	 * Adds to this list a list of preference
	 * @param the list of preference
	 */
	public void addAllPref(List<Preference> le){
		Iterator<Preference> it = le.iterator();
		while(it.hasNext()){
			add((TaskInput)it.next());
		}
	}
	
	/**
	 * Adds to this list a list of option
	 * @param the list of option
	 */
	public void addAllOpt(List<Option> le){
		Iterator<Option> it = le.iterator();
		while(it.hasNext()){
			add((TaskInput)it.next());
		}
	}
}
