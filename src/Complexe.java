public class Complexe {
	private double reel;
	private double imag;
	
	Complexe(){
		reel = 0.0;
		imag = 0.0;
	}
	Complexe(double reel,double imag) {
		this.reel = reel;
		this.imag = imag;
	}
	Complexe somme(Complexe c) {
	    return new Complexe(this.reel+c.reel,this.imag+c.imag);
	  }
	Complexe produit(Complexe c) {
	    double reel = (this.reel*c.reel)+(this.imag*c.imag*-1);
	    double imag = (this.imag*c.reel)+(this.reel*c.imag);
	    return new Complexe(reel,imag);
	}
	public double abs() {
		return Math.hypot(this.reel, this.imag);
	}
	public Complexe square() {
		double reel = this.reel*this.reel - this.imag*this.imag;
        double imag = 2*this.reel*this.imag;
        return new Complexe(reel,imag);
	}
	public Complexe pow(int degre) {
		Complexe res = this;
		for(int i =1;i<degre;i++) {
			res = res.square();
		}
		return res;
	}
	void print(Complexe c) {
		System.out.println(c.reel + " +i" + c.imag);
	}
	public String toString() {
		if (imag>=0) return reel + " + " + Math.abs(imag) + "i";
		return reel + " - " + Math.abs(imag)+"i";
	}
}
