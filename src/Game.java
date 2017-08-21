import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class Game {
	
	// The width and Height of the game window. Static because Disk classes use them on occasion
	public static int GAME_WIDTH = 1400;	
	public static int GAME_HEIGHT = 700;	

	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	
	private long window;
	
	// Game map images
	private BufferedImage map1 = null;
	private BufferedImage map2 = null;
	private BufferedImage map3 = null;
	
	// Declare Disks
	public PlayerDisk player1;
	public PlayerDisk player2;
	public EndDisk end;
	public SwitchDisk switch1;
	public SwitchDisk switch2;
	public GateDisk[] gate1 = new GateDisk[500];
	public GateDisk[] gate2 = new GateDisk[500];
	public HarmfulDisk[] harmful0 = new HarmfulDisk[500];
	public HarmfulDisk[] harmful1 = new HarmfulDisk[500];
	public HarmfulDisk[] harmful2 = new HarmfulDisk[500];
	
	// Boolean for control key
	Boolean ctrlKeyPressed = false;
	
	// used so that any arrays don't draw unnecessary disks
	int harmfulDiskAmount0;
	int harmfulDiskAmount1;
	int harmfulDiskAmount2;
	int gateDiskAmount1;
	int gateDiskAmount2;
	
	// used to keep track of the levels
	int currentLevel = 1;
	
	public void run(){
		System.out.println("System is Running");
		
		try {
			init();
			loop();
			
			// release window and window callbacks
			glfwDestroyWindow(window);
			keyCallback.release();	
		} finally {
			// terminate GLFW and release the GLFWerrorfun
			glfwTerminate();
			errorCallback.release();
		}
	}
	
	private void init() {
		// Setup Error Call Back
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
		
		// Initialize GLFW
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		
		// configure the window
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // will not stay hidden after created
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // will not be resizable
		
		// Create the Window
		window = glfwCreateWindow(GAME_WIDTH, GAME_HEIGHT, "Disk Game", NULL, NULL);
		if(window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		// setup a key callback (it will be called every time a key is pressed)
		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback(){
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods){
				// Escape Key Closes the Window
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, GL_TRUE); // This is detected in the rendering loop
			
				if (key == GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS){
					// switch between true and false upon pressing control
					if(ctrlKeyPressed){						
						ctrlKeyPressed = false;						
					} else {
						ctrlKeyPressed = true;
					}
					
					// stop each player
					player1.stop();
					player2.stop();			
				}
				
				if(!ctrlKeyPressed){
					// Arrow Keys that make the player move
					// Player 1 (Blue)
					if(key == GLFW_KEY_LEFT && action == GLFW_PRESS)
						player1.setLeft(true);	
					
					if(key == GLFW_KEY_LEFT && action == GLFW_RELEASE)
						player1.setLeft(false);
						
					if(key == GLFW_KEY_RIGHT && action == GLFW_PRESS)
						player1.setRight(true);
					
					if(key == GLFW_KEY_RIGHT && action == GLFW_RELEASE)
						player1.setRight(false);
					
					if(key == GLFW_KEY_UP && action == GLFW_PRESS)
						player1.setUp(true);
					
					if(key == GLFW_KEY_UP && action == GLFW_RELEASE)
						player1.setUp(false);
					
					if(key == GLFW_KEY_DOWN && action == GLFW_PRESS)
						player1.setDown(true);
					
					if(key == GLFW_KEY_DOWN && action == GLFW_RELEASE)
						player1.setDown(false);
					
				} else if(ctrlKeyPressed){	
					//Player 2 (Green) will move when control is held
					if(key == GLFW_KEY_LEFT && action == GLFW_PRESS)
						player2.setLeft(true);	
					
					if(key == GLFW_KEY_LEFT && action == GLFW_RELEASE)
						player2.setLeft(false);
						
					if(key == GLFW_KEY_RIGHT && action == GLFW_PRESS)
						player2.setRight(true);
					
					if(key == GLFW_KEY_RIGHT && action == GLFW_RELEASE)
						player2.setRight(false);
					
					if(key == GLFW_KEY_UP && action == GLFW_PRESS)
						player2.setUp(true);
					
					if(key == GLFW_KEY_UP && action == GLFW_RELEASE)
						player2.setUp(false);
					
					if(key == GLFW_KEY_DOWN && action == GLFW_PRESS)
						player2.setDown(true);
					
					if(key == GLFW_KEY_DOWN && action == GLFW_RELEASE)
						player2.setDown(false);				
				}
			}	
		});
		
		// Get the resolution of the primary monitor
		ByteBuffer center = glfwGetVideoMode(glfwGetPrimaryMonitor());	
		// Center the window
		glfwSetWindowPos(window, (GLFWvidmode.width(center) - GAME_WIDTH) / 2, (GLFWvidmode.height(center) - GAME_HEIGHT) / 2);
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// enable v-sync
		glfwSwapInterval(1);
		// Make the window visible
		glfwShowWindow(window);

		
		//Load BufferedImages
		BufferedImageLoader loader = new BufferedImageLoader();
		try{
			map1 = loader.loadImage("/level1.png");
			map2 = loader.loadImage("/level2.png");
			map3 = loader.loadImage("/level3.png");
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		// set up Disks
		initDisks();
		
		// set up the Level
		setUpLevel();
		
	}
	
	private void loop(){
		GLContext.createFromCurrent(); // Critical Line
		
		// set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// init OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, GAME_WIDTH, GAME_HEIGHT, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		// run the rendering loop until the user closes the window or presses ESCAPE
		while(glfwWindowShouldClose(window) == GL_FALSE){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer	
			
			// Draw all disks
			drawDisk(switch1);
			drawDisk(switch2);
			drawDisk(end);
			drawDisk(player1);
			drawDisk(player2);
			
			for (int i = 0; i < gateDiskAmount1; i++){
				drawDisk(gate1[i]);
			}
			
			for (int i = 0; i < gateDiskAmount2; i++){
				drawDisk(gate2[i]);
			}
			
			for	(int i = 0; i < harmfulDiskAmount0; i++){
				drawDisk(harmful0[i]);
			}
			
			for (int i = 0; i < harmfulDiskAmount1; i++){
				drawDisk(harmful1[i]);
			}
			
			for (int i = 0; i < harmfulDiskAmount2; i++){
				drawDisk(harmful2[i]);
			}

			// make sure the player can't leave the boundaries of the game map
			player1.checkBounds();
			player2.checkBounds();
			
			// check the boundaries so that harmful disks cannot leave either the game map
			for (int i = 0; i < harmfulDiskAmount1; i++){
				harmful1[i].checkBounds();
			}
			
			for (int i = 0; i < harmfulDiskAmount2; i++){
				harmful2[i].checkBounds();
			}
			
			// set up the movement for disks
			player1.movement();
			player2.movement();
			
			// Control key either stops or starts the moving harmful disks
			if(!ctrlKeyPressed){			
				for (int i = 0; i < harmfulDiskAmount1; i++){
					harmful1[i].setStop(false);
					harmful1[i].movement();
				}
				
				for (int i = 0; i < harmfulDiskAmount2; i++){
					harmful2[i].setStop(true);
				}	
					
				} else
			if(ctrlKeyPressed){					
				for (int i = 0; i < harmfulDiskAmount2; i++){
					harmful2[i].setStop(false);
					harmful2[i].movement();
				}	
				
				for (int i = 0; i < harmfulDiskAmount1; i++){
					harmful1[i].setStop(true);
				}	
			}
			
			// check for collision detection
			collisionDetection();
			
			glfwSwapBuffers(window); //swap the color buffers
			glfwPollEvents(); // Poll for window events
			
			
		}	
	}
	
	public void drawDisk(Disk d){
		
		// Take information from the disk passed in
		float cx = d.getX();
		float cy = d.getY();
		float r = d.getRadius();
		String color = d.getColor();
		
		int num_segments = 300; // will effect how smooth the circle appears
		
		// set the color of the disk
		switch (color) {
			// Player1
			case "BLUE":
				GL11.glColor3f(0.0f, 0.0f, 1.0f);	
				break;
			// Player 2
			case "DARK_BLUE":
				GL11.glColor3f(0.0f, 0.0f, 0.5f);
				break;
			// Harmful Disks
			case "ORANGE":
				GL11.glColor3f(1.0f, 0.3f, 0.0f);
				break;
			case "RED":
				GL11.glColor3f(1.0f, 0.0f, 0.0f);
				break;
			case "DARK_RED":
				GL11.glColor3f(0.5f, 0.0f, 0.0f);
				break;
			// Switch Disks
			case "CYAN":
				GL11.glColor3f(0.0f, 1.0f, 1.0f);
				break;
			case "DARK_GREEN":
				GL11.glColor3f(0.0f, 0.5f, 0.5f);
				break;	
			// End of Level Disk
			case "WHITE":
				GL11.glColor3f(1.0f, 1.0f, 1.0f);
				break;
			default:
				GL11.glColor3f(0.0f, 0.0f, 0.0f); // Black for default
		}
		
		GL11.glBegin(GL11.GL_POLYGON);
		for(int ii = 0; ii < num_segments; ii++){
			
			float theta = 2.0f * 3.1415926f * (ii) / (num_segments); // get the current angle
			
			float x = (float) (r * Math.cos(theta)); // calc the x component
			float y = (float) (r * Math.sin(theta)); // calc the y component
			
			GL11.glVertex2f(x + cx, y + cy); // output vertex	
		}
		GL11.glEnd();
	}

	public Boolean checkTouching(Disk d1, Disk d2){
		
		float d1x = d1.getX();
		float d1y = d1.getY();
		float d1r = d1.getRadius();
		
		float d2x = d2.getX();
		float d2y = d2.getY();
		float d2r = d2.getRadius();
		
		double distance;
		
		distance = Math.sqrt( (Math.pow((d2x-d1x), 2)) + (Math.pow((d2y-d1y), 2)));
		
		if(distance <= (d1r + d2r + 0.01) ){
			return true;
		} else {
			return false;
		}
		
	}
	
	public void collisionDetection(){
		
		boolean touching = false;
		boolean touching2 = false;

		// Each loop checks if two disk types are touching, and what to do as a result of them touching
		// Orange Disk and Player1: set player 1 back to starting position
		for(int i = 0; i < harmfulDiskAmount0; i++){
			touching = checkTouching(player1, harmful0[i]);
			if(touching){
				player1.setCoordinates(player1.getStartX(), player1.getStartY());
			}
		}
		
		// Orange Disk and Player2: Set Player2 back to starting position
		for(int i = 0; i < harmfulDiskAmount0; i++){
			touching = checkTouching(player2, harmful0[i]);
			if(touching){
				player2.setCoordinates(player2.getStartX(), player2.getStartY());
			}
		}
		
		// Red Disk and player1: Set Player1 back to starting position
		for(int i = 0; i < harmfulDiskAmount1; i++){
			touching = checkTouching(player1, harmful1[i]);
			if(touching){
				player1.setCoordinates(player1.getStartX(), player1.getStartY());
			}
		}
		
		// Red Disk and player2: Set Player2 back to starting position
		for(int i = 0; i < harmfulDiskAmount1; i++){
			touching = checkTouching(player2, harmful1[i]);
			if(touching){
				player2.setCoordinates(player2.getStartX(), player2.getStartY());
			}
		}
		
		// Dark Red Disk and player1: Set Player1 back to starting position
		for(int i = 0; i < harmfulDiskAmount2; i++){
			touching = checkTouching(player1, harmful2[i]);
			if(touching){
				player1.setCoordinates(player1.getStartX(), player1.getStartY());
			}
		}
		
		// Dark Red Disk and player2: Set Player2 back to starting position
		for(int i = 0; i < harmfulDiskAmount2; i++){
			touching = checkTouching(player2, harmful2[i]);
			if(touching){
				player2.setCoordinates(player2.getStartX(), player2.getStartY());
			}
		}
		
		// Dark Red Disk and a Red Disk: Change direction of both disks
		for(int i = 0; i < harmfulDiskAmount1; i++){
			for(int n = 0; n < harmfulDiskAmount2; n++){
				touching = checkTouching(harmful1[i], harmful2[n]);
				
				if(touching){
					harmful1[i].changeDirection();
					harmful2[n].changeDirection();
				}	
			}	
		}
		
		// Dark Red Disk and Orange Disk: Change direction of Dark Red Disk
		for(int i = 0; i < harmfulDiskAmount0; i++){
			for(int n = 0; n < harmfulDiskAmount2; n++){
				touching = checkTouching(harmful0[i], harmful2[n]);
				
				if(touching){
					harmful2[n].changeDirection();
				}	
			}	
		}
		
		// Dark Red Disk and Orange Disk: Change direction of Dark Red Disk
		for(int i = 0; i < harmfulDiskAmount0; i++){
			for(int n = 0; n < harmfulDiskAmount1; n++){
				touching = checkTouching(harmful0[i], harmful1[n]);
				
				if(touching){
					harmful1[n].changeDirection();
				}	
			}	
		}
		
		// Gate 1 and Player 1: Don't let player 1 pass through Gate 1
		for(int i = 0; i < gateDiskAmount1; i++){
			touching = checkTouching(player1, gate1[i]);
			
			if(touching){
				player1.shallNotPass();
			}		
		}
		
		// Gate 1 and Player 2: Don't let player 2 pass through Gate 1
		for(int i = 0; i < gateDiskAmount1; i++){
			touching = checkTouching(player2, gate1[i]);
			
			if(touching){
				player2.shallNotPass();
			}		
		}	
		
		// Gate 2 and Player 1: Same as last two methods
		for(int i = 0; i < gateDiskAmount2; i++){
			touching = checkTouching(player1, gate2[i]);
			
			if(touching){
				player1.shallNotPass();
			}		
		}
		
		//Gate 2 and Player 2
		for(int i = 0; i < gateDiskAmount2; i++){
			touching = checkTouching(player2, gate2[i]);
			
			if(touching){
				player2.shallNotPass();
			}		
		}
		
		//player 1 or 2 and switch 1: disable all of gate1
		touching = checkTouching(player1, switch1);
		touching2 = checkTouching(player2, switch1);
		
		if(touching || touching2) {
			for(int i = 0; i < gateDiskAmount1; i++){
				gate1[i].remove();
			}
		} else {
			for(int i = 0; i < gateDiskAmount1; i++){
				gate1[i].add();
			}
		}
		
		//player 1 or 2 and switch 1: disable all of gate1
		touching = checkTouching(player1, switch2);
		touching2 = checkTouching(player2, switch2);
		
		if(touching || touching2) {
			for(int i = 0; i < gateDiskAmount2; i++){
				gate2[i].remove();
			}
		} else {
			for(int i = 0; i < gateDiskAmount2; i++){
				gate2[i].add();
			}
		}
		
		//player 1 or 2 and End Disk: Finish that level
		touching = checkTouching(player1, end);
		touching2 = checkTouching(player2, end);
		
		if(touching || touching2) {
			System.out.println("END LEVEL");
			nextLevel();
		}
		
		
	}
	
	public void nextLevel(){
		
		// increase currentLevel by one
		currentLevel++;
		
		// clear all arrays and other stored data
		initDisks();
		
		// reload the second map using all of the new data from the next map file.
		setUpLevel();
		
	}
	
	public void loadLevel(BufferedImage image){
		int w = image.getWidth();
		int h = image.getHeight();
		int num0 = 0;
		int num1 = 0;
		int num2 = 0;
		int num3 = 0;
		int num4 = 0;
		
		for(int xx = 0; xx < w; xx++){
			for(int yy = 0; yy < h; yy++){
				int pixel = image.getRGB(xx, yy);	// takes in the RGB value of that point on the map
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				
				
				//if blue pixel
				if(red == 0 && green == 0 && blue == 255){
					player1.setCoordinates(xx*20+player1.getRadius(), yy*20+player1.getRadius());
					player1.setStartingCoordinates(xx*20+player1.getRadius(), yy*20+player1.getRadius());
				}
				//if dark blue pixel
				if(red == 0 && green == 0 && blue == 126){
					player2.setCoordinates(xx*20+player2.getRadius(), yy*20+player2.getRadius());
					player2.setStartingCoordinates(xx*20+player2.getRadius(), yy*20+player2.getRadius());
				}
				if(red == 255 && green == 106 && blue == 0){
					harmful0[num0].setCoordinates(xx*20+harmful1[num0].getRadius(), yy*20+harmful1[num0].getRadius());
					num0++;
				}
				
				//if red pixel
				if(red == 255 && green == 0 && blue == 0){
					harmful1[num1].setCoordinates(xx*20+harmful1[num1].getRadius(), yy*20+harmful1[num1].getRadius());
					num1++;
				}
				
				//if dark red pixel
				if(red == 126 && green == 0 && blue == 0){
					harmful2[num2].setCoordinates(xx*20+harmful2[num2].getRadius(), yy*20+harmful2[num2].getRadius());
					num2++;
				}
				
				//if Cyan Pixel (for switch)
				if(red == 0 && green == 254 && blue == 254){
					switch1.setCoordinates(xx*20+(switch1.getRadius()/2), yy*20+(switch1.getRadius()/2));
				}
				
				//if Cyan Pixel (for Gate)
				if(red == 0 && green == 255 && blue == 255){
					gate1[num3].setCoordinates(xx*20+gate1[num3].getRadius(), yy*20+gate1[num3].getRadius());
					gate1[num3].setInitCoordinates(xx*20+gate1[num3].getRadius(), yy*20+gate1[num3].getRadius());
					num3++;
				}
				
				//if Dark Green Pixel (for switch)
				if(red == 0 && green == 125 && blue == 125){
					switch2.setCoordinates(xx*20+(switch2.getRadius()/2), yy*20+(switch2.getRadius()/2));
				}
				
				//if Dark Green Pixel (for gate)
				if(red == 0 && green == 126 && blue == 126){
					gate2[num4].setCoordinates(xx*20+gate2[num4].getRadius(), yy*20+gate2[num4].getRadius());
					gate2[num4].setInitCoordinates(xx*20+gate2[num4].getRadius(), yy*20+gate2[num4].getRadius());
					num4++;
				}
				
				//if white pixel (For some reason this was acting up when they were all 255)
				if(red == 254  && green == 254 && blue == 254){
					end.setCoordinates(xx*20+(end.getRadius()/2), yy*20+(end.getRadius()/2));
				}
				
			}
		}
		
		harmfulDiskAmount0 = num0;
		harmfulDiskAmount1 = num1;
		harmfulDiskAmount2 = num2;
		
		gateDiskAmount1 = num3;
		gateDiskAmount2 = num4;
	}
	
	public void initDisks(){
		
		// set up player/end/switch disks
		player1 = new PlayerDisk(10, 0, 0, "BLUE", 5);
		player2 = new PlayerDisk(10, 0, 0, "DARK_BLUE", 5);
		end = new EndDisk(20, 0, 0, "WHITE");
		switch1 = new SwitchDisk(20, 0, 0, "CYAN");
		switch2 = new SwitchDisk(20, 0, 0, "DARK_GREEN");
		
		// Sets up Harmful Disks
		for (int i = 0; i < harmful0.length; i++){
			harmful0[i] = new HarmfulDisk(10, 0, 0, "ORANGE", 0, 0);
		}
		
		for (int i = 0; i < harmful1.length; i++){
			harmful1[i] = new HarmfulDisk(10, 0, 0, "RED", 1, 1);
		}
		
		for (int i = 0; i < harmful2.length; i++){
			harmful2[i] = new HarmfulDisk(10, 0, 0, "DARK_RED", 1, 2);
		}
		
		// Sets up gate disks
		for (int i = 0; i < gate1.length; i++){
			gate1[i] = new GateDisk(10, 0, 0, "CYAN", 1);
		}
		
		for (int i = 0; i < gate2.length; i++){
			gate2[i] = new GateDisk(10, 0, 0, "DARK_GREEN", 2);
		}
			
	}
	
	public void setUpLevel(){
		
		// load the level
		switch (currentLevel) {
			case 1:
				loadLevel(map1);
				break;
			case 2:
				loadLevel(map2);
				break;
			case 3:
				loadLevel(map3);
				break;
			case 4:
				System.out.println("YOU WIN!!!");
				System.exit(0);
				break;
			default:
				System.out.println("Error in currentLevel switch");
		}
		
		// set the direction of the moving harmful disks
		// no parameters need to be passed. the direction will be set randomly
		for (int i = 0; i < harmfulDiskAmount1; i++){
			harmful1[i].setDirection();
		}
		for (int i = 0; i < harmfulDiskAmount2; i++){
			harmful2[i].setDirection();
		}
		
	}

	public static void main(String[] args) {
		new Game().run();
	}

}
