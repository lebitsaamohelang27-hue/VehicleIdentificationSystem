package com.vis.controller;

import com.vis.database.DatabaseManager;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.sql.*;

import static com.vis.MainApp.*;

public class DashboardController {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public Node buildView() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox root = new VBox(22);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: transparent;");

        // Page title
        VBox titleBox = new VBox(4);
        Label title = new Label("Dashboard Overview");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: " + TEXT_PRI + ";");
        Label subtitle = new Label("Real-time statistics from your database");
        subtitle.setFont(Font.font("Verdana", 12));
        subtitle.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        titleBox.getChildren().addAll(title, subtitle);

        GridPane statsGrid     = buildStatsGrid();
        HBox     progressSection = buildProgressSection();
        VBox     activitySection = buildActivitySection();

        root.getChildren().addAll(titleBox, statsGrid, progressSection, activitySection);
        scroll.setContent(root);
        return scroll;
    }

    // ── Stats cards ──────────────────────────────────────────────────────────
    private GridPane buildStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(14);

        // {label, value, accentColor, lightColor, iconText}
        Object[][] stats = {
                {"Total Vehicles",    count("Vehicle"),       ACCENT_BLUE,  ACCENT_BLUE_L,  "🚗"},
                {"Total Customers",   count("Customer"),      ACCENT_GREEN, ACCENT_GREEN_L, "👥"},
                {"Service Records",   count("ServiceRecord"), "#7A5F2A",    "#F5EFE0",      "🔧"},
                {"Unpaid Violations", unpaidViolations(),     ACCENT_RED,   ACCENT_RED_L,   "⚠"},
                {"Active Policies",   activePolicies(),       "#3D5C7A",    "#E8EFF6",      "📄"},
                {"Total Revenue (M)", totalRevenue(),         ACCENT_GREY,  ACCENT_GREY_L,  "💰"},
        };

        for (int i = 0; i < stats.length; i++) {
            VBox card = statCard(
                    (String) stats[i][0],
                    (String) stats[i][1],
                    (String) stats[i][2],
                    (String) stats[i][3],
                    (String) stats[i][4]
            );
            grid.add(card, i % 3, i / 3);
            GridPane.setHgrow(card, Priority.ALWAYS);
        }
        return grid;
    }

    private VBox statCard(String title, String value, String accent, String lightBg, String icon) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setMinWidth(200);
        card.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPrefSize(42, 42);
        iconBox.setStyle(
                "-fx-background-color: " + lightBg + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 10;"
        );
        Label ico = new Label(icon);
        ico.setStyle("-fx-font-size: 18px;");
        iconBox.getChildren().add(ico);

        VBox textBox = new VBox(3);
        Label val = new Label(value);
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        val.setStyle("-fx-text-fill: " + accent + ";");
        Label ttl = new Label(title);
        ttl.setFont(Font.font("Verdana", 11));
        ttl.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        textBox.getChildren().addAll(val, ttl);
        top.getChildren().addAll(iconBox, textBox);
        card.getChildren().add(top);
        return card;
    }

    // ── Progress section ─────────────────────────────────────────────────────
    private HBox buildProgressSection() {
        HBox box = new HBox(24);
        box.setPadding(new Insets(22));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        box.getChildren().addAll(
                sectionTitle("System Performance"),
                progressCard("Database Load",      0.75, ACCENT_BLUE),
                progressCard("System Performance", 0.88, ACCENT_GREEN),
                progressCard("Storage Used",       0.52, ACCENT_RED),
                spinnerCard()
        );
        return box;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        l.setStyle("-fx-text-fill: " + TEXT_PRI + ";");
        l.setMinWidth(140);
        return l;
    }

    private VBox progressCard(String label, double val, String color) {
        VBox v = new VBox(7);
        v.setPrefWidth(190);
        Label l = new Label(label);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        l.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        ProgressBar pb = new ProgressBar(val);
        pb.setPrefWidth(190);
        pb.setStyle(
                "-fx-accent: " + color + ";" +
                        "-fx-control-inner-background: " + BG_PAGE + ";" +
                        "-fx-background-radius: 4;" +
                        "-fx-pref-height: 6px;"
        );
        Label pct = new Label(Math.round(val * 100) + "%");
        pct.setFont(Font.font("Verdana", 10));
        pct.setStyle("-fx-text-fill: " + color + ";");
        v.getChildren().addAll(l, pb, pct);
        return v;
    }

    private VBox spinnerCard() {
        VBox v = new VBox(7);
        v.setAlignment(Pos.CENTER);
        Label l = new Label("Live Sync");
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        l.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        ProgressIndicator pi = new ProgressIndicator();
        pi.setPrefSize(40, 40);
        pi.setStyle("-fx-progress-color: " + ACCENT_BLUE + ";");
        v.getChildren().addAll(l, pi);
        return v;
    }

    // ── Activity log ─────────────────────────────────────────────────────────
    private VBox buildActivitySection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(22));
        section.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        Label title = new Label("Recent Activity Log");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: " + TEXT_PRI + ";");

        String[] activities = {
                "🚗  Vehicle ABC-001 registered by Officer Mokoena",
                "👤  Customer Thabiso Mofokeng profile created",
                "🔧  Oil change service completed on XYZ-202",
                "⚠   Speeding violation issued to vehicle DEF-303",
                "📄  Insurance policy POL-2024-001 activated",
                "👤  Customer Lineo Ntho details updated",
                "🗑   Vehicle GHI-404 removed from system",
                "🚔  Police report filed: Theft — vehicle JKL-505",
                "💰  Fine paid for violation #00127",
                "📄  Insurance policy POL-2024-002 renewed",
                "🔧  Brake service completed on MNO-606",
                "👤  Customer Khotso Lerotholi added",
                "🚔  Report filed: Accident — vehicle PQR-707",
                "📄  Insurance policy POL-2024-003 renewed",
                "🚗  Vehicle owner transfer: STU-808",
                "⚠   Parking violation issued to VWX-909",
                "🔧  Engine service completed on YZA-010",
                "👤  Customer Palesa Molapo updated",
                "🚗  Vehicle year updated for BCD-111",
                "✅  System database backup completed"
        };

        int itemsPerPage = 5;
        int pages = (int) Math.ceil((double) activities.length / itemsPerPage);

        Pagination pagination = new Pagination(pages, 0);
        pagination.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        pagination.setPageFactory(pageIdx -> {
            VBox pageContent = new VBox(6);
            pageContent.setPadding(new Insets(4, 0, 4, 0));
            int start = pageIdx * itemsPerPage;
            int end   = Math.min(start + itemsPerPage, activities.length);
            for (int i = start; i < end; i++) {
                Label item = new Label(activities[i]);
                item.setFont(Font.font("Verdana", 12));
                item.setStyle(
                        "-fx-padding: 10 14;" +
                                "-fx-background-color: " + BG_PAGE + ";" +
                                "-fx-background-radius: 7;" +
                                "-fx-text-fill: " + TEXT_PRI + ";" +
                                "-fx-border-color: " + BORDER + ";" +
                                "-fx-border-radius: 7;" +
                                "-fx-border-width: 1;"
                );
                item.setMaxWidth(Double.MAX_VALUE);
                pageContent.getChildren().add(item);
            }
            ScrollPane sp = new ScrollPane(pageContent);
            sp.setFitToWidth(true);
            sp.setPrefHeight(230);
            sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            return sp;
        });

        section.getChildren().addAll(title, pagination);
        return section;
    }

    // ── DB helpers ───────────────────────────────────────────────────────────
    private String count(String table) {
        try (Connection c = db.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (r.next()) return String.valueOf(r.getInt(1));
        } catch (SQLException e) { System.err.println("Count [" + table + "]: " + e.getMessage()); }
        return "0";
    }

    private String unpaidViolations() {
        try (Connection c = db.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COUNT(*) FROM Violation WHERE status='Unpaid'")) {
            if (r.next()) return String.valueOf(r.getInt(1));
        } catch (SQLException e) { System.err.println("Violations: " + e.getMessage()); }
        return "0";
    }

    private String activePolicies() {
        try (Connection c = db.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COUNT(*) FROM InsurancePolicy WHERE end_date >= CURRENT_DATE")) {
            if (r.next()) return String.valueOf(r.getInt(1));
        } catch (SQLException e) { System.err.println("Policies: " + e.getMessage()); }
        return "0";
    }

    private String totalRevenue() {
        try (Connection c = db.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COALESCE(SUM(cost),0) FROM ServiceRecord")) {
            if (r.next()) return String.format("%.2f", r.getDouble(1));
        } catch (SQLException e) { System.err.println("Revenue: " + e.getMessage()); }
        return "0.00";
    }
}