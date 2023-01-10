import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class Fractal extends JPanel implements MouseWheelListener,MouseMotionListener {
	
	// Constante
	private static Complexe constant = new Complexe();
	private static int WIDTH;
	private static int HEIGHT;
	private static int ITERATIONS;
	private static int DEGRE;
	private static float SATURATION;
	private static float HUE;
	private static String TYPE;
	private static int NB;

	private double zoomFactor =200;
	private double axeXmin = -1.5;
	private double axeYmin = 1.5;

	private BufferedImage buffer;

	// **************************Constructeurs***************************************
	public Fractal(int nb,int width,int height,int degre,int iteration,float saturation,float hue){//constructeur de base\
		addKeyboardEvents();
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        NB=nb;
        WIDTH=width;
        HEIGHT=height;
        DEGRE=degre;
        ITERATIONS=iteration;
        SATURATION=saturation;
        HUE=hue;
        // Centrer le fractal
        this.axeXmin=-(width/2)/zoomFactor;
        this.axeYmin=(height/2)/zoomFactor;
        
        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }
	public Fractal(int nb,int width,int height,int degre,int iteration,float saturation,float hue,String type) {
		// Mandelbrot
		this(nb,width,height,degre,iteration,saturation,hue);
		TYPE=type;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		renderThread(NB);
	}
	public Fractal(int nb,float f1,float f2,int width,int height,int iteration,float saturation,float hue,String type) {
		// Julia
		this(nb,width,height,2,iteration,saturation,hue);
		TYPE=type;
		constant = new Complexe(f1,f2);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		buffer = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
		renderThread(NB);
	}
	@Override public void paint(Graphics g) {
		g.drawImage(buffer, 0, 0, null);
	}
	// ****************************Calcul du Fractal***************************************
	
	// Calcul du fractal avec n threads
	public void renderThread(int n){
		List<Thread> lt = new LinkedList<Thread>();
		int interval = WIDTH/n; // Chaque thread couvre une zone de WIDTH/n pixels.
		int tmp=0;
		for(int i=0;i<n;i++) {
			int z = tmp;
			Thread t = new Thread(() -> runRender(z,z+interval)); // On associe chaque thread à sa zone
			tmp += interval;
			lt.add(t);
		}
		for(Thread t : lt) { // On active tous les threads
			t.start();
		}
		for(Thread t : lt) { // Enfin on attend que chaque thread se termine
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	// On calcule la couleur du pixel et on l'ajoute à l'image
	public void runRender(int i,int j) {
		for (int x=i; x<j; x++) { // chaque thread gère sa zone entre i et j.
			for (int y=0; y<HEIGHT; y++){
				int color = divergenceIndex(x, y,TYPE);
				buffer.setRGB(x, y, color);
			}
		}
	}
	// prend x et retourne un point dans le plan complexe
	private double getXPos(double x) {
		return x/zoomFactor + axeXmin;
	}
	private double getYPos(double y) {
		return y/zoomFactor - axeYmin;
	}
	
	// Calcul de l'index de divergence selon le type du fractal. On s'en sert pour obtenir la couleur de chaque point.
	public int divergenceIndex(double x,double y,String type){
		Complexe z = null;
		Complexe c=null;
		// Mandelbrot : 
		// z0 = 0
		// zn+1 = zn² + c 
		if(type.equals("MandelBrot")) {
			z=new Complexe();
			c = new Complexe(getXPos(x), getYPos(y));
		// Julia : f(z) = c + z²
		}else{
			z=new Complexe(getXPos(x), getYPos(y));
			c=constant;
		}
		// Combien de fois on peut appliquer f en partant de z avant que la suite commence à diverger.
		int i;
		for(i=0;i<ITERATIONS;i++) {
			z = z.pow(DEGRE).somme(c);
			if(z.abs() > 2) { // On considère que la suite diverge
				return Color.HSBtoRGB(((float)i / ITERATIONS),SATURATION,HUE);
			}
		}
		return ITERATIONS; // Après ITERATIONS, on considère qu'elle converge.
	}
	// *******************************************************************************************
	//met à jour l'image
	private void update() {
		renderThread(NB);
		repaint();
	}
	private void ZoomEvent(double posX,double posY,double newZoomFactor) {
		// zoom
		axeXmin += posX/zoomFactor; 
		axeYmin -= posY/zoomFactor;
		// recentrage
		axeXmin -= (WIDTH/2)/newZoomFactor;
		axeYmin += (HEIGHT/2)/newZoomFactor;
		zoomFactor = newZoomFactor;
		//mise à jour
		update();
	}
	// Déplacement à la souris
	private void MoveEvent(double posX,double posY){
		axeXmin=-getXPos(posX);
        axeYmin=getYPos(posY);
        update();
    }
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e)) {
			MoveEvent(e.getX(),e.getY());
		}
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int val=e.getWheelRotation();
		if(val<0){
			ZoomEvent(e.getX(),e.getY(),zoomFactor*2);
		}else{
			ZoomEvent(e.getX(),e.getY(),zoomFactor/2);
		}
	}
	// Action pour les touches claviers
	public void addKeyboardEvents() {
		// Screen avec V
		KeyStroke vKey = KeyStroke.getKeyStroke(KeyEvent.VK_V,0);
		Action screen = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = new File("exploration");
				File[] farray = f.listFiles();
				File outputfile = new File("exploration/fractal" + (farray.length) + ".png");
				try {
					ImageIO.write(buffer, "png", outputfile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		//Déplacement avec les flèches directionnelles
		KeyStroke upKey = KeyStroke.getKeyStroke(KeyEvent.VK_UP,0);
		KeyStroke downKey = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0);
		KeyStroke rightKey = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0);
		KeyStroke leftKey = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0);
		Action up = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				axeYmin += (HEIGHT / zoomFactor) / 4;
				update();
			}
		};
		Action down = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				axeYmin -= (HEIGHT / zoomFactor) / 4;
				update();
			}
		};
		Action right = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				axeXmin += (WIDTH / zoomFactor)/4;
				update();
			}
		};
		Action left = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				axeXmin -= (WIDTH / zoomFactor)/4;
				update();
			}
		};
		// Key Binding
		// touche -> id 
		this.getInputMap().put(upKey, "up_key");
		this.getInputMap().put(downKey, "down_key");
		this.getInputMap().put(leftKey, "left_key");
		this.getInputMap().put(rightKey, "right_key");
		this.getInputMap().put(vKey, "v_key");
		// id -> action
		this.getActionMap().put("up_key", up);
		this.getActionMap().put("down_key", down);
		this.getActionMap().put("left_key", left);
		this.getActionMap().put("right_key", right);
		this.getActionMap().put("v_key", screen);
	}
	// *******************************************************************************************
	// Getters
	public BufferedImage getBuffer() {return this.buffer;}
	public int getIterations() {return ITERATIONS;}
	public Complexe getConstant() {return constant;}
	public int getWidth() {return WIDTH;}
	public int getHeight() {return HEIGHT;}
	public int getDegre() {return DEGRE;}
	public float getSaturation() {return SATURATION;}
	public float getHue() {return HUE;}
	public String getType() {return TYPE;}
	public int getNb() {return NB;}
	@Override public void mouseMoved(MouseEvent e) {}
	
	//  Fonction servant à l'étude sur le gain de performance via les threads
	public static void testThread(int n) {
		for(int i=1;i<n;i++) {
			System.out.print(i + " threads : ");
			new Fractal(i,600,600,2,1000,0.5f,0.5f);
		}
	}
}