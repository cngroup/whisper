package vis.vjit.demo.ui.info;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import twitter4j.Status;
import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.util.IconUtil;

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
public class UserInfoItem extends JPanel {

	private static final long serialVersionUID = 3553911515139046554L;
	private Status m_status = null;
	private JLabel m_user = null;
	private JLabel m_time = null;
	private JTextArea m_text = null;
	private IconPanel m_logo = null;
	
	public UserInfoItem(Status s) {
		
		m_status = s;
		
		m_logo = new IconPanel(IconUtil.getICON(s.getUser()));
		
		m_user = new JLabel(s.getUser().getName());
		m_user.setBackground(Color.white);
		m_user.setForeground(Color.black);
		m_user.setOpaque(true);
		m_user.setFont(TweeFlowVjit.isJapan ? new Font("GulimChe", Font.BOLD, 15) : new Font("Verdana", Font.BOLD, 15));
		
		m_time = new JLabel(s.getCreatedAt().toString());
		m_time.setBackground(Color.white);
		m_time.setForeground(Color.black);
		m_time.setOpaque(true);
		m_time.setFont(new Font("Arial", Font.BOLD, 10));
			
		m_text = new JTextArea();
		m_text.setLineWrap(true);
		m_text.setEnabled(true);
		m_text.setEditable(false);
		m_text.setBorder(BorderFactory.createEmptyBorder());
		m_text.setBackground(Color.white);
		m_text.setForeground(Color.gray);
		m_text.setText(m_status.getText());
		m_text.setFont(TweeFlowVjit.isJapan ? new Font("GulimChe", Font.BOLD, 12) : new Font("Verdana", Font.BOLD, 12));
		m_text.setWrapStyleWord(true);
		m_text.setPreferredSize(new Dimension(100, 50));
		
		JPanel title = new JPanel();
		title.setLayout(new GridLayout(2, 1));
		title.add(m_user);
		title.add(m_time);
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(title, BorderLayout.NORTH);
		content.add(m_text, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(m_logo, BorderLayout.WEST);
		this.add(content, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(50, 70));
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
		this.doLayout();
	}
	
}
