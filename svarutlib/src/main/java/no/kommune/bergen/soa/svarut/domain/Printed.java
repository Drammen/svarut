package no.kommune.bergen.soa.svarut.domain;

import java.util.Date;

/** Status for print og postutsendelse for en forsendelse */
public class Printed {
	private Date tidspunktPostlagt;
	private String forsendelsesId;
	private int antallSortHvitSider;
	private int antallFargeSider;
	private int antallArkKonvoluttertAutomatisk;
	private int antallEkstraArkKonvoluttertAutomatisk;
	private int antallArkKonvoluttertManuelt;
	private int antallEkstraArkKonvoluttertManuelt;
	private int vekt;
	private double produksjonskostnader;
	private double porto;

	public Date getTidspunktPostlagt() {
		return tidspunktPostlagt;
	}
	public void setTidspunktPostlagt(Date tidspunktPostlagt) {
		this.tidspunktPostlagt = tidspunktPostlagt;
	}
	public String getForsendelsesId() {
		return forsendelsesId;
	}
	public void setForsendelsesId(String forsendelsesId) {
		this.forsendelsesId = forsendelsesId;
	}
	public int getAntallSortHvitSider() {
		return antallSortHvitSider;
	}
	public void setAntallSortHvitSider(int antallSortHvitSider) {
		this.antallSortHvitSider = antallSortHvitSider;
	}
	public int getAntallFargeSider() {
		return antallFargeSider;
	}
	public void setAntallFargeSider(int antallFargeSider) {
		this.antallFargeSider = antallFargeSider;
	}
	public int getAntallArkKonvoluttertAutomatisk() {
		return antallArkKonvoluttertAutomatisk;
	}
	public void setAntallArkKonvoluttertAutomatisk(
			int antallArkKonvoluttertAutomatisk) {
		this.antallArkKonvoluttertAutomatisk = antallArkKonvoluttertAutomatisk;
	}
	public int getAntallEkstraArkKonvoluttertAutomatisk() {
		return antallEkstraArkKonvoluttertAutomatisk;
	}
	public void setAntallEkstraArkKonvoluttertAutomatisk(
			int antallEkstraArkKonvoluttertAutomatisk) {
		this.antallEkstraArkKonvoluttertAutomatisk = antallEkstraArkKonvoluttertAutomatisk;
	}
	public int getAntallArkKonvoluttertManuelt() {
		return antallArkKonvoluttertManuelt;
	}
	public void setAntallArkKonvoluttertManuelt(int antallArkKonvoluttertManuelt) {
		this.antallArkKonvoluttertManuelt = antallArkKonvoluttertManuelt;
	}
	public int getAntallEkstraArkKonvoluttertManuelt() {
		return antallEkstraArkKonvoluttertManuelt;
	}
	public void setAntallEkstraArkKonvoluttertManuelt(
			int antallEkstraArkKonvoluttertManuelt) {
		this.antallEkstraArkKonvoluttertManuelt = antallEkstraArkKonvoluttertManuelt;
	}
	public int getVekt() {
		return vekt;
	}
	public void setVekt(int vekt) {
		this.vekt = vekt;
	}
	public double getProduksjonskostnader() {
		return produksjonskostnader;
	}
	public void setProduksjonskostnader(double produksjonskostnader) {
		this.produksjonskostnader = produksjonskostnader;
	}
	public double getPorto() {
		return porto;
	}
	public void setPorto(double porto) {
		this.porto = porto;
	}

	@Override
	public String toString() {
		return "Printed [tidspunktPostlagt=" + tidspunktPostlagt
				+ ", forsendelsesId=" + forsendelsesId
				+ ", antallSortHvitSider=" + antallSortHvitSider
				+ ", antallFargeSider=" + antallFargeSider
				+ ", antallArkKonvoluttertAutomatisk="
				+ antallArkKonvoluttertAutomatisk
				+ ", antallEkstraArkKonvoluttertAutomatisk="
				+ antallEkstraArkKonvoluttertAutomatisk
				+ ", antallArkKonvoluttertManuelt="
				+ antallArkKonvoluttertManuelt
				+ ", antallEkstraArkKonvoluttertManuelt="
				+ antallEkstraArkKonvoluttertManuelt + ", vekt=" + vekt
				+ ", produksjonskostnader=" + produksjonskostnader + ", porto="
				+ porto + "]";
	}
}
