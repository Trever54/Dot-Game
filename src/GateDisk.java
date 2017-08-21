public class GateDisk extends Disk {
	
	//These are set when the disk is first created and do not change
	float initX;
	float initY;
	
	public GateDisk(float r, float x, float y, String c, int t){
		super(r, x, y, c);		
	}

	public void remove(){
		setCoordinates(-50, -50);
	}
	
	public void add(){
		setCoordinates(initX, initY);
	}
	
	public void setInitX(float ix){
		initX = ix;
	}
	
	public void setInitY(float iy){
		initY = iy;
	}
	
	public float getInitX(){
		return initX;
	}
	
	public float getInitY(){
		return initY;
	}
	
	public void setInitCoordinates(float ix, float iy){
		initX = ix;
		initY = iy;
	}
}
