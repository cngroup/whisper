package vis.vjit.tweeflow.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import twitter4j.User;

public class IconUtil {

	protected static Map<Long, ImageIcon> m_iconpool = new HashMap<Long, ImageIcon>();
	
	public static synchronized ImageIcon getICON(User user) {
		ImageIcon icon = m_iconpool.get(user.getId());
		if(icon == null) {
			URL url = user.getProfileImageURL();
			try {
				icon = new ImageIcon(ImageIO.read(new BufferedInputStream(url.openStream())));
			} catch (Exception e) {
				icon = null;
			}
			if(icon != null) {
				m_iconpool.put(user.getId(), icon);
			}
		}
		return icon;
	}
	
	public static void remove(long id) {
		m_iconpool.remove(id);
	}
	
	
}
