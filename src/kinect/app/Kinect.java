package kinect.app;

/*A. Barmpoutis. "Tensor Body: Real-time Reconstruction of the Human Body 
* and Avatar Synthesis from RGB-D', IEEE Transactions on Cybernetics, 
* October 2013, Vol. 43(5), Pages: 1347-1356. 
*/

import javax.swing.JLabel;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class Kinect extends J4KSDK {

	private ViewerPanel3D viewer = null;
	private JLabel label = null;
	private boolean mask_players = false;
	private TableModel tm = new TableModel();

	public void maskPlayers(boolean flag) {
		mask_players = flag;
	}

	public Kinect() {
		super();
	}

	// used to initialise kinect object for a
	// type of inect sensor version
	public Kinect(byte type) {
		super(type);
	}

	public void setViewer(ViewerPanel3D viewer) {
		this.viewer = viewer;
	}

	public void setLabel(JLabel l) {
		this.label = l;
	}

	// determine whether to use infrared
	private boolean use_infrared = false;

	public void updateTextureUsingInfrared(boolean flag) {
		use_infrared = flag;
	}

	@Override
	// This is a callback function that is called
	// whenever a depth frame is received
	// from the kinect sensor.
	public void onDepthFrameEvent(short[] depth_frame, byte[] player_index, float[] XYZ, float[] UV) {

		// checks to see if an instance of the ViewerPanel3D exists or a label
		// exists if not, the function returns to the calling function.
		if (viewer == null || label == null)
			return;
		float a[] = getAccelerometerReading();
		label.setText(
				((int) (a[0] * 100) / 100f) + "," + ((int) (a[1] * 100) / 100f) + "," + ((int) (a[2] * 100) / 100f));
		DepthMap map = new DepthMap(getDepthWidth(), getDepthHeight(), XYZ);

		map.setMaximumAllowedDeltaZ(0.5);

		// If a UV texture exists and a the use_infrared boolean is true,
		// the depth map is set to the UV texture. If UV texture exists then
		// then a uniform UV texture is generated for the depth map.
		if (UV != null && !use_infrared)
			map.setUV(UV);
		else if (use_infrared)
			map.setUVuniform();
		if (mask_players) {
			map.setPlayerIndex(depth_frame, player_index);
			map.maskPlayers();
		}

		// this sets the ViewerPanel3D's depth map to the DepthMap map.
		viewer.map = map;
	}

	@Override
	// callback function which executes when a new skeleton frame is received.
	public void onSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
		// checks to see if an instance of the ViewerPanel3D exists or
		// if a skeleton frame exists. If both exist then on
		// each skeleton frame, the ViewerPanel3D object
		// is updated with the position and orientation of
		// the skeleton from the received skeleton frame.
		if (viewer == null || viewer.skeletons == null)
			return;

		for (int i = 0; i < getSkeletonCountLimit(); i++) {
			viewer.skeletons[i] = Skeleton.getSkeleton(i, flags, positions, orientations, state, this);

			// The if statement is used to remove any zero values from the upper limb data.
			if (viewer.skeletons[i].get3DJointX(Skeleton.ELBOW_LEFT) != 0
					&& viewer.skeletons[i].get3DJointY(Skeleton.ELBOW_LEFT) != 0
					&& viewer.skeletons[i].get3DJointX(Skeleton.WRIST_LEFT) != 0
					&& viewer.skeletons[i].get3DJointY(Skeleton.WRIST_LEFT) != 0
					&& viewer.skeletons[i].get3DJointX(Skeleton.SHOULDER_LEFT) != 0
					&& viewer.skeletons[i].get3DJointY(Skeleton.SHOULDER_LEFT) != 0
					&& viewer.skeletons[i].get3DJointX(Skeleton.ELBOW_RIGHT) != 0
					&& viewer.skeletons[i].get3DJointY(Skeleton.ELBOW_RIGHT) != 0
					&& viewer.skeletons[i].get3DJointX(Skeleton.WRIST_RIGHT) != 0
					&& viewer.skeletons[i].get3DJointY(Skeleton.WRIST_RIGHT) != 0
					&& viewer.skeletons[i].get3DJointX(Skeleton.SHOULDER_RIGHT) != 0
					&& viewer.skeletons[i].get3DJointY(Skeleton.SHOULDER_RIGHT) != 0) {

				int value = KinectApp.getComboSelectedValue();

				/*
				 * gets the value (i.e. left arm or right arm) to choose which data to add to
				 * the table model. if user select left arm add data for left arm (wrist, elbow
				 * and shoulder) only and vice versa
				 */
				switch (value) {
				case 0:
					tm.addData(new MyTableData("Elbow Left", viewer.skeletons[i].get3DJointX(Skeleton.ELBOW_LEFT),
							viewer.skeletons[i].get3DJointY(Skeleton.ELBOW_LEFT)));
					tm.addData(new MyTableData("Wrist Left", viewer.skeletons[i].get3DJointX(Skeleton.WRIST_LEFT),
							viewer.skeletons[i].get3DJointY(Skeleton.WRIST_LEFT)));
					tm.addData(new MyTableData("Shoulder Left", viewer.skeletons[i].get3DJointX(Skeleton.SHOULDER_LEFT),
							viewer.skeletons[i].get3DJointY(Skeleton.SHOULDER_LEFT)));

					break;

				case 1:

					tm.addData(new MyTableData("Elbow Right", viewer.skeletons[i].get3DJointX(Skeleton.ELBOW_RIGHT),
							viewer.skeletons[i].get3DJointY(Skeleton.ELBOW_LEFT)));
					tm.addData(new MyTableData("Wrist Right", viewer.skeletons[i].get3DJointX(Skeleton.WRIST_RIGHT),
							viewer.skeletons[i].get3DJointY(Skeleton.WRIST_LEFT)));
					tm.addData(
							new MyTableData("Shoulder Right", viewer.skeletons[i].get3DJointX(Skeleton.SHOULDER_RIGHT),
									viewer.skeletons[i].get3DJointY(Skeleton.SHOULDER_LEFT)));

					break;
				}

			}
		}

	}

	// callback function that executes when new color frame is received
	@Override
	public void onColorFrameEvent(byte[] data) {
		/*
		 * checks to see if video texture exists or use_infrared is set to false if
		 * condition is satisfied, return to method body of calling method otherwise the
		 * ViewerPanel3D is updated with the new video texture.
		 */
		if (viewer == null || viewer.videoTexture == null || use_infrared)
			return;
		viewer.videoTexture.update(getColorWidth(), getColorHeight(), data);
	}

	@Override
	public void onInfraredFrameEvent(short[] data) {
		if (viewer == null || viewer.videoTexture == null || !use_infrared)
			return;
		int sz = getInfraredWidth() * getInfraredHeight();
		byte bgra[] = new byte[sz * 4];
		int idx = 0;
		int iv = 0;
		short sv = 0;
		byte bv = 0;
		for (int i = 0; i < sz; i++) {
			sv = data[i];
			iv = sv >= 0 ? sv : 0x10000 + sv;
			bv = (byte) ((iv & 0xfff8) >> 6);
			bgra[idx] = bv;
			idx++;
			bgra[idx] = bv;
			idx++;
			bgra[idx] = bv;
			idx++;
			bgra[idx] = 0;
			idx++;
		}

		viewer.videoTexture.update(getInfraredWidth(), getInfraredHeight(), bgra);
	}

	// returns the table model object.
	public TableModel getTableModel() {
		return tm;
	}

}
