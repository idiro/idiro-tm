package idiro.tm.task;

import idiro.tm.ProcessManager;
import idiro.utils.Time;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import idiro.tm.task.in.ArgumentParser;
import idiro.tm.task.in.Option;
import idiro.tm.task.in.PrefAndOpt;
import idiro.tm.task.in.Preference;
import idiro.tm.task.in.TaskInput;


/**
 * Abstract class for dynamic tasks.
 * 
 * @author Donal Doyle & Conor Nugent & Etienne Dumoulin
 * 
 */
public abstract class Task extends Process{

	/**
	 * The logger
	 */
	private static Logger taskLogger = Logger.getLogger(Task.class);

	/**
	 * Constructor
	 */
	public Task(){
		add(optHelp);
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
	 * This executes a Task
	 *
	 * @return true if executes ok
	 */
	public final boolean execute(String[] args){

		startTime = System.currentTimeMillis();
		taskLogger.debug("Start to execute "+this.getClass()+" at "+ ((double)startTime)/1000);
		if(!parse(args,getInputList().getAllOptions())){
			taskLogger.error("The class "+this.getClass()+" has not been parsed correctly");
			taskLogger.error(getHelp());
			return false;
		}

		if(optHelp.get()){
			logger.info(getHelp());
		}else{

			if(!checkParsed(getInputList())){
				taskLogger.error("Arguments not parsed correctly for "+this.getClass());
				taskLogger.error(getHelp());
				return false;
			}


			taskLogger.debug("Start to init "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
			if(!init()){
				taskLogger.error("The class "+this.getClass()+" has not been initialised correctly");
				taskLogger.error(getHelp());
				return false;
			}
			taskLogger.debug("Start to run "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
			if(!run()){
				taskLogger.error("The class "+this.getClass()+" has not run correctly");
				return false;
			}
			taskLogger.debug("Start to check "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
			if(!finalCheck()){
				taskLogger.error("The class "+this.getClass()+" has not the output expected");
				return false;
			}
		}

		long endTime = System.currentTimeMillis();
		long[] time = Time.getHMSm(endTime - startTime);
		taskLogger.info("Task complete in " + time[Time.DAYS] + " days " + time[Time.HOURS] + " hours "
				+ time[Time.MINUTES] + " mins " + time[Time.SECONDS] + " seconds " + time[Time.MILLIS] + " ms ");

		return true;
	}



	/**
	 * Returns a help string.
	 * 
	 * @return The help string
	 */
	public final String getHelp() {
		return "\n\nDESCRIPTION:\n" + getDescription() + " \n"
		+ "\nPREFERENCES: \n" + getInputList().getPreferencesString()
		+ "\nSYNOPSIS: \n" + getSynopsis() + "\n"
		+ "\nOPTIONS: \n" + getInputList().getOptionsString();
	}


	/**
	 * This create the default preference settings for each class
	 * 
	 */
	public void resetPreferences(){
		List<Preference> prefList = getInputList().getPrefList();
		prefList.addAll(getInputList().getPrefAndOptList());
		Iterator<Preference> it = prefList.iterator();
		while(it.hasNext()){
			it.next().reset();
		}
	}


}
