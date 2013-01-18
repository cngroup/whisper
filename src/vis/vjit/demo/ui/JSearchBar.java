package vis.vjit.demo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import davinci.util.graphics.ColorLib;

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
public class JSearchBar extends JPanel implements DocumentListener,
		ActionListener {
	private Object m_lock;

	private JTextField m_queryF = new JTextField(15);
	private JLabel m_resultL = new JLabel("          ");
	private JLabel m_searchL = new JLabel(" Search >> ");
	private Box m_sbox = new Box(BoxLayout.X_AXIS);
	private JComboBox m_jfacets = null;

	private Color m_cancelColor = ColorLib.getColor(255, 75, 75);

	private boolean m_includeHitCount = false;
	private boolean m_monitorKeys = false;

	private boolean m_showBorder = false;
	private boolean m_showCancel = true;

	private List<IQueryListener> m_listeners = null;
	
	private String[] m_choices = null;

	/**
	 * Create a new JSearchPanel.
	 * 
	 * @param search
	 *            the search tuple set conducting the searches
	 * @param field
	 *            the data field being searched
	 */
	public JSearchBar(String[] choices) {
		m_choices = choices;
		m_listeners = new ArrayList<IQueryListener>();
		initUI();
	}

	public void addQueryListener(IQueryListener l) {
		m_listeners.add(l);
	}

	public void removeQueryListener(IQueryListener l) {
		m_listeners.remove(l);
	}

	// ------------------------------------------------------------------------
	// Visualization-based constructors

	private void initUI() {
		this.removeAll();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		Font font = new Font("Verdana", Font.PLAIN, 15);

		m_queryF.addActionListener(this);
		if (m_monitorKeys) {
			m_queryF.getDocument().addDocumentListener(this);
		}
		// m_queryF.setMaximumSize(new Dimension(400, 100));
		m_queryF.setPreferredSize(new Dimension(200, 25));
		m_queryF.setFont(font);
		m_queryF.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1,
				Color.lightGray));

		m_jfacets = new JComboBox(m_choices);
		m_jfacets.setBackground(Color.white);
		m_jfacets.setForeground(Color.black);
		m_jfacets.setPreferredSize(new Dimension(120, 25));
		m_jfacets.setFont(font);
		
//		JLabel query = new JLabel("Topic : ");
//		query.setFont(font);
//		query.setBackground(Color.white);
		m_sbox.removeAll();
		// m_sbox.add(Box.createHorizontalStrut(3));
