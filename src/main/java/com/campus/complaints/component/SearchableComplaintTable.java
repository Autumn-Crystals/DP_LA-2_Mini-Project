package com.campus.complaints.component;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import com.campus.complaints.util.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Reusable searchable table component displaying complaints with live search,
 * status filtering, and custom badge renderers.
 */
public class SearchableComplaintTable extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField;
    private final JComboBox<String> statusFilter;
    private final JComboBox<String> categoryFilter;

    private List<Complaint> currentComplaints = new ArrayList<>();
    private Consumer<Complaint> onRowSelected;

    private static final String[] COLUMNS = {
            "#ID", "Title", "Category", "Location", "Priority", "Status", "Staff Assigned"
    };

    public SearchableComplaintTable() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        // Filter toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Search complaints...");
        searchField.setFont(AppTheme.FONT_REGULAR);

        statusFilter = new JComboBox<>();
        statusFilter.addItem("All Statuses");
        for (ComplaintStatus s : ComplaintStatus.values()) {
            statusFilter.addItem(s.name());
        }

        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All Categories");
        for (ComplaintCategory c : ComplaintCategory.values()) {
            categoryFilter.addItem(c.name());
        }

        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);
        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);
        toolbar.add(new JLabel("Category:"));
        toolbar.add(categoryFilter);

        add(toolbar, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.setFont(AppTheme.FONT_REGULAR);
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(AppTheme.FONT_REGULAR.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(AppTheme.BG_HOVER);

        // Custom renderers for status & priority
        table.getColumnModel().getColumn(4).setCellRenderer(new PriorityCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Event listeners
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        statusFilter.addActionListener(e -> applyFilters());
        categoryFilter.addActionListener(e -> applyFilters());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && onRowSelected != null) {
                    Complaint selected = getSelectedComplaint();
                    if (selected != null) {
                        onRowSelected.accept(selected);
                    }
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && onRowSelected != null) {
                Complaint selected = getSelectedComplaint();
                if (selected != null) {
                    onRowSelected.accept(selected);
                }
            }
        });
    }

    public void setOnRowSelected(Consumer<Complaint> listener) {
        this.onRowSelected = listener;
    }

    public void setComplaints(List<Complaint> complaints) {
        this.currentComplaints = new ArrayList<>(complaints);
        refreshTableData();
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        for (Complaint c : currentComplaints) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getTitle(),
                    c.getCategory().name(),
                    c.getLocation(),
                    c.getPriority().name(),
                    c.getStatus().name(),
                    c.getAssignedStaffId() != null ? "#" + c.getAssignedStaffId() : "Unassigned"
            });
        }
    }

    private void applyFilters() {
        String query = searchField.getText().trim();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String selectedCat = (String) categoryFilter.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + query));
        }
        if (selectedStatus != null && !selectedStatus.equals("All Statuses")) {
            filters.add(RowFilter.regexFilter(selectedStatus, 5));
        }
        if (selectedCat != null && !selectedCat.equals("All Categories")) {
            filters.add(RowFilter.regexFilter(selectedCat, 2));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    public Complaint getSelectedComplaint() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return null;
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int complaintId = (int) tableModel.getValueAt(modelRow, 0);

        return currentComplaints.stream()
                .filter(c -> c.getId() == complaintId)
                .findFirst()
                .orElse(null);
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value != null) {
                try {
                    ComplaintStatus status = ComplaintStatus.valueOf(value.toString());
                    return StatusBadge.ofStatus(status);
                } catch (IllegalArgumentException ignored) {}
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value != null) {
                try {
                    Priority priority = Priority.valueOf(value.toString());
                    return StatusBadge.ofPriority(priority);
                } catch (IllegalArgumentException ignored) {}
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
