package ch.elca.el4j.demos.gui.fs.object;

import java.io.Serializable;

public class KontoDTO implements Serializable {
	private String kontoNr;
	private Integer zusNr;
	private Integer seqNr;
	private Double betrag;
	private String funktion;
	private String sachgruppe;
	
	public KontoDTO(String kontoNr, Integer zusNr, Integer seqNr,
            Double betrag, String funktion, String sachgruppe) {
	    this.kontoNr = kontoNr;
	    this.zusNr = zusNr;
	    this.seqNr = seqNr;
	    this.betrag = betrag;
	    this.funktion = funktion;
	    this.sachgruppe = sachgruppe;
    }

	public String getKontoNr() {
		return kontoNr;
	}

	public void setKontoNr(String nr) {
		kontoNr = nr;
	}

	public Integer getZusNr() {
		return zusNr;
	}

	public void setZusNr(Integer zusNr) {
		this.zusNr = zusNr;
	}

	public Integer getSeqNr() {
		return seqNr;
	}

	public void setSeqNr(Integer seqNr) {
		this.seqNr = seqNr;
	}

	public Double getBetrag() {
		return betrag;
	}

	public void setBetrag(Double betrag) {
		this.betrag = betrag;
	}

	public String getFunktion() {
		return funktion;
	}

	public void setFunktion(String funktion) {
		this.funktion = funktion;
	}

	public String getSachgruppe() {
		return sachgruppe;
	}

	public void setSachgruppe(String sachgruppe) {
		this.sachgruppe = sachgruppe;
	}
	
}
