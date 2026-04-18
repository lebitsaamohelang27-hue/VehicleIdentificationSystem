package com.vis.controller;

import com.vis.database.CustomerDAO;
import com.vis.model.Customer;
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

import static com.vis.MainApp.*;

public class CustomerController {

    private CustomerDAO              customerDAO;
    private TableView<Customer>      table;
    private ObservableList<Customer> data;
    private TextField tfName, tfAddress, tfPhone, tfEmail;
    private Label     statusLabel;

    public Node buildView() {
        try {
            customerDAO = new CustomerDAO();
        } catch (Exception e) {
            return errorNode("Database connection failed: " + e.getMessage());
        }

        VBox root = new VBox(18);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: transparent;");

        VBox header = new VBox(4);
        Label title = new Label("Customers Management");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: " + TEXT_PRI + ";");
        Label sub = new Label("Add, edit and manage vehicle owners");
        sub.setFont(Font.font("Verdana", 12));
        sub.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        header.getChildren().addAll(title, sub);

        statusLabel = new Label(" ");
        statusLabel.setFont(Font.font("Verdana", 12));

        VBox formCard  = buildFormCard();
        VBox tableCard = buildTableCard();

        root.getChildren().addAll(header, statusLabel, formCard, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        loadData();
        return root;
    }

    private VBox buildFormCard() {
        VBox card = card("Add / Edit Customer");
        GridPane g = grid();

        tfName    = tf("e.g. Thabiso Mofokeng");
        tfAddress = tf("e.g. Maseru, Lesotho");
        tfPhone   = tf("e.g. +266 5800 0000");
        tfEmail   = tf("e.g. thabiso@email.com");

        g.add(lbl("Full Name:"), 0, 0); g.add(tfName,    1, 0);
        g.add(lbl("Address:"),   2, 0); g.add(tfAddress, 3, 0);
        g.add(lbl("Phone:"),     0, 1); g.add(tfPhone,   1, 1);
        g.add(lbl("Email:"),     2, 1); g.add(tfEmail,   3, 1);

        card.getChildren().addAll(g, buildButtons());
        return card;
    }

    private HBox buildButtons() {
        Button btnAdd    = actionBtn("Add Customer", ACCENT_BLUE,  ACCENT_BLUE_L,  "#B8CCE0");
        Button btnUpdate = actionBtn("Update",       ACCENT_GREEN, ACCENT_GREEN_L, "#B8D8C8");
        Button btnDelete = actionBtn("Delete",       ACCENT_RED,   ACCENT_RED_L,   "#E8C0C0");
        Button btnClear  = actionBtn("Clear",        ACCENT_GREY,  ACCENT_GREY_L,  BORDER);

        FadeTransition ft = new FadeTransition(Duration.millis(1200), btnAdd);
        ft.setFromValue(0.70); ft.setToValue(1.0);
        ft.setAutoReverse(true); ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.play();

        btnAdd.setOnAction(e    -> { ft.stop(); btnAdd.setOpacity(1); addCustomer();    ft.play(); });
        btnUpdate.setOnAction(e -> updateCustomer());
        btnDelete.setOnAction(e -> deleteCustomer());
        btnClear.setOnAction(e  -> clearForm());

        HBox box = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private VBox buildTableCard() {
        VBox card = card("Customer List");

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(380);
        table.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-text-fill: " + TEXT_PRI + ";"
        );

        table.getColumns().addAll(
                col("ID",      "id",      70),
                col("Name",    "name",    180),
                col("Address", "address", 200),
                col("Phone",   "phone",   140),
                col("Email",   "email",   200),
                roleCol()
        );

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfName.setText(sel.getName());
                tfAddress.setText(sel.getAddress());
                tfPhone.setText(sel.getPhone());
                tfEmail.setText(sel.getEmail());
            }
        });

        data = FXCollections.observableArrayList();
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);
        card.getChildren().add(table);
        return card;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────
    private void addCustomer() {
        try {
            if (tfName.getText().isBlank())  throw new IllegalArgumentException("Name is required.");
            if (tfPhone.getText().isBlank()) throw new IllegalArgumentException("Phone is required.");
            Customer c = new Customer(0,
                    tfName.getText().trim(), tfAddress.getText().trim(),
                    tfPhone.getText().trim(), tfEmail.getText().trim());
            customerDAO.addCustomer(c);
            loadData(); clearForm();
            setStatus("Customer added. Role: " + c.getRole(), false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus("Database error: " + e.getMessage(), true); }
    }

    private void updateCustomer() {
        try {
            Customer sel = table.getSelectionModel().getSelectedItem();
            if (sel == null)                   throw new IllegalArgumentException("Select a customer first.");
            if (tfName.getText().isBlank())     throw new IllegalArgumentException("Name cannot be empty.");
            sel.setName(tfName.getText().trim());
            sel.setAddress(tfAddress.getText().trim());
            sel.setPhone(tfPhone.getText().trim());
            sel.setEmail(tfEmail.getText().trim());
            customerDAO.updateCustomer(sel);
            loadData(); clearForm();
            setStatus("Customer updated.", false);
        } catch (IllegalArgumentException e) { setStatus(e.getMessage(), true);
        } catch (SQLException e)             { setStatus("Database error: " + e.getMessage(), true); }
    }

    private void deleteCustomer() {
        Customer sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a customer first.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + sel.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText("Confirm Deletion");
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try { customerDAO.deleteCustomer(sel.getId()); loadData(); clearForm();
                    setStatus("Customer deleted.", false);
                } catch (SQLException e) { setStatus(e.getMessage(), true); }
            }
        });
    }

    private void clearForm() {
        tfName.clear(); tfAddress.clear(); tfPhone.clear(); tfEmail.clear();
        table.getSelectionModel().clearSelection();
        setStatus(" ", false);
    }

    private void loadData() {
        try { data.setAll(customerDAO.getAllCustomers());
            setStatus("Loaded " + data.size() + " customers.", false);
        } catch (SQLException e) { setStatus("Failed to load: " + e.getMessage(), true); }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setStyle(
                "-fx-text-fill: " + (error ? ACCENT_RED : ACCENT_GREEN) + ";" +
                        "-fx-font-size: 12px;"
        );
    }

    private <T> TableColumn<Customer, T> col(String title, String prop, int w) {
        TableColumn<Customer, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(w);
        return c;
    }

    private TableColumn<Customer, String> roleCol() {
        TableColumn<Customer, String> c = new TableColumn<>("Role");
        c.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getRole()));
        c.setPrefWidth(90);
        return c;
    }

    private VBox card(String titleText) {
        VBox v = new VBox(14);
        v.setPadding(new Insets(20));
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

    private GridPane grid() {
        GridPane g = new GridPane();
        g.setHgap(16); g.setVgap(12);
        return g;
    }

    private TextField tf(String prompt) {
        TextField t = new TextField();
        t.setPromptText(prompt);
        t.setPrefWidth(180);
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
        l.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        return l;
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
        Label l = new Label(msg);
        l.setStyle("-fx-text-fill: " + ACCENT_RED + "; -fx-font-size: 14px;");
        VBox v = new VBox(l); v.setPadding(new Insets(40)); return v;
    }
}