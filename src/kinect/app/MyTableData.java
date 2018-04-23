package kinect.app;

public class MyTableData {
	private String bodyPart; //part of the upper limb (e.g. elbow)
	private float xPos; // x coordinate of upper limb
	private float yPos; // y coordinate of upper limb

	// constructor which creates a MyTableData object
	public MyTableData(String bodyPart, float xPos, float yPos) {
		this.bodyPart = bodyPart;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	
	// accessor methods which return field values
	public String getBodyPart() {
		return bodyPart;
	}

	public float getXPos() {
		return xPos;
	}

	public float getYPos() {
		return yPos;
	}
	
	// prints out a string representation of the object.
	@Override
	public String toString() {
		return "Body Part: " + bodyPart + "   " + xPos + "    " + yPos + "\n";
	}

}
