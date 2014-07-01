package com.idiro.tm.task;


import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;

import org.apache.log4j.Logger;

import com.idiro.tm.ProcessManager;
import com.idiro.tm.task.in.Option;
import com.idiro.tm.task.in.Preference;
import com.idiro.tm.task.in.TaskInput;
import com.idiro.tm.task.in.TaskInputList;
import com.idiro.tm.utils.CheckBackingStore;
import com.idiro.utils.Time;


public abstract class SuperTask extends Process{

	/**
	 * Super Task Logger.
	 */
	private Logger superTaskLogger = Logger.getLogger(SuperTask.class);

	/**
	 * The list of task input available for the SuperTask
	 */
	private TaskInputList inputSuperTaskList;
	/**
	 * The list of task input available for the Task
	 */
	private TaskInputList inputTaskList;

	/**
	 * Default super task options
	 */
	private Option<String> 	optCreate,
	optRemove,
	optRename;

	/**
	 * Preference which store the task managed by the this super task
	 */
	private Preference<String> taskPref;


	/**
	 * True if the task preferences are set to run
	 */
	private boolean taskSet;


	/**
	 * The default constructor.
	 * 
	 * By default the preferences of the tasks and super task 
	 * are stored in the system tree
	 */
	public SuperTask() {
		super();
		initConstructor();
	}

	/**
	 * The constructor, choose if the task default preference are system or user
	 *  
	 * @param systemPref true if the task default preference in the system tree
	 */
	public SuperTask(boolean systemPref){
		super(systemPref);
		initConstructor();
	}

	/**
	 * The default initialisation method called by the constructor
	 */
	private void initConstructor(){
		inputSuperTaskList = new TaskInputList();
		inputTaskList = new TaskInputList();


		optCreate = new Option<String>("create",
				"Create a new task for "+this.getClass(),"null");

		optRemove = new Option<String>("remove",
				"Remove a task from "+this.getClass(),"null");

		optRename = new Option<String>("rename",
				"Rename a task of "+this.getClass(),"null,null");

		taskPref = new Preference<String>(prefs,"List of the generated task","");
		taskPref.setEditable(false);

		taskSet = false;
		addSuperTaskInput(optCreate);
		addSuperTaskInput(optRemove);
		addSuperTaskInput(optRename);
		addSuperTaskInput(optHelp);
		addSuperTaskInput(taskPref);
	}

