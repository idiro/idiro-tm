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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Class to manage JTree simple operations 
 * @author etienne
 *
 */
public class TreeManager {

	/**
	 * The top node of the tree
	 */
	DefaultMutableTreeNode top;
	
	public TreeManager(String rootName){
		top = new DefaultMutableTreeNode(rootName);
	}
	
	/**
	 * edit the tree
	 * 
	 * @return the new tree
	 * @throws ClassNotFoundException the tasks class has not been found
	 */
	public JTree editTree(List<String> nodes) throws ClassNotFoundException{
		
		JTree tree = new JTree(top);;
		TreePath tp_prec;
		Iterator<String> it = nodes.iterator();
		String[] path,
		path_cur;
		int i,j;

		while(it.hasNext()){
			tp_prec = null;
			path = it.next().split("\\.");
			for(i=0; i < path.length;++i){

				path_cur = new String[i+1];
				for(j = 0; j <= i; ++j)
					path_cur[j] = path[j];

				TreePath tp = findByName(tree, path_cur); 
				if( tp == null ){
					DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(path[i]);
					if(tp_prec != null){
						DefaultMutableTreeNode node_par = (DefaultMutableTreeNode) (tp_prec.getLastPathComponent());
						node_par.add( new DefaultMutableTreeNode(new_node));
					}
				}
				tree = new JTree(top);
				tp_prec = findByName(tree, path_cur);
			}

		}
		return new JTree(top);
	}
	
	// Finds the path in tree as specified by the node array. The node array is a sequence
	// of nodes where nodes[0] is the root and nodes[i] is a child of nodes[i-1].
	// Comparison is done using Object.equals(). Returns null if not found.
	public TreePath find(JTree tree, Object[] nodes) {
	    TreeNode root = (TreeNode)tree.getModel().getRoot();
	    return find2(new TreePath(root), nodes, 0, false);
	}

	
	/**
	 * Finds the path in a tree
	 * 
	 * 
	 * Finds the path in tree as specified by the array of names. The names array is a
	 * sequence of names where names[0] is the root and names[i] is a child of names[i-1].
	 * 
	 * @param tree The tree in which we are searching
	 * @param names
	 * @return the TreePath or null if not found.
	 */
	public TreePath findByName(JTree tree, String[] names) {
		TreeNode root = (TreeNode)tree.getModel().getRoot();
		return find2(new TreePath(root), names, 0, true);
	}

	/**
	 * Recursive function for searching
	 * 
	 * 
	 * @param parent the input TreePath
	 * @param nodes  the nodes we are looking for
	 * @param depth  the current depth we are in the tree
	 * @param byName true if we are searching by name, 
	 * 		  false if it is by object
	 * 
	 * @return The tree path found or null if not found
	 */
	private TreePath find2(TreePath parent, Object[] nodes, int depth, boolean byName) {
		TreeNode node = (TreeNode)parent.getLastPathComponent();
		Object o = node;

		// If by name, convert node to a string
		if (byName) {
			o = o.toString();
		}

		// If equal, go down the branch
		if (o.equals(nodes[depth])) {
			// If at end, return match
			if (depth == nodes.length-1) {
				return parent;
			}

			// Traverse children
			if (node.getChildCount() >= 0) {
				for (Enumeration e=node.children(); e.hasMoreElements(); ) {
					TreeNode n = (TreeNode)e.nextElement();
					TreePath path = parent.pathByAddingChild(n);
					TreePath result = find2(path, nodes, depth+1, byName);
					// Found a match
					if (result != null) {
						return result;
					}
				}
			}
		}

		// No match at this branch
		return null;
	}

	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree.
	public void expandAll(JTree tree, boolean expand) {
	    TreeNode root = (TreeNode)tree.getModel().getRoot();

	    // Traverse tree from root
	    expandAll(tree, new TreePath(root), expand);
	}
	
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
	    // Traverse children
	    TreeNode node = (TreeNode)parent.getLastPathComponent();
	    if (node.getChildCount() >= 0) {
	        for (Enumeration e=node.children(); e.hasMoreElements(); ) {
	            TreeNode n = (TreeNode)e.nextElement();
	            TreePath path = parent.pathByAddingChild(n);
	            expandAll(tree, path, expand);
	        }
	    }

	    // Expansion or collapse must be done bottom-up
	    if (expand) {
	        tree.expandPath(parent);
	    } else {
	        tree.collapsePath(parent);
	    }
	}
	

	/**
	 * @return the top
	 */
	public DefaultMutableTreeNode getTop() {
		return top;
	}


	/**
	 * @param top the top to set
	 */
	public void setTop(DefaultMutableTreeNode top) {
		this.top = top;
	}
	
}