//		m_sbox.add(query);
		m_sbox.add(m_jfacets);
		m_sbox.add(Box.createHorizontalStrut(2));
		m_sbox.add(m_queryF);
		m_sbox.add(Box.createHorizontalStrut(3));
		if (m_showCancel) {
			m_sbox.add(new CancelButton());
			m_sbox.add(Box.createHorizontalStrut(3));
		}
		if (m_showBorder) {
			m_sbox.setBorder(BorderFactory.createLineBorder(getForeground()));
		} else {
			m_sbox.setBorder(null);
		}
		m_sbox.setPreferredSize(new Dimension(210, 25));

		Box b = new Box(BoxLayout.X_AXIS);
		if (m_includeHitCount) {
			b.add(m_resultL);
			b.add(Box.createHorizontalStrut(10));
			// b.add(Box.createHorizontalGlue());
		}
		// b.add(m_searchL);
		// b.add(Box.createHorizontalStrut(3));
		b.add(m_sbox);

		this.setBackground(Color.white);
		this.setForeground(Color.black);

		this.add(b);
	}

	// ------------------------------------------------------------------------

	/**
	 * Request the keyboard focus for this component.
	 */
	public void requestFocus() {
		this.m_queryF.requestFocus();
	}

	/**
	 * Set the lock, an object to synchronize on while issuing queries.
	 * 
	 * @param lock
	 *            the synchronization lock
	 */
	public void setLock(Object lock) {
		m_lock = lock;
	}

	/**
	 * Indicates if the component should show the number of search results.
	 * 
	 * @param b
	 *            true to show the result count, false to hide it
	 */
	public void setShowResultCount(boolean b) {
		this.m_includeHitCount = b;
		initUI();
		validate();
	}

	/**
	 * Indicates if the component should show a border around the text field.
	 * 
	 * @param b
	 *            true to show the text field border, false to hide it
	 */
	public void setShowBorder(boolean b) {
		m_showBorder = b;
		initUI();
		validate();
	}

	/**
	 * Indicates if the component should show the cancel query button.
	 * 
	 * @param b
	 *            true to show the cancel query button, false to hide it
	 */
	public void setShowCancel(boolean b) {
		m_showCancel = b;
		initUI();
		validate();
	}

	/**
	 * Update the search results based on the current query.
	 */
	protected void fireSearch(String facet, String querystr) {
		if (null == querystr || "".equals(querystr)) {
			return;
		}

		for (IQueryListener l : m_listeners) {
			l.query(facet, querystr);
		}
	}

	protected void fireCancel() {
		for (IQueryListener l : m_listeners) {
			l.cancel();
		}
	}

	/**
	 * Set the query string in the text field.
	 * 
	 * @param query
	 *            the query string to use
	 */
	public void setQuery(String query) {
		Document d = m_queryF.getDocument();
		d.removeDocumentListener(this);
		m_queryF.setText(query);
		if (m_monitorKeys)
			d.addDocumentListener(this);
		fireSearch((String)m_jfacets.getSelectedItem(), query);
	}

	/**
	 * Get the query string in the text field.
	 * 
	 * @return the current query string
	 */
	public String getQuery() {
		return m_queryF.getText();
	}

	/**
	 * Set the fill color of the cancel 'x' button that appears when the button
	 * has the mouse pointer over it.
	 * 
	 * @param c
	 *            the cancel color
	 */
	public void setCancelColor(Color c) {
		m_cancelColor = c;
	}

	/**
	 * @see java.awt.Component#setBackground(java.awt.Color)
	 */
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (m_queryF != null)
			m_queryF.setBackground(bg);
		if (m_resultL != null)
			m_resultL.setBackground(bg);
		if (m_searchL != null)
			m_searchL.setBackground(bg);
	}

	/**
	 * @see java.awt.Component#setForeground(java.awt.Color)
	 */
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (m_queryF != null) {
			m_queryF.setForeground(fg);
			m_queryF.setCaretColor(fg);
		}
		if (m_resultL != null)
			m_resultL.setForeground(fg);
		if (m_searchL != null)
			m_searchL.setForeground(fg);
		if (m_sbox != null && m_showBorder)
			m_sbox.setBorder(BorderFactory.createLineBorder(fg));
	}

	/**
	 * @see javax.swing.JComponent#setOpaque(boolean)
	 */
	public void setOpaque(boolean opaque) {
		super.setOpaque(opaque);
		if (m_queryF != null) {
			m_queryF.setOpaque(opaque);
		}
		if (m_resultL != null)
			m_resultL.setOpaque(opaque);
		if (m_searchL != null)
			m_searchL.setOpaque(opaque);
	}

	/**
	 * @see java.awt.Component#setFont(java.awt.Font)
	 */
	public void setFont(Font f) {
		super.setFont(f);
		;
		if (m_queryF != null)
			m_queryF.setFont(f);
		if (m_resultL != null)
			m_resultL.setFont(f);
		if (m_searchL != null)
			m_searchL.setFont(f);
	}

	/**
	 * Set the label text used on this component.
	 * 
	 * @param text
	 *            the label text, use null to show no label
	 */
	public void setLabelText(String text) {
		m_searchL.setText(text);
	}

	/**
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent e) {
		fireSearch((String)m_jfacets.getSelectedItem(), m_queryF.getText().trim());
	}

	/**
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent e) {
		fireSearch((String)m_jfacets.getSelectedItem(), m_queryF.getText().trim());
	}

	/**
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e) {
		fireSearch((String)m_jfacets.getSelectedItem(), m_queryF.getText().trim());
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == m_queryF) {
			String str = m_queryF.getText().trim();
			fireSearch((String)m_jfacets.getSelectedItem(), str);
		}
	}

	/**
	 * A button depicted as an "X" that allows users to cancel the current query
	 * and clear the query field.
	 */
	public class CancelButton extends JComponent implements MouseListener {

		private boolean hover = false;
		private int[] outline = new int[] { 0, 0, 2, 0, 4, 2, 5, 2, 7, 0, 9, 0,
				9, 2, 7, 4, 7, 5, 9, 7, 9, 9, 7, 9, 5, 7, 4, 7, 2, 9, 0, 9, 0,
				7, 2, 5, 2, 4, 0, 2, 0, 0 };
		private int[] fill = new int[] { 1, 1, 8, 8, 1, 2, 7, 8, 2, 1, 8, 7, 7,
				1, 1, 7, 8, 2, 2, 8, 1, 8, 8, 1 };

		public CancelButton() {
			// set button size
			Dimension d = new Dimension(10, 10);
			this.setPreferredSize(d);
			this.setMinimumSize(d);
			this.setMaximumSize(d);

			// prevent the widget from getting the keyboard focus
			this.setFocusable(false);

			// add callbacks
			this.addMouseListener(this);
		}

		public void paintComponent(Graphics g) {
			if (hover) { // draw fill
				g.setColor(m_cancelColor);
				for (int i = 0; i + 3 < fill.length; i += 4) {
					g.drawLine(fill[i], fill[i + 1], fill[i + 2], fill[i + 3]);
				}
			}
			g.setColor(JSearchBar.this.getForeground());
			for (int i = 0; i + 3 < outline.length; i += 2) {
				g.drawLine(outline[i], outline[i + 1], outline[i + 2],
						outline[i + 3]);
			}
		}

		public void mouseClicked(MouseEvent arg0) {
			fireCancel();
			setQuery(null);
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
		}

		public void mouseEntered(MouseEvent arg0) {
			hover = true;
			repaint();
		}

		public void mouseExited(MouseEvent arg0) {
			hover = false;
			repaint();
		}

	} // end of class CancelButton

} // end of class JSearchPanel
