package com.vis.controller;

import com.vis.database.VehicleDAO;
import com.vis.model.ServiceRecord;
import com.vis.model.Vehicle;
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

public class VehicleController {

    private VehicleDAO dao;

    private TableView<Vehicle>       vehicleTable;
    private ObservableList<Vehicle>  vehicleData;
    private TextField                tfReg, tfMake, tfModel, tfYear;
    private ComboBox<String>         ownerCombo;

    private TableView<ServiceRecord>      serviceTable;
    private ObservableList<ServiceRecord> serviceData;
    private ComboBox<String>             vehicleCombo;
    private DatePicker                   dpServiceDate;
    private TextField                    tfServiceType, tfCost;
    private TextArea                     taDescription;

    private Label statusLabel;

    public Node buildView() {
        try { dao = new VehicleDAO(); }
        catch (Exception e) { return errorNode("Cannot connect: " + e.getMessage()); }

        VBox root = new VBox(18);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: transparent;");

        VBox header = new VBox(4);
        Label title = new Label("Vehicles & Services");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: " + TEXT_PRI + ";");
        Label sub = new Label("Manage registered vehicles and service history");
        sub.setFont(Font.font("Verdana", 12));
        sub.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        header.getChildren().addAll(title, sub);

        statusLabel = new Label(" ");
        statusLabel.setFont(Font.font("Verdana", 12));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: transparent;");
        tabs.getTabs().addAll(
                new Tab("Vehicles", buildVehicleTab()),
                new Tab("Services", buildServiceTab())
        );

        root.getChildren().addAll(header, statusLabel, tabs);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        loadVehicles(); loadServices(); loadOwnerCombo(); refreshVehicleCombo();
        return root;
    }

    // ── Vehicle tab ───────────────────────────────────────────────────────────
    private VBox buildVehicleTab() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: transparent;");

        VBox formCard = card("Add / Edit Vehicle");
        formCard.getChildren().addAll(buildVehicleForm(), buildVehicleButtons());

        vehicleTable = buildVehicleTable();
        VBox tableCard = card("Vehicle List");
        tableCard.getChildren().add(vehicleTable);
        VBox.setVgrow(vehicleTable, Priority.ALWAYS);

        box.getChildren().addAll(formCard, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        return box;
    }

    private GridPane buildVehicleForm() {
        GridPane g = grid();
        tfReg   = tf("e.g. ABC-1234"); tfMake  = tf("e.g. Toyota");
        tfModel = tf("e.g. Camry");    tfYear  = tf("e.g. 2022");
        ownerCombo = new ComboBox<>();
        ownerCombo.setPromptText("Select Owner"); ownerCombo.setPrefWidth(200);
        styleCombo(ownerCombo);

        g.add(lbl("Registration:"), 0, 0); g.add(tfReg,      1, 0);
        g.add(lbl("Make:"),         2, 0); g.add(tfMake,     3, 0);
        g.add(lbl("Model:"),        0, 1); g.add(tfModel,    1, 1);
        g.add(lbl("Year:"),         2, 1); g.add(tfYear,     3, 1);
        g.add(lbl("Owner:"),        0, 2); g.add(ownerCombo, 1, 2);
        return g;
    }

