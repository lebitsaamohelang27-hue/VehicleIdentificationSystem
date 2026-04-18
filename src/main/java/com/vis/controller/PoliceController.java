package com.vis.controller;

import com.vis.database.PoliceDAO;
import com.vis.database.VehicleDAO;
import com.vis.model.PoliceReport;
import com.vis.model.Violation;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.vis.MainApp.*;

public class PoliceController {

    private PoliceDAO  policeDAO;
    private VehicleDAO vehicleDAO;

    private TableView<PoliceReport>      reportTable;
    private ObservableList<PoliceReport> reportData;
    private ComboBox<String> reportVehicleCombo;
    private DatePicker       dpReportDate;
    private ComboBox<String> reportTypeCombo;
    private TextField        tfOfficerName;
    private TextArea         taReportDesc;

    private TableView<Violation>      violationTable;
    private ObservableList<Violation> violationData;
    private ComboBox<String> violVehicleCombo;
    private DatePicker       dpViolDate;
    private ComboBox<String> violTypeCombo;
    private TextField        tfFine;

    private Label statusLabel;

    public Node buildView() {
        try { policeDAO = new PoliceDAO(); vehicleDAO = new VehicleDAO(); }
        catch (Exception e) { return errorNode("Cannot connect: " + e.getMessage()); }

        VBox root = new VBox(18);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: transparent;");

        VBox header = new VBox(4);
        Label title = new Label("Police Module");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: " + TEXT_PRI + ";");
        Label sub = new Label("Manage police reports and traffic violations");
        sub.setFont(Font.font("Verdana", 12));
        sub.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        header.getChildren().addAll(title, sub);

        statusLabel = new Label(" ");
        statusLabel.setFont(Font.font("Verdana", 12));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: transparent;");
        tabs.getTabs().addAll(
                new Tab("Police Reports", buildReportTab()),
                new Tab("Violations",     buildViolationTab())
        );

        root.getChildren().addAll(header, statusLabel, tabs);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        loadVehicleOptions(); loadReports(); loadViolations();
        return root;
    }

    // ── Reports tab ───────────────────────────────────────────────────────────
    private VBox buildReportTab() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: transparent;");

        VBox formCard = card("File Police Report");
        formCard.getChildren().addAll(buildReportForm(), buildReportButtons());

        reportTable = buildReportTable();
        VBox tableCard = card("Police Reports");
        tableCard.getChildren().add(reportTable);
        VBox.setVgrow(reportTable, Priority.ALWAYS);

        box.getChildren().addAll(formCard, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        return box;
    }

    private GridPane buildReportForm() {
        GridPane g = grid();
        reportVehicleCombo = styledCombo("Select Vehicle", 220);
        dpReportDate = datePicker(LocalDate.now());
        reportTypeCombo = styledCombo("Report Type", 180);
        reportTypeCombo.getItems().addAll("Accident", "Theft", "Vandalism", "Fraud", "Other");
        tfOfficerName = tf("Officer Full Name");
        taReportDesc  = styledArea("Describe the incident...");

        g.add(lbl("Vehicle:"),      0, 0); g.add(reportVehicleCombo, 1, 0);
        g.add(lbl("Date:"),         2, 0); g.add(dpReportDate,       3, 0);
        g.add(lbl("Report Type:"),  0, 1); g.add(reportTypeCombo,    1, 1);
        g.add(lbl("Officer Name:"), 2, 1); g.add(tfOfficerName,      3, 1);
        g.add(lbl("Description:"),  0, 2); g.add(taReportDesc,       1, 2, 3, 1);
        return g;
    }

    private HBox buildReportButtons() {
        Button btnAdd    = actionBtn("File Report", ACCENT_BLUE,  ACCENT_BLUE_L,  "#B8CCE0");
        Button btnDelete = actionBtn("Delete",      ACCENT_RED,   ACCENT_RED_L,   "#E8C0C0");
        Button btnClear  = actionBtn("Clear",       ACCENT_GREY,  ACCENT_GREY_L,  BORDER);

        FadeTransition ft = new FadeTransition(Duration.millis(1200), btnAdd);
        ft.setFromValue(0.70); ft.setToValue(1.0);
        ft.setAutoReverse(true); ft.setCycleCount(FadeTransition.INDEFINITE); ft.play();

        btnAdd.setOnAction(e    -> { ft.stop(); btnAdd.setOpacity(1); addReport(); ft.play(); });
        btnDelete.setOnAction(e -> deleteReport());
        btnClear.setOnAction(e  -> clearReportForm());

        HBox box = new HBox(10, btnAdd, btnDelete, btnClear);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<PoliceReport> buildReportTable() {
        TableView<PoliceReport> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(260);
        tv.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;"
        );
        tv.getColumns().addAll(
                rcol("ID",          "reportId",    60),
                rcol("Vehicle",     "vehicleReg",  120),
                rcol("Date",        "reportDate",  100),
                rcol("Type",        "reportType",  100),
                rcol("Officer",     "officerName", 140),
                rcol("Description", "description", 200)
        );
        reportData = FXCollections.observableArrayList();
        tv.setItems(reportData);
        return tv;
    }

