
package vis.vjit.demo.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

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
public class AppMainMenuBar extends JMenuBar {
	
	private JFrame m_owner = null;
	
	private JFileChooser m_fc = null;
	
	private DBChosenDlg m_dc = null;
	
	private List<IMenuListener> m_listeners = null;
	
	public AppMainMenuBar(JFrame owner) {
		m_listeners = new ArrayList<IMenuListener>();
		m_owner = owner;
		m_fc = new JFileChooser("./data/saves");
		m_fc.setDialogTitle("Data Loading");
		m_fc.setBackground(Color.white);
		m_fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName();
				if(name.lastIndexOf(".sav") < 0 && !f.isDirectory()) {
					return false;
				}
				return true;
			}
			public String getDescription() {
				return "monitoring";
			}
		});
		
		m_dc = new DBChosenDlg(owner);
		
		initComponent();
	}
	
	private void initComponent() {
		JMenu file = new JMenu("File");
		JMenuItem load1 = new JMenuItem("Load File...");
		load1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int value = m_fc.showOpenDialog(m_owner);
				if(value != JFileChooser.CANCEL_OPTION) {
					File file = m_fc.getSelectedFile();
					fireFileChoosed(file);
				}
			}
		});
		file.add(load1);
		
		JMenuItem load2 = new JMenuItem("Load DB...");
		load2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(m_dc.show()) {
					fireDBSelected(m_dc.dbname);
				}
			}
		});
		file.add(load2);
		
		
		file.addSeparator();
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		file.add(exit);

		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About...");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(m_owner, "WhisperFlower V1.0 \n Monitoring information diffusion in micro-blogs. \n Nan Cao : nancao@cse.ust.hk \n Hong Kong University of Science & Technology \n", "Version", 1);
			}
		});
		help.add(about);
		this.add(file);
		this.add(help);
	}
	
	public void addMenuListener(IMenuListener l) {
		m_listeners.add(l);
	}
	
	public void fireFileChoosed(File file) {
		for(IMenuListener l : m_listeners) {
			l.fileloaded(file);
		}
	}
	
	public void fireDBSelected(String dbname) {
		for(IMenuListener l : m_listeners) {
			l.dbselected(dbname);
		}
	}
}
