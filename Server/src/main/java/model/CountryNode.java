package model;

public class CountryNode {
    public int score;
    public String country;
    public CountryNode next;

    CountryNode(String country, int score) {
        this.score = score;
        this.country = country;
    }
}
