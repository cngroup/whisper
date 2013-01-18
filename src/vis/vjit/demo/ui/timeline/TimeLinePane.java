package vis.vjit.demo.ui.timeline;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vis.vjit.demo.ui.Toolbar;
import vis.vjit.tweeflow.Constant;

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
public class TimeLinePane extends JPanel {

	private static final long serialVersionUID = 6916401843463007530L;

	private TimelineVjit m_vjit = null;
//	private JButton m_play = null;
	private JButton m_stop = null;
	private JButton m_foreward = null;
	private JButton m_backward = null;
	
	private JSpinner m_span = null;
//	private JSpinner m_interval = null;
	
	private Toolbar m_toolbar = null;

	public TimeLinePane() {

		m_vjit = new TimelineVjit();
		m_vjit.setBackground(Color.white);
		m_vjit.setWindowSize(Constant.TIME_WINDOW_SIZE);

		m_stop = new JButton(new ImageIcon("./stop.png"));
		m_stop.setBorder(null);
		m_stop.setBackground(Color.white);
		m_stop.setFocusPainted(false);
		m_stop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				m_vjit.fireStop();
			}			
		});

		m_foreward = new JButton(new ImageIcon("./foreward.png"));
		m_foreward.setBorder(null);
		m_foreward.setBackground(Color.white);
		m_foreward.setFocusPainted(false);
		m_foreward.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				m_vjit.foreward();
			}
		}); 

		m_backward = new JButton(new ImageIcon("./backward.png"));
		m_backward.setBorder(null);
		m_backward.setBackground(Color.white);
		m_backward.setFocusPainted(false);
		m_backward.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				m_vjit.backward();
			}			
		});
				
		Font font = new Font("Verdana", Font.PLAIN, 14);
		SpinnerNumberModel mspan = new SpinnerNumberModel();
		mspan.setMaximum(240);
		mspan.setMinimum(1);
		mspan.setStepSize(1);
		m_span = new JSpinner(mspan);
		m_span.setFont(font);
		m_span.setBackground(Color.black);
		m_span.setForeground(Color.white);
		m_span.setPreferredSize(new Dimension(50, 25));
		m_span.setValue(Constant.TIME_WINDOW_SIZE);
		m_span.setEnabled(true);
		m_span.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int v = (Integer) m_span.getValue();
				Constant.TIME_WINDOW_SIZE = v;
				Constant.TIME_INTERVAL = Math.round(Constant.TIME_BIN_INTEVAL * Constant.TIME_WINDOW_SIZE / Constant.TIME_SUB_WINDOW_SIZE);
				Constant.ACTIVE_TWEET_LIFE = Constant.TIME_SUB_WINDOW_SIZE;
				Constant.INACTIVE_TWEET_LIFE = Constant.ACTIVE_TWEET_LIFE / 2;
				m_vjit.setWindowSize(v);
			}
		});
		
//		SpinnerNumberModel minterval = new SpinnerNumberModel();
//		minterval.setMaximum(10);
//		minterval.setMinimum(1);
//		minterval.setStepSize(1);
//		m_interval = new JSpinner(minterval);
//		m_interval.setFont(font);
//		m_interval.setBackground(Color.black);
//		m_interval.setForeground(Color.white);
//		m_interval.setPreferredSize(new Dimension(35, 25));
//		m_interval.setValue(1);
//		m_interval.setEnabled(true);
//		m_interval.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				m_vjit.setInterval((Integer) m_interval.getValue() * 1000 * 60);
//			}
//		});
		
		JPanel front = new JPanel();
		front.setLayout(new GridLayout(1, 2));
//		front.add(m_play);
		front.add(m_stop);
		front.add(m_backward);
		front.add(m_foreward);
		front.setPreferredSize(new Dimension(100, 25));
		
		JLabel label1 = new JLabel("time window : ");
		label1.setFont(font);
		
		JLabel label2 = new JLabel(String.format("x %d min(s)", Math.round(Constant.TIME_BIN_INTEVAL / (60.0 * 1000))));
		label2.setFont(font);
		
		int x = 5;
		m_toolbar = new Toolbar();
		m_toolbar.setPreferredSize(new Dimension(250, 30));
		m_toolbar.setYSpace(3);
//		x = m_toolbar.place(label1, x, 95, 25);
		x = m_toolbar.place(label1, x, 105, 25);
		x = m_toolbar.place(m_span, x, 50, 25);
		x = m_toolbar.place(label2, x + 5, 85, 25);
		
		this.setLayout(new BorderLayout());
		this.add(m_vjit, BorderLayout.CENTER);
		this.add(front, BorderLayout.WEST);
		this.add(m_toolbar, BorderLayout.EAST);

		this.setBorder(null);
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(100, 30));
	}

	public TimelineVjit getTimelineVjit() {
		return m_vjit;
	}
	
	public void clear() {
		m_vjit.clear();
	}

}
