package fr.TPIII_INF4063;

import java.util.List;


public class Trajet {
	private List<Station> listeDesStationsDuTrajet;
	private int tempsRequis;

	public List<Station> getListeDesStationsDuTrajet() {
		return listeDesStationsDuTrajet;
	}

	public void setListeDesStationsDuTrajet(List<Station> listeDesStationsDuTrajet) {
		this.listeDesStationsDuTrajet = listeDesStationsDuTrajet;
	}

	public int getTempsRequis() {
		return tempsRequis;
	}

	public void setTempsRequis(int tempsRequis) {
		this.tempsRequis = tempsRequis;
	}

	@Override
	public String toString() {
		return "Trajet{" +
				"listeDesStationsDuTrajet=" + listeDesStationsDuTrajet +
				", tempsRequis=" + tempsRequis +
				'}';
	}
}
