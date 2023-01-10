import java.util.Scanner;

// Mode Console qui demande à l'utilisateur les propriétés du fractale
public class Console {
	public static void fractConsole() {
try ( Scanner scanner = new Scanner( System.in ) ) {
            int degre = 0;
            System.out.print( "Julia / Mandelbrot ?" );
            String type = scanner.nextLine();
            if(type.equals("Mandelbrot")) {
            	System.out.print( "degre ?" );
                degre = scanner.nextInt();
            }
            System.out.println("nb thread ?");
            int nbT = scanner.nextInt();
            
            System.out.println("longueur ?");
            int longueur = scanner.nextInt();
            
            System.out.println("largeur ?");
            int largeur = scanner.nextInt();
            
            System.out.println("iteration ?");
            int iteration = scanner.nextInt();
            
            System.out.println("saturation ?");
            float saturation = scanner.nextFloat();
            
            System.out.println("hue ?");
            float hue = scanner.nextFloat();

            
            if(type.equals("Julia")) {
            	System.out.println("Re(c) ?");
                float reel = scanner.nextFloat();
                
                System.out.println("Im(c) ?");
                float imag = scanner.nextFloat();
                
                View.generatePNG(new Fractal(nbT,reel, imag, longueur, largeur,iteration,saturation,hue,"Julia"));
                
            }
            else {
            	View.generatePNG(new Fractal(nbT,longueur, largeur, degre,iteration,saturation,hue,"MandelBrot"));
            }
            
            System.out.println("Image dans le dossier images");
            
            

        }catch(Exception e) {
        	System.out.println("Mauvais argument");
        }
	}
}
