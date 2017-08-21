
public class PlayerDisk extends Disk{

	float startX;
	float startY;
	
	int speed;
	
	public PlayerDisk(float r, float x, float y, String c, int s){
		super(r, x, y, c);
		startX = 0;
		startY = 0;
		speed = s;
	}
	
	public void setLeft(Boolean b){
		Left = b;
	}
	
	public void setRight(Boolean b){
		Right = b;
	}
	
	public void setDown(Boolean b){
		Down = b;
	}
	
	public void setUp(Boolean b){
		Up = b;
	}
	
	public boolean getLeft(){
		return Left;
	}
	
	public boolean getRight(){
		return Right;
	}
	
	public boolean getDown(){
		return Down;
	}
	
	public boolean getUp(){
		return Up;
	}
	
	public void stop(){
		Left = false;
		Right = false;
		Up = false;
		Down = false;
	}
	
	public void setStartingCoordinates(float x, float y){
		startX = x;
		startY = y;
	}
	
	public float getStartX(){
		return startX;
	}
	
	public float getStartY(){
		return startY;
	}
	
	public void movement(){
		if(getLeft())
			changeX(-speed);
		
		if(getRight())
			changeX(speed);
		
		if(getUp())
			changeY(-speed);
		
		if(getDown())
			changeY(speed);
	}
	
	public void shallNotPass(){
		if (getLeft()){
			changeX(speed*2);
		} else
		if (getRight()){
			changeX(-speed*2);
		} else
		if (getUp()){
			changeY(speed*2);
		} else
		if (getDown()){
			changeY(-speed*2);
		}
		stop();	
	}
}
