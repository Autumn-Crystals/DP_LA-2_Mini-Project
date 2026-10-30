package com.campus.complaints.designpatterns.prototype;

import com.campus.complaints.model.Complaint;

/**
 * <b>GoF Prototype Pattern</b> — Prototype Interface.
 *
 * <p>Defines the contract for creating duplicates of domain complaints.
 * Concrete prototypes produce independent copies with reset lifecycle states.
 */
public interface ComplaintPrototype {

    /**
     * Creates and returns an independent deep copy of this complaint.
     * The cloned instance is unassigned, has status PENDING, and a distinct identity.
     */
    Complaint deepCopy();
}
