package planetarium;

import java.util.ArrayList;

public class SistemaStellare {
	String nome;
	Stella stella = null;
	
	public SistemaStellare(String nome) {
		this.nome = nome;
	}
	
	public boolean aggiungiStella(Stella _stella) {
		if(stella == null) {
			stella = _stella;
			return true;
		} else return false;
	}
	public boolean rimuoviStella() {
		if(stella != null) {
			stella = null;
			return true;
		} else return false;
	}
	

	public Stella getStella() {
		return stella;
	}

	/** 
     * @return 
     */
	public Punto calcolaCentroMassa(){
	    int massa = stella.getPeso();
	    Punto punto = stella.getCord();
	    double x = (punto.getX() * stella.getPeso());
	    double y = (punto.getY() * stella.getPeso());
	    
	    for(Pianeta pianeta: stella.getPianeti()){
	        punto = pianeta.getCord();
	        massa += pianeta.getPeso();
	        x += (punto.getX() * pianeta.getPeso());
	        y += (punto.getY() * pianeta.getPeso());
	        for(Satellite satellite: pianeta.getSatelliti()){
	            punto = satellite.getCord();
	            massa += satellite.getPeso();
	            x += (punto.getX() * satellite.getPeso());
	            y += (punto.getY() * satellite.getPeso());
	        }
	    }
	    x /= massa;
	    y /= massa;
	    Punto centroMassa = new Punto(x,y);
	    return centroMassa;
	}
	/** 
     * @param codiceA
     * @param codiceB
     * @return 
     */
	public boolean collisione(String codiceA,String codiceB) {

		double vA[] = getDistanza(codiceA);
		double vB[] = getDistanza(codiceB);
		double d = vA[0];
		double e = vA[1];
		double d0 = vB[0];
		double e0 = vB[1];
		if(codiceA.equals(codiceB)) return false;
		if((getCorpoDaCodice(codiceA,"c").equals("stella")&& getCorpoDaCodice(codiceB,"c").equals("pianeta"))|| //stella e pianeta
				getCorpoDaCodice(codiceA,"c").equals("pianeta")&& getCorpoDaCodice(codiceB,"c").equals("stella")) {
				return false;
		}else if(getCorpoDaCodice(codiceA,"c").equals("pianeta") && getCorpoDaCodice(codiceB,"c").equals("pianeta")) {
			return false; 
		}else {
			if(getCorpoDaCodice(codiceA,"c").equals("pianeta") && getCorpoDaCodice(codiceB,"c").equals("satellite")) { //satB appartenente 
				for(Pianeta pianeta: stella.getPianeti()) {										   //pianetaA
					if(pianeta.getCodice().equals(codiceA)) {
						if(pianeta.cercaSatellite(codiceB)!=null) {
							return false;
						}
					}
				}
			}
			if(getCorpoDaCodice(codiceA,"c").equals("satellite") && getCorpoDaCodice(codiceB,"c").equals("pianeta")) {//satA appartenente 
				for(Pianeta pianeta: stella.getPianeti()) {										   //pianetaB
					if(pianeta.getCodice().equals(codiceB)) {
						if(pianeta.cercaSatellite(codiceA)!=null) {
							return false;
						}
					}
				}	
			}
			if(getCorpoDaCodice(codiceA,"c").equals("satellite")&&getCorpoDaCodice(codiceB,"c").equals("satellite")) {
				for(Pianeta pianeta: stella.getPianeti()) {
					boolean a = false, b = false;
					for(Satellite satellite: pianeta.getSatelliti()) {
						if(satellite.getCodice().equals(codiceA)) {
							a = true;
						}
						if(satellite.getCodice().equals(codiceB)) {
							b = true;
						}
					}
					if(a == true && b == true) {
						return false;
					}
				}
			}
			if(d0 > d) {
				if((d0-e0)<=(d+e)) {
					System.out.println("qui");
					return true;
				}
			}else {
				if((d0+e0)>=(d-e)) {
					System.out.println("qua");
					return true;
				}
			}
		}
		return false;
	
	}
	
