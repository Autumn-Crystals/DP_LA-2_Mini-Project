package com.campus.complaints.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for vector-drawn icons using Java2D.
 *
 * <p>All icons are drawn programmatically with consistent stroke widths
 * and the application colour palette. This avoids external icon fonts or
 * runtime SVG loading, making the application fully self-contained.
 *
 * <p>Icons follow a 20x20 design grid at size 16 by default.
 */
public final class IconFactory {

    private IconFactory() {}

    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    public static final int SIZE_SM = 14;
    public static final int SIZE_MD = 16;
    public static final int SIZE_LG = 20;

    // ---- Named icon getters ----

    public static Icon dashboard(int size, Color color) { return create("dashboard", size, color); }
    public static Icon complaints(int size, Color color) { return create("complaints", size, color); }
    public static Icon submit(int size, Color color)     { return create("submit", size, color); }
    public static Icon staff(int size, Color color)      { return create("staff", size, color); }
    public static Icon logout(int size, Color color)     { return create("logout", size, color); }
    public static Icon view(int size, Color color)       { return create("view", size, color); }
    public static Icon edit(int size, Color color)       { return create("edit", size, color); }
    public static Icon duplicate(int size, Color color)  { return create("duplicate", size, color); }
    public static Icon undo(int size, Color color)       { return create("undo", size, color); }
    public static Icon assign(int size, Color color)     { return create("assign", size, color); }
    public static Icon search(int size, Color color)     { return create("search", size, color); }
    public static Icon filter(int size, Color color)     { return create("filter", size, color); }
    public static Icon check(int size, Color color)      { return create("check", size, color); }
    public static Icon warning(int size, Color color)    { return create("warning", size, color); }
    public static Icon error(int size, Color color)      { return create("error", size, color); }
    public static Icon info(int size, Color color)       { return create("info", size, color); }
    public static Icon resolve(int size, Color color)    { return create("resolve", size, color); }
    public static Icon add(int size, Color color)        { return create("add", size, color); }
    public static Icon user(int size, Color color)       { return create("user", size, color); }
    public static Icon refresh(int size, Color color)    { return create("refresh", size, color); }

    // ---- Core factory ----

    private static Icon create(String name, int size, Color color) {
        String key = name + "_" + size + "_" + color.getRGB();
        return CACHE.computeIfAbsent(key, k -> drawIcon(name, size, color));
    }

