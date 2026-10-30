package com.campus.complaints.designpatterns.memento;

import com.campus.complaints.config.ApplicationConfiguration;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <b>GoF Memento Pattern</b> — Caretaker.
 *
 * <p>Maintains an undo history stack of {@link ComplaintMemento} snapshots.
 * Enforces bounded stack size to prevent unbounded memory growth while supporting
 * multi-level undo operations.
 */
public class ComplaintHistory {

    private final Deque<ComplaintMemento> history = new ArrayDeque<>();
    private static final int MAX_STACK_SIZE = ApplicationConfiguration.MAX_UNDO_STACK_SIZE;

    /**
     * Pushes a new memento snapshot onto the history stack.
     */
    public void push(ComplaintMemento memento) {
        if (memento == null) return;
        history.push(memento);
        if (history.size() > MAX_STACK_SIZE) {
            ComplaintMemento[] arr = history.toArray(new ComplaintMemento[0]);
            history.clear();
            for (int i = 0; i < arr.length - 1; i++) {
                history.push(arr[i]);
            }
        }
    }

    /**
     * Pops and returns the most recent memento snapshot, or null if empty.
     */
    public ComplaintMemento pop() {
        return history.isEmpty() ? null : history.pop();
    }

    /**
     * Peeks at the top snapshot without removing it.
     */
    public ComplaintMemento peek() {
        return history.isEmpty() ? null : history.peek();
    }

    /**
     * Returns true when undo is possible.
     */
    public boolean canUndo() {
        return !history.isEmpty();
    }

    /**
     * Clears all recorded history.
     */
    public void clear() {
        history.clear();
    }

    public int size() {
        return history.size();
    }
}
