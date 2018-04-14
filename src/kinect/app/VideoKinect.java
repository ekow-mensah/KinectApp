package kinect.app;

import edu.ufl.digitalworlds.j4k.J4KSDK;


public class VideoKinect extends J4KSDK {

	VideoPanel viewer = null;

	public void setViewer(VideoPanel viewer) {
		this.viewer = viewer;
	}

	@Override
	public void onColorFrameEvent(byte[] color_frame) {
		if (viewer == null || viewer.videoTexture == null)
			return;
		viewer.videoTexture.update(getColorWidth(), getColorHeight(), color_frame);
	}

	@Override
	public void onDepthFrameEvent(short[] depth_frame, byte[] playerIndex, float[] XYZ, float[] UV) {

	}

    @Override 
    public void onSkeletonFrameEvent(boolean[] skeleton_tracked, float[] joint_position, float[] joint_orientation, byte[] joint_status) { 
    	
            
    } 

}
