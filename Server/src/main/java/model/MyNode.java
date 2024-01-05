package model;

public class MyNode {
    public String id;
    public int score;
    public String country;
    public MyNode next;

    MyNode(String id, int score, String country) {
        this.id = id;
        this.score = score;
        this.country = country;
    }
}