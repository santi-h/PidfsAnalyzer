import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;

public class MainFrame extends JFrame implements ActionListener, AdjustmentListener {
	private static final long serialVersionUID = 1L;
	private static final String btnLogCommand = "btnLog";
	private static final String btnPlayCommand = "btnPlay";
	private static final String btnPauseCommand = "btnPause";
	private static final String timerCommand = "timer";
	private LogParser parser;
	private JPanel stacksPanel;
	private JTextField txtFile;
	private JButton btnPlay;
	private JButton btnPause;
	private JButton btnLog;
	private JScrollBar sclTime;
	private JTextField txtTimestamp;
	private JTextField txtIteration;
	private JTextField txtCutoff;
	private Timer timer;

	public MainFrame(String name) {
        super(name);
        addComponentsToPane(this.getContentPane());
        timer = new Timer(500,this);
        timer.setActionCommand(timerCommand);
        this.pack();
    }

    private void addComponentsToPane(final Container pane) {
	    pane.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
		c.ipady = 0;
		c.insets = new Insets(5,10,5,10);
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		c.weighty = 0;
		pane.add(createLogFilePanel(), c);
		
	    c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
		c.ipady = 0;
		c.insets = new Insets(5,10,5,10);
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		c.weighty = 0;
		pane.add(createTimePanel(), c);
	
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 0;
		c.ipady = 0;
		c.insets = new Insets(5,10,5,10);
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0;
		c.weighty = 0;
		pane.add(createInfoPanel(), c);
	
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 0;
		c.ipady = 0;
		c.insets = new Insets(5,10,5,10);
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		c.weighty = 1;
		stacksPanel = createStackPanel();
		JScrollPane stacksPanelScroll = new JScrollPane(stacksPanel);
		stacksPanelScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		stacksPanelScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		stacksPanel.setAutoscrolls(true);
    	pane.add(stacksPanelScroll, c);
	}

	private JPanel createLogFilePanel()
    {
    	JPanel ret = new JPanel(new GridBagLayout());
    	ret.setBorder(BorderFactory.createEtchedBorder());
    	
    	btnLog = new JButton("Log file");
    	btnLog.setActionCommand(btnLogCommand);
    	btnLog.addActionListener(this);
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 0;
    	c.weighty = 0;
    	ret.add(btnLog, c);
    	
    	JLabel label = new JLabel("File:");
    	c = new GridBagConstraints();
    	c.gridx = 1;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 0;
    	c.weighty = 0;
    	ret.add(label, c);
    	
    	txtFile = new JTextField();
    	txtFile.setEditable(false);
    	c = new GridBagConstraints();
    	c.gridx = 2;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 1;
    	c.weighty = 0;
    	ret.add(txtFile, c);
    	
    	return ret;
    }
    
    private JPanel createTimePanel()
    {
    	JPanel ret = new JPanel(new GridBagLayout());
    	ret.setBorder(BorderFactory.createEtchedBorder());
    	
    	JLabel label = new JLabel("Time:");
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 0;
    	c.weighty = 0;
    	ret.add(label, c);
    	
    	sclTime = new JScrollBar(JScrollBar.HORIZONTAL);
    	sclTime.addAdjustmentListener(this);
    	sclTime.setEnabled(false);
    	c = new GridBagConstraints();
    	c.gridx = 1;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 1;
    	c.weighty = 0;
    	ret.add(sclTime, c);

    	btnPause = new JButton("||");
    	btnPause.setActionCommand(btnPauseCommand);
    	btnPause.addActionListener(this);
    	btnPause.setToolTipText("Pause");
    	btnPause.setEnabled(false);
    	c = new GridBagConstraints();
    	c.gridx = 2;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 0;
    	c.weighty = 0;
    	ret.add(btnPause, c);

    	btnPlay = new JButton(">");
    	btnPlay.setActionCommand(btnPlayCommand);
    	btnPlay.addActionListener(this);
    	btnPlay.setEnabled(false);
    	btnPlay.setToolTipText("Play");
    	c = new GridBagConstraints();
    	c.gridx = 3;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 0;
    	c.weighty = 0;
    	ret.add(btnPlay, c);
    	
    	
    	return ret;
    }
    
    private JPanel createInfoPanel() {
    	JPanel ret = new JPanel(new GridBagLayout());
    	ret.setBorder(BorderFactory.createEtchedBorder());

    	JLabel label = new JLabel("Timestamp:");
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.LINE_START;
    	c.weightx = 0;
    	c.weighty = 0;
    	ret.add(label, c);
    	
    	txtTimestamp = new JTextField();
    	txtTimestamp.setEditable(false);
    	c = new GridBagConstraints();
    	c.gridx = 1;
    	c.gridy = 0;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipadx = 50;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 1;
    	c.weighty = 0;
    	ret.add(txtTimestamp, c);
    	
    	label = new JLabel("Iteration:");
    	c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 1;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.LINE_START;
    	c.weightx = 0;
    	c.weighty = 0;
    	ret.add(label, c);
    	
    	txtIteration = new JTextField();
    	txtIteration.setEditable(false);
    	c = new GridBagConstraints();
    	c.gridx = 1;
    	c.gridy = 1;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipadx = 50;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 1;
    	c.weighty = 0;
    	ret.add(txtIteration, c);
    	
    	label = new JLabel("Cutoff:");
    	c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 2;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.LINE_START;
    	c.weightx = 0;
    	c.weighty = 0;
    	ret.add(label, c);
    	
    	txtCutoff = new JTextField();
    	txtCutoff.setEditable(false);
    	c = new GridBagConstraints();
    	c.gridx = 1;
    	c.gridy = 2;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipadx = 50;
    	c.ipady = 0;
    	c.insets = new Insets(5,5,5,5);
    	c.anchor = GridBagConstraints.CENTER;
    	c.weightx = 1;
    	c.weighty = 0;
    	ret.add(txtCutoff, c);
    	
    	return ret;
    }
    
