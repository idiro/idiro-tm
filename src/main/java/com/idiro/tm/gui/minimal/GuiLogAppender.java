/** 
 *  Copyright © 2016 Red Sqirl, Ltd. All rights reserved.
 *  Red Sqirl, Clarendon House, 34 Clarendon St., Dublin 2. Ireland
 *
 *  This file is part of Utility for command line programmes
 *
 *  User agrees that use of this software is governed by: 
 *  (1) the applicable user limitations and specified terms and conditions of 
 *      the license agreement which has been entered into with Red Sqirl; and 
 *  (2) the proprietary and restricted rights notices included in this software.
 *  
 *  WARNING: THE PROPRIETARY INFORMATION OF Utility for command line programmes IS PROTECTED BY IRISH AND 
 *  INTERNATIONAL LAW.  UNAUTHORISED REPRODUCTION, DISTRIBUTION OR ANY PORTION
 *  OF IT, MAY RESULT IN CIVIL AND/OR CRIMINAL PENALTIES.
 *  
 *  If you have received this software in error please contact Red Sqirl at 
 *  support@redsqirl.com
 */

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
