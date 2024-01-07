package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MyLinkedList {
    private MyNode head;
    private Set<String> deletedNodes;

    public MyLinkedList() {
        this.head = null;
        this.deletedNodes = new HashSet<>();
    }

    public MyNode getHeadElement() {
        return head;
    }

    public synchronized void append(String id, int score, String country) {
        if (score == -1 || deletedNodes.contains(id)) {
            if (!deletedNodes.contains(id)) {
                delete(id);
                deletedNodes.add(id);
            }
            return;
        }
        MyNode newMyNode = new MyNode(id, score, country);
        if (head == null) {
            head = newMyNode;
            return;
        }
        MyNode alreadyExistingMyNode = search(id);
        if (alreadyExistingMyNode != null) {
            alreadyExistingMyNode.score += score;
            recalibrateList();
            return;
        }

        MyNode lastMyNode = head;
        while (lastMyNode.next != null) {
            lastMyNode = lastMyNode.next;
        }
        lastMyNode.next = newMyNode;
        recalibrateList();
    }

    private synchronized void delete(String id) {
        MyNode currentMyNode = head;
        MyNode prevMyNode = null;
        if (currentMyNode != null && Objects.equals(currentMyNode.id, id)) {
            head = currentMyNode.next;
            return;
        }
        while (currentMyNode != null && !Objects.equals(currentMyNode.id, id)) {
            prevMyNode = currentMyNode;
            currentMyNode = currentMyNode.next;
        }
        if (currentMyNode == null) {
            return;
        }
        prevMyNode.next = currentMyNode.next;
    }

    private MyNode search(String id) {
        MyNode currentMyNode = head;
        while (currentMyNode != null) {
            if (currentMyNode.id.equals(id)) {
                return currentMyNode;
            }
            currentMyNode = currentMyNode.next;
        }
        return null;
    }

    private synchronized void recalibrateList() {
        if (head == null || head.next == null) {
            return;
        }
        MyNode current = head;
        while (current.next != null) {
            MyNode index = current.next;
            while (index != null) {
                if (current.score < index.score || (current.score == index.score && current.id.compareTo(index.id) < 0)) {
                    int tempScore = current.score;
                    current.score = index.score;
                    index.score = tempScore;
                    String tempId = current.id;
                    current.id = index.id;
                    index.id = tempId;
                    String tempC = current.country;
                    current.country = index.country;
                    index.country = tempC;
                }
                index = index.next;
            }
            current = current.next;
        }
    }

    public void display() {
        MyNode currentMyNode = head;
        while (currentMyNode != null) {
            System.out.println("(" + currentMyNode.id + ", " + currentMyNode.score + ")");
            currentMyNode = currentMyNode.next;
        }
    }

    public void printToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            MyNode currentMyNode = head;
            while (currentMyNode != null) {
                writer.write(currentMyNode.id + "," + currentMyNode.score + "," + currentMyNode.country + "\n");
                currentMyNode = currentMyNode.next;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}