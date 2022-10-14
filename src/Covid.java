package covid19;

import java.io.Serializable;

public class Covid implements Comparable<Covid>, Serializable{
	
	//필드
	private String region; // 지역
	private int confirmed; //확진자 수
	private int death; // 사망자 수
	private	double conPercentage; // 전국 확진자 비율 
	private	double deathPercentage; // 전국 사망자 비율 
	private String rating; // 해당 지역 감염 위험도
	private int ranking; //순위
	private double inoculation;
	
	//생성자
	public Covid(String region, int confirmed, int death) {
		this(region, confirmed, death, 0.0, 0.0, null, 0);
	}
	
	public Covid(String region, double conPercentage, double inoculation) {
		super();
		this.region = region;
		this.conPercentage = conPercentage;
		this.inoculation = inoculation;
	}
	
	public Covid(String region, double inoculation) {
		super();
		this.region = region;
		this.inoculation = inoculation;
	}

	public Covid(String region, int confirmed, int death, double conPercentage, double deathPercentage, String rating,
			int ranking) {
		super();
		this.region = region;
		this.confirmed = confirmed;
		this.death = death;
		this.conPercentage = conPercentage;
		this.deathPercentage = deathPercentage;
		this.rating = rating;
		this.ranking = ranking;
	}

	public Covid(String region, int confirmed, int death, double conPercentage, double deathPercentage, String rating,
			int ranking, double inoculation) {
		super();
		this.region = region;
		this.confirmed = confirmed;
		this.death = death;
		this.conPercentage = conPercentage;
		this.deathPercentage = deathPercentage;
		this.rating = rating;
		this.ranking = ranking;
		this.inoculation = inoculation;
	}

	//메소드 (hashcode, equals, compared)
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(int confirmed) {
		this.confirmed = confirmed;
	}

	public int getDeath() {
		return death;
	}

	public void setDeath(int death) {
		this.death = death;
	}

	public double getconpercentage() {
		return conPercentage;
	}

	public void setconpercentage(int conpercentage) {
		this.conPercentage = conpercentage;
	}

	public double getDeathPercentage() {
		return deathPercentage;
	}

	public void setDeathPercentage(int deathPercentage) {
		this.deathPercentage = deathPercentage;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public int getranking() {
		return ranking;
	}

	public void setranking(int ranking) {
		this.ranking = ranking;
	}
	
	public double getInoculation() {
		return inoculation;
	}

	public void setInoculation(double inoculation) {
		this.inoculation = inoculation;
	}

	@Override
	public int hashCode() {
		return this.region.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Covid)) return false;
		return this.region.equalsIgnoreCase(((Covid)obj).region);
	}

	@Override
	public int compareTo(Covid corona) {
		return this.region.compareToIgnoreCase(corona.region);
	}

	@Override
	public String toString() {
		return region + "\t" + confirmed + "\t\t" + death + "\t\t"
				+ conPercentage + "%" + "\t\t" + deathPercentage + "%" + "\t\t" + rating + "\t"
				+ ranking + "\t" + inoculation;
	}

}
