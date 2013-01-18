/**
 * peony : A light weighted InfoVis toolkit
 * Copyright (C) 2005 - 2006 IBM CRL Information Visualization and Analysis Team All Rights Reserved
 * @author CaoNan (nancao@cn.ibm.com)
 * IBM Confidential
 */
package vis.vjit.demo.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/***
 * 
 * This piece of code is a joint research between HKUST and Harvard University. 
 * It is based on the CPL opensource license. Please check the 
 * term before using.
 * 
 * The paper is published in InfoVIs 2013: 
 * "Whisper: Tracing the Spatiotemporal Process of Information Diffusion in Real Time"
 * 
 * Visit Whisper's main website here : whipserseer.com
 * 
 * @author NanCao(nancao@gmail.com)
 *
 */
public class PStateBar extends JPanel{

	private static final long serialVersionUID = 7881093782109733194L;
	private JProgressBar m_process = null;
	private JLabel m_status = null;
	private Container m_containter = null;
	private boolean bProcessing = false;
	
	public PStateBar() {
		this.m_process = new JProgressBar(0, 100);
		this.m_process.setVisible(false);
		this.m_process.setBackground(new Color(0, 0, 51));
		
		this.m_status = new JLabel("", JLabel.LEFT);
		this.m_status.setText(" Ready");
		this.m_status.setVisible(true);
		this.m_status.setFont(new Font("Verdana", Font.PLAIN, 10));
		this.m_status.setForeground(Color.green);
		
		this.setLayout(new GridLayout(1, 1));
		this.setBorder(BorderFactory.createLineBorder(Color.blue));
		this.setBackground(new Color(0, 0, 51));
		this.add(m_status);
	}
	
	/**
	 * Change the status bar into a processbar to 
	 * depict the proceduel is going on.
	 *
	 */
	public synchronized void startProcess() {
		this.bProcessing = true;
		if(null == m_containter) {
			m_containter = this.m_status.getParent();
		}
		if(null != m_containter) {
			this.m_process.setIndeterminate(true);
			this.m_process.setVisible(true);
			this.m_status.setVisible(false);
			m_containter.remove(this.m_status);
			m_containter.add(this.m_process);
		}
	}
	
	/**
	 * Change back to the processBar
	 *
	 */
	public synchronized void stopProcess() {
		this.bProcessing = false;
		if(null != m_containter) {
			this.m_process.setIndeterminate(false);
			this.m_process.setVisible(false);
			this.m_status.setVisible(true);
			this.m_containter.remove(this.m_process);
			this.m_containter.add(this.m_status);
			this.m_containter.repaint();
		}
	}
	
	public boolean isProcessing() {
		return bProcessing;
	}
	
	/**
	 * Set status text
	 * 
	 * @param text
	 */
	public void setStateText(String text) {
		this.m_status.setText(text);
	}
}
