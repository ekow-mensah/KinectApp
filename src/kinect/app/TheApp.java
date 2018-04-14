package kinect.app;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import edu.ufl.digitalworlds.j4k.J4KSDK;


@SuppressWarnings("serial")
public class TheApp extends JFrame {
	//VideoPanel viewer;
	ViewerPanel3D viewer;
	Kinect myKinect;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JPanel dataPanel;
	private JLabel lblWebcamStream;
	private JLabel lblKinectStream;
	private JLabel lblDataPanel;
	JLabel accelerometer;

	public TheApp() {

		setTitle("Main App");
		setSize(1280, 960);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 38, 313, 39, 299, 0 };
		gridBagLayout.rowHeights = new int[]{0, 264, 60, 0, 264, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		lblWebcamStream = new JLabel("Webcam Stream");
		GridBagConstraints gbc_lblWebcamStream = new GridBagConstraints();
		gbc_lblWebcamStream.insets = new Insets(0, 0, 5, 5);
		gbc_lblWebcamStream.gridx = 1;
		gbc_lblWebcamStream.gridy = 0;
		getContentPane().add(lblWebcamStream, gbc_lblWebcamStream);

		lblKinectStream = new JLabel("Kinect Stream");
		GridBagConstraints gbc_lblKinectStream = new GridBagConstraints();
		gbc_lblKinectStream.insets = new Insets(0, 0, 5, 0);
		gbc_lblKinectStream.gridx = 3;
		gbc_lblKinectStream.gridy = 0;
		getContentPane().add(lblKinectStream, gbc_lblKinectStream);

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
		accelerometer=new JLabel("0,0,0");
		myKinect.start(Kinect.DEPTH| Kinect.COLOR |Kinect.SKELETON |Kinect.XYZ|Kinect.PLAYER_INDEX);
		myKinect.computeUV(true);
		myKinect.setNearMode(false);
		myKinect.setSeatedSkeletonTracking(true);
		myKinect.setColorResolution(640, 480);
		myKinect.setDepthResolution(640, 480);
		myKinect.setViewer(viewer);
		myKinect.setLabel(accelerometer);

		GridBagConstraints gbc_kinectpanel = new GridBagConstraints();
		gbc_kinectpanel.fill = GridBagConstraints.BOTH;
		gbc_kinectpanel.insets = new Insets(0, 0, 0, 5);
		gbc_kinectpanel.gridx = 3;
		gbc_kinectpanel.gridy = 1;
		getContentPane().add(viewer, gbc_kinectpanel);

		lblDataPanel = new JLabel("Data Panel");
		GridBagConstraints gbc_lblDataPanel = new GridBagConstraints();
		gbc_lblDataPanel.insets = new Insets(0, 0, 5, 5);
		gbc_lblDataPanel.gridx = 1;
		gbc_lblDataPanel.gridy = 3;
		getContentPane().add(lblDataPanel, gbc_lblDataPanel);

		dataPanel = new JPanel();
		dataPanel.setBackground(Color.DARK_GRAY);
		GridBagConstraints gbc_dataPanel = new GridBagConstraints();
		gbc_dataPanel.fill = GridBagConstraints.BOTH;
		gbc_dataPanel.insets = new Insets(0, 0, 5, 5);
		gbc_dataPanel.gridx = 1;
		gbc_dataPanel.gridy = 4;
		getContentPane().add(dataPanel, gbc_dataPanel);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		setVisible(true);

	}

	public static void main(String[] args) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TheApp();
			}
		});
	}
}
