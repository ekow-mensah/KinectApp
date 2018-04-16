package kinect.app;

public class MyTableData {
	private String bodyPart;
	private float xPos;
	private float yPos;
	
	public MyTableData(String bodyPart, float xPos, float yPos) {
		this.bodyPart = bodyPart;
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	public void setBodyPart(String bodyPart) {
		this.bodyPart = bodyPart;
	}
	
	public void setXPos(float xPos) {
		this.xPos = xPos;
	}
	
	public void setYPos (float yPos) {
		this.yPos = yPos;
	}
	
	public String getBodyPart() {
		return bodyPart;
	}
	
	public float getXPos() {
		return xPos;
	}
	
	public float getYPos() {
		return yPos;
	}
	
	@Override
	public String toString() {
		return "Body Part: " + bodyPart + "\n" + "xPos: " + xPos + "\n" + "yPos: " +  yPos + "\n";
	}

}
