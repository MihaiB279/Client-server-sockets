package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CountryList {
    private CountryNode head;

    public CountryList() {
        this.head = null;
    }

    public synchronized void append(int score, String country) {
        CountryNode newMyNode = new CountryNode(country, score);
        if (head == null) {
            head = newMyNode;
            return;
        }
        CountryNode alreadyExistingMyNode = search(country);
        if(alreadyExistingMyNode != null){
            alreadyExistingMyNode.score += score;
            return;
        }

        CountryNode lastMyNode = head;
        while (lastMyNode.next != null) {
            lastMyNode = lastMyNode.next;
        }
        lastMyNode.next = newMyNode;
    }
    public CountryNode getHeadElement(){
        return head;
    }
    private CountryNode search(String country) {
        CountryNode currentMyNode = head;
        while (currentMyNode != null) {
            if (currentMyNode.country.equals(country)) {
                return currentMyNode;
            }
            currentMyNode = currentMyNode.next;
        }
        return null;
    }

    public synchronized void recalibrateList() {
        if (head == null || head.next == null) {
            return;
        }
        CountryNode current = head;
        while (current.next != null) {
            CountryNode index = current.next;
            while (index != null) {
                if (current.score < index.score || (current.score == index.score && current.country.compareTo(index.country) < 0)) {
                    int tempScore = current.score;
                    current.score = index.score;
                    index.score = tempScore;
                    String tempCountry = current.country;
                    current.country = index.country;
                    index.country = tempCountry;
                }
                index = index.next;
            }
            current = current.next;
        }
    }

    public void display() {
        CountryNode currentMyNode = head;
        while (currentMyNode != null) {
            System.out.println("(" + currentMyNode.country + ", " + currentMyNode.score + ")");
            currentMyNode = currentMyNode.next;
        }
    }

    public void printToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            CountryNode currentMyNode = head;
            while (currentMyNode != null) {
                writer.write(currentMyNode.country + "," + currentMyNode.score + "\n");
                currentMyNode = currentMyNode.next;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
