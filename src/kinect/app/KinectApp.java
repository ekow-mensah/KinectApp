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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

import javax.swing.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import edu.ufl.digitalworlds.j4k.J4KSDK;

@SuppressWarnings("serial")
public class KinectApp extends JFrame {
	/* Field definitions */
	ViewerPanel3D viewer;
	Kinect myKinect;
	private JPanel dataPanel;
	private JPanel oldTable;
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
	private WindowListener exitWindowListener;
	JLabel accelerometer;

	/* constructor contains all the initialization code */
	public KinectApp() {

		setTitle("Kinect App");
		setSize(779, 768);
		// set the background colour of the pane.
		getContentPane().setBackground(Color.white);

		/*
		 * set to nothing on close so that the windowClosing method can handle the event
		 * when "X" button is clicked on the main frame.
		 */
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		exitWindowListener = new WindowAdapter() {
			/*
			 * creates JOptionPane and asks user if they want to quit if yes is selected
			 * application closes else the application keeps running
			 */
			@Override
			public void windowClosing(WindowEvent event) {
				int option = JOptionPane.showConfirmDialog(messageFrame, "Are you sure you want to quit?",
						"Exiting Application", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					System.exit(0);
				} else {
					messageFrame.dispose();
					System.out.println("I selected no");

				}

			}
		};

		// adds the window listener to the main frame
		addWindowListener(exitWindowListener);
		// used to get the dimensions of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// sets frame to centre of screen based on dimensions
		setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

		/* Initialises layout manager and adds layout to the frame */
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 38, 313, 39, 299, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 264, 60, 0, 264, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
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

		/* Initialises the Web camera */
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

		myKinect = new Kinect();

		/* if the Kinect sensor cannot initialise, a message is sent to the user */
		if (!myKinect.start(J4KSDK.DEPTH | J4KSDK.SKELETON | J4KSDK.COLOR | J4KSDK.XYZ | J4KSDK.PLAYER_INDEX)) {
			JOptionPane.showMessageDialog(messageFrame, "Please Check that the " + "Kinect is plugged in",
					"Could not initialise Kinect", JOptionPane.ERROR_MESSAGE);
		}
		accelerometer = new JLabel("0,0,0");

		/* initialises the Kinect viewer panel */
		viewer = new ViewerPanel3D();
		viewer.setShowVideo(false);
		myKinect.setViewer(viewer);
		myKinect.setLabel(accelerometer);
		myKinect.computeUV(true);
		myKinect.setNearMode(false);
		myKinect.setSeatedSkeletonTracking(true);
		myKinect.setColorResolution(640, 480);
		myKinect.setDepthResolution(640, 480);

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
		logControls.setBackground(Color.WHITE);
		GridBagConstraints gbc_logControls = new GridBagConstraints();
		gbc_logControls.insets = new Insets(0, 0, 5, 5);
		gbc_logControls.fill = GridBagConstraints.BOTH;
		gbc_logControls.gridx = 3;
		gbc_logControls.gridy = 2;
		getContentPane().add(logControls, gbc_logControls);

		btnStart = new JButton("Start");
		btnStart.setToolTipText("Starts recording the data " + "from the Kinect sensor");
		btnPause = new JButton("Pause");
		btnPause.setToolTipText("Stops the Kinect video footage" + " temporarily until unpased");
		btnStop = new JButton("Stop");
		btnStop.setToolTipText("Stops collecting data from" + " the Kinect sensor");

		/*
		 * when start button is clicked, it looks for Kinect studio application and
		 * launches it.
		 */
		btnStart.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					Process process = Runtime.getRuntime()
							.exec(new String[] { "C:\\Program Files\\Microsoft SDKs\\Kinect\\"
									+ "Developer Toolkit v1.8.0\\Tools\\" + "KinectStudio\\KinectStudio.exe" });
					System.out.println("hello");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(messageFrame,
							"Cannot find path to Kinect Studio. " + "Please install Kinect Studio on your machine.",
							"Could find Kinect Studio", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		/*
		 * when the pause button is clicked the stop method is called and kinect stops
		 * caturing video footage. when clicked again, The kinect sensor is
		 * re-initialised.
		 */
		btnPause.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (btnPause.getText().compareTo("Pause") == 0) {
					myKinect.stop();
					JOptionPane.showMessageDialog(messageFrame, "Kinect Video Stream Paused");
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

		/* stops kinect sensor and then shows user a message */
		btnStop.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				myKinect.stop();
				JOptionPane.showMessageDialog(messageFrame, "Stopped Kinect Feed", "Recording Stopped",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		/* adds buttons to the panel */
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

		/* initialises table */
		table = new JTable();
		table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		table.setModel(myKinect.getTableModel());
		dataPanel.setLayout(new BorderLayout());
		dataPanel.add(table, BorderLayout.CENTER);
		dataPanel.add(table.getTableHeader(), BorderLayout.NORTH);
		dataPanel.add(new JScrollPane(table));

		oldTable = new JPanel();
		GridBagConstraints gbc_logPanel = new GridBagConstraints();
		gbc_logPanel.insets = new Insets(0, 0, 20, 30);
		gbc_logPanel.fill = GridBagConstraints.BOTH;
		gbc_logPanel.gridx = 3;
		gbc_logPanel.gridy = 4;
		getContentPane().add(oldTable, gbc_logPanel);

		oldDataTable = new JTable();
		oldTable.setLayout(new BorderLayout());
		oldTable.add(oldDataTable, BorderLayout.CENTER);
		oldTable.add(oldDataTable.getTableHeader(), BorderLayout.NORTH);
		oldTable.add(new JScrollPane(oldDataTable));

		dataControlPanel = new JPanel();
		dataControlPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc_dataControlPanel = new GridBagConstraints();
		gbc_dataControlPanel.insets = new Insets(0, 0, 5, 5);
		gbc_dataControlPanel.fill = GridBagConstraints.BOTH;
		gbc_dataControlPanel.gridx = 1;
		gbc_dataControlPanel.gridy = 5;
		getContentPane().add(dataControlPanel, gbc_dataControlPanel);

		btnLoad = new JButton("Load");
		btnLoad.setToolTipText("Loads data from a CSV file into the table");
		btnSave = new JButton("Save");
		btnSave.setToolTipText("Saves the data in the table in a CSV file format");
		btnClear = new JButton("Clear");
		btnClear.setToolTipText("Clears the data displayed in the table");
		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Left Arm", "Right Arm" }));
		dataControlPanel.add(comboBox);

		/*
		 * creates a file chooser and gets user to input name of file. A time stamp is
		 * automatically added to the file and the print writer saves the file. A
		 * message is then sent to the user if the save was successful. If not
		 * successful An error message is shown to the user.
		 */
		btnSave.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				fc = new JFileChooser();
				fc.showSaveDialog(KinectApp.this);

				LocalDate date = LocalDate.now();
				try {
					pw = new PrintWriter(new File(fc.getSelectedFile() + date.toString() + ".csv"));
					for (MyTableData data : myKinect.getTableModel().getTableData()) {
						pw.write(data.toString());
					}
					JOptionPane.showMessageDialog(messageFrame, "Data Saved " + "Successfully");
				} catch (Exception writerException) {
					JOptionPane.showMessageDialog(messageFrame,
							"Please check that the file is named properly. "
									+ "Avoid using any special characters (e.g. -/:@)",
							"Error while saving file", JOptionPane.ERROR_MESSAGE);
				} finally {
					pw.close();
				}

			}
		});

		/*
		 * File chooser is displayed and user selects the file to load. The data in the
		 * file is parsed in order to add it to the appropriate columns in the table.
		 */
		btnLoad.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fc = new JFileChooser();
				fc.showOpenDialog(KinectApp.this);
				File dataFile = fc.getSelectedFile();
				oldDataModel = new TableModel();
				try {
					csvReader = new Scanner(dataFile);
					csvReader.useDelimiter("\n"); // separates file by new line.
					while (csvReader.hasNext()) {
						String bodyPart = "";
						float xPos = 0;
						float yPos = 0;

						// store each new line in string
						String dataString = csvReader.next();

						// parse the data to obtain each attribute for the table
						bodyPart = dataString.substring(10, 24).trim();
						xPos = Float.parseFloat(dataString.substring(24, 36).trim());
						yPos = Float.parseFloat(dataString.substring(40, dataString.length()).trim());

						// add the data to the table model
						oldDataModel.addData(new MyTableData(bodyPart, xPos, yPos));
					}

					oldDataTable.setModel(oldDataModel);

					// update the changes.
					oldDataModel.fireTableDataChanged();

				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(messageFrame,
							"The File could not be found. Please check to " + "see if file exists on the system.");
				} finally {
					csvReader.close();
				}
			}
		});

		/* deletes all the data in the table */
		btnClear.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				myKinect.getTableModel().deleteData();
				myKinect.getTableModel().fireTableDataChanged();
			}
		});

		dataControlPanel.add(btnLoad);
		dataControlPanel.add(btnSave);
		dataControlPanel.add(btnClear);

		setVisible(true);

	}

	/* returns an instance of the kinect class */
	public Kinect getKinect() {
		return myKinect;
	}

	public static int getComboSelectedValue() {
		return comboBox.getSelectedIndex();
	}

	public static void main(String[] args) {

		// launches the main application screen
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new KinectApp();
			}
		});
	}
}
