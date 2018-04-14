package kinect.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import edu.ufl.digitalworlds.j4k.J4KSDK;

@SuppressWarnings("serial")
public class TheApp extends JFrame  {
	VideoPanel viewer;
	//private ViewerPanel3D viewer_3D;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	
	public TheApp() {
		
		setTitle("Main App");
		setSize(1280,960);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{38, 313, 39, 299, 0};
		gridBagLayout.rowHeights = new int[]{0, 264, 60, 264, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		

		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		WebcamPanel webcamPanel = new WebcamPanel(webcam);
		webcamPanel.setFPSDisplayed(true);
		webcamPanel.setImageSizeDisplayed(true);
		webcamPanel.setMirrored(true);
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		getContentPane().add(webcamPanel, gbc_panel);
		
		viewer = new VideoPanel();
		VideoKinect myKinect = new VideoKinect();
		myKinect.start(J4KSDK.COLOR);
		myKinect.setViewer(viewer);
		
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 3;
		getContentPane().add(viewer, gbc_panel_2);
		
		
		
		
		//add(viewer);
		
		//add(webcamPanel, BorderLayout.SOUTH);
		
		
		//pack();
		
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
