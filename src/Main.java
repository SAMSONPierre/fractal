public class Main {
	private static void infoFormat(){
		System.out.println("Rappel format Mandelbrot: java Main NbThread WIDTH HEIGHT DEGRE ITERATION SATURATION HUE TYPE");
		System.out.println("Rappel format Julia: java Main NbThread Z C WIDTH HEIGHT ITERATION SATURATION HUE TYPE");
	}
	public static void main(String[] args) {
		//Fractal.testThread(100);
		if (args.length==0) {
			View v = new View();
		}else if(args.length==8) {
			if(args[args.length-1].equals("MandelBrot")) {
				new View(args);
			}else{
				System.out.println("name MandelBrot");
				infoFormat();
			}
		}else if(args.length==9){
			if(args[args.length-1].equals("Julia")) {
				System.out.println("Name Julia");
				new View(args);
			}else{
				infoFormat();
			}
		}else{
			infoFormat();
		}
	}
}
