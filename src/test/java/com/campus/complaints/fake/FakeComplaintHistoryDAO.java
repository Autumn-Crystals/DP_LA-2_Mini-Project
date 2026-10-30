package com.campus.complaints.fake;

import com.campus.complaints.dao.ComplaintHistoryDAO;
import com.campus.complaints.designpatterns.memento.ComplaintMemento;

import java.util.*;

/**
 * In-memory test double for {@link ComplaintHistoryDAO}.
 */
public class FakeComplaintHistoryDAO extends ComplaintHistoryDAO {

    private final Map<Integer, List<ComplaintMemento>> storage = new HashMap<>();

    @Override
    public void saveHistory(ComplaintMemento memento, String previousStateJson, String newStateJson) {
        storage.computeIfAbsent(memento.getComplaintId(), k -> new ArrayList<>()).add(0, memento);
    }

    @Override
    public List<ComplaintMemento> loadHistory(int complaintId, int limit) {
        List<ComplaintMemento> list = storage.getOrDefault(complaintId, Collections.emptyList());
        if (list.size() <= limit) return new ArrayList<>(list);
        return new ArrayList<>(list.subList(0, limit));
    }
}
