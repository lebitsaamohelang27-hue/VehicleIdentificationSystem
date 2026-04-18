package com.vis.ui;

import com.vis.controller.CustomerController;
import com.vis.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * VIEW layer — builds and switches between module views.
 * The Customer module is now delegated to CustomerController (MVC separation).
 */
public class MainView {

    private StackPane contentArea;
    private DatabaseManager dbManager;

    public MainView() {
        dbManager = DatabaseManager.getInstance();
    }

    public VBox getView() {
        VBox mainView = new VBox(0);
        mainView.setStyle("-fx-background-color: #f5f7fa;");

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        showDashboard();

        mainView.getChildren().add(contentArea);
        return mainView;
    }

    // ════════════════════════════════════════════════
    //  NAVIGATION methods (called by sidebar)
    // ════════════════════════════════════════════════

    public void showDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome to Vehicle Identification System");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");

        GridPane statsGrid = createStatsCards();
        HBox progressBox  = createProgressSection();

        dashboard.getChildren().addAll(welcomeLabel, statsGrid, progressBox);
        swap(dashboard);
        System.out.println("Dashboard loaded");
    }

    public void showVehicles() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(
                title("🚗  Vehicles Management"),
                createVehicleForm(),
                createVehicleTable()
        );
        swap(view);
        System.out.println("Vehicles view loaded");
    }

    /**
     * Delegates to CustomerController — MVC separation of concerns.
     */
    public void showCustomers() {
        CustomerController controller = new CustomerController();
        swap(controller.buildView());
        System.out.println("Customers view loaded");
    }

    public void showServices() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(
                title("🔧  Services Management"),
                createServiceForm(),
                createServiceTable()
        );
        swap(view);
    }

    public void showPolice() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(
                title("🚔  Police Module"),
                createViolationForm(),
                createViolationTable()
        );
        swap(view);
    }

    public void showInsurance() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(
                title("📄  Insurance Management"),
                createInsuranceForm()
        );
        swap(view);
    }

    public void showReports() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(
                title("📊  Reports & Analytics"),
                createReportStats()
        );
        swap(view);
    }

    // ════════════════════════════════════════════════
    //  DASHBOARD helpers
    // ════════════════════════════════════════════════

    private GridPane createStatsCards() {
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);

        String[][] stats = {
                {"Total Vehicles",   getCount("Vehicle"),     "🚗"},
                {"Total Customers",  getCount("Customer"),    "👥"},
                {"Service Records",  getCount("ServiceRecord"),"🔧"},
                {"Unpaid Violations",getViolationCount(),     "⚠️"}
        };

        for (int i = 0; i < stats.length; i++) {
            VBox card = createStatCard(stats[i][0], stats[i][1], stats[i][2]);
            grid.add(card, i % 2, i / 2);
            GridPane.setHgrow(card, Priority.ALWAYS);
        }
        return grid;
    }

    private VBox createStatCard(String title, String value, String icon) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 18; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.09), 6, 0, 0, 3);");
        card.setAlignment(Pos.CENTER);
        card.setMinWidth(200);

        Label iconLabel  = new Label(icon);  iconLabel.setStyle("-fx-font-size: 32px;");
        Label valueLabel = new Label(value); valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        Label titleLabel = new Label(title); titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }

    private HBox createProgressSection() {
        HBox box = new HBox(30);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.09), 6, 0, 0, 3);");
        box.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(
                progressCard("Database Load",      0.75),
                progressCard("System Performance", 0.85)
        );
        return box;
    }

    private VBox progressCard(String labelText, double progress) {
        VBox v = new VBox(6);
        ProgressBar pb = new ProgressBar(progress);
        pb.setPrefWidth(220);
        pb.setStyle("-fx-accent: #1a237e;");
        v.getChildren().addAll(new Label(labelText), pb);
        return v;
    }

    // ════════════════════════════════════════════════
    //  VEHICLE forms & tables
    // ════════════════════════════════════════════════

    private TitledPane createVehicleForm() {
        GridPane g = form();

        TextField regField   = tf("ABC-1234");
        TextField makeField  = tf("Toyota");
        TextField modelField = tf("Camry");
        TextField yearField  = tf("2020");
        ComboBox<String> ownerCombo = new ComboBox<>();
        ownerCombo.setPromptText("Select Owner");
        loadOwners(ownerCombo);

        Button addBtn = addBtn("Add Vehicle");
        addBtn.setOnAction(e -> addVehicle(regField, makeField, modelField, yearField, ownerCombo));

        g.add(lbl("Registration:"), 0, 0); g.add(regField,   1, 0);
        g.add(lbl("Make:"),         2, 0); g.add(makeField,  3, 0);
        g.add(lbl("Model:"),        0, 1); g.add(modelField, 1, 1);
        g.add(lbl("Year:"),         2, 1); g.add(yearField,  3, 1);
        g.add(lbl("Owner:"),        0, 2); g.add(ownerCombo, 1, 2);
        g.add(addBtn,               2, 3);

        return titled("Add New Vehicle", g);
    }

    private TitledPane createVehicleTable() {
        // ✅ FIXED: each column gets its own index — no more sharing the last index
        TableView<ObservableList<String>> tv = rawTable();
        String[] cols = {"ID", "Registration", "Make", "Model", "Year", "Owner"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(p ->
                    new javafx.beans.property.SimpleStringProperty(
                            idx < p.getValue().size() ? p.getValue().get(idx) : ""));
            tv.getColumns().add(col);
        }
        tv.setPrefHeight(350);
        refreshVehicleTable(tv);
        return titled("Vehicle List", tv);
    }

    // ════════════════════════════════════════════════
    //  SERVICE forms & tables
    // ════════════════════════════════════════════════

    private TitledPane createServiceForm() {
        GridPane g = form();

        ComboBox<String> vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle");
        loadVehiclesForCombo(vehicleCombo);

        DatePicker sDate = new DatePicker(LocalDate.now());
        TextField  sType = tf("Oil Change");
        TextArea   desc  = new TextArea(); desc.setPromptText("Details..."); desc.setPrefRowCount(2);
        TextField  cost  = tf("0.00");

        Button addBtn = addBtn("Add Service Record");
        addBtn.setOnAction(e -> addServiceRecord(vehicleCombo, sDate, sType, desc, cost));

        g.add(lbl("Vehicle:"),      0, 0); g.add(vehicleCombo, 1, 0);
        g.add(lbl("Service Date:"), 2, 0); g.add(sDate,        3, 0);
        g.add(lbl("Type:"),         0, 1); g.add(sType,        1, 1);
        g.add(lbl("Description:"),  0, 2); g.add(desc,         1, 2, 3, 1);
        g.add(lbl("Cost (M):"),     0, 3); g.add(cost,         1, 3);
        g.add(addBtn,               2, 4);

        return titled("Add Service Record", g);
    }

    private TitledPane createServiceTable() {
        TableView<ObservableList<String>> tv = rawTable();
        String[] cols = {"ID", "Vehicle", "Date", "Type", "Description", "Cost (M)"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(p ->
                    new javafx.beans.property.SimpleStringProperty(
                            idx < p.getValue().size() ? p.getValue().get(idx) : ""));
            tv.getColumns().add(col);
        }
        tv.setPrefHeight(350);
        refreshServiceTable(tv);
        return titled("Service History", tv);
    }

    // ════════════════════════════════════════════════
    //  POLICE / VIOLATION forms & tables
    // ════════════════════════════════════════════════

    private TitledPane createViolationForm() {
        GridPane g = form();

        ComboBox<String> vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle");
        loadVehiclesForCombo(vehicleCombo);

        DatePicker vDate = new DatePicker(LocalDate.now());
        TextField  vType = tf("Speeding");
        TextField  fine  = tf("0.00");

        Button addBtn = new Button("Report Violation");
        addBtn.setStyle("-fx-background-color: #b71c1c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        addBtn.setOnAction(e -> addViolation(vehicleCombo, vDate, vType, fine));

        g.add(lbl("Vehicle:"),        0, 0); g.add(vehicleCombo, 1, 0);
        g.add(lbl("Violation Date:"), 2, 0); g.add(vDate,        3, 0);
        g.add(lbl("Type:"),           0, 1); g.add(vType,        1, 1);
        g.add(lbl("Fine (M):"),       2, 1); g.add(fine,         3, 1);
        g.add(addBtn,                 2, 2);

        return titled("Report Violation", g);
    }

    private TitledPane createViolationTable() {
        TableView<ObservableList<String>> tv = rawTable();
        String[] cols = {"ID", "Vehicle", "Date", "Type", "Fine (M)", "Status"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(p ->
                    new javafx.beans.property.SimpleStringProperty(
                            idx < p.getValue().size() ? p.getValue().get(idx) : ""));
            tv.getColumns().add(col);
        }
        tv.setPrefHeight(350);
        refreshViolationTable(tv);
        return titled("Violations List", tv);
    }

    // ════════════════════════════════════════════════
    //  INSURANCE form
    // ════════════════════════════════════════════════

    private TitledPane createInsuranceForm() {
        GridPane g = form();

        ComboBox<String> vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle");
        loadVehiclesForCombo(vehicleCombo);

        TextField  policyNum = tf("POL-001");
        TextField  provider  = tf("Insurance Co.");
        DatePicker startDate = new DatePicker(LocalDate.now());
        DatePicker endDate   = new DatePicker(LocalDate.now().plusYears(1));
        TextField  premium   = tf("0.00");

        Button addBtn = addBtn("Add Insurance Policy");
        addBtn.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        addBtn.setOnAction(e -> addInsurancePolicy(vehicleCombo, policyNum, provider, startDate, endDate, premium));

        g.add(lbl("Vehicle:"),       0, 0); g.add(vehicleCombo, 1, 0);
        g.add(lbl("Policy No.:"),    2, 0); g.add(policyNum,    3, 0);
        g.add(lbl("Provider:"),      0, 1); g.add(provider,     1, 1);
        g.add(lbl("Start Date:"),    2, 1); g.add(startDate,    3, 1);
        g.add(lbl("End Date:"),      0, 2); g.add(endDate,      1, 2);
        g.add(lbl("Premium (M):"),   2, 2); g.add(premium,      3, 2);
        g.add(addBtn,                2, 3);

        return titled("Add Insurance Policy", g);
    }

    // ════════════════════════════════════════════════
    //  REPORTS
    // ════════════════════════════════════════════════

    private GridPane createReportStats() {
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);
        grid.setPadding(new Insets(10));

        String[][] stats = {
                {"Total Service Revenue",  getTotalRevenue(),        "M"},
                {"Total Unpaid Fines",     getTotalUnpaidFines(),    "M"},
                {"Active Policies",        getActivePoliciesCount(), "📄"},
                {"Total Vehicles",         getCount("Vehicle"),      "🚗"}
        };

        for (int i = 0; i < stats.length; i++) {
            VBox card = createStatCard(stats[i][0], stats[i][1], stats[i][2]);
            grid.add(card, i % 2, i / 2);
        }
        return grid;
    }

    // ════════════════════════════════════════════════
    //  DATABASE helpers
    // ════════════════════════════════════════════════

    private String getCount(String table) {
        try (Connection c = dbManager.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (r.next()) return String.valueOf(r.getInt(1));
        } catch (SQLException e) { /* fall through */ }
        return "0";
    }

    private String getViolationCount() {
        try (Connection c = dbManager.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COUNT(*) FROM Violation WHERE status='Unpaid'")) {
            if (r.next()) return String.valueOf(r.getInt(1));
        } catch (SQLException e) { /* fall through */ }
        return "0";
    }

    private String getTotalRevenue() {
        try (Connection c = dbManager.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COALESCE(SUM(cost),0) FROM ServiceRecord")) {
            if (r.next()) return String.format("%.2f", r.getDouble(1));
        } catch (SQLException e) { /* fall through */ }
        return "0.00";
    }

    private String getTotalUnpaidFines() {
        try (Connection c = dbManager.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COALESCE(SUM(fine_amount),0) FROM Violation WHERE status='Unpaid'")) {
            if (r.next()) return String.format("%.2f", r.getDouble(1));
        } catch (SQLException e) { /* fall through */ }
        return "0.00";
    }

    private String getActivePoliciesCount() {
        try (Connection c = dbManager.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT COUNT(*) FROM InsurancePolicy WHERE end_date >= CURRENT_DATE")) {
            if (r.next()) return String.valueOf(r.getInt(1));
        } catch (SQLException e) { /* fall through */ }
        return "0";
    }

    private void loadOwners(ComboBox<String> combo) {
        try (Connection c = dbManager.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT customer_id, name FROM Customer ORDER BY name")) {
            while (r.next())
                combo.getItems().add(r.getInt("customer_id") + " - " + r.getString("name"));
        } catch (SQLException e) { /* empty combo is fine */ }
    }

    private void loadVehiclesForCombo(ComboBox<String> combo) {
        try (Connection c = dbManager.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT registration_number, make, model FROM Vehicle ORDER BY registration_number")) {
            while (r.next())
                combo.getItems().add(r.getString(1) + " - " + r.getString(2) + " " + r.getString(3));
        } catch (SQLException e) { /* empty combo is fine */ }
    }

    // ════════════════════════════════════════════════
    //  INSERT methods
    // ════════════════════════════════════════════════

    private void addVehicle(TextField reg, TextField make, TextField model, TextField year, ComboBox<String> owner) {
        try {
            if (reg.getText().isBlank() || owner.getValue() == null)
                throw new IllegalArgumentException("Please fill all fields and select an owner.");
            int ownerId = Integer.parseInt(owner.getValue().split(" - ")[0]);
            String sql  = "INSERT INTO Vehicle (registration_number, make, model, year, owner_id) VALUES (?,?,?,?,?)";
            try (Connection c = dbManager.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, reg.getText().trim());
                ps.setString(2, make.getText().trim());
                ps.setString(3, model.getText().trim());
                ps.setInt(4, Integer.parseInt(year.getText().trim()));
                ps.setInt(5, ownerId);
                ps.executeUpdate();
            }
            alert("Success", "Vehicle added successfully!", false);
            reg.clear(); make.clear(); model.clear(); year.clear(); owner.setValue(null);
            showVehicles();
        } catch (IllegalArgumentException e) {
            alert("Validation", e.getMessage(), true);
        } catch (Exception e) {
            alert("Error", "Failed to add vehicle: " + e.getMessage(), true);
        }
    }

    private void addServiceRecord(ComboBox<String> vehicle, DatePicker date, TextField type, TextArea desc, TextField cost) {
        try {
            if (vehicle.getValue() == null) throw new IllegalArgumentException("Select a vehicle.");
            String reg = vehicle.getValue().split(" - ")[0];
            String sql = "INSERT INTO ServiceRecord (vehicle_id, service_date, service_type, description, cost) " +
                    "VALUES ((SELECT vehicle_id FROM Vehicle WHERE registration_number=?), ?, ?, ?, ?)";
            try (Connection c = dbManager.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, reg);
                ps.setDate(2, Date.valueOf(date.getValue()));
                ps.setString(3, type.getText().trim());
                ps.setString(4, desc.getText().trim());
                ps.setDouble(5, Double.parseDouble(cost.getText().trim()));
                ps.executeUpdate();
            }
            alert("Success", "Service record added!", false);
            showServices();
        } catch (IllegalArgumentException e) {
            alert("Validation", e.getMessage(), true);
        } catch (Exception e) {
            alert("Error", "Failed: " + e.getMessage(), true);
        }
    }

    private void addViolation(ComboBox<String> vehicle, DatePicker date, TextField type, TextField fine) {
        try {
            if (vehicle.getValue() == null) throw new IllegalArgumentException("Select a vehicle.");
            String reg = vehicle.getValue().split(" - ")[0];
            String sql = "INSERT INTO Violation (vehicle_id, violation_date, violation_type, fine_amount, status) " +
                    "VALUES ((SELECT vehicle_id FROM Vehicle WHERE registration_number=?), ?, ?, ?, 'Unpaid')";
            try (Connection c = dbManager.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, reg);
                ps.setDate(2, Date.valueOf(date.getValue()));
                ps.setString(3, type.getText().trim());
                ps.setDouble(4, Double.parseDouble(fine.getText().trim()));
                ps.executeUpdate();
            }
            alert("Success", "Violation reported!", false);
            showPolice();
        } catch (IllegalArgumentException e) {
            alert("Validation", e.getMessage(), true);
        } catch (Exception e) {
            alert("Error", "Failed: " + e.getMessage(), true);
        }
    }

    private void addInsurancePolicy(ComboBox<String> vehicle, TextField policyNum, TextField provider,
                                    DatePicker start, DatePicker end, TextField premium) {
        try {
            if (vehicle.getValue() == null) throw new IllegalArgumentException("Select a vehicle.");
            String reg = vehicle.getValue().split(" - ")[0];
            String sql = "INSERT INTO InsurancePolicy (vehicle_id, policy_number, insurance_company, start_date, end_date, premium_amount) " +
                    "VALUES ((SELECT vehicle_id FROM Vehicle WHERE registration_number=?), ?, ?, ?, ?, ?)";
            try (Connection c = dbManager.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, reg);
                ps.setString(2, policyNum.getText().trim());
                ps.setString(3, provider.getText().trim());
                ps.setDate(4, Date.valueOf(start.getValue()));
                ps.setDate(5, Date.valueOf(end.getValue()));
                ps.setDouble(6, Double.parseDouble(premium.getText().trim()));
                ps.executeUpdate();
            }
            alert("Success", "Insurance policy added!", false);
        } catch (IllegalArgumentException e) {
            alert("Validation", e.getMessage(), true);
        } catch (Exception e) {
            alert("Error", "Failed: " + e.getMessage(), true);
        }
    }

    // ════════════════════════════════════════════════
    //  TABLE REFRESH methods  (✅ FIXED indexes)
    // ════════════════════════════════════════════════

    private void refreshVehicleTable(TableView<ObservableList<String>> tv) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, " +
                "COALESCE(c.name,'—') FROM Vehicle v LEFT JOIN Customer c ON v.owner_id=c.customer_id";
        try (Connection conn = dbManager.getConnection();
             Statement s = conn.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 6; i++) row.add(r.getString(i) != null ? r.getString(i) : "");
                data.add(row);
            }
        } catch (SQLException ignored) {}
        tv.setItems(data);
    }

    private void refreshServiceTable(TableView<ObservableList<String>> tv) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String sql = "SELECT s.service_id, v.registration_number, s.service_date, s.service_type, s.description, s.cost " +
                "FROM ServiceRecord s JOIN Vehicle v ON s.vehicle_id=v.vehicle_id ORDER BY s.service_date DESC";
        try (Connection conn = dbManager.getConnection();
             Statement s = conn.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 6; i++) row.add(r.getString(i) != null ? r.getString(i) : "");
                data.add(row);
            }
        } catch (SQLException ignored) {}
        tv.setItems(data);
    }

    private void refreshViolationTable(TableView<ObservableList<String>> tv) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String sql = "SELECT v.violation_id, v2.registration_number, v.violation_date, v.violation_type, v.fine_amount, v.status " +
                "FROM Violation v JOIN Vehicle v2 ON v.vehicle_id=v2.vehicle_id ORDER BY v.violation_date DESC";
        try (Connection conn = dbManager.getConnection();
             Statement s = conn.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 6; i++) row.add(r.getString(i) != null ? r.getString(i) : "");
                data.add(row);
            }
        } catch (SQLException ignored) {}
        tv.setItems(data);
    }

    // ════════════════════════════════════════════════
    //  UI helpers
    // ════════════════════════════════════════════════

    /** Replace content area content */
    private void swap(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
    }

    private Label title(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        return l;
    }

    private GridPane form() {
        GridPane g = new GridPane();
        g.setHgap(14); g.setVgap(12);
        g.setPadding(new Insets(16));
        g.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        return g;
    }

    private TitledPane titled(String title, javafx.scene.Node content) {
        TitledPane p = new TitledPane(title, content);
        p.setCollapsible(false);
        return p;
    }

    private TableView<ObservableList<String>> rawTable() {
        TableView<ObservableList<String>> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setStyle("-fx-font-size: 13px;");
        return tv;
    }

    private TextField tf(String prompt) {
        TextField t = new TextField(); t.setPromptText(prompt); return t;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        return l;
    }

    private Button addBtn(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #1a6b3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        return b;
    }

    private void alert(String title, String msg, boolean error) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.getDialogPane().setStyle(error
                ? "-fx-background-color: #f8d7da;"
                : "-fx-background-color: #d4edda;");
        a.showAndWait();
    }
}