package vis.vjit.demo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import vis.vjit.tweeflow.io.TwitterProxy;

/***
 * 
 * This piece of code is a joint research with Harvard University. 
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
public class DBChosenDlg {
		
	private JPanel inputPanel = null;
	private JFrame m_owner = null;

	public String dbname = "";
	
	private Font m_font = new Font("Arial", Font.PLAIN, 14);
	
	private JList m_list = null;

	private boolean bselected = false;
	
	private DefaultListModel m_model = null;

	public DBChosenDlg(JFrame frame) {
		this.m_owner = frame;
		initComponent();
	}

	public boolean show() {
		
		m_model.clear();
		String[] name = list();
		for(int i = 0; i < name.length; ++i) {
			m_model.addElement(name[i]);
		}
		
		String[] temp = { "Chose", "Delete", "Cancel" };
		int value = -1;
		do {
			value = JOptionPane.showOptionDialog(this.m_owner, this.inputPanel,
					"Dataset Selection", JOptionPane.DEFAULT_OPTION,
					-1, null, temp, temp[0]);
			if(value == 1 && checkParameter()) {
				dbname = (String)m_list.getSelectedValue();
				delete(dbname);
			}
		} while ((0 == value && !checkParameter()) || 1 == value);
		
		if (0 == value) {
			bselected = true;
			dbname = (String)m_list.getSelectedValue();
		} else if (1 == value) {
			
		} else {
			bselected = false;
		}
		return this.bselected;
	}

	private void initComponent() {
		
		m_model = new DefaultListModel();
		this.m_list = new JList(m_model);
		this.m_list.setFont(m_font);
		this.m_list.setBackground(Color.white);
		
		JScrollPane scroll = new JScrollPane(m_list);
		scroll.setBackground(Color.white);
		
		this.inputPanel = new JPanel(false);
		this.inputPanel.setLayout(new BorderLayout());
		this.inputPanel.add(scroll, BorderLayout.CENTER);	
		this.inputPanel.setBackground(Color.white);
	}

	private boolean checkParameter() {
		String v = (String)m_list.getSelectedValue();
		if(v == null || "".equals(v)) {
			JOptionPane.showMessageDialog(m_owner, "Please select a dataset",
					"Error", 0);
			return false;
		}
		return true;
	}
	
	private String[] list() {
		try {
			ResultSet rs = TwitterProxy
					.executeQuery("select * from catelog order by time");
			if (rs == null) {
				return null;
			}
			List<String> list = new ArrayList<String>();
			while (rs.next()) {
				list.add(rs.getString(2));
			}
			return list.toArray(new String[0]);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void delete(String dbname) {
		try {
			int idx = 0;
			for(int i = 0; i < m_model.getSize(); ++i) {
				String table = (String)m_model.getElementAt(i);
				if(dbname.equals(table)) {
					idx = i;
					break;
				}
			}
			m_model.remove(idx);
			TwitterProxy.executeUpdate(String.format("delete from catelog where name = '%s'", dbname));
			TwitterProxy.executeUpdate(String.format("drop table if exists %s;", dbname));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException {
		TwitterProxy.connectDB("./twitter.db");
		DBChosenDlg dlg = new DBChosenDlg(null);
		dlg.show();
		TwitterProxy.disconnect();
	}
}
