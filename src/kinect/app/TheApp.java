package kinect.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import edu.ufl.digitalworlds.j4k.J4KSDK;

@SuppressWarnings("serial")
public class TheApp extends JFrame {
	ViewerPanel3D viewer;
	Kinect myKinect;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JPanel dataPanel;
	private JPanel logPanel;
	private JPanel logControls;
	private JLabel lblWebcamStream;
	private JLabel lblKinectStream;
	private JLabel lblLiveData;
	private JLabel lblOldData;
	private JTable table;
	private JPanel dataControlPanel;
	private JButton btnLoad;
	private JButton btnSave;
	private JButton btnStart;
	private JButton btnPause;
	private JButton btnStop;
	private JButton btnClear;
	private JFrame messageFrame = new JFrame();
	private JFileChooser fc;
	private PrintWriter pw;
	private Scanner csvReader;
	private static JComboBox<String> comboBox;
	private TableModel oldDataModel;
	private JTable oldDataTable;
	
	JLabel accelerometer;
	ArrayList<MyTableData> tableData;

	public TheApp() {

		setTitle("Main App");
		setSize(779, 768);
		getContentPane().setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{38, 313, 39, 299, 0};
		gridBagLayout.rowHeights = new int[]{0, 264, 60, 0, 264, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		getContentPane().setLayout(gridBagLayout);

		lblWebcamStream = new JLabel("WebCam Stream");
		GridBagConstraints gbc_lblWebcamFeed = new GridBagConstraints();
		gbc_lblWebcamFeed.insets = new Insets(10, 0, 5, 5);
		gbc_lblWebcamFeed.gridx = 1;
		gbc_lblWebcamFeed.gridy = 0;
		getContentPane().add(lblWebcamStream, gbc_lblWebcamFeed);

		lblKinectStream = new JLabel("Kinect Stream");
		GridBagConstraints gbc_lblKinectFeed = new GridBagConstraints();
		gbc_lblKinectFeed.insets = new Insets(10, 0, 5, 0);
		gbc_lblKinectFeed.gridx = 3;
		gbc_lblKinectFeed.gridy = 0;
		getContentPane().add(lblKinectStream, gbc_lblKinectFeed);

		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		WebcamPanel webcamPanel = new WebcamPanel(webcam);
		webcamPanel.setFPSDisplayed(true);
		webcamPanel.setImageSizeDisplayed(true);
		webcamPanel.setMirrored(true);

		GridBagConstraints gbc_webcampanel = new GridBagConstraints();
		gbc_webcampanel.fill = GridBagConstraints.BOTH;
		gbc_webcampanel.insets = new Insets(0, 0, 5, 5);
		gbc_webcampanel.gridx = 1;
		gbc_webcampanel.gridy = 1;
		getContentPane().add(webcamPanel, gbc_webcampanel);

		viewer = new ViewerPanel3D();
		viewer.setShowVideo(false);
		myKinect = new Kinect();
		accelerometer = new JLabel("0,0,0");
		myKinect.start(J4KSDK.DEPTH | J4KSDK.SKELETON | J4KSDK.COLOR | J4KSDK.XYZ | J4KSDK.PLAYER_INDEX);
		myKinect.computeUV(true);
		myKinect.setNearMode(false);
		myKinect.setSeatedSkeletonTracking(true);
		myKinect.setColorResolution(640, 480);
		myKinect.setDepthResolution(640, 480);
		myKinect.setViewer(viewer);
		myKinect.setLabel(accelerometer);

		GridBagConstraints gbc_kinectpanel = new GridBagConstraints();
		gbc_kinectpanel.fill = GridBagConstraints.BOTH;
		gbc_kinectpanel.insets = new Insets(0, 0, 5, 30);
		gbc_kinectpanel.gridx = 3;
		gbc_kinectpanel.gridy = 1;
		getContentPane().add(viewer, gbc_kinectpanel);

		lblLiveData = new JLabel("Live Data");
		GridBagConstraints gbc_lblDataPanel = new GridBagConstraints();
		gbc_lblDataPanel.insets = new Insets(0, 0, 5, 5);
		gbc_lblDataPanel.gridx = 1;
		gbc_lblDataPanel.gridy = 3;
		getContentPane().add(lblLiveData, gbc_lblDataPanel);

		logControls = new JPanel();
		GridBagConstraints gbc_logControls = new GridBagConstraints();
		gbc_logControls.insets = new Insets(0, 0, 5, 0);
		gbc_logControls.fill = GridBagConstraints.BOTH;
		gbc_logControls.gridx = 3;
		gbc_logControls.gridy = 5;
		getContentPane().add(logControls, gbc_logControls);

		btnStart = new JButton("Start");
		btnPause = new JButton("Pause");
		btnStop = new JButton("Stop");

		btnStart.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					Process process = Runtime.getRuntime().exec(new String[] {
							"C:\\Program Files\\Microsoft SDKs\\Kinect\\Developer Toolkit v1.8.0\\Tools\\KinectStudio\\KinectStudio.exe" });
					System.out.println("hello");
				} catch (IOException e1) {
					System.out.println("\n");
					e1.printStackTrace();
				}
			}
		});

		btnPause.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (btnPause.getText().compareTo("Pause") == 0) {
					myKinect.stop();
					btnPause.setText("UnPause");
				} else {
					myKinect.start(J4KSDK.DEPTH | J4KSDK.SKELETON | J4KSDK.COLOR | J4KSDK.XYZ | J4KSDK.PLAYER_INDEX);
					myKinect.computeUV(true);
					myKinect.setNearMode(false);
					myKinect.setSeatedSkeletonTracking(true);
					myKinect.setColorResolution(640, 480);
					myKinect.setDepthResolution(640, 480);
					myKinect.setViewer(viewer);
					myKinect.setLabel(accelerometer);
					btnPause.setText("Pause");
				}
			}
		});
		btnStop.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				myKinect.stop();
				JOptionPane.showMessageDialog(messageFrame, "Stopped Kinect Feed", "Recording Stopped",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		logControls.add(btnStart);
		logControls.add(btnPause);
		logControls.add(btnStop);

		lblOldData = new JLabel("Old Data");
		GridBagConstraints gbc_lblLogDisplay = new GridBagConstraints();
		gbc_lblLogDisplay.insets = new Insets(0, 0, 5, 0);
		gbc_lblLogDisplay.gridx = 3;
		gbc_lblLogDisplay.gridy = 3;
		getContentPane().add(lblOldData, gbc_lblLogDisplay);

		dataPanel = new JPanel();
		dataPanel.setBackground(Color.DARK_GRAY);
		GridBagConstraints gbc_dataPanel = new GridBagConstraints();
		gbc_dataPanel.fill = GridBagConstraints.BOTH;
		gbc_dataPanel.insets = new Insets(0, 0, 20, 5);
		gbc_dataPanel.gridx = 1;
		gbc_dataPanel.gridy = 4;
		gbc_dataPanel.anchor = GridBagConstraints.SOUTH;
		getContentPane().add(dataPanel, gbc_dataPanel);

		table = new JTable();
		table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		table.setModel(myKinect.getTableModel());
		dataPanel.setLayout(new BorderLayout());
		dataPanel.add(table, BorderLayout.CENTER);
		dataPanel.add(table.getTableHeader(), BorderLayout.NORTH);
		dataPanel.add(new JScrollPane(table));

		logPanel = new JPanel();
		GridBagConstraints gbc_logPanel = new GridBagConstraints();
		gbc_logPanel.insets = new Insets(0, 0, 20, 30);
		gbc_logPanel.fill = GridBagConstraints.BOTH;
		gbc_logPanel.gridx = 3;
		gbc_logPanel.gridy = 4;
		getContentPane().add(logPanel, gbc_logPanel);
		
		oldDataTable = new JTable();
		logPanel.setLayout(new BorderLayout());
		logPanel.add(oldDataTable, BorderLayout.CENTER);
		logPanel.add(oldDataTable.getTableHeader(), BorderLayout.NORTH);
		logPanel.add(new JScrollPane(oldDataTable));

		dataControlPanel = new JPanel();
		GridBagConstraints gbc_dataControlPanel = new GridBagConstraints();
		gbc_dataControlPanel.insets = new Insets(0, 0, 5, 5);
		gbc_dataControlPanel.fill = GridBagConstraints.BOTH;
		gbc_dataControlPanel.gridx = 1;
		gbc_dataControlPanel.gridy = 5;
		getContentPane().add(dataControlPanel, gbc_dataControlPanel);

		btnLoad = new JButton("Load");
		btnSave = new JButton("Save");
		btnClear = new JButton("Clear");
		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Left Arm", "Right Arm" }));
		dataControlPanel.add(comboBox);

		btnSave.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				fc = new JFileChooser();
				fc.showSaveDialog(TheApp.this);
				
				LocalDate date = LocalDate.now();
				try {
					pw = new PrintWriter(new File(fc.getSelectedFile() + date.toString() + ".csv"));
					for (MyTableData data : myKinect.getTableModel().getTableData()) {
						pw.write(data.toString());
					}
					JOptionPane.showMessageDialog(messageFrame, "Data Saved Successfully");
				} catch (Exception writerException) {
					System.err.println(writerException.getMessage());
				} finally {
					pw.close();
				}

			}
		});

		btnLoad.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fc = new JFileChooser();
				fc.showOpenDialog(TheApp.this);
				File dataFile = fc.getSelectedFile();
				oldDataModel = new TableModel();
				try {
					csvReader = new Scanner(dataFile);
					csvReader.useDelimiter("\n");
					while (csvReader.hasNext()) {
						String bodyPart = "";
						float xPos = 0; 
						float yPos = 0;
						String dataString = csvReader.next();
						bodyPart = dataString.substring(10,24).trim();
						xPos = Float.parseFloat(dataString.substring(24,36).trim());
						yPos = Float.parseFloat(dataString.substring(40, dataString.length()).trim());
						oldDataModel.addData(new MyTableData(bodyPart, xPos, yPos));
					}
					
					oldDataTable.setModel(oldDataModel);
					oldDataModel.fireTableDataChanged();
					
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(messageFrame,
							"The File could not be found. Please check to see if file exists on the system.");
				} finally {
					csvReader.close();
				}
			}
		});

		btnClear.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				myKinect.getTableModel().deleteData();
				myKinect.getTableModel().fireTableDataChanged();
			}
		});

		dataControlPanel.add(btnLoad);
		dataControlPanel.add(btnSave);
		dataControlPanel.add(btnClear);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		setVisible(true);

	}

	public Kinect getKinect() {
		return myKinect;
	}

	public static int getComboSelectedValue() {
		return comboBox.getSelectedIndex();
	}

	public static void main(String[] args) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TheApp();
			}
		});
	}
}