	public String getCorpoDaCodice(String codice,String tipo) {
		if(stella.getCodice().equals(codice)) {
			return "stella";
		}else {
			for(Pianeta pianeta: stella.getPianeti()) {
				if(pianeta.getCodice().equals(codice)) {
					return "pianeta";
				}else {
					for(Satellite satellite: pianeta.getSatelliti()) {
						if(satellite.getCodice().equals(codice))
							return "satellite";
					}
				}
			}
		}
		return "";
	}
	public String rotta(String partenza,String arrivo, SistemaStellare ss) {
		String rotta = "";
		if(presenteCorpoCodice(partenza) && presenteCorpoCodice(arrivo)) {
			if(partenza.equals(arrivo)) return "Sono lo stesso corpo";
		} else return "Almeno uno dei 2 codici non esiste";
		CorpoCeleste nextHopPartenza = CorpoCeleste.getCorpoFromCodice(ss, partenza);
		ArrayList<CorpoCeleste> parentPartenza = new ArrayList<CorpoCeleste>();
		do {
			parentPartenza.add(nextHopPartenza);
			nextHopPartenza = nextHopPartenza.getParent(ss);
		} while(nextHopPartenza != null);

		CorpoCeleste nextHopArrivo = CorpoCeleste.getCorpoFromCodice(ss, arrivo);
		ArrayList<CorpoCeleste> parentArrivo = new ArrayList<CorpoCeleste>();
		do {
			parentArrivo.add(nextHopArrivo);
			nextHopArrivo = nextHopArrivo.getParent(ss);
		} while(nextHopArrivo != null);
		
		
		int posIncontroPartenza = 0;
		int posIncontroArrivo = 0;
		ricercaCorrispondenza:
		for(CorpoCeleste ramoPartenza : parentPartenza) {
			posIncontroArrivo = 0;
			for(CorpoCeleste ramoArrivo : parentArrivo) {
				if(ramoPartenza.equals(ramoArrivo)) break ricercaCorrispondenza;
				posIncontroArrivo++;
			}
			posIncontroPartenza++;
		}
		
		
		
		if(posIncontroPartenza != 0) rotta = rotta.concat(parentPartenza.get(0).getNome() + " (Codice: "+parentPartenza.get(0).getCodice()+")");
		for(int i=1; i<posIncontroPartenza; i++) {
			rotta = rotta.concat(" > " +parentPartenza.get(i).getNome() + " (Codice: "+parentPartenza.get(i).getCodice()+")");
		}
		
		if(posIncontroPartenza != 0) rotta = rotta.concat(" > ");
		rotta = rotta.concat(parentArrivo.get(posIncontroArrivo).getNome() + " (Codice: "+parentArrivo.get(posIncontroArrivo).getCodice()+")");
		for(int i=posIncontroArrivo-1; i>=0; i--) {
			rotta = rotta.concat(" > " + parentArrivo.get(i).getNome() + " (Codice: "+parentArrivo.get(i).getCodice()+")");
		}
		
		return rotta;
	}
	
		
	/**
	 * @param codice 
     * @return 
     */
	public boolean presenteCorpoCodice(String codice) {
		Stella stella = getStella();
		if(stella.getCodice().equals(codice)) return true;
		else {
			for(Pianeta pianeta : stella.getPianeti()) {
	    		if(pianeta.getCodice().equals(codice)) return true;
	    		else {
	    			for(Satellite satellite : pianeta.getSatelliti()) {
	    				if(satellite.getCodice().equals(codice)) return true;
	    			}
	    		}
	    	}
			return false;
		}
	}
	/** 
     * @param codice
     * @return 
     */
	public String percorso(String codice){
		String perc = "Stella : "+ stella.getNome()+"("+stella.getCodice()+")";
		if(stella.getCodice().equals(codice)) return perc;
		else {
			perc = perc.concat("\n  Pianeta: ");
			for(Pianeta pianeta: stella.getPianeti()) {
				String sPian = pianeta.getNome()+"("+pianeta.getCodice()+")";
				if(pianeta.getCodice().equals(codice)) {
					perc = perc.concat(sPian);
					return perc;
				}else {
					sPian = sPian.concat("\n    Satellite: ");
					for(Satellite satellite: pianeta.getSatelliti()) {
						String sSat = satellite.getNome()+"("+satellite.getCodice()+")";
						if(satellite.getCodice().equals(codice)) {
							perc = perc.concat(sPian);
							perc = perc.concat(sSat);
							return perc;
						}
					}
				}
			}
		}
		perc = "Non è stato trovato nessun corpo celeste con quel codice";
		return perc;
	}
	
	public double[] getDistanza(String codice) {
		Stella stella = getStella();
		double[]c = new double[2];
		double d = 0, e = 0;
		boolean memo = false;
		if(presenteCorpoCodice(codice)) {
			for(Pianeta pianeta: stella.getPianeti()) {
				if(pianeta.getCodice().equals(codice) || memo == true) break;
				else {
					double x1 = Math.pow(pianeta.getCord().getX()-stella.getCord().getX(),2);
					double y1 = Math.pow(pianeta.getCord().getY()-stella.getCord().getY(),2);
					d = Math.sqrt(x1+y1);
					for(Satellite satellite: pianeta.getSatelliti()) {
						double x2 = Math.pow(satellite.getCord().getX()-pianeta.getCord().getX(),2);
						double y2 = Math.pow(satellite.getCord().getY()-pianeta.getCord().getY(),2);
						e = Math.sqrt(x2+y2);
						if(satellite.getCodice().equals(codice)) {
							memo = true;
							break;
						}
					}
				}
			}
		}
		c[0]=d;
		c[1] =e;
		return c;
	}
	public boolean presenteCorpoNome(String nome) {
		if(stella.getNome().equals(nome)) return true;
		for(Pianeta pianeta: stella.getPianeti()) {
			if(pianeta.getNome().equals(nome)) return true;
			for(Satellite satellite: pianeta.getSatelliti()) {
				if(satellite.getNome().equals(nome)) return true;
			}
		}
		return false;
	}
	public boolean presenteCorpoPunto(Punto punto) {
		if(stella.getCord().getX() == punto.getX() &&
		   stella.getCord().getY() == punto.getY()) return true;
		for(Pianeta pianeta: stella.getPianeti()) {
			if(pianeta.getCord().getX() == punto.getX() &&
					   pianeta.getCord().getY() == punto.getY()) return true;
			for(Satellite satellite: pianeta.getSatelliti()) {
				if(satellite.getCord().getX() == punto.getX() &&
						   satellite.getCord().getY() == punto.getY()) return true;
			}
		}
		return false;
	}
}
