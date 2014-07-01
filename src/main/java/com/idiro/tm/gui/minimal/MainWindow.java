package com.idiro.tm.gui.minimal;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.idiro.tm.ProcessManager;
import com.idiro.tm.gui.MultiSplitLayout;
import com.idiro.tm.gui.MultiSplitPane;
import com.idiro.tm.task.Process;


/**
 * Main Window of the minimal gui
 * @author etienne
 */
public class MainWindow extends JFrame{

	/**
	 * Logger of the class
	 */
	private Logger logger = Logger.getLogger(MainWindow.class);

	/**
	 * Help field
	 */
	private JEditorPane txt_help =new JEditorPane();

	/**
	 * Log field
	 */
	private GuiLogAppender ap_log;

	/**
	 * Tree of tasks
	 */
	private JTree		tree;

	/**
	 * Class for managing editing, searching... in a tree
	 */
	private TreeManager  treeMan = new TreeManager(ProcessManager.getInstance().getRootPackage());
	
	/**
	 * Component allowing to change preferences
	 */
	private LeafPreferences prefs = new LeafPreferences();


	/**
	 * Takes the list of nodes as input
	 * 
	 * Takes the list of nodes as input under the formes
	 * tasks.vfie.weekly.'...'.Export
	 * 
	 * @param nodes list of nodes
	 */
	public MainWindow(){
		super();

		setTitle(ProcessManager.getInstance().getRootPackage());
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		try{
			initObject();
			initPosition();
			initListener();
		}catch(ClassNotFoundException e){
			logger.error("The tasks tree has failed to be created");
			logger.error(e);
		}


		setVisible(true);
	}

	/**
	 * Initialise all the objects of MainWindow 
	 * @throws ClassNotFoundException 
	 */
	private void initObject() throws ClassNotFoundException{

		List<String> classes = ProcessManager.getInstance().getNonAbstractProcessClasses();
		tree = treeMan.editTree(classes);

		txt_help.setEditable(false);
		txt_help.setEditorKit(new HTMLEditorKit());

		Set<String> dictionary = new HashSet<String>();
		Iterator<String> it = classes.iterator();
		while(it.hasNext()){
			String cur = it.next();
			String[] split = cur.split("\\.");
			String element = split[0];
			dictionary.add(element);
			for(int i = 1; i < split.length; ++i){
				element += "."+ split[i];
				dictionary.add(element);
			}
		}

		ap_log = new GuiLogAppender(new ArrayList<String>(dictionary));
		ap_log.getText().setBackground(Color.BLACK);
		ap_log.getText().setForeground(Color.WHITE);
		ap_log.getText().setCaretColor(Color.WHITE);
		Logger.getRootLogger().addAppender(ap_log);

	}

	/**
	 * Initialise all the object positions in the frame 
	 */
	private void initPosition(){
		

		//Initialisation of the layout
		String layoutDef = "(COLUMN (ROW left middle right) bottom)";

		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);

		MultiSplitPane multiSplitPane = new MultiSplitPane();
		add(multiSplitPane);
		multiSplitPane.getMultiSplitLayout().setModel(modelRoot);
		
		//Init tree panel (left)
		JScrollPane treePane = new JScrollPane(tree,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		multiSplitPane.add(treePane, "left");
		

		//Init help panel (middle)
		JScrollPane helpPane = new JScrollPane(txt_help,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		multiSplitPane.add(helpPane, "middle");
		

		//Init pref panel (right)
		JScrollPane prefPane = new JScrollPane(prefs,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		multiSplitPane.add(prefPane, "right");
		
		
		//Init log panel (bottom)
		JScrollPane logPane = new JScrollPane(ap_log.getText(),
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		multiSplitPane.add(logPane, "bottom");
		
		
		//Gives an initialised size to the component and to the window
		treePane.setPreferredSize(new Dimension(250,600));
		helpPane.setPreferredSize(new Dimension(550,600));
		logPane.setPreferredSize(new Dimension(1200,200));
		prefPane.setPreferredSize(new Dimension(400,600));
		
		setSize(1200,800);

	}

	/**
	 * Initialise the listener of the integrated objects
	 * that interact with each other 
	 */
	private void initListener(){
		logger.info("Initialise Listener");


		/**
		 * Listener for the tree (when a node is selected)
		 * 
		 * When an object is selected in the tree, update the help
		 * Research if it is a process, and if it is, gives the
		 * help in their relative field
		 */
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				System.out.println("Enter in tree listener");
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				tree.getLastSelectedPathComponent();



				// if nothing is selected  
				if (node == null) return;

				// retrieve the node that was selected  
				TreeNode[]  tn = node.getPath();
				String func = new String();
				for(int i=0; i < tn.length - 1;++i){
					func += tn[i].toString() +".";
				}
				func += tn[tn.length - 1].toString();
				TerminalArea txt_log = ap_log.getText();
				try{
					System.out.println("display on log window "+func);
					int startChar = txt_log.getLineStartOffset(txt_log.getLineCount()-1),
					endChar   = txt_log.getLineEndOffset(txt_log.getLineCount()-1);
					txt_log.replaceRange(func,startChar,endChar);
				}catch(Exception e1){
					System.out.println(e1.getMessage());
					txt_log.append("\n"+func);
				}
				try{
					// React to the node selection. 
					List<String> classeNames;
					classeNames = ProcessManager.getInstance().getNonAbstractProcessClasses();

					String taskName = func.toLowerCase();
					for (String className : classeNames) {
						if (className.toLowerCase().endsWith(taskName)) {
							// This is the class to load
							Class<?> c = Class.forName(className);
							Process t = (Process) c.newInstance();
							txt_help.setText(formatHelp("Help "+tn[tn.length - 1].toString()+":\n\n"+t.getHelp()));
							prefs.loadPreferences(t);
							prefs.revalidate();
							prefs.repaint();
							ap_log.getText().repaint();
						}
					}
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}

			}

			/**
			 * Method which format the help text into html
			 * 
			 * @param text: the string to format
			 * @return the formated text
			 */
			private String formatHelp(String text) {
				StringBuffer newText = new StringBuffer();
				newText.append("<html>\n<b>");
				String[] lines = text.split("\n");
				newText.append(lines[0]+"</b><br>");
				for(int i=1; i < lines.length;++i){
					String line = lines[i];
					if(line.split(" ").length == 1 && line.contains(":")){
						newText.append("<b>").append(line).append("</b><br>");
					}else{
						newText.append(line).append("<br>");
					}
				}

				newText.append("</html>");

				return newText.toString();
			}
		});



		/**
		 * Listener for the log field 
		 * Which selects a node of a tree if possible
		 * 
		 */
		ap_log.getText().addKeyListener(new KeyListener (){

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {

				try{
					if( ap_log.getText().getMode() == TerminalArea.Mode.COMPLETION){
						return;
					}
					
					TerminalArea txt_log = ap_log.getText();
					int i,
					startChar = txt_log.getLineStartOffset(txt_log.getLineCount()-1),
					endChar   = txt_log.getLineEndOffset(txt_log.getLineCount()-1);

					String[] node = txt_log.getText().substring(startChar,endChar).split("\\.");

					TreePath cur_p = treeMan.findByName(tree, node);
					if(cur_p != null){
						tree.expandPath(cur_p);
						tree.setSelectionPath(cur_p);
					}
				}catch(Exception e){
					System.out.println(e.getMessage());
				}

			}

		});
	}

	/**
	 * @return the tree
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * @param tree the tree to set
	 */
	public void setTree(JTree tree) {
		this.tree = tree;
	}


}
