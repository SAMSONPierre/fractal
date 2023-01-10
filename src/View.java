import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class View extends JFrame {
	private JButton jb1 = new JButton("Mandelbrot set");
	private JButton jb2 = new JButton("Julia set");
	private JButton jb3 = new JButton("Mode terminal");
	private JButton jb4 = new JButton("Random MandelBrot");
	private JButton jb5 = new JButton("Random Julia");
	View(String[] args){
		JFrame f=new JFrame();
		if(args.length==8) {
			f.setTitle("MandelBrot");

			Fractal mandelBrot=new Fractal(
					Integer.parseInt(args[0]),
					Integer.parseInt(args[1]),
					Integer.parseInt(args[2]),
					Integer.parseInt(args[3]),
					Integer.parseInt(args[4]),
					Float.parseFloat(args[5]),
					Float.parseFloat(args[6]),
					args[7]);
			f.add(mandelBrot);
		}else{
			f.setTitle("Julia");
			Fractal julia=new Fractal(
					Integer.parseInt(args[0]),
					Float.parseFloat(args[1]),
					Float.parseFloat(args[2]),
					Integer.parseInt(args[3]),
					Integer.parseInt(args[4]),
					Integer.parseInt(args[5]),
					Float.parseFloat(args[6]),
					Float.parseFloat(args[7]),
					args[8]);
			for(String i: args){
				System.out.println(i);
			}
			f.add(julia);
		}
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	View(){
		JPanel panneau = new JPanel();
		jb1.addActionListener((event)->{
			JFrame mb = new JFrame("Ensemble de MandelBrot");
			Fractal mbs = getMBS();
			mb.add(mbs);
			mb.pack();
			mb.setVisible(true);
			generatePNG(mbs);
		});
		jb2.addActionListener((event)->{
			JFrame julia = new JFrame("Ensemble de Julia");
			Fractal js = getJS();
			julia.add(js);
			julia.pack();
			julia.setVisible(true);
			generatePNG(js);
		});
		
		jb3.addActionListener((event)->{
			Console.fractConsole();
			dispose();
		});
		jb4.addActionListener((event)->{
			JFrame mb = new JFrame("Ensemble de MandelBrot");
			Fractal mbs = randomMBS();
			mb.add(mbs);
			mb.pack();
			mb.setVisible(true);
			generatePNG(mbs);
		});
		jb5.addActionListener((event)->{
			JFrame julia = new JFrame("Ensemble de Julia");
			Fractal js = randomJS();
			julia.add(js);
			julia.pack();
			julia.setVisible(true);
			generatePNG(js);
		});
		panneau.add(jb1);
		panneau.add(jb2);
		panneau.add(jb3);
		panneau.add(jb4);
		panneau.add(jb5);
		this.setContentPane(panneau);
		this.setTitle("Fractal");
		this.setSize(200,200);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	// crée un ensemble de MandelBrot aléatoire
	public Fractal randomMBS() {
		Random r = new Random();
		float sat = (float) (r.nextFloat() * (1.4 - 0));
		float hue = (float) (r.nextFloat() * (1.4 - 0));
		int deg = r.nextInt(4)+2;
		
		return new Fractal(6,600,600,deg,200,sat,hue,"MandelBrot");
	}
	// crée un ensemble de Julia aléatoire sur certains critères.
	public Fractal randomJS() {
		Float[][] array = {{0.3f,0.5f},{0.285f,0.01f},{-1.417022285618f,0.0099534f},{0.285f,0.013f},{0.285f,0.01f},{-1.476f,0f},{-0.4f,0.6f},{-0.8f,0.156f}};
		Random r = new Random();
		int fract = r.nextInt(8);
		float f1 = array[fract][0];
		float f2 = array[fract][1];
		float sat = (float) (r.nextFloat() * (1.4 - 0));
		float hue = (float) (r.nextFloat() * (1.4 - 0));
		System.out.println(fract);
		return new Fractal(6,f1,f2,600,600,100,sat,hue,"Julia");
	}
	// Png + fichier de création du fractal
	public static void generatePNG(Fractal fs) {
		File f = new File("images");
		try {
			f.mkdir();
		}catch(Exception e){}
		File[] farray = f.listFiles();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("images/fractal" + (farray.length/2) + ".txt","UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String constant = "";
		if(fs.getConstant() != null) {
			constant = " + " + fs.getConstant().toString();
		}
		writer.println("Fichier de creation du fractal numero" + (farray.length/2));
		if(fs.getType().equals("MandelBrot")) {
			writer.println("Type : MandelBrot");
		}
		else {
			writer.println("Type : Julia");
		}
		writer.println("Dimension :" + fs.getWidth() +"x" +fs.getHeight() );
		writer.println("f(z) = z^" + fs.getDegre() + constant + " avec " + fs.getIterations() + " iterations");
		writer.println("Saturation : " + fs.getSaturation() + " Hue : " + fs.getHue());
		writer.close();
		File outputfile = new File("images/fractal" + (farray.length/2) + ".png");
		try {
			ImageIO.write(fs.getBuffer(), "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Demande les paramètres pour un ensemble de Julia.
	public Fractal getJS() {
		int nbT = askBoxInt("Nb thread : ");
		float f1 = askBoxFloat("Re(c) : ");
		float f2 = askBoxFloat("Im(c) : ");
		int longueur = askBoxInt("longueur : ");
		int largeur = askBoxInt("largeur : ");
		int iteration = askBoxInt("iteration : ");
		float saturation = askBoxFloat("Saturation : ");
		float hue = askBoxFloat("Hue : ");
		return new Fractal(nbT,f1,f2,longueur,largeur,iteration,saturation,hue,"Julia");
	}
	// Demande les paramètres pour un ensemble de Mandelbrot.
	public Fractal getMBS() {
		int nbT = askBoxInt("Nb thread : ");
		int longueur = askBoxInt("longueur : ");
		int largeur = askBoxInt("largeur : ");
		int degre = askBoxInt("degre : ");
		int iteration = askBoxInt("iteration : ");
		float saturation = askBoxFloat("Saturation : ");
		float hue = askBoxFloat("Hue : ");
		return new Fractal(nbT,longueur,largeur,degre,iteration,saturation,hue,"MandelBrot");
	}
	
	public float askBoxFloat(String question) {
		String answ1 = JOptionPane.showInputDialog(question);
		try {
			Float.parseFloat(answ1);
			return Float.parseFloat(answ1);
		}catch(Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Erreur de type");
			return askBoxFloat(question);
		}
	}
	public int askBoxInt(String question) {
		String answ1 = JOptionPane.showInputDialog(question);
		try{
			Integer.parseInt(answ1);
			return Integer.parseInt(answ1);
		}catch(Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Erreur de type");
			return askBoxInt(question);
		}
	}

}
