
class RestaurantS {
	RestaurantS(String[] args) throws NumberFormatException {
        int antall = Integer.parseInt(args[0]); // antall porsjoner
        FellesBord bord = new FellesBord();
        Kokk kokk = new Kokk(bord,antall);
        Kokk kokk1 = new Kokk(bord,antall);
        Kokk kokk2 = new Kokk(bord,antall);
        Kokk kokk3 = new Kokk(bord,antall);
        Kokk kokk4 = new Kokk(bord,antall);
        Kokk kokk5 = new Kokk(bord,antall);
        kokk.start();
        kokk1.start();
        kokk2.start();
        kokk3.start();
        kokk4.start();
        Servitor  servitor = new Servitor(bord,antall);
        servitor.start();
    }
    public static void main(String[] args) {
        try {
    	   new RestaurantS(args);
        } catch (NumberFormatException e) {
            System.out.println("Trenger en integer.");
            System.exit(1);
        }
    }
}
class FellesBord {    // en monitor
    private int antallPaBordet = 0;  // invarianten gjelder
    private final int BORD_KAPASITET = 4;
    /* Invariant: 0 <= antallPaBordet <= BORD_KAPASITET */
    synchronized void settTallerken() {
    	while (antallPaBordet >= BORD_KAPASITET) {
            /* Så lenge det allerede er  BORD_KAPASITET tallerkner
            på bordet er det ikke lov å sette på flere. */
            try {
            	wait();
            } catch (InterruptedException e) {
                System.out.println("Noe feil med trådene...");
                System.exit(2);
            }
          // Nå er antallPaBordet < BORD_KAPASITET
        }
		antallPaBordet++; // bevarer invarianten 
		System.out.println("Antall på bordet: " + antallPaBordet); 
		notify(); // Si fra til den som henter tallerkener
	}
	synchronized void hentTallerken() {
		while (antallPaBordet == 0) {
    		/* Så lenge det ikke er noen talerkener på
    		bordet er det ikke lov å ta en */
    		try { 
    			wait();
    		} catch (InterruptedException e) {
                System.out.println("Noe feil med trådene...");
                System.exit(2); 
            } // Nå er antallPaBordet > 0
    	}
		antallPaBordet --;  // bevarer invarianten
		notifyAll(); // si fra til den som setter på tallerkener
	}
}
class Kokk extends Thread {
	private FellesBord bord;
	private final int ANTALL;
	private int laget = 0;
	Kokk(FellesBord bord, int ant) {
		this.bord = bord;  ANTALL = ant;
	}
	public void run() {
		while(ANTALL != laget) {
			laget ++;
			System.out.println(getName() + " lager tallerken nr: " + laget);
            bord.settTallerken();  // lag og lever tallerken
            try { sleep((long) (1000 * Math.random()));
            } catch (InterruptedException e) {}
        }  // Kokken er ferdig
    }
}
class Servitor extends Thread {
 	private FellesBord bord;
 	private final int ANTALL;
 	private int servert = 0;
 	Servitor(FellesBord bord, int ant) {
 		this.bord = bord;  ANTALL = ant;
 	}
 	public void run() {
 		while (ANTALL != servert)  {
 			bord.hentTallerken(); /* hent tallerken og server */
 			servert++;
 			System.out.println("Servitør serverer nr:" +  servert);
 			try { sleep((long) (1000 * Math.random()));
 			} catch (InterruptedException e) {}
        } // Servitøren er ferdig
    }
}