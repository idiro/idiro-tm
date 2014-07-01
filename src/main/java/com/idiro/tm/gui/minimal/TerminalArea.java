package com.idiro.tm.gui.minimal;



import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.idiro.tm.ProcessManager;


/**
 * Terminal emulator 
 * The terminal emulator object has a completion facility 
 * and is able to launch tasks directly from the text
 * 
 * @author etienne
 *
 */
public class TerminalArea extends JTextArea
implements DocumentListener {

	private static final String COMMIT_ACTION = "commit";
	private int maxLine = 0;
	public static enum Mode { INSERT, COMPLETION };
	private List<String> words;
	private Mode mode = Mode.INSERT;
	//Indicate if the terminal is busy or not
	private boolean busy;

	/**
	 * Logger of the class
	 */
	private Logger logger = Logger.getLogger(TerminalArea.class);

	/**
	 * Constructor
	 * @param dictionary the words recognised by the terminal
	 */
	public TerminalArea(List<String> dictionary) {
		getDocument().addDocumentListener(this);

		getInputMap().put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);
		getActionMap().put(COMMIT_ACTION, new CommitAction());

		words = dictionary;
		Collections.sort(words);

		Iterator<String> it = words.iterator();
		logger.debug("words contained in the dictionary:");
		while(it.hasNext()){
			logger.debug(it.next());
		}
		logger.debug("");



		//Catch the backspace between the editable text and the non-editable text
		this.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e){
				try{

					if( maxLine < getLineStartOffset(getLineCount()-1)){
						maxLine = getLineStartOffset(getLineCount()-1);
					}
				}catch(Exception e1){
					System.out.println(e1.getMessage());
				}

				//catch Backspace and delete
				if (e.getKeyChar() == '\u0008' || e.getKeyChar() == '\u007F') {
					if(getCaret().getDot() == maxLine - 1 ){
						//setEditable(true);
						append("\n");
						getCaret().setDot(maxLine);
					}

				}else if(e.getKeyChar() == '\n'){
					try{
						System.out.println("execute");
						String[] args = getText().substring(
								getLineStartOffset(getLineCount()-2),
								getLineEndOffset(getLineCount()-2)-1 ).split(" ");
						args[0] = args[0].substring(args[0].indexOf('.')+1).toLowerCase();
						busy = true;
						ProcessManager.runTaskManager(args);
						busy = false;
					}catch(Exception e1){
						System.out.println(e1.getMessage());
					}
				}
			}
		});

		//Make the text non-editable if the caret is before the last line
		this.addCaretListener(new CaretListener(){

			@Override
			public void caretUpdate(CaretEvent arg0) {
				try{


					if( maxLine < getLineStartOffset(getLineCount()-1)){
						maxLine = getLineStartOffset(getLineCount()-1);
					}

					if(arg0.getDot() < maxLine || busy){
						setEditable(false);
					}else{
						setEditable(true);
					}
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
			}

		});

		//Make the text editable for the caret
		this.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
				if(busy)
					setEditable(false);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				setEditable(true);
			}

		});
	}

	// Listener methods
	public void changedUpdate(DocumentEvent ev) {
	}

	public void removeUpdate(DocumentEvent ev) {
	}

	public void insertUpdate(DocumentEvent ev) {
		if (ev.getLength() != 1) {
			return;
		}

		int pos = ev.getOffset();
		String content = null;
		try {
			content = getText(0, pos + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if( ! Character.isLetter(content.charAt(w)) && content.charAt(w) != '.') {
				break;
			}
		}
		if (pos - w < 2) {
			// Too few chars
			return;
		}

		String prefix = content.substring(w + 1);
		//System.out.println("prefix autocompletion : "+prefix);
		int n = Collections.binarySearch(words, prefix);
		//System.out.println("n: "+n+" "+words.size());
		if (n < 0 && -n <= words.size()) {
			String match = words.get(-n - 1);
			if (match.startsWith(prefix)) {
				//System.out.println("match autocompletion : "+match);
				// A completion is found
				String completion = match.substring(pos - w);
				// We cannot modify Document from within notification,
				// so we submit a task that does the change later
				SwingUtilities.invokeLater(
						new CompletionTask(completion, pos + 1));
			}
		} else {
			// Nothing found
			mode = Mode.INSERT;
		}
	}


	private class CompletionTask implements Runnable {
		String completion;
		int position;

		CompletionTask(String completion, int position) {
			this.completion = completion;
			this.position = position;
		}

		public void run() {
			insert(completion, position);
			setCaretPosition(position + completion.length());
			moveCaretPosition(position);
			mode = Mode.COMPLETION;
		}
	}

	private class CommitAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			if (mode == Mode.COMPLETION) {
				int pos = getSelectionEnd();
				setCaretPosition(pos);
				mode = Mode.INSERT;
			}
		}
	}

	/**
	 * @return the words
	 */
	public List<String> getWords() {
		return words;
	}

	/**
	 * @param words the words to set
	 */
	public void setWords(List<String> words) {
		this.words = words;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * @param busy the busy to set
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	/**
	 * @return the busy
	 */
	public boolean isBusy() {
		return busy;
	}


}