    private HBox buildVehicleButtons() {
        Button btnAdd    = actionBtn("Add",    ACCENT_BLUE,  ACCENT_BLUE_L,  "#B8CCE0");
        Button btnUpdate = actionBtn("Update", ACCENT_GREEN, ACCENT_GREEN_L, "#B8D8C8");
        Button btnDelete = actionBtn("Delete", ACCENT_RED,   ACCENT_RED_L,   "#E8C0C0");
        Button btnClear  = actionBtn("Clear",  ACCENT_GREY,  ACCENT_GREY_L,  BORDER);

        FadeTransition ft = new FadeTransition(Duration.millis(1200), btnAdd);
        ft.setFromValue(0.70); ft.setToValue(1.0);
        ft.setAutoReverse(true); ft.setCycleCount(FadeTransition.INDEFINITE); ft.play();

        btnAdd.setOnAction(e    -> { ft.stop(); btnAdd.setOpacity(1); addVehicle();    ft.play(); });
        btnUpdate.setOnAction(e -> updateVehicle());
        btnDelete.setOnAction(e -> deleteVehicle());
        btnClear.setOnAction(e  -> clearVehicleForm());

        HBox box = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<Vehicle> buildVehicleTable() {
        TableView<Vehicle> tv = styledTable();
        tv.setPrefHeight(280);
        tv.getColumns().addAll(
                vcol("ID",           "id",                 60),
                vcol("Registration", "registrationNumber", 130),
                vcol("Make",         "make",               100),
                vcol("Model",        "model",              100),
                vcol("Year",         "year",               70),
                vcol("Owner",        "ownerName",          160)
        );
        tv.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfReg.setText(sel.getRegistrationNumber());
                tfMake.setText(sel.getMake()); tfModel.setText(sel.getModel());
                tfYear.setText(String.valueOf(sel.getYear()));
                ownerCombo.getItems().stream()
                        .filter(s -> s.startsWith(sel.getOwnerId() + " | "))
                        .findFirst().ifPresent(ownerCombo::setValue);
            }
        });
        vehicleData = FXCollections.observableArrayList();
        tv.setItems(vehicleData);
        return tv;
    }

    // ── Service tab ───────────────────────────────────────────────────────────
    private VBox buildServiceTab() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: transparent;");

        VBox formCard = card("Add Service Record");
        formCard.getChildren().addAll(buildServiceForm(), buildServiceButtons());

        serviceTable = buildServiceTable();
        VBox tableCard = card("Service History");
        tableCard.getChildren().add(serviceTable);
        VBox.setVgrow(serviceTable, Priority.ALWAYS);

        box.getChildren().addAll(formCard, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        return box;
    }

    private GridPane buildServiceForm() {
        GridPane g = grid();
        vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle"); vehicleCombo.setPrefWidth(220);
        styleCombo(vehicleCombo);
        dpServiceDate = new DatePicker(LocalDate.now());
        dpServiceDate.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;"
        );
        tfServiceType = tf("e.g. Oil Change");
        tfCost        = tf("0.00");
        taDescription = new TextArea();
        taDescription.setPromptText("Service details...");
        taDescription.setPrefRowCount(2); taDescription.setPrefWidth(300);
        taDescription.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-prompt-text-fill: " + TEXT_HINT + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        g.add(lbl("Vehicle:"),     0, 0); g.add(vehicleCombo,  1, 0);
        g.add(lbl("Date:"),        2, 0); g.add(dpServiceDate, 3, 0);
        g.add(lbl("Type:"),        0, 1); g.add(tfServiceType, 1, 1);
        g.add(lbl("Cost (M):"),    2, 1); g.add(tfCost,        3, 1);
        g.add(lbl("Description:"), 0, 2); g.add(taDescription, 1, 2, 3, 1);
        return g;
    }

    private HBox buildServiceButtons() {
        Button btnAdd    = actionBtn("Add Record", ACCENT_BLUE,  ACCENT_BLUE_L,  "#B8CCE0");
        Button btnDelete = actionBtn("Delete",     ACCENT_RED,   ACCENT_RED_L,   "#E8C0C0");
        Button btnClear  = actionBtn("Clear",      ACCENT_GREY,  ACCENT_GREY_L,  BORDER);
        btnAdd.setOnAction(e    -> addService());
        btnDelete.setOnAction(e -> deleteService());
        btnClear.setOnAction(e  -> clearServiceForm());
        HBox box = new HBox(10, btnAdd, btnDelete, btnClear);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<ServiceRecord> buildServiceTable() {
        TableView<ServiceRecord> tv = styledTable();
        tv.setPrefHeight(280);
        tv.getColumns().addAll(
                scol("ID",          "serviceId",   60),
                scol("Vehicle",     "vehicleReg",  120),
                scol("Date",        "serviceDate", 100),
                scol("Type",        "serviceType", 130),
                scol("Description", "description", 200),
                scol("Cost (M)",    "cost",        90)
        );
        serviceData = FXCollections.observableArrayList();
        tv.setItems(serviceData);
        return tv;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    private void addVehicle() {
        try {
            if (tfReg.getText().isBlank())     throw new IllegalArgumentException("Registration is required.");
            if (ownerCombo.getValue() == null) throw new IllegalArgumentException("Select an owner.");
            if (tfYear.getText().isBlank())    throw new IllegalArgumentException("Year is required.");
            int ownerId = Integer.parseInt(ownerCombo.getValue().split(" \\| ")[0]);
            dao.addVehicle(new Vehicle(0, tfReg.getText().trim(), tfMake.getText().trim(),
                    tfModel.getText().trim(), Integer.parseInt(tfYear.getText().trim()), ownerId, ""));
            loadVehicles(); refreshVehicleCombo(); clearVehicleForm();
            setStatus("Vehicle added.", false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus("Database error: " + e.getMessage(), true); }
    }

    private void updateVehicle() {
        try {
            Vehicle sel = vehicleTable.getSelectionModel().getSelectedItem();
            if (sel == null)                   throw new IllegalArgumentException("Select a vehicle first.");
            if (ownerCombo.getValue() == null) throw new IllegalArgumentException("Select an owner.");
            int ownerId = Integer.parseInt(ownerCombo.getValue().split(" \\| ")[0]);
            sel.setRegistrationNumber(tfReg.getText().trim()); sel.setMake(tfMake.getText().trim());
            sel.setModel(tfModel.getText().trim()); sel.setYear(Integer.parseInt(tfYear.getText().trim()));
            sel.setOwnerId(ownerId);
            dao.updateVehicle(sel);
            loadVehicles(); refreshVehicleCombo(); clearVehicleForm();
            setStatus("Vehicle updated.", false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus("Database error: " + e.getMessage(), true); }
    }

    private void deleteVehicle() {
        Vehicle sel = vehicleTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a vehicle.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + sel.getRegistrationNumber() + "?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText("Confirm"); a.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                try { dao.deleteVehicle(sel.getId()); loadVehicles(); refreshVehicleCombo(); clearVehicleForm();
                    setStatus("Vehicle deleted.", false);
                } catch (SQLException e) { setStatus(e.getMessage(), true); }
            }
        });
    }

    private void clearVehicleForm() {
        tfReg.clear(); tfMake.clear(); tfModel.clear(); tfYear.clear();
        ownerCombo.setValue(null); vehicleTable.getSelectionModel().clearSelection();
    }

    private void addService() {
        try {
            if (vehicleCombo.getValue() == null)  throw new IllegalArgumentException("Select a vehicle.");
            if (tfServiceType.getText().isBlank()) throw new IllegalArgumentException("Service type required.");
            if (tfCost.getText().isBlank())        throw new IllegalArgumentException("Cost required.");
            int vid = Integer.parseInt(vehicleCombo.getValue().split(" \\| ")[0]);
            dao.addServiceRecord(new ServiceRecord(0, vid, "",
                    dpServiceDate.getValue().toString(), tfServiceType.getText().trim(),
                    taDescription.getText().trim(), Double.parseDouble(tfCost.getText().trim())));
            loadServices(); clearServiceForm();
            setStatus("Service record added.", false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus("Database error: " + e.getMessage(), true); }
    }

    private void deleteService() {
        ServiceRecord sel = serviceTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a record.", true); return; }
        try { dao.deleteServiceRecord(sel.getServiceId()); loadServices(); setStatus("Record deleted.", false);
        } catch (SQLException e) { setStatus(e.getMessage(), true); }
    }

    private void clearServiceForm() {
        vehicleCombo.setValue(null); dpServiceDate.setValue(LocalDate.now());
        tfServiceType.clear(); tfCost.clear(); taDescription.clear();
        serviceTable.getSelectionModel().clearSelection();
    }

    // ── Loaders ───────────────────────────────────────────────────────────────
    private void loadVehicles() {
        try { vehicleData.setAll(dao.getAllVehicles()); }
        catch (SQLException e) { setStatus("Load error: " + e.getMessage(), true); }
    }

    private void loadServices() {
        try { serviceData.setAll(dao.getAllServiceRecords()); }
        catch (SQLException e) { setStatus("Load error: " + e.getMessage(), true); }
    }

    private void loadOwnerCombo() {
        try { ownerCombo.getItems().setAll(dao.getCustomerOptions()); }
        catch (SQLException e) { setStatus("Could not load owners.", true); }
    }

    private void refreshVehicleCombo() {
        try { List<String> opts = dao.getVehicleOptions(); vehicleCombo.getItems().setAll(opts); }
        catch (SQLException e) { setStatus("Could not load vehicles.", true); }
    }

    // ── UI helpers ─────────────────────────────────────────────────────────────
    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setStyle(
                "-fx-text-fill: " + (error ? ACCENT_RED : ACCENT_GREEN) + "; -fx-font-size: 12px;"
        );
    }

    private <T> TableColumn<Vehicle, T> vcol(String t, String p, int w) {
        TableColumn<Vehicle, T> c = new TableColumn<>(t);
        c.setCellValueFactory(new PropertyValueFactory<>(p)); c.setPrefWidth(w); return c;
    }

    private <T> TableColumn<ServiceRecord, T> scol(String t, String p, int w) {
        TableColumn<ServiceRecord, T> c = new TableColumn<>(t);
        c.setCellValueFactory(new PropertyValueFactory<>(p)); c.setPrefWidth(w); return c;
    }

    private <T> TableView<T> styledTable() {
        TableView<T> tv = new TableView<>();
        tv.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;"
        );
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
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

    private void styleCombo(ComboBox<String> c) {
        c.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
    }

    private Node errorNode(String msg) {
        Label l = new Label(msg); l.setStyle("-fx-text-fill: " + ACCENT_RED + "; -fx-font-size: 14px;");
        VBox v = new VBox(l); v.setPadding(new Insets(40)); return v;
    }
}