package data;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.esri.map.ArcGISTiledMapServiceLayer;
import com.esri.map.GraphicsLayer;
import com.esri.map.JMap;
import com.esri.map.LayerList;
import com.esri.toolkit.overlays.HitTestListener;
import com.esri.toolkit.overlays.HitTestOverlay;
import com.esri.map.MapEventListenerAdapter;


public class StateMap extends JPanel {

	JMap map;
	ArcGISTiledMapServiceLayer baseLayer;
	GraphicsLayer graphicsLayer;
	Map<String, Geometry> stateGeometry;
	Map<String, Integer> stateScore;
	Map<Integer, String> stateInt;
	StateSelecter stateSelecter;
	JButton btnAdvance;
	USA usa;
	String[] initialChoice;
	String selection;
	int dataSet;
	StateMap stateMap;
	Thread timeThread;
	boolean running;
	int turn;
	JTextField txtTitle;
	
	public StateMap(JFrame frame)
	{
		dataSet = 0;
		stateMap = this;
		running = false;
		turn = 0;
		
		map = new JMap();
		map.setWrapAroundEnabled(true);
		map.setExtent(new Envelope(-15000000, 2000000, -7000000, 8000000));
		baseLayer = new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer");
		stateGeometry = new HashMap<String, Geometry>();
		stateScore = new HashMap<String, Integer>();
		stateInt = new HashMap<Integer, String>();
		LayerList layers = map.getLayers();
		layers.add(baseLayer);

		// -----------------------------------------------------------------------------------------
		// Graphics Layer - to highlight a selected feature
		// -----------------------------------------------------------------------------------------
		graphicsLayer = new GraphicsLayer();
		layers.add(graphicsLayer);

		stateSelecter = new StateSelecter(this);
		final HitTestOverlay selectionOverlay = new HitTestOverlay(graphicsLayer, stateSelecter);
		map.addMapOverlay(selectionOverlay);


		//frame.getContentPane().add(this);

		//frame.getContentPane().add(createUI());
	}

	public void addStates()
	{
		QueryParameters query = new QueryParameters();

		// specify * to fetch all attributes.
		query.setOutFields(new String[] {"*"});
		// get information of the input state only.
		query.setWhere("1=1");
		QueryTask task = new QueryTask("http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Demographics/ESRI_Census_USA/MapServer/5");
		FeatureResult queryResult = null;
		try {
			queryResult = task.execute(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(queryResult == null)
		{
			System.out.println("NULL!");
		}
		System.out.println(queryResult.featureCount());
		Feature selectedState;
		Iterator<Object> it = queryResult.iterator();
		for(int i = 0; i < queryResult.featureCount(); i++ )
		{
			selectedState = (Feature) it.next();
			System.out.println(i + ":" + selectedState.getAttributeValue("STATE_ABBR"));
			stateGeometry.put((String)selectedState.getAttributeValue("STATE_ABBR"),
					GeometryEngine.project(selectedState.getGeometry(),
							SpatialReference.create(4269), map.getSpatialReference()));

			stateScore.put((String)selectedState.getAttributeValue("STATE_ABBR"), 255);

			updateStates();
		}
	}

	public int valueCorrect(int val)
	{
		double prop = val/100.0;
		int result = (int)(prop*255);
		return result;
	}

	public void setStateScore(String state, int score)
	{
		if(stateScore.containsKey(state))
		{
			stateScore.put(state, score);
		}
	}

	public void updateStates()
	{
		graphicsLayer.removeAll();
		stateInt = new HashMap<Integer, String>();
		
		if(usa != null)
		{
			HashMap<String, State> states = usa.getStates();
			for(Map.Entry<String, State> s : states.entrySet())
			{
				if(dataSet == 0)
				{
					double score = s.getValue().getHumanPop();
					score = score/s.getValue().getStartingHumanPop();
					score = score * 255.0;
					stateScore.put(s.getKey(), (int)score);
				}
				else if(dataSet == 1)
				{
					double score = 255.0*s.getValue().getHumanPop()/(s.getValue().getHumanPop() + s.getValue().getTotalmutantZombies());
					stateScore.put(s.getKey(), (int)score);
				}
				else if(dataSet == 2)
				{
					double score = 255.0*s.getValue().getTotalBunnies()/(s.getValue().getTotalBunnies() + s.getValue().getTotalmutantZombies() + 1);
					stateScore.put(s.getKey(), (int)score);
				}
			}
		}
		
		for(Map.Entry<String, Integer> a : stateScore.entrySet())
		{
			
			int score = a.getValue();

			Graphic graphic = new Graphic(stateGeometry.get(a.getKey()),
					new SimpleFillSymbol(new Color(255-score, score, 0, 150),
							new SimpleLineSymbol(Color.WHITE, 1)));
			
			stateInt.put(graphicsLayer.addGraphic(graphic), a.getKey());
		}
	}

	public JComponent createUI()
	{
		Dimension preferredSize = new Dimension(170, 30);

		// title
		txtTitle = new JTextField();
		txtTitle.setText("Turn: 0");
		txtTitle.setHorizontalAlignment(SwingConstants.CENTER);
		txtTitle.setFont(new Font(txtTitle.getFont().getName(), Font.PLAIN, 16));
		txtTitle.setPreferredSize(preferredSize);
		txtTitle.setMaximumSize(preferredSize);
		txtTitle.setMinimumSize(preferredSize);
		txtTitle.setBackground(new Color(0, 0, 0, 255));
		txtTitle.setForeground(Color.WHITE);
		txtTitle.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

		// drop-down list for the states
		JComboBox<String> cbxDataList = new JComboBox<>();
		cbxDataList.addItem("Human population");
		cbxDataList.addItem("Zombie to human ratio");
		cbxDataList.addItem("Zombie to bunny ratio");
		cbxDataList.setPreferredSize(preferredSize);
		cbxDataList.setMaximumSize(preferredSize);
		cbxDataList.setMinimumSize(preferredSize);
		// add a hook for the action to be taken when selected state changes
		cbxDataList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selected = (String)((JComboBox<String>) e.getSource()).getSelectedItem();
				// execute when button is pressed.
				if(selected.equals("Human population"))
				{
					dataSet = 0;
				}
				else if(selected.equals("Zombie to human ratio"))
				{
					dataSet = 1;
				}
				else if(selected.equals("Zombie to bunny ratio"))
				{
					dataSet = 2;
				}
				updateStates();
			}
		});

