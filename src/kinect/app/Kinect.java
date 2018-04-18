package kinect.app;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

/*
 * Copyright 2011-2014, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * When this program is used for academic or research purposes, 
 * please cite the following article that introduced this Java library: 
 * 
 * A. Barmpoutis. "Tensor Body: Real-time Reconstruction of the Human Body 
 * and Avatar Synthesis from RGB-D', IEEE Transactions on Cybernetics, 
 * October 2013, Vol. 43(5), Pages: 1347-1356. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain this copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce this
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Kinect extends J4KSDK {

	ViewerPanel3D viewer = null;
	JLabel label = null;
	boolean mask_players = false;
	private TableModel tm = new TableModel();

	public void maskPlayers(boolean flag) {
		mask_players = flag;
	}

	public Kinect() {
		super();
	}

	public Kinect(byte type) {
		super(type);
	}

	public void setViewer(ViewerPanel3D viewer) {
		this.viewer = viewer;
	}

	public void setLabel(JLabel l) {
		this.label = l;
	}

	private boolean use_infrared = false;

	public void updateTextureUsingInfrared(boolean flag) {
		use_infrared = flag;
	}

	@Override
	public void onDepthFrameEvent(short[] depth_frame, byte[] player_index, float[] XYZ, float[] UV) {

		if (viewer == null || label == null)
			return;
		float a[] = getAccelerometerReading();
		label.setText(
				((int) (a[0] * 100) / 100f) + "," + ((int) (a[1] * 100) / 100f) + "," + ((int) (a[2] * 100) / 100f));
		DepthMap map = new DepthMap(getDepthWidth(), getDepthHeight(), XYZ);

		map.setMaximumAllowedDeltaZ(0.5);

		if (UV != null && !use_infrared)
			map.setUV(UV);
		else if (use_infrared)
			map.setUVuniform();
		if (mask_players) {
			map.setPlayerIndex(depth_frame, player_index);
			map.maskPlayers();
		}
		viewer.map = map;
	}

	@Override
	public void onSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
		if (viewer == null || viewer.skeletons == null)
			return;

		for (int i = 0; i < getSkeletonCountLimit(); i++) {
			viewer.skeletons[i] = Skeleton.getSkeleton(i, flags, positions, orientations, state, this);

			// DecimalFormat df = new DecimalFormat("#.###");

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

				int value = TheApp.getComboSelectedValue();

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
				/*
				 * System.out.printf("Elbow Left X Position: %f\n",
				 * viewer.skeletons[i].get3DJointX(Skeleton.ELBOW_LEFT));
				 * System.out.printf("Elbow Left Y Position: %f\n",
				 * viewer.skeletons[i].get3DJointY(Skeleton.ELBOW_LEFT));
				 * System.out.printf("Wrist Left X Position: %f\n",
				 * viewer.skeletons[i].get3DJointX(Skeleton.WRIST_LEFT));
				 * System.out.printf("Wrist Left Y position: %f\n",
				 * viewer.skeletons[i].get3DJointY(Skeleton.WRIST_LEFT));
				 * System.out.printf("Shoulder Left X Position: %f\n",
				 * viewer.skeletons[i].get3DJointX(Skeleton.SHOULDER_LEFT));
				 * System.out.printf("Shoulder Left Y Position: %f\n",
				 * viewer.skeletons[i].get3DJointY(Skeleton.SHOULDER_LEFT));
				 */

			}
		}

	}

	@Override
	public void onColorFrameEvent(byte[] data) {
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

	public TableModel getTableModel() {
		return tm;
	}

}