    private JPanel createStackPanel() {
    	JPanel ret = new JPanel(new GridBagLayout());
    	ret.setPreferredSize(new Dimension(this.getWidth(), 500));
    	ret.setBorder(BorderFactory.createEtchedBorder());
    	return ret;
    }

    private void addStacks( ) {
    	stacksPanel.removeAll();
    	//stacksPanel.setBackground(Color.cyan);
    	StackPanelProperties p = new StackPanelProperties();
    	p.actionsPad = 2;
    	p.actionWidth = 15;
    	p.insidePad = 5;
    	p.nActions = 5;
    	p.outsidePad = 5;
    	p.stateWidth = 120;

    	GridBagConstraints c;
    	JScrollPane scroll;
    	StackPanel stackPanel;
    	int panelwidth = 0;
    	for( int tid=0; tid<parser.getNThreads(); tid++) {
    		stackPanel = new StackPanel(parser.getStack(tid), p, new GridBagLayout());
    		c = new GridBagConstraints();
    		c.gridx = tid;
    		c.gridy = 0;
    		c.gridheight = 1;
    		c.gridwidth = 1;
    		c.fill = GridBagConstraints.VERTICAL;
    		c.ipadx = 0;
    		c.ipady = 0;
    		c.insets = new Insets(2,2,2,2);
    		c.anchor = GridBagConstraints.CENTER;
    		c.weightx = 0;
    		c.weighty = 1;
    		scroll = new JScrollPane(stackPanel);
    		int scrollwidth = stackPanel.getPreferredSize().width;
    		int scrollheight = stackPanel.getPreferredSize().height;
    		scroll.setMinimumSize(new Dimension(scrollwidth, scrollheight));
    		stackPanel.setAutoscrolls(true);
    		stacksPanel.add(scroll, c);
    		panelwidth += scrollwidth;
    	}
    	stacksPanel.setPreferredSize(new Dimension(panelwidth+50,200));
    	stacksPanel.revalidate();
    	stacksPanel.repaint();
    }

    private void btnLogAction(ActionEvent e) {
    	JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
    	int res = fc.showOpenDialog(this);
    	if(res == JFileChooser.APPROVE_OPTION) {
    		File file = fc.getSelectedFile();
    		System.out.println(file.getAbsolutePath());
    		try {
				parser = new LogParser( file);
				txtFile.setText(file.getCanonicalPath());
				btnPause.setEnabled(false);
				btnPlay.setEnabled(true);
				sclTime.setValues(-1, 0, -1, parser.getNTimestamps()-1);
				sclTime.setEnabled(true);
				addStacks( );
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    	}
    }
    
    private void btnPauseAction(ActionEvent e) {
    	timer.stop();
    	btnLog.setEnabled(true);
    	sclTime.setEnabled(true);
    	btnPause.setEnabled(false);
    	btnPlay.setEnabled(true);
    }
    
    private void btnPlayAction(ActionEvent e) {
    	if( sclTime.getValue() < sclTime.getMaximum()) {
    		btnLog.setEnabled(false);
    		sclTime.setEnabled(false);
    		btnPause.setEnabled(true);
    		btnPlay.setEnabled(false);
    		timer.start();
    	}
    }

    private void timerAction( ActionEvent e) {
    	if( sclTime.getValue() < sclTime.getMaximum()) {
    		sclTime.setValue(sclTime.getValue()+1);
    	}
    	else {
    		btnPauseAction( e);
    	}
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		if( cmd.equals(btnLogCommand))
			btnLogAction( e);
		else if( cmd.equals(btnPlayCommand))
			btnPlayAction( e);
		else if( cmd.equals(btnPauseCommand))
			btnPauseAction( e);
		else if( cmd.equals(timerCommand))
			timerAction( e);
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent a) {
		int value = a.getValue();
		int currentTime = parser.getCurrentTime();
		if( value > currentTime) {
			try {
				parser.advanceTime(value-currentTime);
				stacksPanel.repaint();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if( value < currentTime) {
			parser.reverseTime(currentTime-value);
			stacksPanel.repaint();
		}

		txtTimestamp.setText(String.valueOf(parser.getCurrentTime()));
		txtIteration.setText(String.valueOf(parser.getCurrentIteration()));

	}
}