		// button for time advancement
		btnAdvance = new JButton("Begin");
		btnAdvance.setEnabled(false);
		btnAdvance.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(btnAdvance.getText().equals("Begin"))
				{
					usa = new USA(initialChoice);
					btnAdvance.setText("Advance Time");
					graphicsLayer.clearSelection();
					stateSelecter.setInitialChoice(false);
				}
				else
				{
					advanceTime();
				}
			}
		});
		btnAdvance.setPreferredSize(preferredSize);
		btnAdvance.setMaximumSize(preferredSize);
		btnAdvance.setMinimumSize(preferredSize);

		// group the above UI items into a panel
		final JPanel controlPanel = new JPanel();
		//BoxLayout boxLayout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
		//controlPanel.setLayout(boxLayout);

		controlPanel.setLayout(new BorderLayout());
		controlPanel.setLocation(10, 10);
		controlPanel.setSize(170, 95);
		controlPanel.setBackground(new Color(0, 0, 0, 100));
		controlPanel.setBorder(new LineBorder(Color.BLACK, 3));
		controlPanel.add(txtTitle, BorderLayout.NORTH);
		controlPanel.add(cbxDataList, BorderLayout.CENTER);
		controlPanel.add(btnAdvance, BorderLayout.SOUTH);

		return controlPanel;
	}

	public JMap getMap()
	{
		return this.map;
	}

	public void dispose()
	{
		map.dispose();
		//stateSelecter.dispose();
	}

	public void fivePicked(int[] picked)
	{
		if(picked != null && picked.length == 5)
		{
			initialChoice = new String[5];
			int i = 0;
			for(Map.Entry<Integer, String> a : stateInt.entrySet())
			{
				if(graphicsLayer.isGraphicSelected(a.getKey()))
				{
					initialChoice[i] = a.getValue();
					i++;
				}
				btnAdvance.setEnabled(true);
			}
		}
		else
		{
			initialChoice = null;
			btnAdvance.setEnabled(false);
		}
	}
	
	public void advanceTime()
	{
		usa.HappyNewYearToAll();
		turn++;
		txtTitle.setText("Turn: " + turn);
		txtTitle.repaint();
		updateStates();
	}

	public void select(int value)
	{
		selection = stateInt.get(value);
		usa.getStates().get(selection).openPieChart();
	}
	
	public static void setup()
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// create the UI, including the map, for the application.
					JFrame appWindow = new JFrame("BUNNIES!");
					appWindow.setBounds(100, 100, 1000, 800);
					appWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					// instance of this application
					StateMap stateMap = new StateMap(appWindow);
					appWindow.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent windowEvent) {
							super.windowClosing(windowEvent);
							stateMap.dispose();
						}
					});
					appWindow.setVisible(true);
					JPanel content = new JPanel();
					content.setLayout(new BorderLayout(0, 0));
					content.setBackground(Color.DARK_GRAY);
					content.add(stateMap.createUI());
					content.add(stateMap.getMap());
					appWindow.setContentPane(content);
					stateMap.addStates();

				} catch (Exception e) {
					// on any error, display the stack trace.
					e.printStackTrace();
				}
			}
		});
	}

	//public static void main(String[] args) {
	//	setup();
	//}

}