	/**
	 * Executes a super task or a task
	 *
	 * @return true if executes ok
	 */
	public boolean execute(String[] args){

		startTime = System.currentTimeMillis();
		superTaskLogger.debug("Start to execute "+this.getClass()+" at "+ ((double)startTime)/1000);

		if(args[0].startsWith("-")){

			//executes SuperTask
			if(!parse(args,inputSuperTaskList.getOptionList())){
				superTaskLogger.error("The class "+this.getClass()+" has not been parsed correctly");
				superTaskLogger.error(getHelp());
				return false;
			}

			if(optHelp.get()){
				logger.info(getHelp());
			}else{

				if(!checkParsed(inputSuperTaskList)){
					superTaskLogger.error("Arguments not parsed correctly for "+this.getClass());
					superTaskLogger.error(getHelp());
					return false;
				}

				superTaskLogger.debug("Start to run the task generator "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
				if(!runSuperTask()){
					superTaskLogger.error("The class "+this.getClass()+" has not run correctly");
					return false;
				}
			}

		}else{
			//Try to executes the task parsed in first argument
			if(!setTask(args[0])){
				return false;
			}
			
			//Set the arguments of the task
			String[] taskArgs = new String[args.length-1];
			for(int i=1; i< args.length;++i){
				taskArgs[i-1] = args[i];
			}

			if(!parse(taskArgs,getInputList().getAllOptions())){
				superTaskLogger.error("The class "+this.getClass()+" has not been parsed correctly");
				superTaskLogger.error(getHelp());
				return false;
			}

			superTaskLogger.debug("Start to init "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
			if(!init()){
				superTaskLogger.error("The class "+this.getClass()+" has not been initialised correctly");
				superTaskLogger.error(getHelp());
				return false;
			}
			superTaskLogger.debug("Start to run "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
			if(!run()){
				superTaskLogger.error("The class "+this.getClass()+" has not run correctly");
				return false;
			}
			superTaskLogger.debug("Start to check "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
			if(!finalCheck()){
				superTaskLogger.error("The class "+this.getClass()+" has not the output expected");
				return false;
			}

		}
		long endTime = System.currentTimeMillis();
		long[] time = Time.getHMSm(endTime - startTime);
		superTaskLogger.info("Task complete in " + time[Time.DAYS] + " days " + time[Time.HOURS] + " hours "
				+ time[Time.MINUTES] + " mins " + time[Time.SECONDS] + " seconds " + time[Time.MILLIS] + " ms ");

		return true;
	}

	/**
	 * Check if the sub-task of this superTask exists or not
	 * @param taskName name of the task
	 * @return
	 */
	private boolean checkTaskExist(String taskName) {
		String[] tasks = taskPref.get().split(" ");
		boolean found = false;
		int i = 0;
		while(i < tasks.length && !found){
			if(tasks[i++].equals(taskName)){
				found = true;
			}
		}
		return found;
	}

	/**
	 * Run a super task, which contains the 3 default options:
	 * create, remove, rename
	 * @return true if it is ok
	 */
	protected boolean runSuperTask() {
		boolean ok = true;

		//Create option
		if(!optCreate.isDefaultValue()){
			String[] newTaskList = optCreate.get().split(",");

			for(String newTask: newTaskList){

				ok = ok && createTask(newTask);
			}

		}

		//Remove option
		if(!optRemove.isDefaultValue()){
			String[] removeTaskList = optRemove.get().split(",");

			for(String removeTask: removeTaskList){

				ok = ok && removeTask(removeTask);

			}

		}

		//Rename option
		if(!optRename.isDefaultValue()){

			String[] names = optRename.get().split(",");
			if(names.length != 2){
				superTaskLogger.error("Error arguments not parsed correctly, expect two fields separate by a comma: oldname,newname");
				String fieldParsed = new String();
				for(int i = 0; i < names.length; ++i){
					fieldParsed += names[i]+"|";
				}
				superTaskLogger.error("Arguments parsed: "+optRename.get()+" : |"+fieldParsed);
				ok = false;
			}

			if(ok){
				ok = renameTask(names[0],names[1]);
			}

		}

		return ok;
	}


	/**
	 * Create a task with the default preferences
	 * 
	 * @param newTask The name of the new task
	 * 
	 * @return true if runs ok
	 */
	private boolean createTask(String newTask) {
		boolean ok = true;

		if(!CheckBackingStore.isAvailable()){
			superTaskLogger.error("The backing store is not available");
			ok = false;
		}

		if(!ok && checkTaskExist(newTask)){
			superTaskLogger.error("A task with this name exists already");
			ok = false;
		}
		if(ok){

			superTaskLogger.info("Create the default preferences for "+newTask);
			// Create the node with the default parameter
			List<Preference> prefList = getInputTaskList().getAllPreferences();
			Iterator<Preference> it = prefList.iterator();
			Preference pref;
			while(it.hasNext()){
				pref = it.next();
				pref.setPrefs(pref.getPrefs().node(newTask));
				pref.reset();
				pref.setPrefs(pref.getPrefs().parent());
			}

			superTaskLogger.info("Update the superTask list "+newTask);
			String tasks = taskPref.get();
			if(tasks.isEmpty()){
				taskPref.put(newTask);
			}else{
				taskPref.put(tasks +" "+ newTask);
			}
			superTaskLogger.info("New list "+taskPref.get());
		}

		return ok;
	}

	/**
	 * Remove a task from the super task
	 * @param oldTask name of the task to remove
	 * @return true if runs ok
	 */
	private boolean removeTask(String oldTask){

		boolean ok = true;

		try{
			String tasks = taskPref.get();
			if(checkTaskExist(oldTask)){
				//Remove the name from the list
				String newTasks = tasks.replace(oldTask, "");
				newTasks = newTasks.replace("  ", " ").trim();
				superTaskLogger.info("Remove "+oldTask+" from the superTask list");
				superTaskLogger.info("Old list:"+tasks+"  new list: "+newTasks);
				taskPref.put(newTasks);
				if(prefs.nodeExists(oldTask) ){
					superTaskLogger.info("Remove the preferences corresponding to the task "+oldTask);
					Iterator<Preference> itPref = getInputTaskList().getAllPreferences().iterator();
					while(itPref.hasNext()){
						prefs.node(oldTask).remove(itPref.next().getDescription());
					}
					//Remove the node if there is no more preference in there
					if(prefs.node(oldTask).keys().length == 0){
						prefs.node(oldTask).removeNode();
						superTaskLogger.info("Remove the node "+oldTask+", no more keys saved in there");
					}

				}else{
					superTaskLogger.error("Remove: could not find the node "+oldTask);
					ok = false;
				}
			}else{
				superTaskLogger.error("Remove: could not find the task "+oldTask);
				ok = false;
			}
		}catch(BackingStoreException e){
			superTaskLogger.error(e.getMessage());
			ok = false;
		}
		return ok;

	}

	/**
	 * Renames a task of the super task
	 * @param oldName former name of the task
	 * @param newName new name of the task
	 * @return true if runs ok
	 */
	private boolean renameTask(String oldName, String newName){
		// Create the node with the default parameter
		Iterator<Preference> it =  getInputTaskList().getAllPreferences().iterator();
		Preference pref;

		boolean ok = checkTaskExist(oldName) && !checkTaskExist(newName); 
		if( ok){
			ok = createTask(newName);
		}else{
			superTaskLogger.error("Please check that "+oldName+" exists and "+newName+" does not");
		}
		if( ok ){
			superTaskLogger.info("Move the preferences from "+oldName+" to "+newName);
			while(it.hasNext()){
				pref = it.next();
				pref.setPrefs(pref.getPrefs().node(oldName));
				Object obj = pref.get();
				pref.setPrefs(pref.getPrefs().node(newName));
				pref.put(obj);
			}
			ok = removeTask(oldName);
		}
		return ok;
	}


	/**
	 * Set a the task preference to the task name given in input
	 * @param taskName the task name
	 * @return false is the name does not exist
	 */
	public boolean setTask(String taskName){

		boolean ok = true;
		if(checkTaskExist(taskName)){
			clearTask();
			Iterator<Preference> itPref = getInputTaskList().getAllPreferences().iterator();
			while(itPref.hasNext()){
				Preference pref = itPref.next();
				pref.setPrefs(pref.getPrefs().node(taskName));
			}
			taskSet = true;
		}else{
			superTaskLogger.error("The node "+taskName+" in "+this.getClass()+" does not exists");
			ok = false;
		}
		return ok;
	}

	private void clearTask(){
		if(taskSet){
			Iterator<Preference> itPref = getInputTaskList().getAllPreferences().iterator();
			while(itPref.hasNext()){
				Preference pref = itPref.next();
				pref.setPrefs(pref.getPrefs().parent());
				taskSet = false;
			}
		}
	}
	/**
	 * Creates the default preference settings for each class
	 * 
	 */
	public void resetPreferences(){

		//Reset SuperTask preferences
		List<Preference> prefList = inputSuperTaskList.getPrefList();
		prefList.addAll(inputSuperTaskList.getPrefAndOptList());
		Iterator<Preference> it = prefList.iterator();
		while(it.hasNext()){
			it.next().reset();
		}
		//Reset task preferences
		if(!taskPref.isDefaultValue()){
			String[] tasks = taskPref.get().split(" ");

			for(String task : tasks){
				removeTask(task);
			}
			createTask("default");
		}

	}

	/**
	 * Returns a help string.
	 * 
	 * @return The help string
	 */
	public String getHelp() {
		return "Super task containing : "+ taskPref.get()
		+ "\n\nDESCRIPTION:\n" + getDescription() + " \n"
		+ "\nSUPERPREFERENCES: \n" + inputSuperTaskList.getPreferencesString()
		+ "\nSUPERSYNOPSIS: \n" + getSuperTaskSynopsis() + "\n"
		+ "\nSUPEROPTIONS: \n" + inputSuperTaskList.getOptionsString()
		+ "\nPREFERENCES: \n" + getInputTaskList().getPreferencesString()
		+ "\nSYNOPSIS: \n" + getSynopsis() + "\n"
		+ "\nOPTIONS: \n" + getInputTaskList().getOptionsString();

	}

	/**
	 * Returns how the task should be run.
	 * 
	 * @return How the task should be run.
	 */
	public String getSuperTaskSynopsis(){
		List<Option> listOpt = inputSuperTaskList.getAllOptions();
		Option opt = null;
		StringBuffer str = new StringBuffer();
		Iterator<Option> it = listOpt.iterator();
		while(it.hasNext()){
			opt  = it.next();
			String[] classDef = opt.getDefaultValue().getClass().toString().split("\\.");
			if(opt.isOptional()){
				str.append(" [--"+opt.getLongForm()+"=<"+classDef[classDef.length-1]+">]");
			}else{
				str.append(" --"+opt.getLongForm()+"=<"+classDef[classDef.length-1]+">");
			}
		}
		return ProcessManager.getProcessCommand(this.getClass())+str;
	}

	/**
	 * Returns how the task should be run.
	 * 
	 * @return How the task should be run.
	 */
	public String getSynopsis(){
		List<Option> listOpt = getInputList().getAllOptions();
		Option opt = null;
		StringBuffer str = new StringBuffer();
		Iterator<Option> it = listOpt.iterator();
		str.append(" ");
		str.append(taskPref.get().replace(' ', '|'));
		str.append(" ");
		while(it.hasNext()){
			opt  = it.next();
			String[] classDef = opt.getDefaultValue().getClass().toString().split("\\.");
			if(opt.isOptional()){
				str.append(" [--"+opt.getLongForm()+"=<"+classDef[classDef.length-1]+">]");
			}else{
				str.append(" --"+opt.getLongForm()+"=<"+classDef[classDef.length-1]+">");
			}
		}
		return ProcessManager.getProcessCommand(this.getClass())+str;
	}

	/**
	 * Gets the inputSuperTaskList for this instance.
	 *
	 * @return The inputSuperTaskList.
	 */
	public TaskInputList getInputSuperTaskList()
	{
		return this.inputSuperTaskList;
	}
	/**
	 * @see List#add(TaskInput)
	 */
	public boolean addSuperTaskInput(TaskInput e)
	{
		boolean ok = inputSuperTaskList.add(e);
		ok &= add(e);
		return ok;
	}

	/**
	 * @see List#addAll(List<TaskInput>)
	 */
	public boolean addAllSuperTask(List<TaskInput> le)
	{
		boolean ok = inputSuperTaskList.addAll(le);
		ok &= addAll(le);
		return ok;
	}

	/**
	 * @param inputTaskList the inputTaskList to set
	 */
	public void setInputTaskList(TaskInputList inputTaskList) {
		this.inputTaskList = inputTaskList;
	}

	/**
	 * @return the inputTaskList
	 */
	public TaskInputList getInputTaskList() {
		return inputTaskList;
	}
	/**
	 * @see List#add(TaskInput)
	 */
	public boolean addTaskInput(TaskInput e)
	{
		boolean ok = inputTaskList.add(e);
		ok &= add(e);
		return ok;
	}

	/**
	 * @see List#addAll(List<TaskInput>)
	 */
	public boolean addAllTask(List<TaskInput> le)
	{
		boolean ok = inputTaskList.addAll(le);
		ok &= addAll(le);
		return ok;
	}

	/**
	 * @return the taskPref
	 */
	public Preference<String> getTaskPref() {
		return taskPref;
	}

	/**
	 * @param taskPref the taskPref to set
	 */
	public void setTaskPref(Preference<String> taskPref) {
		this.taskPref = taskPref;
	}
}