    // ── Violations tab ────────────────────────────────────────────────────────
    private VBox buildViolationTab() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: transparent;");

        VBox formCard = card("Record Violation");
        formCard.getChildren().addAll(buildViolationForm(), buildViolationButtons());

        violationTable = buildViolationTable();
        VBox tableCard = card("Violations");
        tableCard.getChildren().add(violationTable);
        VBox.setVgrow(violationTable, Priority.ALWAYS);

        box.getChildren().addAll(formCard, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        return box;
    }

    private GridPane buildViolationForm() {
        GridPane g = grid();
        violVehicleCombo = styledCombo("Select Vehicle", 220);
        dpViolDate = datePicker(LocalDate.now());
        violTypeCombo = styledCombo("Violation Type", 180);
        violTypeCombo.getItems().addAll("Speeding", "Parking", "DUI", "No License", "No Insurance", "Other");
        tfFine = tf("0.00");

        g.add(lbl("Vehicle:"),  0, 0); g.add(violVehicleCombo, 1, 0);
        g.add(lbl("Date:"),     2, 0); g.add(dpViolDate,       3, 0);
        g.add(lbl("Type:"),     0, 1); g.add(violTypeCombo,    1, 1);
        g.add(lbl("Fine (M):"), 2, 1); g.add(tfFine,           3, 1);
        return g;
    }

