package com.campus.complaints.designpatterns.prototype;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintStatus;

import java.time.LocalDateTime;

/**
 * <b>GoF Prototype Pattern</b> — Client/Cloner Component.
 *
 * <p>Handles cloning of complaints for recurring campus issues.
 * Ensures the duplicated complaint has clean lifecycle parameters:
 * <ul>
 *   <li>Resets primary key ID to 0 (unsaved new entity)</li>
 *   <li>Resets status to {@link ComplaintStatus#PENDING}</li>
 *   <li>Clears assigned staff ID, resolution timestamps, and resolution notes</li>
 *   <li>Appends ' (Copy)' to title</li>
 *   <li>Sets creation and update timestamps to now</li>
 * </ul>
 */
public class ComplaintCloner {

    /**
     * Clones an existing complaint using its prototype implementation
     * and performs verification.
     */
    public static Complaint cloneComplaint(Complaint original) {
        if (original == null) {
            throw new IllegalArgumentException("Original complaint to clone cannot be null.");
        }
        return original.deepCopy();
    }

    /**
     * Clones an existing complaint and customizes its title and location.
     */
    public static Complaint cloneWithLocation(Complaint original, String newLocation) {
        Complaint copy = cloneComplaint(original);
        if (newLocation != null && !newLocation.isBlank()) {
            copy.setLocation(newLocation.trim());
        }
        return copy;
    }
}
