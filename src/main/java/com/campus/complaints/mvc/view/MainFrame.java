package com.campus.complaints.mvc.view;

import com.campus.complaints.component.NotificationBanner;
import com.campus.complaints.designpatterns.mediator.ComplaintMediator;
import com.campus.complaints.model.User;
import com.campus.complaints.util.AppTheme;
import com.campus.complaints.util.IconFactory;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main application frame shell in the MVC architecture.
 * Provides sidebar navigation, top header, content card deck,
 * and notification banner.
 */
public class MainFrame extends JFrame {

    private final JPanel sidebarPanel;
    private final JPanel headerPanel;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;
    private final NotificationBanner notificationBanner;

    private final ButtonGroup navGroup = new ButtonGroup();
    private JToggleButton activeSidebarBtn;

    private final JLabel pageTitle;
    private final JLabel userLabel;

    public MainFrame() {
        super("Smart Campus Complaint System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1280, 800);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(AppTheme.BG_PAGE);

        sidebarPanel = buildSidebar();
        root.add(sidebarPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setBackground(AppTheme.BG_PAGE);

        pageTitle   = new JLabel("Dashboard");
        userLabel   = new JLabel();
        headerPanel = buildHeader();
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        notificationBanner = new NotificationBanner();

        JPanel rightCenter = new JPanel(new BorderLayout(0, 0));
        rightCenter.setBackground(AppTheme.BG_PAGE);
        rightCenter.add(notificationBanner, BorderLayout.NORTH);

        contentLayout = new CardLayout();
        contentPanel  = new JPanel(contentLayout);
        contentPanel.setBackground(AppTheme.BG_PAGE);
        rightCenter.add(contentPanel, BorderLayout.CENTER);

        rightPanel.add(rightCenter, BorderLayout.CENTER);
        root.add(rightPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(AppTheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return sidebar;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_CARD);
        header.setPreferredSize(new Dimension(0, AppTheme.HEADER_HEIGHT));
        header.setBorder(new MatteBorder(0, 0, 1, 0, AppTheme.BORDER));

        pageTitle.setFont(AppTheme.FONT_HEADING);
        pageTitle.setForeground(AppTheme.TEXT_PRIMARY);
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, AppTheme.SPACING_LG, 0, 0));

        userLabel.setFont(AppTheme.FONT_BODY);
        userLabel.setForeground(AppTheme.TEXT_SECONDARY);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, AppTheme.SPACING_LG));
        userLabel.setIcon(IconFactory.user(IconFactory.SIZE_MD, AppTheme.TEXT_SECONDARY));
        userLabel.setIconTextGap(6);

        header.add(pageTitle, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);
        return header;
    }

    public void buildSidebarForUser(User user,
                                    ActionListener onDashboard,
                                    ActionListener onComplaints,
                                    ActionListener onSubmit,
                                    ActionListener onStaff,
                                    ActionListener onLogout) {
        sidebarPanel.removeAll();
        navGroup.clearSelection();

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 18));
        logo.setBackground(AppTheme.BG_SIDEBAR);
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel logoText = new JLabel("CampusDesk");
        logoText.setFont(AppTheme.FONT_SIDEBAR_BOLD);
        logoText.setForeground(Color.WHITE);
        logoText.setIcon(IconFactory.complaints(IconFactory.SIZE_MD, Color.WHITE));
        logoText.setIconTextGap(8);
        logo.add(logoText);
        sidebarPanel.add(logo);

        sidebarPanel.add(createSidebarDivider());

        JToggleButton btnDash = createNavButton("Dashboard",
                IconFactory.dashboard(IconFactory.SIZE_MD, AppTheme.TEXT_SIDEBAR), onDashboard);
        sidebarPanel.add(btnDash);

        JToggleButton btnComplaints = createNavButton("Complaints",
                IconFactory.complaints(IconFactory.SIZE_MD, AppTheme.TEXT_SIDEBAR), onComplaints);
        sidebarPanel.add(btnComplaints);

        if (user.isStudent()) {
            JToggleButton btnSubmit = createNavButton("Submit Complaint",
                    IconFactory.submit(IconFactory.SIZE_MD, AppTheme.TEXT_SIDEBAR), onSubmit);
            sidebarPanel.add(btnSubmit);
        }

        if (user.isAdmin()) {
            JToggleButton btnStaff = createNavButton("Staff Management",
                    IconFactory.staff(IconFactory.SIZE_MD, AppTheme.TEXT_SIDEBAR), onStaff);
            sidebarPanel.add(btnStaff);
        }

        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(createSidebarDivider());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setIcon(IconFactory.logout(IconFactory.SIZE_MD, AppTheme.TEXT_SIDEBAR));
        logoutBtn.setFont(AppTheme.FONT_SIDEBAR);
        logoutBtn.setForeground(AppTheme.TEXT_SIDEBAR);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setIconTextGap(10);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 16));
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                logoutBtn.setForeground(Color.WHITE);
                logoutBtn.setOpaque(true);
                logoutBtn.setBackground(AppTheme.BG_SIDEBAR_SEL);
            }
            @Override public void mouseExited(MouseEvent e) {
                logoutBtn.setForeground(AppTheme.TEXT_SIDEBAR);
                logoutBtn.setOpaque(false);
            }
        });
        logoutBtn.addActionListener(onLogout);
        sidebarPanel.add(logoutBtn);
        sidebarPanel.add(Box.createVerticalStrut(8));

        btnDash.setSelected(true);
        activeSidebarBtn = btnDash;

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private JToggleButton createNavButton(String text, Icon icon, ActionListener action) {
        JToggleButton btn = new JToggleButton(text);
        btn.setIcon(icon);
        btn.setFont(AppTheme.FONT_SIDEBAR);
        btn.setForeground(AppTheme.TEXT_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(10);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 16));

        navGroup.add(btn);

        btn.addActionListener(e -> {
            activeSidebarBtn = btn;
            if (action != null) action.actionPerformed(e);
        });

        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(AppTheme.BG_SIDEBAR_SEL);
                btn.setForeground(Color.WHITE);
                btn.setOpaque(true);
            } else {
                btn.setBackground(AppTheme.BG_SIDEBAR);
                btn.setForeground(AppTheme.TEXT_SIDEBAR);
                btn.setOpaque(false);
            }
        });

        return btn;
    }

    private JSeparator createSidebarDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BG_SIDEBAR_SEL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    public void addView(String name, JComponent view) {
        contentPanel.add(view, name);
    }

    public void showView(String name) {
        contentLayout.show(contentPanel, name);
    }

    public void setPageTitle(String title) {
        pageTitle.setText(title);
    }

    public void setUserLabel(User user) {
        userLabel.setText(user.getName() + "  |  " + user.getRole().getDisplayName());
    }

    public void showNotification(String message, ComplaintMediator.NotificationType type) {
        notificationBanner.show(message, type);
    }

    public NotificationBanner getNotificationBanner() {
        return notificationBanner;
    }
}
