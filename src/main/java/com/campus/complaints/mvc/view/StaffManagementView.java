package com.campus.complaints.mvc.view;

import com.campus.complaints.component.CardPanel;
import com.campus.complaints.component.StyledButton;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.util.AppTheme;
import com.campus.complaints.util.IconFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Staff management view in the MVC architecture.
 * Displays maintenance technicians, load, skills, and provides add/edit actions.
 */
public class StaffManagementView extends JPanel {

    private static final String[] COLUMNS = {
        "ID", "Name", "Email", "Phone", "Skills", "Availability", "Active Complaints", "Actions"
    };

    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<MaintenanceStaff> currentData = new ArrayList<>();

    private ActionListener onEdit;
    private ActionListener onAddNew;

    public StaffManagementView() {
        setBackground(AppTheme.BG_PAGE);
        setLayout(new BorderLayout(0, AppTheme.SPACING_MD));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Title + add button
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel("Staff Management");
        title.setFont(AppTheme.FONT_LARGE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titleBar.add(title, BorderLayout.WEST);

        StyledButton addBtn = new StyledButton("Add Staff Member", StyledButton.Variant.PRIMARY,
                IconFactory.add(IconFactory.SIZE_MD, Color.WHITE));
        addBtn.addActionListener(e -> { if (onAddNew != null) onAddNew.actionPerformed(e); });
        titleBar.add(addBtn, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };

        table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(AppTheme.PRIMARY_LIGHT);
        table.setFillsViewportHeight(true);
        table.setBackground(AppTheme.BG_CARD);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0xF8FAFC));
        th.setForeground(AppTheme.TEXT_SECONDARY);
        th.setFont(AppTheme.FONT_SMALL);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                    boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? AppTheme.BG_CARD : new Color(0xFAFAFC));
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

                if (col == 5 && val != null) {
                    String v = val.toString();
                    if (v.contains("Available")) setForeground(AppTheme.SUCCESS);
                    else if (v.contains("Unavailable")) setForeground(AppTheme.ERROR);
                    else setForeground(AppTheme.WARNING);
                } else {
                    setForeground(AppTheme.TEXT_PRIMARY);
                }
                return c;
            }
        });

        table.getColumn("Actions").setCellRenderer(new ActionCellRenderer());
        table.getColumn("Actions").setCellEditor(new ActionCellEditor());

        int[] widths = {40, 150, 160, 110, 200, 110, 120, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        CardPanel tableCard = new CardPanel();
        tableCard.setLayout(new BorderLayout());

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(new Color(0xF8FAFC));
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        JLabel tblLabel = new JLabel("Maintenance Staff");
        tblLabel.setFont(AppTheme.FONT_SUBHEAD);
        tblLabel.setForeground(AppTheme.TEXT_PRIMARY);
        tableHeader.add(tblLabel, BorderLayout.WEST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void loadStaff(List<MaintenanceStaff> staff) {
        this.currentData = staff;
        tableModel.setRowCount(0);
        for (MaintenanceStaff s : staff) {
            String skills = s.getSkills().stream()
                    .map(c -> c.getDisplayName())
                    .reduce((a, b) -> a + ", " + b).orElse("None");
            tableModel.addRow(new Object[]{
                s.getId(),
                s.getName(),
                s.getEmail(),
                s.getPhone() != null ? s.getPhone() : "-",
                skills,
                s.getAvailabilityStatus().getDisplayName(),
                s.getActiveComplaintCount(),
                s.getId()
            });
        }
    }

    public MaintenanceStaff getStaffAtRow(int row) {
        if (row < 0 || row >= currentData.size()) return null;
        return currentData.get(row);
    }

    public void setOnEditListener(ActionListener l)    { this.onEdit = l; }
    public void setOnAddNewListener(ActionListener l)  { this.onAddNew = l; }

    private class ActionCellRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            return buildBtn(row);
        }
    }

    private class ActionCellEditor extends DefaultCellEditor {
        private JButton btn;
        private int editRow;
        ActionCellEditor() { super(new JCheckBox()); }

        @Override public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
            editRow = row;
            btn = buildBtn(row);
            return btn;
        }
        @Override public Object getCellEditorValue() { return editRow; }
    }

    private JButton buildBtn(int row) {
        JButton btn = new JButton(IconFactory.edit(IconFactory.SIZE_MD, AppTheme.PRIMARY));
        btn.setToolTipText("Edit Staff");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            table.setRowSelectionInterval(row, row);
            if (onEdit != null) onEdit.actionPerformed(
                    new java.awt.event.ActionEvent(this, 0, String.valueOf(row)));
        });
        return btn;
    }
}
