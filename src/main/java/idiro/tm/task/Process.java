package idiro.tm.task;

import idiro.Block;

import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;


import idiro.tm.task.in.*;

/**
 * Abstract class which allows to launch processes 
 * from TaskInput
 * 
 * @author etienne
 *
 */
public abstract class Process extends Block{
	

	/**
	 * The list of argument available for the Task
	 */
	private TaskInputList inputList;
	
	
	/**
	 * Preferences node for task
	 * @note For super task, prefs is initialised to the parent node. 
	 */
	protected Preferences prefs;
	
	/**
	 * Option to give a help of the task
	 */
	protected Option<Boolean> optHelp;
	

	/**
	 * Logger of the task
	 */
	private Logger processLogger = Logger.getLogger(Process.class);
	
	
	/**
	 * The constructor.
	 * 
	 * By default, the preferences are stored in the system tree
	 */
	public Process() {
		inputList = new TaskInputList();
		prefs = Preferences.systemNodeForPackage(this.getClass());
		
		optHelp = new Option<Boolean>("help", 'h',
				"Gives a help for "+this.getClass(),false);
	}
	
	/**
	 * The constructor, choose if the task default preference are system or user
	 *  
	 * @param systemPref true the preferences are stored in the system tree
	 */
	public Process(boolean systemPref){
		if(systemPref){
			prefs = Preferences.systemNodeForPackage(this.getClass());
		}else{
			prefs = Preferences.userNodeForPackage(this.getClass());
		}
	}

	/**
	 * Returns a description string.
	 * 
	 * @return The description string
	 */
	public abstract String getDescription();
	
	/**
	 * Returns how the task should be run.
	 * 
	 * @return How the task should be run.
	 */
	public abstract String getSynopsis();

	/**
	 * Check if the arguments are parsed correctly
	 * 
	 * @param args The arguments
	 * @return true, if it is ok
	 */
	protected boolean checkParsed(TaskInputList tList){
		
		List<PrefAndOpt> prefAndOptList = tList.getPrefAndOptList();
		PrefAndOpt prefAndOpt;
		Iterator<PrefAndOpt> itPrefAndOpt = prefAndOptList.iterator();
		boolean ok = true,
				boolReturn = true;

		while( itPrefAndOpt.hasNext()){
			prefAndOpt = itPrefAndOpt.next();
			if(!prefAndOpt.isOptional()){
				ok = !prefAndOpt.isDefaultValue();
				if(!ok){
					boolReturn = false;
					processLogger.error("Preference and Option "+prefAndOpt.getDescription()+
							prefAndOpt.getOpt().getLongForm()+" is not set");
				}
			}
		}


		List<Preference> prefList = tList.getPrefList();
		Preference pref;
		Iterator<Preference> itPref = prefList.iterator();
		while( itPref.hasNext()){
			pref = itPref.next();
			if(!pref.isOptional()){
				ok = !pref.isDefaultValue();
				if(!ok){
					boolReturn = false;
					processLogger.error("Preference "+pref.getDescription() + " is not set");
				}
			}
		}

		List<Option> optionList = tList.getOptionList();
		Option   opt;
		Iterator<Option> itOpt = null;
		itOpt = optionList.iterator();
		while( itOpt.hasNext()){
			opt = itOpt.next();
			if(!opt.isOptional()){
				ok = !opt.isDefaultValue();
				if(!ok){
					boolReturn = false;
					processLogger.error("Option " + opt.getLongForm()+" ("+opt.getDescription() + ") is not set");
				}
			}
		}
		
		return boolReturn;
	}

	
	/**
	 * Parse the arguments in the list of option given in parameter
	 * 
	 * @return The argument parser.
	 */
	protected final boolean parse(String[] args, List<Option> optionList) {

		ArgumentParser parser = new ArgumentParser();

		for (Option option : optionList) {
			parser.addOption(option);
		}
		boolean boolReturn = parser.parseOptions(args);
		return boolReturn;
	}
	
	/**
	 * Creates the default preference settings for each class
	 * 
	 */
	public abstract void resetPreferences();
	

	/**
	 * Returns a help string.
	 * 
	 * @return The help string
	 */
	public abstract String getHelp();
	


	/**
	 * Gets the inputList for this instance.
	 *
	 * @return The inputList.
	 */
	public TaskInputList getInputList()
	{
		return this.inputList;
	}

	/**
	 * @see List#add(TaskInput)
	 */
	public boolean add(TaskInput e)
	{
		return inputList.add(e);
	}
	
	/**
	 * @see List#addAll(List<TaskInput>)
	 */
	public boolean addAll(List<TaskInput> le)
	{
		return inputList.addAll(le);
	}
}
