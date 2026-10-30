package com.campus.complaints.mvc.view;

import com.campus.complaints.component.BadgeLabel;
import com.campus.complaints.component.CardPanel;
import com.campus.complaints.component.StyledButton;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import com.campus.complaints.util.AppTheme;
import com.campus.complaints.util.IconFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Complaint management and listing view in the MVC architecture.
 * Provides a sortable, filterable table with action buttons for viewing details
 * and cloning/duplicating complaints (Prototype Pattern).
 */
public class ComplaintManagementView extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final String[] COLUMNS = {
        "ID", "Title", "Category", "Location", "Priority", "Status", "Date", "Actions"
    };

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private final JComboBox<String> statusFilter;
    private final JComboBox<String> categoryFilter;

    // Callbacks
    private ActionListener onView;
    private ActionListener onDuplicate;
    private List<Complaint> currentData;

    private final boolean isAdmin;

    public ComplaintManagementView(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setBackground(AppTheme.BG_PAGE);
        setLayout(new BorderLayout(0, AppTheme.SPACING_MD));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // ---- Title bar ----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel(isAdmin ? "Complaint Management" : "My Complaints");
        title.setFont(AppTheme.FONT_LARGE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // ---- Toolbar (search + filters) ----
        CardPanel toolbar = new CardPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        searchField = new JTextField(18);
        searchField.setFont(AppTheme.FONT_BODY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        searchField.putClientProperty("JTextField.placeholderText", "Search by ID or title...");

        StyledButton searchBtn = new StyledButton("Search", StyledButton.Variant.PRIMARY,
                IconFactory.search(IconFactory.SIZE_SM, Color.WHITE));
        searchBtn.addActionListener(e -> fireSearch());

        statusFilter   = buildComboFilter("All Statuses",
                ComplaintStatus.values(), s -> ((ComplaintStatus)s).getDisplayName());
        categoryFilter = buildComboFilter("All Categories",
                ComplaintCategory.values(), c -> ((ComplaintCategory)c).getDisplayName());

        StyledButton clearBtn = new StyledButton("Clear", StyledButton.Variant.GHOST,
                IconFactory.refresh(IconFactory.SIZE_SM, AppTheme.PRIMARY));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
            categoryFilter.setSelectedIndex(0);
            fireSearch();
        });

        toolbar.add(new JLabel(IconFactory.search(IconFactory.SIZE_MD, AppTheme.TEXT_SECONDARY)));
        toolbar.add(searchField);
        toolbar.add(searchBtn);
        toolbar.add(statusFilter);
        toolbar.add(categoryFilter);
        toolbar.add(clearBtn);

        // ---- Table ----
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
            @Override public Class<?> getColumnClass(int c) { return c == 7 ? JPanel.class : Object.class; }
        };

        table = new JTable(tableModel);
        styleTable();

        table.getColumn("Actions").setCellRenderer(new ActionCellRenderer());
        table.getColumn("Actions").setCellEditor(new ActionCellEditor());

        int[] widths = {50, 200, 110, 150, 80, 100, 90, 140};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        CardPanel tableCard = new CardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(buildTableHeader(), BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        removeAll();
        add(titleBar, BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout(0, AppTheme.SPACING_MD));
        middle.setOpaque(false);
        middle.add(toolbar, BorderLayout.NORTH);
        middle.add(tableCard, BorderLayout.CENTER);
        add(middle, BorderLayout.CENTER);
    }

    private JPanel buildTableHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0xF8FAFC));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        JLabel lbl = new JLabel("Complaints");
        lbl.setFont(AppTheme.FONT_SUBHEAD);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        header.add(lbl, BorderLayout.WEST);
        return header;
    }

    private void styleTable() {
        table.setFont(AppTheme.FONT_BODY);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setRowHeight(46);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(AppTheme.PRIMARY_LIGHT);
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setBackground(AppTheme.BG_CARD);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0xF8FAFC));
        th.setForeground(AppTheme.TEXT_SECONDARY);
        th.setFont(AppTheme.FONT_SMALL);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));
        th.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? AppTheme.BG_CARD : new Color(0xFAFAFC));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T> JComboBox<String> buildComboFilter(String allLabel, T[] values,
            java.util.function.Function<T, String> nameGetter) {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem(allLabel);
        for (T v : values) combo.addItem(nameGetter.apply(v));
        combo.setFont(AppTheme.FONT_BODY);
        combo.setBackground(AppTheme.BG_CARD);
        return combo;
    }

    public void loadComplaints(List<Complaint> complaints) {
        this.currentData = complaints;
        tableModel.setRowCount(0);
        for (Complaint c : complaints) {
            tableModel.addRow(new Object[]{
                "#" + c.getId(),
                c.getTitle(),
                c.getCategory().getDisplayName(),
                c.getLocation(),
                c.getPriority().getDisplayName(),
                c.getStatus().getDisplayName(),
                c.getCreatedAt() != null ? c.getCreatedAt().format(FMT) : "-",
                c.getId()
            });
        }
    }

    public void refreshData() {
        if (currentData != null) {
            loadComplaints(currentData);
        }
    }

    public String getSearchQuery() { return searchField.getText().trim(); }
    public int getStatusFilterIndex() { return statusFilter.getSelectedIndex(); }
    public int getCategoryFilterIndex() { return categoryFilter.getSelectedIndex(); }
    public ComplaintStatus getSelectedStatus() {
        int idx = statusFilter.getSelectedIndex();
        return idx == 0 ? null : ComplaintStatus.values()[idx - 1];
    }
    public ComplaintCategory getSelectedCategory() {
        int idx = categoryFilter.getSelectedIndex();
        return idx == 0 ? null : ComplaintCategory.values()[idx - 1];
    }

    public void addSearchListener(ActionListener l) {
        searchField.addActionListener(l);
        statusFilter.addActionListener(l::actionPerformed);
        categoryFilter.addActionListener(l::actionPerformed);
    }

    public void setOnViewListener(ActionListener l)       { this.onView = l; }
    public void setOnDuplicateListener(ActionListener l) { this.onDuplicate = l; }

    public Complaint getComplaintAtRow(int row) {
        if (currentData == null || row < 0 || row >= currentData.size()) return null;
        return currentData.get(row);
    }

    public int getSelectedRow() { return table.getSelectedRow(); }

    private void fireSearch() {
        searchField.postActionEvent();
    }

    private class ActionCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            return buildActionPanel(row);
        }
    }

    private class ActionCellEditor extends DefaultCellEditor {
        private int editRow;

        ActionCellEditor() { super(new JCheckBox()); }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
            editRow = row;
            return buildActionPanel(row);
        }

        @Override public Object getCellEditorValue() { return editRow; }
    }

    private JPanel buildActionPanel(int row) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
        p.setOpaque(true);
        p.setBackground(row % 2 == 0 ? AppTheme.BG_CARD : new Color(0xFAFAFC));

        JButton viewBtn = iconButton(IconFactory.view(IconFactory.SIZE_MD, AppTheme.PRIMARY),
                "View Details");
        viewBtn.addActionListener(e -> {
            table.setRowSelectionInterval(row, row);
            if (onView != null) onView.actionPerformed(
                    new java.awt.event.ActionEvent(this, 0, String.valueOf(row)));
        });

        JButton dupBtn = iconButton(IconFactory.duplicate(IconFactory.SIZE_MD, AppTheme.TEXT_SECONDARY),
                "Duplicate Complaint");
        dupBtn.addActionListener(e -> {
            table.setRowSelectionInterval(row, row);
            if (onDuplicate != null) onDuplicate.actionPerformed(
                    new java.awt.event.ActionEvent(this, 0, String.valueOf(row)));
        });

        p.add(viewBtn);
        p.add(dupBtn);
        return p;
    }

    private JButton iconButton(Icon icon, String tooltip) {
        JButton btn = new JButton(icon);
        btn.setToolTipText(tooltip);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(28, 28));
        return btn;
    }
}
