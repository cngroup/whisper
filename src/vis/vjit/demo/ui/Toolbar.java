package vis.vjit.demo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

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
public class Toolbar extends JPanel {

	private static final long serialVersionUID = -2860562971429763839L;

	private int yspace = 5;
	
	public Toolbar() {
		
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(100, 32));
	}
	
	public int place(Component comp, int x, int w, int h) {		
		this.add(comp);
			
		SpringLayout layout = (SpringLayout)this.getLayout();

		SpringLayout.Constraints control = layout.getConstraints(comp);
		SpringLayout.Constraints panel = layout.getConstraints(this);
		
		control.setWidth(Spring.constant(w));
		control.setHeight(Spring.constant(h));
		control.setY(Spring.constant(yspace));
		control.setConstraint("West", Spring.constant(x));
		panel.setConstraint("South", Spring.sum(Spring.constant(yspace),
				control.getConstraint("South")));
		
		return x + w;
	}
	
	public void setYSpace(int y) {
		yspace = y;
	}
	
}