    private static ImageIcon drawIcon(String name, int size, Color color) {
        int s = size;
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(s, s, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setColor(color);
        float stroke = Math.max(1.2f, s / 13f);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (name) {
            case "dashboard" -> drawDashboard(g, s);
            case "complaints" -> drawComplaints(g, s);
            case "submit" -> drawSubmit(g, s);
            case "staff" -> drawStaff(g, s);
            case "logout" -> drawLogout(g, s);
            case "view" -> drawView(g, s);
            case "edit" -> drawEdit(g, s);
            case "duplicate" -> drawDuplicate(g, s);
            case "undo" -> drawUndo(g, s);
            case "assign" -> drawAssign(g, s);
            case "search" -> drawSearch(g, s);
            case "filter" -> drawFilter(g, s);
            case "check" -> drawCheck(g, s);
            case "warning" -> drawWarning(g, s);
            case "error" -> drawErrorIcon(g, s);
            case "info" -> drawInfoIcon(g, s);
            case "resolve" -> drawResolve(g, s);
            case "add" -> drawAdd(g, s);
            case "user" -> drawUser(g, s);
            case "refresh" -> drawRefresh(g, s);
            default -> drawPlaceholder(g, s);
        }

        g.dispose();
        return new ImageIcon(img);
    }

    private static void drawDashboard(Graphics2D g, int s) {
        int p = s / 6;
        int m = s / 2 - p / 2;
        // Four squares layout
        g.drawRoundRect(p, p, m - p, m - p, 2, 2);
        g.drawRoundRect(m + p/2, p, m - p, m - p, 2, 2);
        g.drawRoundRect(p, m + p/2, m - p, m - p, 2, 2);
        g.drawRoundRect(m + p/2, m + p/2, m - p, m - p, 2, 2);
    }

    private static void drawComplaints(Graphics2D g, int s) {
        int p = s / 8;
        int r = s / 7;
        // Document shape
        g.drawRoundRect(p, p, s - 2*p, s - 2*p - r, r, r);
        // Lines
        g.drawLine(p*2, s/3, s - p*2, s/3);
        g.drawLine(p*2, s/2, s - p*2, s/2);
        g.drawLine(p*2, s*2/3, s*2/3, s*2/3);
        // Alert dot
        g.fillOval(s - p*2 - r, s - p - r, r+1, r+1);
    }

    private static void drawSubmit(Graphics2D g, int s) {
        int p = s / 6;
        // Plus sign
        g.drawLine(s/2, p, s/2, s - p);
        g.drawLine(p, s/2, s - p, s/2);
    }

    private static void drawStaff(Graphics2D g, int s) {
        int p = s / 6;
        // Head
        int hr = s / 5;
        g.drawOval(s/2 - hr, p, hr*2, hr*2);
        // Body arc
        g.drawArc(p, s/2, s - 2*p, s - p, 0, 180);
    }

    private static void drawLogout(Graphics2D g, int s) {
        int p = s / 6;
        // Arrow pointing right
        g.drawLine(s/3, s/2, s - p, s/2);
        g.drawLine(s - p, s/2, s - p - s/4, s/2 - s/5);
        g.drawLine(s - p, s/2, s - p - s/4, s/2 + s/5);
        // Door line
        g.drawLine(s/3, p, s/3, s - p);
        g.drawLine(p, p, p, s - p);
        g.drawLine(p, p, s/3, p);
        g.drawLine(p, s - p, s/3, s - p);
    }

    private static void drawView(Graphics2D g, int s) {
        int p = s / 6;
        // Eye shape
        g.drawArc(p, s/3, s - 2*p, s/3, 0, 180);
        g.drawArc(p, s/3, s - 2*p, s/3, 180, 180);
        // Pupil
        int pr = s / 7;
        g.drawOval(s/2 - pr, s/2 - pr, pr*2, pr*2);
    }

    private static void drawEdit(Graphics2D g, int s) {
        int p = s / 8;
        // Pencil shape
        int[] px = {p*2, s - p*2, s - p, p*3};
        int[] py = {s - p*2, p*2, p*3, s - p};
        g.drawPolygon(px, py, 4);
        g.drawLine(p*2, s - p*2, p, s - p);
    }

    private static void drawDuplicate(Graphics2D g, int s) {
        int p = s / 5;
        int offset = s / 4;
        // Two overlapping document outlines
        g.drawRoundRect(p, p + offset, s - p - offset, s - p - offset, 3, 3);
        g.drawRoundRect(offset, p, s - p - offset, s - p - offset, 3, 3);
    }

    private static void drawUndo(Graphics2D g, int s) {
        int p = s / 5;
        // Counter-clockwise arc
        g.drawArc(p, p, s - 2*p, s - 2*p, 0, 270);
        // Arrow head
        int ax = p + (s - 2*p)/2;
        int ay = p;
        g.drawLine(ax, ay, ax - s/5, ay + s/5);
        g.drawLine(ax, ay, ax + s/5, ay + s/5);
    }

    private static void drawAssign(Graphics2D g, int s) {
        int p = s / 6;
        // Person + arrow
        int hr = s / 5;
        g.drawOval(p, p, hr*2, hr*2);
        g.drawArc(p - s/8, p + hr*2, hr*2 + s/4, hr*2, 0, 180);
        // Arrow
        g.drawLine(s/2 + p, s/2, s - p, s/2);
        g.drawLine(s - p, s/2, s - p - s/5, s/2 - s/6);
        g.drawLine(s - p, s/2, s - p - s/5, s/2 + s/6);
    }

    private static void drawSearch(Graphics2D g, int s) {
        int r = (int)(s * 0.35);
        int cx = (int)(s * 0.42);
        int cy = (int)(s * 0.42);
        g.drawOval(cx - r, cy - r, r*2, r*2);
        int lx = (int)(cx + r * 0.7);
        int ly = (int)(cy + r * 0.7);
        g.drawLine(lx, ly, s - s/6, s - s/6);
    }

    private static void drawFilter(Graphics2D g, int s) {
        int p = s / 5;
        g.drawLine(p, p, s - p, p);
        g.drawLine(p*2, s/2, s - p*2, s/2);
        g.drawLine(p*3, s - p, s - p*3, s - p);
    }

    private static void drawCheck(Graphics2D g, int s) {
        int p = s / 5;
        g.drawLine(p, s/2, s*2/5, s*3/4);
        g.drawLine(s*2/5, s*3/4, s - p, p + s/8);
    }

    private static void drawWarning(Graphics2D g, int s) {
        int p = s / 8;
        int[] px = {s/2, s - p, p};
        int[] py = {p, s - p, s - p};
        g.drawPolygon(px, py, 3);
        g.drawLine(s/2, s/3, s/2, s*3/5);
        g.fillOval(s/2 - 1, s*3/5 + 3, 3, 3);
    }

    private static void drawErrorIcon(Graphics2D g, int s) {
        int p = s / 6;
        g.drawOval(p, p, s - 2*p, s - 2*p);
        g.drawLine(s/3, s/3, s*2/3, s*2/3);
        g.drawLine(s*2/3, s/3, s/3, s*2/3);
    }

    private static void drawInfoIcon(Graphics2D g, int s) {
        int p = s / 6;
        g.drawOval(p, p, s - 2*p, s - 2*p);
        g.drawLine(s/2, s/3, s/2, s/3 + 1);
        g.drawLine(s/2, s*4/9, s/2, s*2/3);
    }

    private static void drawResolve(Graphics2D g, int s) {
        int p = s / 6;
        g.drawOval(p, p, s - 2*p, s - 2*p);
        drawCheck(g, s);
    }

    private static void drawAdd(Graphics2D g, int s) {
        int p = s / 6;
        g.drawOval(p, p, s - 2*p, s - 2*p);
        g.drawLine(s/2, s/3, s/2, s*2/3);
        g.drawLine(s/3, s/2, s*2/3, s/2);
    }

    private static void drawUser(Graphics2D g, int s) {
        int p = s / 6;
        int hr = s / 4;
        g.drawOval(s/2 - hr, p, hr*2, hr*2);
        g.drawArc(p, s/2, s - 2*p, s/2, 0, 180);
    }

    private static void drawRefresh(Graphics2D g, int s) {
        int p = s / 5;
        g.drawArc(p, p, s - 2*p, s - 2*p, 45, 270);
        int ax = s - p;
        int ay = s/2 - s/8;
        g.drawLine(ax, ay, ax - s/5, ay - s/6);
        g.drawLine(ax, ay, ax - s/5, ay + s/6);
    }

    private static void drawPlaceholder(Graphics2D g, int s) {
        g.fillOval(s/4, s/4, s/2, s/2);
    }
}
