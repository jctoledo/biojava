/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on 24.05.2004
 * @author Andreas Prlic
 *
 */

package org.biojava.bio.structure.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkListener;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileReader;


/** A class that provides a simple GUI for Jmol
 * 
 * @author Andreas Prlic
 *
 *
 *
 */
public class BiojavaJmol {

	public static final String viewer = "org.jmol.api.JmolSimpleViewer";
	public static final String adapter    = "org.jmol.api.JmolAdapter";
	public static final String smartAdapter = "org.jmol.adapter.smarter.SmarterJmolAdapter";

	//public static final String viewer = "JmolSimpleViewer";


	Structure structure; 

	JmolPanel jmolPanel;
	JFrame frame ;
	JMenuBar menu;
	
	public static void main(String[] args){
		try {

			PDBFileReader pdbr = new PDBFileReader();   
			//pdbr.setAutoFetch(true);
			pdbr.setPath("/nfs/team71/phd/ap3/WORK/PDB/");

			String pdbCode = "5pti";

			Structure struc = pdbr.getStructureById(pdbCode);

			BiojavaJmol jmolPanel = new BiojavaJmol();

			jmolPanel.setStructure(struc);

			// send some RASMOL style commands to Jmol
			jmolPanel.evalString("select * ; color chain;");
			jmolPanel.evalString("select *; spacefill off; wireframe off; backbone 0.4;  ");

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	
	

	public BiojavaJmol() {		
		
		frame = new JFrame();
		
		//initMenu();
		
		frame.addWindowListener(new ApplicationCloser());
		
		Container contentPane = frame.getContentPane();
				
		Box vBox = Box.createVerticalBox();

		try {
			
			jmolPanel = new JmolPanel();
			
		} catch (ClassNotFoundException e){
			e.printStackTrace();
			System.err.println("could not find Jmol in classpath, please install first");
			return;
		}
		jmolPanel.setPreferredSize(new Dimension(200,200));
		vBox.add(jmolPanel);


		JTextField field = new JTextField();

		field.setMaximumSize(new Dimension(Short.MAX_VALUE,30));   
		field.setText("enter RASMOL like command...");
		RasmolCommandListener listener = new RasmolCommandListener(jmolPanel,field) ;

		field.addActionListener(listener);
		field.addMouseListener(listener);
		field.addKeyListener(listener);
		vBox.add(field);

		contentPane.add(vBox);



		frame.pack();
		frame.setVisible(true); 

	}

	private void initMenu(){

		// show a menu
		
		menu = new JMenuBar();
		
		JMenu file= new JMenu("File");
		file.getAccessibleContext().setAccessibleDescription("exit the application");
		JMenuItem exitI = new JMenuItem("Exit");
		exitI.setMnemonic(KeyEvent.VK_X);
		exitI.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				 String cmd = e.getActionCommand();
			        
			        if ( cmd.equals("Exit")){
			        	System.exit(0);
			        }				
			}			
		});
		
		JMenu about = new JMenu("About");
		JMenuItem aboutI = new JMenuItem("PDBview");
		aboutI.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				 String cmd = e.getActionCommand();
			        
