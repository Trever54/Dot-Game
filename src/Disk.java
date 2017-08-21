public class Disk {
	
	Boolean Left = false;
	Boolean Right = false;
	Boolean Up = false;
	Boolean Down = false;
	
	float radius;
	float x;
	float y;
	String color;
	
	// Default Constructor
	public Disk(){
		this.radius = 0;
		this.x = 0;
		this.y = 0;
		this.color = "";
	}
	
	// Constructor that takes in all variables
	public Disk(float r, float x, float y, String c){
		this.radius = r;
		this.x = x;
		this.y = y;
		this.color = c;
	}
	
	public void setInfo(float r, float x, float y, String c){
		this.radius = r;
		this.x = x;
		this.y = y;
		this.color = c;
	}
	
	public void setRadius(float r){
		radius = r;
	}
	
	public void setCoordinates(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float getRadius(){
		return radius;
	}

	public float getY(){
		return y;
	}
	
	public float getX(){
		return x;
	}
	
	public String getColor(){
		return color;
	}

	public void changeX(int vel){
		this.x += vel;
	}
	
	public void changeY(int vel){
		this.y += vel;
	}
	
	// This method checks the bounds of the Game Window so that no Disk can leave the window
	public void checkBounds(){
		
		if (getX() + radius >= Game.GAME_WIDTH){
			Right = false;
		}
		
		if (getX() - radius <= 0){
			Left = false;
		}
		
		if (getY() + radius >= Game.GAME_HEIGHT){
			Down = false;
		}
		
		if (getY() - radius <= 0){
			Up = false;
		}
	}
}