    private HBox buildViolationButtons() {
        Button btnAdd      = actionBtn("Record Violation", ACCENT_RED,   ACCENT_RED_L,   "#E8C0C0");
        Button btnMarkPaid = actionBtn("Mark Paid",        ACCENT_GREEN, ACCENT_GREEN_L, "#B8D8C8");
        Button btnDelete   = actionBtn("Delete",           ACCENT_GREY,  ACCENT_GREY_L,  BORDER);

        FadeTransition ft = new FadeTransition(Duration.millis(1200), btnAdd);
        ft.setFromValue(0.70); ft.setToValue(1.0);
        ft.setAutoReverse(true); ft.setCycleCount(FadeTransition.INDEFINITE); ft.play();

        btnAdd.setOnAction(e      -> { ft.stop(); btnAdd.setOpacity(1); addViolation(); ft.play(); });
        btnMarkPaid.setOnAction(e -> markPaid());
        btnDelete.setOnAction(e   -> deleteViolation());

        HBox box = new HBox(10, btnAdd, btnMarkPaid, btnDelete);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<Violation> buildViolationTable() {
        TableView<Violation> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(260);
        tv.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;"
        );
        tv.getColumns().addAll(
                vcol("ID",       "violationId",   60),
                vcol("Vehicle",  "vehicleReg",    120),
                vcol("Date",     "violationDate", 100),
                vcol("Type",     "violationType", 120),
                vcol("Fine (M)", "fineAmount",    90),
                vcol("Status",   "status",        90)
        );
        violationData = FXCollections.observableArrayList();
        tv.setItems(violationData);
        return tv;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    private void addReport() {
        try {
            if (reportVehicleCombo.getValue() == null) throw new IllegalArgumentException("Select a vehicle.");
            if (reportTypeCombo.getValue() == null)    throw new IllegalArgumentException("Select a report type.");
            if (tfOfficerName.getText().isBlank())     throw new IllegalArgumentException("Officer name required.");
            int vid = Integer.parseInt(reportVehicleCombo.getValue().split(" \\| ")[0]);
            policeDAO.addReport(new PoliceReport(0, vid, "", dpReportDate.getValue().toString(),
                    reportTypeCombo.getValue(), taReportDesc.getText().trim(), tfOfficerName.getText().trim()));
            loadReports(); clearReportForm();
            setStatus("Report filed.", false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus(e.getMessage(), true); }
    }

    private void deleteReport() {
        PoliceReport sel = reportTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a report.", true); return; }
        try { policeDAO.deleteReport(sel.getReportId()); loadReports(); setStatus("Report deleted.", false);
        } catch (SQLException e) { setStatus(e.getMessage(), true); }
    }

    private void clearReportForm() {
        reportVehicleCombo.setValue(null); reportTypeCombo.setValue(null);
        dpReportDate.setValue(LocalDate.now()); tfOfficerName.clear(); taReportDesc.clear();
        reportTable.getSelectionModel().clearSelection();
    }

    private void addViolation() {
        try {
            if (violVehicleCombo.getValue() == null) throw new IllegalArgumentException("Select a vehicle.");
            if (violTypeCombo.getValue() == null)    throw new IllegalArgumentException("Select a violation type.");
            if (tfFine.getText().isBlank())          throw new IllegalArgumentException("Fine amount required.");
            int vid = Integer.parseInt(violVehicleCombo.getValue().split(" \\| ")[0]);
            policeDAO.addViolation(new Violation(0, vid, "", dpViolDate.getValue().toString(),
                    violTypeCombo.getValue(), Double.parseDouble(tfFine.getText().trim()), "Unpaid"));
            loadViolations(); clearViolationForm();
            setStatus("Violation recorded.", false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus(e.getMessage(), true); }
    }

    private void markPaid() {
        Violation sel = violationTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a violation.", true); return; }
        try { policeDAO.updateViolationStatus(sel.getViolationId(), "Paid"); loadViolations();
            setStatus("Marked as Paid.", false);
        } catch (SQLException e) { setStatus(e.getMessage(), true); }
    }

    private void deleteViolation() {
        Violation sel = violationTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a violation.", true); return; }
        try { policeDAO.deleteViolation(sel.getViolationId()); loadViolations(); setStatus("Violation deleted.", false);
        } catch (SQLException e) { setStatus(e.getMessage(), true); }
    }

    private void clearViolationForm() {
        violVehicleCombo.setValue(null); violTypeCombo.setValue(null);
        dpViolDate.setValue(LocalDate.now()); tfFine.clear();
        violationTable.getSelectionModel().clearSelection();
    }

    // ── Loaders ───────────────────────────────────────────────────────────────
    private void loadReports() {
        try { reportData.setAll(policeDAO.getAllReports()); }
        catch (SQLException e) { setStatus(e.getMessage(), true); }
    }

    private void loadViolations() {
        try { violationData.setAll(policeDAO.getAllViolations()); }
        catch (SQLException e) { setStatus(e.getMessage(), true); }
    }

    private void loadVehicleOptions() {
        try {
            List<String> opts = vehicleDAO.getVehicleOptions();
            reportVehicleCombo.getItems().setAll(opts);
            violVehicleCombo.getItems().setAll(opts);
        } catch (SQLException e) { setStatus("Could not load vehicles.", true); }
    }

    // ── UI helpers ─────────────────────────────────────────────────────────────
    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setStyle(
                "-fx-text-fill: " + (error ? ACCENT_RED : ACCENT_GREEN) + "; -fx-font-size: 12px;"
        );
    }

    private <T> TableColumn<PoliceReport, T> rcol(String t, String p, int w) {
        TableColumn<PoliceReport, T> c = new TableColumn<>(t);
        c.setCellValueFactory(new PropertyValueFactory<>(p)); c.setPrefWidth(w); return c;
    }

    private <T> TableColumn<Violation, T> vcol(String t, String p, int w) {
        TableColumn<Violation, T> c = new TableColumn<>(t);
        c.setCellValueFactory(new PropertyValueFactory<>(p)); c.setPrefWidth(w); return c;
    }

    private VBox card(String titleText) {
        VBox v = new VBox(14); v.setPadding(new Insets(20));
        v.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );
        Label t = new Label(titleText);
        t.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        t.setStyle("-fx-text-fill: " + TEXT_PRI + ";");
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + BORDER + ";");
        v.getChildren().addAll(t, sep);
        return v;
    }

    private GridPane grid() { GridPane g = new GridPane(); g.setHgap(16); g.setVgap(12); return g; }

    private TextField tf(String prompt) {
        TextField t = new TextField(); t.setPromptText(prompt); t.setPrefWidth(170);
        t.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-prompt-text-fill: " + TEXT_HINT + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 9 12;"
        );
        return t;
    }

    private DatePicker datePicker(LocalDate value) {
        DatePicker dp = new DatePicker(value);
        dp.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;"
        );
        return dp;
    }

    private ComboBox<String> styledCombo(String prompt, int width) {
        ComboBox<String> c = new ComboBox<>();
        c.setPromptText(prompt); c.setPrefWidth(width);
        c.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
        return c;
    }

    private TextArea styledArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt); ta.setPrefRowCount(2); ta.setPrefWidth(300);
        ta.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-prompt-text-fill: " + TEXT_HINT + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
        return ta;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        l.setStyle("-fx-text-fill: " + TEXT_SEC + ";"); return l;
    }

    private Button actionBtn(String text, String fg, String bg, String border) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + fg + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: Verdana;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 9 18;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: " + border + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        return b;
    }

    private Node errorNode(String msg) {
        Label l = new Label(msg); l.setStyle("-fx-text-fill: " + ACCENT_RED + "; -fx-font-size: 14px;");
        VBox v = new VBox(l); v.setPadding(new Insets(40)); return v;
    }
}