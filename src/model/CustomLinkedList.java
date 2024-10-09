package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList {

    private Node head;
    private Node tail;
    private final Map<Integer, Node> history = new HashMap<>();

    public void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);;
        }
        int id = task.getId();
        history.put(id, newNode);
    }

    public List<Task> getTasks(){
        List<Task> list = new ArrayList<>();
        Node current = head;
        while(current != null) {
            list.add(current.getTask());
            current = current.getNext();
        }
        return list;
    }

    public void removeNode(Node node) {
        Node next = node.getNext();
        Node prev = node.getPrev();
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setNext(null);
        }
        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }
        node.setTask(null);
    }

    public Map<Integer, Node> getHistory() {
        return history;
    }
}
