package com.idiro.tm.gui.minimal;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.idiro.tm.task.Process;
import com.idiro.tm.task.SuperTask;
import com.idiro.tm.task.in.Preference;


/**
 * Displays the preferences involved in one process
 * 
 * For a task display it in a content
 * For a SuperTask display the preferences in different tab
 * 
 * @author etienne
 *
 */
public class LeafPreferences extends JComponent{

	/**
	 * The current process
	 */
	Process pro;

	/**
	 * List of labels and texts
	 */
	List<Label> labels = new LinkedList<Label>();
	List<JTextField> texts = new LinkedList<JTextField>();

	/**
	 * Current content of the LeafPreferences component
	 */
	JComponent content = null; 

	/**
	 * Log
	 */
	private Logger logger = Logger.getLogger(LeafPreferences.class);

	LeafPreferences(){
		setLayout(new GridLayout(0,1,0,1));
	}

	/**
	 * Loads new content to the panel by parsing a process
	 * 
	 * @param pro process from which we want the list of preference
	 */
	public void loadPreferences(Process pro){
		this.pro = pro;

		clear();
		Iterator<Preference> it = pro.getInputList().getAllPreferences().iterator();

		logger.debug("Load Preference gui for : "+pro.getClass().getCanonicalName());

		if(isSuperTask(pro.getClass().getCanonicalName())){

			JTabbedPane tab = new JTabbedPane();

			tab.setTabPlacement(JTabbedPane.TOP);

			SuperTask st = (SuperTask) pro;
			tab.addTab("Super Task", newTab(st.getInputSuperTaskList().getAllPreferences().iterator()));
			String[] taskNames = st.getTaskPref().get().split(" ");
			for(int i = 0; i < taskNames.length; ++i){
				st.setTask(taskNames[i]);
				tab.addTab(taskNames[i], newTab(st.getInputTaskList().getAllPreferences().iterator()) );
			}
			add(content = tab);
		}else{
			add(content = newTab(pro.getInputList().getAllPreferences().iterator()));
		}

	}

	/**
	 * Create a content from an iterator on a preference list
	 * 
	 * @param it list iterator
	 * @return the content
	 */
	private JComponent newTab(Iterator<Preference> it){
		JComponent tabContent = new JPanel();
		tabContent.setLayout(new GridLayout(0,1,0,1));
		Preference cur = null;
		int index = 0;
		while(it.hasNext()){
			cur = it.next();
			Label label = new Label(cur.getDescription());
			final JTextField text = new JTextField(cur.get().toString());
			labels.add(label);
			texts.add(text);
			tabContent.add(label);
			tabContent.add(text);
			if(cur.isEditable()){

				text.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						Preference pref = getPro().getInputList().getAllPreferences().get(texts.indexOf(text));
						pref.put(text.getText());
					}

				});
			}else{
				text.setEditable(false);
			}
			++index;
		}
		return tabContent;
	}

	/**
	 * Return true if the className parsed is a superTask (sub-class of process)
	 * @param className
	 * @return
	 */
	private boolean isSuperTask(String className){
		try{
			Class<?> c = Class.forName(className);
			Class<?> nextClass = c.getSuperclass();
			while (nextClass != null){

				if (nextClass.getCanonicalName().equals(SuperTask.class.getCanonicalName())){
					return true;
				}
				nextClass = nextClass.getSuperclass();
			}
		}catch(ClassNotFoundException e){
			logger.warn("The class "+className+", cannot be loaded");
		}
		return false;
	}

	/**
	 * Clear the component: remove all the fields which are on it
	 */
	public void clear(){
		labels.clear();
		texts.clear();

		if(content != null)
			remove(content);

	}

	/**
	 * @return the pro
	 */
	public Process getPro() {
		return pro;
	}

	/**
	 * @param pro the pro to set
	 */
	public void setPro(Process pro) {
		this.pro = pro;
	}

	/**
	 * @return the labels
	 */
	public List<Label> getLabels() {
		return labels;
	}

	/**
	 * @param labels the labels to set
	 */
	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	/**
	 * @return the texts
	 */
	public List<JTextField> getTexts() {
		return texts;
	}

	/**
	 * @param texts the texts to set
	 */
	public void setTexts(List<JTextField> texts) {
		this.texts = texts;
	}


}
