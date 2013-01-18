package vis.vjit.demo;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import twitter4j.Status;
import vis.vjit.demo.ui.AppMainMenuBar;
import vis.vjit.demo.ui.IMenuListener;
import vis.vjit.demo.ui.IQueryListener;
import vis.vjit.demo.ui.JSearchBar;
import vis.vjit.demo.ui.PStateBar;
import vis.vjit.demo.ui.Toolbar;
import vis.vjit.demo.ui.info.InfoList;
import vis.vjit.demo.ui.legend.ILegendSelectionListener;
import vis.vjit.demo.ui.legend.Legend;
import vis.vjit.demo.ui.threadline.ThreadLineVjit;
import vis.vjit.demo.ui.timeline.ITimelineListener;
import vis.vjit.demo.ui.timeline.TimeLinePane;
import vis.vjit.tweeflow.Config;
import vis.vjit.tweeflow.Constant;
import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.data.filter.SentimentFilter;
import vis.vjit.tweeflow.io.ITwitterSaver;
import vis.vjit.tweeflow.io.TweetInfo;
import vis.vjit.tweeflow.io.TwitterDBLoader;
import vis.vjit.tweeflow.io.TwitterDBSaver;
import vis.vjit.tweeflow.io.TwitterFileLoader;
import vis.vjit.tweeflow.io.TwitterMonitor;
import vis.vjit.tweeflow.io.TwitterProxy;
import vis.vjit.tweeflow.io.TwitterSearcher;
import vis.vjit.tweeflow.util.GraphicsUtil;
import vis.vjit.tweeflow.util.SentimentAnalyzer;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;
import vis.vjit.tweeflow.util.time.TimeHelper;
import davinci.data.elem.IElement;
import davinci.interaction.ActionAdapter;

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
public class TweeFlowDemo extends JFrame implements ITimelineListener,
		IMenuListener, IQueryListener {

	private static final long serialVersionUID = 1139986690663631264L;

	private TweeFlowVjit m_vjit = null;
	private InfoList m_list = null;
//	private WordleVjit m_wordle = null;
	private TimeLinePane m_timeline = null;
	private ThreadLineVjit m_thread = null;

	private TwitterMonitor m_monitor = null;
	private TwitterSearcher m_querier = null;
	private TwitterFileLoader m_fileloader = null;
	private TwitterDBLoader m_dbloader = null;

	private AppMainMenuBar m_menu = null;
	private Toolbar m_toolbar = null;
	private Toolbar m_filterbar = null;
	private PStateBar m_statebar = null;
	private JSearchBar m_query = null;
	// private JSpinner m_num1 = null;
	// private JSpinner m_num2 = null;
	private JComboBox m_layout1 = null;
	private JComboBox m_layout2 = null;

	private Legend[] legend = null;
	private String m_dataset = "";

	// filters
	private JCheckBox[] m_topics = null;
	private JSlider m_filter = null;
	private ActionListener m_tfilterAct = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(null == m_topics || m_topics.length == 0) {
				return;
			}
			
			List<String> topics = new ArrayList<String>();
			int ncnt = 0;
			for(int i = 0; i < m_topics.length; ++i) {
				if(m_topics[i].isSelected()) {
					ncnt ++;
					topics.add(m_topics[i].getText());
				}
			}
			if(ncnt < m_topics.length) {
				m_vjit.filter(topics.toArray(new String[]{}));
			} else {
				m_vjit.filter(null);
			}
		}		
	};

	public TweeFlowDemo(String dbname) {
		try {
			initcomponent(dbname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initcomponent(String dbname) throws Exception {
		
		TimeHelper.setTimeZom("EST");
		
		// UI Styles
		UIConfig.load("./ui.ini");

		// load API keys
		Config.load("./conf.ini");
		
		MediaType.load("./data/media.txt");

		// initialize dispatcher
		TwitterProxy.connectDB(dbname);
		TwitterProxy.connectTW();

		ITwitterSaver saver = new TwitterDBSaver();
		m_monitor = new TwitterMonitor(saver);
		m_querier = new TwitterSearcher(saver);
		m_fileloader = new TwitterFileLoader();
		m_dbloader = new TwitterDBLoader();

		// whisperflower
		m_vjit = new TweeFlowVjit();
		m_vjit.setBorder(null);
		m_vjit.addAction(new ActionAdapter() {
			public void elemEntered(IElement e, MouseEvent evn) {
				if (e instanceof VisTweet && ((VisTweet) e).isActive()) {
					VisTube[] tubes = ((VisTweet) e).getTubes();
					if (tubes.length > 0) {
						m_thread.clear();
						m_thread.update(tubes);
					}
				}
			}
		});

		// twitter infolist
		m_list = new InfoList();
		m_list.setBorder(BorderFactory.createEtchedBorder());
		
		// wordle
//		m_wordle = new WordleVjit();
//		m_wordle.setPreferredSize(new Dimension(350, 165));
		
		JPanel context = new JPanel();
		context.setLayout(new BorderLayout());
		context.add(m_list, BorderLayout.CENTER);
//		context.add(m_wordle, BorderLayout.NORTH);

		// timeline
		m_timeline = new TimeLinePane();
		m_timeline.setBorder(null);
		m_timeline.getTimelineVjit().addListener(this);

		// threadline
		m_thread = new ThreadLineVjit();
		m_thread.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
				Color.lightGray));
		m_thread.setPreferredSize(new Dimension(100, 100));

		JPanel threads = new JPanel();
		threads.setLayout(new BorderLayout());
		threads.add(m_thread, BorderLayout.CENTER);
		threads.add(m_timeline, BorderLayout.SOUTH);

		if (UIConfig.isWhite()) {
			m_vjit.setBackground(Color.white);
			m_thread.setBackground(Color.white);
			m_timeline.setBackground(Color.white);
			m_thread.setBackground(Color.white);
		} else {
			m_vjit.setBackground(Color.black);
			m_thread.setBackground(Color.black);
			m_timeline.setBackground(Color.black);
			m_thread.setBackground(Color.black);
		}

		// initialize monitor
		m_monitor.addListener(m_list);
		m_monitor.addListener(m_vjit);
		m_monitor.addListener(m_timeline.getTimelineVjit());
		m_monitor.addListener(m_thread);
		m_fileloader.addListener(m_list);
		m_fileloader.addListener(m_vjit);
		m_fileloader.addListener(m_timeline.getTimelineVjit());
		m_fileloader.addListener(m_thread);
		m_querier.addListener(m_list);
		m_querier.addListener(m_vjit);
		m_querier.addListener(m_timeline.getTimelineVjit());
		m_querier.addListener(m_thread);
		m_dbloader.addListener(m_list);
		m_dbloader.addListener(m_vjit);
		m_dbloader.addListener(m_timeline.getTimelineVjit());
		m_dbloader.addListener(m_thread);

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(m_vjit, BorderLayout.CENTER);
		main.add(threads, BorderLayout.SOUTH);
		main.setBorder(BorderFactory.createEtchedBorder(1));

		// init controls
		m_menu = new AppMainMenuBar(this);
		m_menu.addMenuListener(this);
		setJMenuBar(m_menu);

		// init status bar
		m_statebar = new PStateBar();

		// init toolbar
		m_toolbar = new Toolbar();
		m_query = new JSearchBar(new String[] { "monitor", "query" });
		m_query.addQueryListener(this);

		Font font = new Font("Verdana", Font.PLAIN, 14);
		String[] layouts = new String[] { "equal space", "longitude" };
		JLabel label3 = new JLabel("Layout:");
		label3.setFont(font);
		label3.setBackground(Color.white);
		m_layout1 = new JComboBox(layouts);
		m_layout1.setFont(font);
		m_layout1.setBackground(Color.white);
		m_layout1.setForeground(Color.black);
		m_layout1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_layout1.getSelectedIndex() == 0) {
					m_vjit.enableLongitude(false);
				} else {
					m_vjit.enableLongitude(true);
				}
			}
		});

		layouts = new String[] { "propogation", "convergence" };
		m_layout2 = new JComboBox(layouts);
		m_layout2.setFont(font);
		m_layout2.setBackground(Color.white);
		m_layout2.setForeground(Color.black);
		m_layout2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (m_layout2.getSelectedIndex() == 0) {
					m_vjit.isDiffusion = true;
				} else {
					m_vjit.isDiffusion = false;
				}
				
				m_vjit.doLayout();
				m_vjit.repaint();
			}
		});

		legend = new Legend[3];
		legend[0] = new Legend("positive", 0);
		legend[0].addListener(new ILegendSelectionListener(){
			public void select(boolean bselect) {
				if(bselect) {
					SentimentFilter.add(2);
				} else {
					SentimentFilter.remove(2);
				}
				m_vjit.filter();
			}			
		});
		legend[1] = new Legend("neutral", 1);
		legend[1].addListener(new ILegendSelectionListener(){
			public void select(boolean bselect) {
				if(bselect) {
					SentimentFilter.add(1);
				} else {
					SentimentFilter.remove(1);
				}
				m_vjit.filter();
			}			
		});
		legend[2] = new Legend("negative", 2);
		legend[2].addListener(new ILegendSelectionListener(){
			public void select(boolean bselect) {
				if(bselect) {
					SentimentFilter.add(0);
				} else {
					SentimentFilter.remove(0);
				}
				m_vjit.filter();
			}			
		});

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

