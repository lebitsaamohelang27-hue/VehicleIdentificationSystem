package com.vis.controller;

import com.vis.database.InsuranceDAO;
import com.vis.database.VehicleDAO;
import com.vis.model.Insurance;
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

public class InsuranceController {

    private InsuranceDAO insuranceDAO;
    private VehicleDAO   vehicleDAO;

    private TableView<Insurance>      table;
    private ObservableList<Insurance> data;

    private ComboBox<String> vehicleCombo;
    private TextField        tfPolicyNumber, tfCompany, tfPremium;
    private DatePicker       dpStart, dpEnd;
    private Label            statusLabel;

    public Node buildView() {
        try { insuranceDAO = new InsuranceDAO(); vehicleDAO = new VehicleDAO(); }
        catch (Exception e) { return errorNode("Cannot connect: " + e.getMessage()); }

        VBox root = new VBox(18);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: transparent;");

        VBox header = new VBox(4);
        Label title = new Label("Insurance Management");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: " + TEXT_PRI + ";");
        Label sub = new Label("Manage vehicle insurance policies");
        sub.setFont(Font.font("Verdana", 12));
        sub.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        header.getChildren().addAll(title, sub);

        statusLabel = new Label(" ");
        statusLabel.setFont(Font.font("Verdana", 12));

        VBox formCard  = buildFormCard();
        table = buildTable();
        VBox tableCard = card("Insurance Policies");
        tableCard.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(header, statusLabel, formCard, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        loadVehicleOptions();
        loadData();
        return root;
    }

    private VBox buildFormCard() {
        VBox c = card("Add / Edit Policy");
        GridPane g = grid();

        vehicleCombo   = styledCombo("Select Vehicle", 220);
        tfPolicyNumber = tf("e.g. POL-2024-001");
        tfCompany      = tf("e.g. Lesotho National Insurance");
        tfPremium      = tf("0.00");
        dpStart        = datePicker(LocalDate.now());
        dpEnd          = datePicker(LocalDate.now().plusYears(1));

        g.add(lbl("Vehicle:"),     0, 0); g.add(vehicleCombo,   1, 0);
        g.add(lbl("Policy No.:"),  2, 0); g.add(tfPolicyNumber, 3, 0);
        g.add(lbl("Company:"),     0, 1); g.add(tfCompany,      1, 1);
        g.add(lbl("Premium (M):"), 2, 1); g.add(tfPremium,      3, 1);
        g.add(lbl("Start Date:"),  0, 2); g.add(dpStart,        1, 2);
        g.add(lbl("End Date:"),    2, 2); g.add(dpEnd,          3, 2);

        c.getChildren().addAll(g, buildButtons());
        return c;
    }

    private HBox buildButtons() {
        Button btnAdd    = actionBtn("Add Policy", ACCENT_BLUE,  ACCENT_BLUE_L,  "#B8CCE0");
        Button btnUpdate = actionBtn("Update",     ACCENT_GREEN, ACCENT_GREEN_L, "#B8D8C8");
        Button btnDelete = actionBtn("Delete",     ACCENT_RED,   ACCENT_RED_L,   "#E8C0C0");
        Button btnClear  = actionBtn("Clear",      ACCENT_GREY,  ACCENT_GREY_L,  BORDER);

        FadeTransition ft = new FadeTransition(Duration.millis(1200), btnAdd);
        ft.setFromValue(0.70); ft.setToValue(1.0);
        ft.setAutoReverse(true); ft.setCycleCount(FadeTransition.INDEFINITE); ft.play();

        btnAdd.setOnAction(e    -> { ft.stop(); btnAdd.setOpacity(1); addPolicy();    ft.play(); });
        btnUpdate.setOnAction(e -> updatePolicy());
        btnDelete.setOnAction(e -> deletePolicy());
        btnClear.setOnAction(e  -> clearForm());

        HBox box = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<Insurance> buildTable() {
        TableView<Insurance> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(380);
        tv.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;"
        );

        tv.getColumns().addAll(
                col("ID",          "policyId",        60),
                col("Vehicle",     "vehicleReg",      120),
                col("Policy No.",  "policyNumber",    130),
                col("Company",     "insuranceCompany",170),
                col("Start",       "startDate",       100),
                col("End",         "endDate",         100),
                col("Premium (M)", "premiumAmount",   110)
        );

        tv.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                vehicleCombo.getItems().stream()
                        .filter(s -> s.startsWith(sel.getVehicleId() + " | "))
                        .findFirst().ifPresent(vehicleCombo::setValue);
                tfPolicyNumber.setText(sel.getPolicyNumber());
                tfCompany.setText(sel.getInsuranceCompany());
                tfPremium.setText(String.valueOf(sel.getPremiumAmount()));
                try {
                    dpStart.setValue(LocalDate.parse(sel.getStartDate()));
                    dpEnd.setValue(LocalDate.parse(sel.getEndDate()));
                } catch (Exception ignored) {}
            }
        });

        data = FXCollections.observableArrayList();
        tv.setItems(data);
        return tv;
    }

    private void addPolicy() {
        try {
            if (vehicleCombo.getValue() == null)   throw new IllegalArgumentException("Select a vehicle.");
            if (tfPolicyNumber.getText().isBlank()) throw new IllegalArgumentException("Policy number required.");
            if (tfCompany.getText().isBlank())      throw new IllegalArgumentException("Company name required.");
            if (tfPremium.getText().isBlank())      throw new IllegalArgumentException("Premium amount required.");
            int vid = Integer.parseInt(vehicleCombo.getValue().split(" \\| ")[0]);
            insuranceDAO.addPolicy(new Insurance(0, vid, "",
                    tfPolicyNumber.getText().trim(), tfCompany.getText().trim(),
                    dpStart.getValue().toString(), dpEnd.getValue().toString(),
                    Double.parseDouble(tfPremium.getText().trim())));
            loadData(); clearForm();
            setStatus("Policy added.", false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus(e.getMessage(), true); }
    }

    private void updatePolicy() {
        try {
            Insurance sel = table.getSelectionModel().getSelectedItem();
            if (sel == null)                         throw new IllegalArgumentException("Select a policy first.");
            if (vehicleCombo.getValue() == null)     throw new IllegalArgumentException("Select a vehicle.");
            if (tfPolicyNumber.getText().isBlank())  throw new IllegalArgumentException("Policy number required.");
            int vid = Integer.parseInt(vehicleCombo.getValue().split(" \\| ")[0]);
            sel.setVehicleId(vid);
            sel.setPolicyNumber(tfPolicyNumber.getText().trim());
            sel.setInsuranceCompany(tfCompany.getText().trim());
            sel.setStartDate(dpStart.getValue().toString());
            sel.setEndDate(dpEnd.getValue().toString());
            sel.setPremiumAmount(Double.parseDouble(tfPremium.getText().trim()));
            insuranceDAO.updatePolicy(sel);
            loadData(); clearForm();
            setStatus("Policy updated.", false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus(e.getMessage(), true); }
    }

    private void deletePolicy() {
        Insurance sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a policy first.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete policy " + sel.getPolicyNumber() + "?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText("Confirm"); a.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                try { insuranceDAO.deletePolicy(sel.getPolicyId()); loadData(); clearForm();
                    setStatus("Policy deleted.", false);
                } catch (SQLException e) { setStatus(e.getMessage(), true); }
            }
        });
    }

    private void clearForm() {
        vehicleCombo.setValue(null); tfPolicyNumber.clear(); tfCompany.clear(); tfPremium.clear();
        dpStart.setValue(LocalDate.now()); dpEnd.setValue(LocalDate.now().plusYears(1));
        table.getSelectionModel().clearSelection();
    }

    private void loadData() {
        try { data.setAll(insuranceDAO.getAllPolicies());
            setStatus("Loaded " + data.size() + " policies.", false);
        } catch (SQLException e) { setStatus(e.getMessage(), true); }
    }

    private void loadVehicleOptions() {
        try { List<String> opts = vehicleDAO.getVehicleOptions(); vehicleCombo.getItems().setAll(opts); }
        catch (SQLException e) { setStatus("Could not load vehicles.", true); }
    }

    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setStyle(
                "-fx-text-fill: " + (error ? ACCENT_RED : ACCENT_GREEN) + "; -fx-font-size: 12px;"
        );
    }

    private <T> TableColumn<Insurance, T> col(String t, String p, int w) {
        TableColumn<Insurance, T> c = new TableColumn<>(t);
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
        TextField t = new TextField(); t.setPromptText(prompt); t.setPrefWidth(180);
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