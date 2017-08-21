public class HarmfulDisk extends Disk {
	
	int type; // 0 for Stationary, 1 for vertical, 2 for Horizontal
	int speed;
	
	boolean stop = false;
	
	public HarmfulDisk(float r, float x, float y, String c, int s, int t){
		super(r, x, y, c);
		type = t;
		speed = s;
	}
	
	public void setStop(boolean b){
		stop = b;
	}
	
	public void setDirection(){
		int random = (int) (Math.random()*10);
		
		if(type == 1){
			if(random > 5){
				Down = true;
			} else{
				Up = true;
			}
		} else 
		if(type == 2){
			if(random > 5){
				Right = true;
			} else{
				Left = true;
			}
		}	
	}
	
	public void changeDirection(){
		if(Up){
			Up = false;
			Down = true;
			this.setCoordinates(this.getX(), this.getY());
		} else
		if(Down){
			Down = false;
			Up = true;
			this.setCoordinates(this.getX(), this.getY());
		} else
		if(Left){
			Left = false;
			Right = true;
			this.setCoordinates(this.getX(), this.getY());
		} else
		if(Right){
			Right = false;
			Left = true;
			this.setCoordinates(this.getX(), this.getY());
		}
	}
	
	public void movement(){
		if(!stop){		
			if(Up){
				changeY(-speed);
			} else
			if(Down){
				changeY(speed);
			} else
			if(Right){
				changeX(speed);
			} else
			if(Left){
				changeX(-speed);
			}
		}
	}
	
	// checkBounds method for harmful disks (so they change direction)
	public void checkBounds(){
		if (getX() + radius >= Game.GAME_WIDTH){
			Right = false;
			Left = true;
		}
		
		if (getX() - radius <= 0){
			Left = false;
			Right = true;
		}
		
		if (getY() + radius >= Game.GAME_HEIGHT){
			Down = false;
			Up = true;
		}
		
		if (getY() - radius <= 0){
			Up = false;
			Down = true;
		}
	}
}
