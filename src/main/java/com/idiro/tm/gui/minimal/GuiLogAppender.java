package com.idiro.tm.gui.minimal;

import javax.swing.JTextArea;

import javax.swing.*;
import java.util.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.GroupLayout.*;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;

import org.apache.log4j.spi.LoggingEvent;

/**
 * Appends log4j to a terminal emulator
 * @author etienne
 *
 */
class GuiLogAppender extends AppenderSkeleton{ 

    TerminalArea text;

    /**
     * Constructor
     * 
     * @param dictionary words recognised by the terminal for the completion
     */
    GuiLogAppender(List<String> dictionary){
        setThreshold(Level.INFO);
        text = new TerminalArea(dictionary);
    }

    @Override
        protected void append(LoggingEvent arg0) {
            text.append(arg0.getLevel()+" "+arg0.getLoggerName()+" - "+arg0.getRenderedMessage()+"\n");
        }

    @Override
        public void close() {

        }

    @Override
        public boolean requiresLayout() {
            return false;
        }

    /**
     * @return the text
     */
    public TerminalArea getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(TerminalArea text) {
        this.text = text;
    }


}