//		setSize((int) (screenSize.width * 0.65),
//				(int) (screenSize.height * 0.75) + 10);
		setSize((int) (screenSize.width),
				(int) (screenSize.height));

		int size = (int) (screenSize.height * 0.75);

		int x = 5;
		x = m_toolbar.place(m_query, x, (int) (size * 0.4f), 25);
		x = m_toolbar.place(label3, x + 10, 60, 25);
		x = m_toolbar.place(m_layout1, x, 105, 25);
		x = m_toolbar.place(m_layout2, x, 105, 25);

		JLabel label = new JLabel(legend[0].getLabel() + ":");
		label.setFont(font);
		x = m_toolbar.place(label, x + 10, 65, 25);
		x = m_toolbar.place(legend[0], x, 25, 25);

		label = new JLabel(legend[1].getLabel() + ":");
		label.setFont(font);
		x = m_toolbar.place(label, x + 3, 60, 25);
		x = m_toolbar.place(legend[1], x, 25, 25);

		label = new JLabel(legend[2].getLabel() + ":");
		label.setFont(font);
		x = m_toolbar.place(label, x + 3, 70, 25);
		x = m_toolbar.place(legend[2], x, 25, 25);

		// ////////////////////////////////////////////////////////////////////
		// dynamic query slider
		font = new Font("Arial", Font.PLAIN, 12);
		final JLabel flabel = new JLabel("Influence Filter (followers > 0) : ");
		flabel.setFont(font);
		m_filter = new JSlider();
		m_filter.setMinimum(0);
		m_filter.setMaximum(10000);
		m_filter.setMinorTickSpacing(10);
		m_filter.setMajorTickSpacing(10);
		m_filter.setValue(1);
		m_filter.setSnapToTicks(true);
		m_filter.addChangeListener(new ChangeListener() {
			public synchronized void stateChanged(ChangeEvent e) {
				if (!m_filter.getValueIsAdjusting()) {
					flabel.setText(String.format("Influence Filter (followers > %5d) :",
							m_filter.getValue()));
					m_vjit.filter((double)m_filter.getValue());
				}
			}
		});
		m_filter.setFont(font);
		JPanel sliderbar = new JPanel();
		sliderbar.setLayout(new BorderLayout());
		sliderbar.add(flabel, BorderLayout.WEST);
		sliderbar.add(m_filter, BorderLayout.CENTER);
		
		// init filter bar
		x = 0;
		m_filterbar = new Toolbar();
		m_filterbar.setBackground(Color.black);
		x = m_filterbar.place(sliderbar, x, 450, 25);
		JLabel tlabel = new JLabel("Topic Filter : ");
		tlabel.setFont(font);
		tlabel.setBackground(Color.black);
		tlabel.setForeground(Color.white);
		m_filterbar.place(tlabel, x + 5, 100, 25);

		// screen styles
		m_filter.setBackground(Color.black);
		sliderbar.setBackground(Color.black);
		flabel.setBackground(Color.black);
		flabel.setForeground(Color.white);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(main, BorderLayout.CENTER);
		panel.add(m_toolbar, BorderLayout.NORTH);
		panel.add(m_filterbar, BorderLayout.SOUTH);

		JSplitPane spliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, context);
		spliter.setOneTouchExpandable(true);
		spliter.setDividerLocation((int) (getWidth() * 0.75));
		GraphicsUtil.setSplitPaneDividerColor(spliter, Color.white);

		// initialize window
		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent arg0) {
				System.out.println();
				if (m_monitor.isDispatching()) {
					m_monitor.shutdown();
				}
				if (m_querier.isDispatching()) {
					m_querier.shutdown();
				}
				if (m_fileloader.isDispatching()) {
					m_fileloader.shutdown();
				}
				if (m_dbloader.isDispatching()) {
					m_dbloader.shutdown();
				}
				try {
					TwitterProxy.disconnect();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2
				- windowHeight / 2);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(spliter, BorderLayout.CENTER);
		this.getContentPane().add(m_statebar, BorderLayout.SOUTH);
		this.setTitle("Twitter Monitor");
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public boolean step(final long start, final long duration) {
		if (null == m_dataset || "".equals(m_dataset)) {
			return false;
		}

		if (m_vjit.getActivityManager().isPerforming()) {
			return false;
		}

		new Thread() {
			public void run() {
				TwitterFlower flower = (TwitterFlower) m_vjit.getData("flower");
				if (flower == null) {
					return;
				}
				m_vjit.clear();
				String tlab = flower.topic().getLabel();
				String focus = flower.focus().getID();
				m_dbloader.shutdown();
				m_fileloader.shutdown();
				if (m_querier.isDispatching()) {
					m_querier.shutdown();
				}
				if (m_monitor.isDispatching()) {
					m_monitor.shutdown();
				}
				m_thread.clear();
				
				flower.topic().clear();
				flower.clear();
				flower = null;
				VisTopic topic = new VisTopic();
				topic.setID("t" + tlab);
				topic.setLabel(tlab);
				topic.setTime(start);
				flower = new TwitterFlower(topic);
				flower.setID("flower");
				m_vjit.addData(flower);
				String sql = String
						.format("select * from %s where status is not null and time >= %d and time < %d order by time",
								m_dataset, start, start + duration);
				try {
					flower.lock();
					double sentiments = 0;
					Status s = null;
					TweetInfo tinfo = null;
					GeoInfoV3 info = null;
					ResultSet rs = TwitterProxy.executeQuery(sql);
					while (rs.next()) {
						long time = rs.getLong(1);
						byte[] obj = rs.getBytes(2);
						ObjectInputStream in = new ObjectInputStream(
								new ByteArrayInputStream(obj));
						tinfo = (TweetInfo) in.readObject();
						in.close();
						s = tinfo.status;
						info = tinfo.geoinfo;
						if (info == null) {
							continue;
						}
						sentiments = SentimentAnalyzer.sentiment(s.getText());
//						m_thread.statusPosted(s, info, time, sentiments);
						m_vjit.postUpdate(topic, s, info, time, sentiments);
						
					}
					flower.unlock();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				flower.clean();
				FlowerNode f = flower.getNode(focus);
				if (f != null && f != flower.focus()) {
					flower.zoom((VisTopic) f);
				}
				m_vjit.doLayout();
			}
		}.start();
		return true;
	}

	public void play() {
	}

	public void stop() {
		m_dbloader.shutdown();
		m_fileloader.shutdown();
		if (m_monitor.isDispatching()) {
			m_monitor.shutdown();
		}
		if (m_querier.isDispatching()) {
			m_querier.shutdown();
		}
		m_vjit.stop();
	}

	public void pause() {
	}
	
	//////////////////////////////////////////////////////////////////
	// TODO: Menu Listener Implementation
	public void fileloaded(File file) {
		m_dbloader.shutdown();
		m_fileloader.shutdown();
		if (m_monitor.isDispatching()) {
			m_monitor.shutdown();
		}
		if (m_querier.isDispatching()) {
			m_querier.shutdown();
		}
		m_timeline.clear();
		m_list.clear();
		m_vjit.clear();
		String fname = file.getName();

		String id = fname.substring(0, fname.indexOf("-"));
		final VisTopic topic = new VisTopic();
		String stopic = id.replace("_", ",");
		stopic = stopic.replaceAll("0", " ");
		topic.setID("t-" + id);
		topic.setLabel(stopic);

		Font font = new Font("Verdana", Font.PLAIN, 12);
		String[] topics = id.split("_");
		int cnt = m_filterbar.getComponentCount();
		for(int i = 2; i < cnt; ++i) {
			m_filterbar.remove(2);
		}
		m_topics = new JCheckBox[topics.length];
		for (int i = 0, x = 525; i < topics.length; ++i, x += 5) {
			m_topics[i] = new JCheckBox(topics[i]);
			m_topics[i].setBackground(Color.black);
			m_topics[i].setForeground(Color.white);
			m_topics[i].setOpaque(true);
			m_topics[i].setFont(font);
			m_topics[i].addActionListener(m_tfilterAct);
			x = m_filterbar.place(m_topics[i], x, 80, 25);
		}
		m_filterbar.updateUI();

		TwitterFlower flower = new TwitterFlower(topic);
		flower.setID("flower");
		m_vjit.addData(flower);
		m_fileloader.load(file.getAbsolutePath(), Constant.LOADING_TIME_INTERVAL);
	}

	public void dbselected(String dbname) {
		m_dbloader.shutdown();
		m_fileloader.shutdown();
		if (m_monitor.isDispatching()) {
			m_monitor.shutdown();
		}
		if (m_querier.isDispatching()) {
			m_querier.shutdown();
		}
		m_timeline.clear();
		m_dataset = dbname;
		m_list.clear();
		m_vjit.clear();
		
//		m_wordle.setImage("./data/earthquake.png");
//		m_wordle.repaint();

		int sidx = dbname.indexOf("_") + 1;
		int eidx = dbname.lastIndexOf("_");
		String id = dbname.substring(sidx, eidx);
		final VisTopic topic = new VisTopic();
		String stopic = id.replace("_", ",");
		stopic = stopic.replaceAll("0", " ");
		topic.setID("t-" + id);
		topic.setLabel(stopic);

		Font font = new Font("Verdana", Font.PLAIN, 12);
		String[] topics = id.split("_");
		int cnt = m_filterbar.getComponentCount();
		for(int i = 2; i < cnt; ++i) {
			m_filterbar.remove(2);
		}
		m_topics = new JCheckBox[topics.length];
		for (int i = 0, x = 525; i < topics.length; ++i, x += 5) {
			m_topics[i] = new JCheckBox(topics[i]);
			m_topics[i].setBackground(Color.black);
			m_topics[i].setForeground(Color.white);
			m_topics[i].setOpaque(true);
			m_topics[i].setFont(font);
			m_topics[i].setSelected(true);
			m_topics[i].addActionListener(m_tfilterAct);
			x = m_filterbar.place(m_topics[i], x, 125, 25);
		}
		m_filterbar.updateUI();

		TwitterFlower flower = new TwitterFlower(topic);
		flower.setID("flower");
		m_vjit.addData(flower);
		m_dbloader.setStateBar(m_statebar);
		
		long interval = Constant.LOADING_TIME_INTERVAL;
		m_dbloader.load(dbname, 200);
	}
	
	///////////////////////////////////////////////////////////////////
	// TODO: Query Listener Implementation
	public void query(String choice, String querystr) {
		if ("".equals(querystr)) {
			return;
		}
		if ("monitor".equals(choice)) {
			System.out.println("start moningoring.....");

			stop();
			m_vjit.start();
			
			// m_num1.setValue(Constant.INACTIVE_TWEET_LIFE);
			// m_num2.setValue(Constant.ACTIVE_TWEET_LIFE);
			TwitterFlower flower = null;
			TwitterFlower f = (TwitterFlower) m_vjit.getData("flower");
			if (f != null) {
				f.lock();
			}
			m_timeline.clear();
			m_list.clear();
			m_vjit.clear();
			m_thread.clear();
			VisTopic topic = new VisTopic();
			topic.setID("t-" + querystr);
			topic.setLabel(querystr);
			flower = new TwitterFlower(topic);
			flower.setID("flower");
			m_vjit.addData(flower);
			if (f != null) {
				f.unlock();
				f.clear();
				f = null;
				System.gc();
			}
			
			Font font = new Font("Verdana", Font.PLAIN, 12);
			String[] topics = querystr.split("\\,");
			int cnt = m_filterbar.getComponentCount();
			for(int i = 2; i < cnt; ++i) {
				m_filterbar.remove(2);
			}
			m_topics = new JCheckBox[topics.length];
			for (int i = 0, x = 525; i < topics.length; ++i, x += 5) {
				m_topics[i] = new JCheckBox(topics[i]);
				m_topics[i].setBackground(Color.black);
				m_topics[i].setForeground(Color.white);
				m_topics[i].setOpaque(true);
				m_topics[i].setFont(font);
				m_topics[i].addActionListener(m_tfilterAct);
				x = m_filterbar.place(m_topics[i], x, 80, 25);
			}
			m_filterbar.updateUI();
			
			m_vjit.doLayout();
			m_vjit.repaint();
			m_dataset = m_monitor.monitor(querystr);
			m_statebar.setStateText(" Monitoring on \"" + querystr
					+ "\"...");
		} else {

			stop();

			TwitterFlower flower = null;
			TwitterFlower f = (TwitterFlower) m_vjit.getData("flower");
			if (f != null) {
				f.lock();
			}
			m_timeline.clear();
			m_list.clear();
			m_vjit.clear();
			m_thread.clear();
			VisTopic topic = new VisTopic();
			topic.setID("t-" + querystr);
			topic.setLabel(querystr);
			flower = new TwitterFlower(topic);
			flower.setID("flower");
			m_vjit.addData(flower);
			m_dataset = m_querier.query(querystr, 3, 100);
			if (f != null) {
				f.unlock();
				f.clear();
				f = null;
				System.gc();
			}
			m_vjit.doLayout();
			m_vjit.repaint();
			m_statebar.setStateText(" Query on \"" + querystr + "\"...");
		}
	}

	public void cancel() {
		if (m_monitor.isDispatching()) {
			System.out.println("stop monitoring...");
			stop();
			m_statebar.setStateText(" Ready");
		}
	}

	public static void main(String[] args) {
		if (null == args || args.length == 0) {
//			args = new String[] { "./data/tornado-20120305.db" };
//			args = new String[]{"./data/ipad_7M.db"};
//			args = new String[]{"./data/mittromney-20120306.db"};
			args = new String[]{"./data/cases/earthquake-20120315.db"};
//			args = new String[]{"./data/cases/all-20120307.db"};
//			args = new String[]{"./data/mytest.db"};
		}
		TweeFlowDemo demo = new TweeFlowDemo(args[0]);
		demo.setVisible(true);
	}
}