			        if ( cmd.equals("PDBview")){
			        	showAboutDialog();
			        }				
			}			
		});
		
		about.add(aboutI);
		
		file.add(exitI);
		menu.add(file);
		
		menu.add(Box.createGlue());
		menu.add(about);
		frame.setJMenuBar(menu);
		
		frame.repaint();

	}
	
	/** returns true if Jmol can be found in the classpath, otherwise false.
	 * 
	 * @return true/false depending if Jmol can be found
	 */
	public static boolean jmolInClassPath(){
		try {
			Class.forName(viewer);		
		} catch (ClassNotFoundException e){
			e.printStackTrace();			
			return false;
		}
		return true;
	}

	public void evalString(String rasmolScript){
		if ( jmolPanel == null ){
			System.err.println("please install Jmol first");
			return;
		}
		jmolPanel.evalString(rasmolScript);
	}

	public void setStructure(Structure s) {

		if ( jmolPanel == null ){
			System.err.println("please install Jmol first");
			return;
		}
		
		setTitle(s.getPDBCode());
		
		// actually this is very simple
		// just convert the structure to a PDB file

		String pdb = s.toPDB();	
		//System.out.println(s.isNmr());

		//System.out.println(pdb);
		// Jmol could also read the file directly from your file system
		//viewer.openFile("/Path/To/PDB/1tim.pdb");

		//System.out.println(pdb);
		jmolPanel.openStringInline(pdb);

		// send the PDB file to Jmol.
		// there are also other ways to interact with Jmol, e.g make it directly
		// access the biojava structure object, but they require more
		// code. See the SPICE code repository for how to do this.




	}

	public void setTitle(String label){
		frame.setTitle(label);
		frame.repaint();
	}




	static class ApplicationCloser extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	static class JmolPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3661941083797644242L;

		Class viewerC;
		Class adapterC;
		Class smartAdapterC;

		Object viewerO;
		Object adapterO;

		Method evalString;
		Method renderScreenImage;
		Method openStringInline;

		//JmolSimpleViewer viewer;
		//JmolAdapter adapter;
		JmolPanel() throws ClassNotFoundException {

			try {
				viewerC = Class.forName(viewer);

				adapterC = Class.forName(adapter);
				smartAdapterC = Class.forName(smartAdapter);

				Method m = viewerC.getMethod("allocateSimpleViewer", new Class[]{Component.class,adapterC});

				Constructor constructor = smartAdapterC.getConstructor(new Class[]{});
				adapterO = constructor.newInstance(new Object[]{});

				//viewerC = JmolSimpleViewer.allocateSimpleViewer(this, adapter);
				viewerO = m.invoke(viewerC, this, adapterO);

				evalString = viewerC.getMethod("evalString",String.class);

				renderScreenImage = viewerC.getMethod("renderScreenImage",
						new Class[]{Graphics.class,Dimension.class, Rectangle.class});

				openStringInline = viewerC.getMethod("openStringInline", new Class[]{String.class});

			} catch (InstantiationException e){
				e.printStackTrace();
			} catch (NoSuchMethodException e){
				e.printStackTrace();        		
			} catch ( InvocationTargetException e){
				e.printStackTrace();
			} catch ( IllegalAccessException e){
				e.printStackTrace();
			}

		}

		public Class getViewer() {
			return viewerC;
		}

		public void evalString(String rasmolScript){
			try {
				evalString.invoke(viewerO, rasmolScript);
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		public void openStringInline(String pdbFile){
			try {
				openStringInline.invoke(viewerO, pdbFile);
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		public void executeCmd(String rasmolScript){
			try {
				evalString.invoke(viewerO, rasmolScript);
			} catch (Exception e){
				e.printStackTrace();
			}
		}


		final Dimension currentSize = new Dimension();
		final Rectangle rectClip = new Rectangle();

		public void paint(Graphics g) {
			getSize(currentSize);
			g.getClipBounds(rectClip);
			//viewer.renderScreenImage(g, currentSize, rectClip);

			try {
				renderScreenImage.invoke(viewerO,g,currentSize,rectClip);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	private void showAboutDialog(){
		JDialog dialog = new JDialog();

		dialog.setSize(new Dimension(800,600));

		String msg = "A simple PDB viewer based on BioJava and Jmol. Author: Andreas Prlic";
		
		
		JEditorPane txt = new JEditorPane("text/html", msg);
		txt.setEditable(false);

		
		 JScrollPane scroll = new JScrollPane(txt);

		 Box vBox = Box.createVerticalBox();
		 vBox.add(scroll);

		 JButton close = new JButton("Close");

		 close.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent event) {
				 Object source = event.getSource();
				 //System.out.println(source);
				 JButton but = (JButton)source;
				 Container parent = but.getParent().getParent().getParent().getParent().getParent().getParent() ;
				 //System.out.println(parent);
				 JDialog dia = (JDialog) parent;
				 dia.dispose();
			 }
		 });

		 Box hBoxb = Box.createHorizontalBox();
		 hBoxb.add(Box.createGlue());
		 hBoxb.add(close,BorderLayout.EAST);

		 vBox.add(hBoxb);

		 dialog.getContentPane().add(vBox);
		 dialog.setVisible(true);
		
		
	}
	
	
}