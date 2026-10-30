package com.campus.complaints.util;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralised design system constants for the application UI.
 * Defines the colour palette, typography, and spacing scale.
 */
public final class AppTheme {

    private AppTheme() {}

    // ---- Palette ----
    public static final Color BG_PAGE        = new Color(0xF8FAFC);
    public static final Color BG_CARD        = Color.WHITE;
    public static final Color BG_SIDEBAR     = new Color(0x1E293B);
    public static final Color BG_SIDEBAR_SEL = new Color(0x334155);

    public static final Color PRIMARY        = new Color(0x2563EB);
    public static final Color PRIMARY_DARK   = new Color(0x1E293B);
    public static final Color PRIMARY_HOVER  = new Color(0x1D4ED8);
    public static final Color PRIMARY_LIGHT  = new Color(0xEFF6FF);
    public static final Color BG_HOVER       = new Color(0xF1F5F9);

    public static final Color TEXT_PRIMARY   = new Color(0x1E293B);
    public static final Color TEXT_SECONDARY = new Color(0x64748B);
    public static final Color TEXT_MUTED     = new Color(0x94A3B8);
    public static final Color TEXT_SIDEBAR   = new Color(0xCBD5E1);
    public static final Color TEXT_SIDEBAR_ACTIVE = Color.WHITE;

    public static final Color BORDER         = new Color(0xE2E8F0);
    public static final Color BORDER_FOCUS   = new Color(0x93C5FD);

    public static final Color SUCCESS        = new Color(0x16A34A);
    public static final Color SUCCESS_BG     = new Color(0xDCFCE7);
    public static final Color WARNING        = new Color(0xD97706);
    public static final Color WARNING_BG     = new Color(0xFEF3C7);
    public static final Color ERROR          = new Color(0xDC2626);
    public static final Color ERROR_BG       = new Color(0xFEE2E2);
    public static final Color INFO           = new Color(0x0891B2);
    public static final Color INFO_BG        = new Color(0xE0F2FE);

    // Status badge colours
    public static final Color STATUS_PENDING_FG    = new Color(0xD97706);
    public static final Color STATUS_PENDING_BG    = new Color(0xFEF3C7);
    public static final Color STATUS_ASSIGNED_FG   = new Color(0x2563EB);
    public static final Color STATUS_ASSIGNED_BG   = new Color(0xEFF6FF);
    public static final Color STATUS_INPROGRESS_FG = new Color(0x7C3AED);
    public static final Color STATUS_INPROGRESS_BG = new Color(0xF5F3FF);
    public static final Color STATUS_RESOLVED_FG   = new Color(0x16A34A);
    public static final Color STATUS_RESOLVED_BG   = new Color(0xDCFCE7);
    public static final Color STATUS_CANCELLED_FG  = new Color(0x6B7280);
    public static final Color STATUS_CANCELLED_BG  = new Color(0xF3F4F6);

    // Priority badge colours
    public static final Color PRIORITY_LOW_FG      = new Color(0x16A34A);
    public static final Color PRIORITY_LOW_BG      = new Color(0xDCFCE7);
    public static final Color PRIORITY_MED_FG      = new Color(0x2563EB);
    public static final Color PRIORITY_MED_BG      = new Color(0xEFF6FF);
    public static final Color PRIORITY_HIGH_FG     = new Color(0xD97706);
    public static final Color PRIORITY_HIGH_BG     = new Color(0xFEF3C7);
    public static final Color PRIORITY_CRIT_FG     = new Color(0xDC2626);
    public static final Color PRIORITY_CRIT_BG     = new Color(0xFEE2E2);

    // ---- Typography ----
    public static final Font FONT_LARGE   = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_TITLE   = FONT_LARGE;
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_REGULAR = FONT_BODY;
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SECONDARY = FONT_SMALL;
    public static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN, 12);
    public static final Font FONT_SIDEBAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BADGE   = new Font("Segoe UI", Font.BOLD,  11);

    // ---- Spacing ----
    public static final int SPACING_XS  = 4;
    public static final int SPACING_SM  = 8;
    public static final int SPACING_MD  = 16;
    public static final int SPACING_LG  = 24;
    public static final int SPACING_XL  = 32;

    public static final int SIDEBAR_WIDTH = 220;
    public static final int HEADER_HEIGHT = 60;
    public static final int CARD_RADIUS   = 10;
    public static final int BTN_RADIUS    = 6;
}
