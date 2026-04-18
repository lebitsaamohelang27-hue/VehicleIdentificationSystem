package com.vis;

import com.vis.controller.*;
import com.vis.database.DatabaseManager;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

    private Stage     primaryStage;
    private StackPane contentArea;
    private Button    activeNavBtn = null;

    private DashboardController dashboardController;
    private VehicleController   vehicleController;
    private CustomerController  customerController;
    private PoliceController    policeController;
    private InsuranceController insuranceController;

    // ─── Colour palette ───────────────────────────────────────────────────────
    public static final String BG_PAGE      = "#F0F4FA";
    public static final String BG_SURFACE   = "#FFFFFF";
    public static final String BG_HEADER    = "#FFFFFF";
    public static final String BORDER       = "#D8E3F0";
    public static final String TEXT_PRI     = "#1A2B4A";
    public static final String TEXT_SEC     = "#4A6080";
    public static final String TEXT_HINT    = "#90A8C0";

    public static final String ACCENT_BLUE    = "#1A5FAB";
    public static final String ACCENT_BLUE_L  = "#DCE9F8";
    public static final String ACCENT_BLUE_M  = "#3A7FD5";
    public static final String ACCENT_GREEN   = "#1A7A48";
    public static final String ACCENT_GREEN_L = "#DCF0E8";
    public static final String ACCENT_RED     = "#AB2020";
    public static final String ACCENT_RED_L   = "#F8DCDC";
    public static final String ACCENT_GREY    = "#3A4A60";
    public static final String ACCENT_GREY_L  = "#E8ECF4";

    public static final String NAV_ACTIVE_BG     = "#DCE9F8";
    public static final String NAV_ACTIVE_TEXT   = "#1A5FAB";
    public static final String NAV_ACTIVE_BORDER = "#1A5FAB";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Vehicle Identification System — VIS Portal");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setMaximized(true);
        showLoginScreen();
        stage.show();
    }

    // ════════════════════════════════════════════════════════
    //  LOGIN SCREEN
    // ════════════════════════════════════════════════════════
    private void showLoginScreen() {

        HBox loginRoot = new HBox();

        // ══════════════════════════════════
        //  LEFT PANEL — blue branding side
        // ══════════════════════════════════
        StackPane leftPanel = new StackPane();
        leftPanel.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #1A5FAB, #0D3D78);"
        );
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // Decorative blurred circles
        Pane decorLeft = new Pane();
        decorLeft.setMouseTransparent(true);
        double[][] cData = {
                {280, -100, -100}, {200, 400, -60},
                {320, -80,  500},  {180, 500, 400}, {150, 200, 700}
        };
        for (double[] d : cData) {
            Circle c = new Circle(d[0]);
            c.setCenterX(d[1]); c.setCenterY(d[2]);
            c.setFill(Color.web("#FFFFFF", 0.06));
            decorLeft.getChildren().add(c);
        }

        // Left content
        VBox leftContent = new VBox(36);
        leftContent.setAlignment(Pos.CENTER);
        leftContent.setPadding(new Insets(60));
        leftContent.setMaxWidth(560);

        // Brand row
        HBox brandRow = new HBox(16);
        brandRow.setAlignment(Pos.CENTER_LEFT);
        StackPane brandIcon = new StackPane();
        Rectangle brandIconBg = new Rectangle(52, 52);
        brandIconBg.setArcWidth(14); brandIconBg.setArcHeight(14);
        brandIconBg.setFill(Color.web("#FFFFFF", 0.20));
        Label brandIconLbl = new Label("VIS");
        brandIconLbl.setFont(Font.font("Georgia", FontWeight.BOLD, 15));
        brandIconLbl.setStyle("-fx-text-fill: white;");
        brandIcon.getChildren().addAll(brandIconBg, brandIconLbl);
        VBox brandText = new VBox(3);
        Label brandName = new Label("VIS Portal");
        brandName.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        brandName.setStyle("-fx-text-fill: white;");
        Label brandSub2 = new Label("Vehicle Identification System");
        brandSub2.setFont(Font.font("Verdana", 11));
        brandSub2.setStyle("-fx-text-fill: rgba(255,255,255,0.70);");
        brandText.getChildren().addAll(brandName, brandSub2);
        brandRow.getChildren().addAll(brandIcon, brandText);

        // Vehicle illustration
        Pane vehicle = buildVehicleScene();

        // Headline
        VBox headline = new VBox(12);
        Label h1 = new Label("Identify. Track.");
        Label h2 = new Label("Protect.");
        h1.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        h2.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        h1.setStyle("-fx-text-fill: white;");
        h2.setStyle("-fx-text-fill: rgba(255,255,255,0.85);");
        Label desc = new Label(
                "Lesotho's premier vehicle identification\n" +
                        "platform for law enforcement, insurance\n" +
                        "and fleet management professionals."
        );
        desc.setFont(Font.font("Verdana", 13));
        desc.setStyle("-fx-text-fill: rgba(255,255,255,0.68); -fx-line-spacing: 4;");
        headline.getChildren().addAll(h1, h2, desc);

        // Stats
        HBox stats = new HBox(0);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
                statCard("10K+", "Vehicles Registered", true),
                statCard("5K+",  "Customers",           false),
                statCard("99%",  "System Uptime",       false)
        );

        leftContent.getChildren().addAll(brandRow, vehicle, headline, stats);
        leftPanel.getChildren().addAll(decorLeft, leftContent);

        // ══════════════════════════════════
        //  RIGHT PANEL — login form
        // ══════════════════════════════════
        VBox rightPanel = new VBox();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setStyle("-fx-background-color: " + BG_SURFACE + ";");
        rightPanel.setMinWidth(460);
        rightPanel.setMaxWidth(460);

        VBox form = new VBox(20);
        form.setAlignment(Pos.TOP_LEFT);
        form.setPadding(new Insets(0, 60, 0, 60));

        // Form header
        VBox formHeader = new VBox(8);
        Label welcomeLbl = new Label("Welcome Back");
        welcomeLbl.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        welcomeLbl.setStyle("-fx-text-fill: " + TEXT_PRI + ";");
        Label welcomeSub = new Label("Sign in to access the VIS dashboard");
        welcomeSub.setFont(Font.font("Verdana", 12));
        welcomeSub.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
        formHeader.getChildren().addAll(welcomeLbl, welcomeSub);

        Rectangle divider = new Rectangle(60, 4);
        divider.setArcWidth(4); divider.setArcHeight(4);
        divider.setFill(Color.web(ACCENT_BLUE));

        // Username
        VBox userGroup = fieldGroup("USERNAME");
        TextField tfUser = styledField("Enter username");
        tfUser.setText("admin");
        userGroup.getChildren().add(tfUser);

        // Password
        VBox passGroup = fieldGroup("PASSWORD");
        PasswordField tfPass = new PasswordField();
        tfPass.setPromptText("Enter your password");
        tfPass.setText("admin123");
        styleField(tfPass);
        passGroup.getChildren().add(tfPass);

        // Status
        Label statusLbl = new Label(" ");
        statusLbl.setFont(Font.font("Verdana", 11));
        statusLbl.setWrapText(true);

        // Sign in button
        Button loginBtn = new Button("Sign In  \u2192");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle(
                "-fx-background-color: " + ACCENT_BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: Verdana;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 16 0;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        DropShadow btnShadow = new DropShadow(20, Color.web(ACCENT_BLUE, 0.38));
        loginBtn.setEffect(btnShadow);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
                "-fx-background-color: " + ACCENT_BLUE_M + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: Verdana;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 16 0;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
                "-fx-background-color: " + ACCENT_BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: Verdana;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 16 0;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        FadeTransition ft = new FadeTransition(Duration.millis(1400), loginBtn);
        ft.setFromValue(0.82); ft.setToValue(1.0);
        ft.setAutoReverse(true); ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.play();

        Label hintLbl = new Label("Default credentials: admin / admin123");
        hintLbl.setFont(Font.font("Verdana", 10));
        hintLbl.setStyle("-fx-text-fill: " + TEXT_HINT + ";");

        Runnable doLogin = () -> {
            if (tfUser.getText().trim().equals("admin") &&
                    tfPass.getText().trim().equals("admin123")) {
                statusLbl.setText("Access granted. Loading...");
                statusLbl.setStyle("-fx-text-fill: " + ACCENT_GREEN + "; -fx-font-size: 11px;");
                ft.stop(); loginBtn.setOpacity(1);
                FadeTransition out = new FadeTransition(Duration.millis(500), loginRoot);
                out.setFromValue(1); out.setToValue(0);
                out.setOnFinished(e -> showMainApp());
                out.play();
            } else {
                statusLbl.setText("Invalid credentials. Please try again.");
                statusLbl.setStyle("-fx-text-fill: " + ACCENT_RED + "; -fx-font-size: 11px;");
                Timeline shake = new Timeline(
                        new KeyFrame(Duration.ZERO,        new KeyValue(form.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(60),  new KeyValue(form.translateXProperty(), -12)),
                        new KeyFrame(Duration.millis(120), new KeyValue(form.translateXProperty(), 12)),
                        new KeyFrame(Duration.millis(180), new KeyValue(form.translateXProperty(), -8)),
                        new KeyFrame(Duration.millis(240), new KeyValue(form.translateXProperty(), 0))
                );
                shake.play();
            }
        };
        loginBtn.setOnAction(e -> doLogin.run());
        tfPass.setOnAction(e   -> doLogin.run());

        form.getChildren().addAll(
                formHeader, divider,
                userGroup, passGroup,
                statusLbl, loginBtn, hintLbl
        );

        // Right footer
        HBox rightFooter = new HBox();
        rightFooter.setAlignment(Pos.CENTER);
        rightFooter.setPadding(new Insets(20));
        Label footerLbl = new Label("© 2024 VIS — B/DIOP2210 OOP II  |  Faculty of ICT");
        footerLbl.setFont(Font.font("Verdana", 9));
        footerLbl.setStyle("-fx-text-fill: " + TEXT_HINT + ";");
        rightFooter.getChildren().add(footerLbl);

        Region vSpacer1 = new Region(); VBox.setVgrow(vSpacer1, Priority.ALWAYS);
        Region vSpacer2 = new Region(); VBox.setVgrow(vSpacer2, Priority.ALWAYS);
        rightPanel.getChildren().addAll(vSpacer1, form, vSpacer2, rightFooter);

        loginRoot.getChildren().addAll(leftPanel, rightPanel);
        loginRoot.setOpacity(0);

        Scene scene = new Scene(loginRoot);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), loginRoot);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);
        fadeIn.play();
    }

    // ── Vehicle illustration ──────────────────────────────────────────────────
    private Pane buildVehicleScene() {
        Pane pane = new Pane();
        pane.setPrefSize(420, 175);

        // Background card
        Rectangle bg = new Rectangle(0, 0, 420, 150);
        bg.setFill(Color.web("#FFFFFF", 0.07));
        bg.setArcWidth(16); bg.setArcHeight(16);

        // Road
        Rectangle road = new Rectangle(0, 122, 420, 32);
        road.setFill(Color.web("#FFFFFF", 0.10));
        for (int x = 10; x < 400; x += 55) {
            Rectangle dash = new Rectangle(x, 135, 35, 5);
            dash.setFill(Color.web("#FFFFFF", 0.30));
            dash.setArcWidth(3); dash.setArcHeight(3);
            pane.getChildren().add(dash);
        }

        // Building silhouettes
        Rectangle b1 = new Rectangle(8,  55, 38, 68); b1.setFill(Color.web("#FFFFFF", 0.07));
        Rectangle b2 = new Rectangle(52, 38, 28, 85); b2.setFill(Color.web("#FFFFFF", 0.05));
        Rectangle b3 = new Rectangle(350,48, 34, 75); b3.setFill(Color.web("#FFFFFF", 0.07));
        Rectangle b4 = new Rectangle(388,62, 28, 61); b4.setFill(Color.web("#FFFFFF", 0.05));

        // Car body
        Rectangle carBody = new Rectangle(80, 78, 260, 52);
        carBody.setFill(Color.web("#FFFFFF", 0.22));
        carBody.setArcWidth(16); carBody.setArcHeight(16);

        // Car roof
        Rectangle carRoof = new Rectangle(122, 48, 158, 40);
        carRoof.setFill(Color.web("#FFFFFF", 0.18));
        carRoof.setArcWidth(14); carRoof.setArcHeight(14);

        // Windows
        Rectangle windscreen = new Rectangle(172, 54, 58, 30);
        windscreen.setFill(Color.web("#A8D0F8", 0.55));
        windscreen.setArcWidth(8); windscreen.setArcHeight(8);

        Rectangle rearWin = new Rectangle(130, 54, 36, 30);
        rearWin.setFill(Color.web("#A8D0F8", 0.45));
        rearWin.setArcWidth(8); rearWin.setArcHeight(8);

        // Door line
        Line door = new Line(210, 80, 210, 128);
        door.setStroke(Color.web("#FFFFFF", 0.18)); door.setStrokeWidth(1.5);

        // Wheels
        Circle wL = new Circle(122, 128, 21); wL.setFill(Color.web("#0A2040"));
        Circle wR = new Circle(296, 128, 21); wR.setFill(Color.web("#0A2040"));
        Circle hL = new Circle(122, 128,  9); hL.setFill(Color.web("#FFFFFF", 0.28));
        Circle hR = new Circle(296, 128,  9); hR.setFill(Color.web("#FFFFFF", 0.28));

        // Wheel spokes
        for (int a = 0; a < 360; a += 60) {
            double rad = Math.toRadians(a);
            Line sL = new Line(122, 128, 122 + 8*Math.cos(rad), 128 + 8*Math.sin(rad));
            Line sR = new Line(296, 128, 296 + 8*Math.cos(rad), 128 + 8*Math.sin(rad));
            sL.setStroke(Color.web("#FFFFFF", 0.45)); sL.setStrokeWidth(1.4);
            sR.setStroke(Color.web("#FFFFFF", 0.45)); sR.setStrokeWidth(1.4);
            pane.getChildren().addAll(sL, sR);
        }

        // Lights
        Rectangle hl = new Rectangle(334, 90, 16, 10);
        hl.setFill(Color.web("#FFF4C2", 0.90)); hl.setArcWidth(4); hl.setArcHeight(4);
        Rectangle tl = new Rectangle(70, 90, 13, 10);
        tl.setFill(Color.web("#FF9090", 0.90)); tl.setArcWidth(4); tl.setArcHeight(4);

        pane.getChildren().addAll(bg, b1, b2, b3, b4, road,
                carBody, carRoof, windscreen, rearWin, door,
                wL, wR, hL, hR, hl, tl);

        TranslateTransition tt = new TranslateTransition(Duration.millis(2800), pane);
        tt.setByY(-7); tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();

        return pane;
    }

    private VBox statCard(String value, String label, boolean first) {
        VBox b = new VBox(4);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setPadding(new Insets(14, 28, 14, first ? 0 : 28));
        if (!first) {
            b.setStyle(
                    "-fx-border-color: rgba(255,255,255,0.20) transparent transparent transparent;" +
                            "-fx-border-width: 0 0 0 1;"
            );
        }
        Label val = new Label(value);
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        val.setStyle("-fx-text-fill: white;");
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", 10));
        lbl.setStyle("-fx-text-fill: rgba(255,255,255,0.62);");
        b.getChildren().addAll(val, lbl);
        return b;
    }

    private VBox fieldGroup(String labelText) {
        VBox g = new VBox(7);
        Label l = new Label(labelText);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        l.setStyle("-fx-text-fill: " + TEXT_SEC + "; -fx-letter-spacing: 1.5px;");
        g.getChildren().add(l);
        return g;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        styleField(tf);
        return tf;
    }

    private void styleField(TextInputControl tf) {
        String base =
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-prompt-text-fill: " + TEXT_HINT + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 13 16;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: Verdana;";
        String focused =
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-prompt-text-fill: " + TEXT_HINT + ";" +
                        "-fx-border-color: " + ACCENT_BLUE + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 13 16;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: Verdana;";
        tf.setStyle(base);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.focusedProperty().addListener((obs, old, isFocused) ->
                tf.setStyle(isFocused ? focused : base));
    }

    // ════════════════════════════════════════════════════════
    //  MAIN APP
    // ════════════════════════════════════════════════════════
    private void showMainApp() {
        dashboardController = new DashboardController();
        vehicleController   = new VehicleController();
        customerController  = new CustomerController();
        policeController    = new PoliceController();
        insuranceController = new InsuranceController();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");
        root.setTop(buildTopBar());

        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: " + BG_PAGE + ";");
        root.setCenter(contentArea);
        root.setBottom(buildAppFooter());

        root.setOpacity(0);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);
        fadeIn.setOnFinished(e -> showDashboard());
        fadeIn.play();
    }

    // ════════════════════════════════════════════════════════
    //  TOP BAR
    // ════════════════════════════════════════════════════════
    private VBox buildTopBar() {
        VBox top = new VBox(0);

        // Blue gradient header
        HBox header = new HBox(16);
        header.setPadding(new Insets(0, 28, 0, 28));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(56);
        header.setStyle("-fx-background-color: linear-gradient(to right, #1A5FAB, #2878D0);");

        StackPane logoBox = new StackPane();
        Rectangle logoBg = new Rectangle(42, 42);
        logoBg.setArcWidth(10); logoBg.setArcHeight(10);
        logoBg.setFill(Color.web("#FFFFFF", 0.18));
        Label logoLbl = new Label("VIS");
        logoLbl.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        logoLbl.setStyle("-fx-text-fill: white;");
        logoBox.getChildren().addAll(logoBg, logoLbl);

        VBox brandBox = new VBox(1);
        Label brandTitle = new Label("VEHICLE IDENTIFICATION SYSTEM");
        brandTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        brandTitle.setStyle("-fx-text-fill: white;");
        Label brandSub = new Label("Law Enforcement & Fleet Management Portal — Kingdom of Lesotho");
        brandSub.setFont(Font.font("Verdana", 9));
        brandSub.setStyle("-fx-text-fill: rgba(255,255,255,0.72);");
        brandBox.getChildren().addAll(brandTitle, brandSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean connected = DatabaseManager.getInstance().testConnection();
        HBox dbBadge = new HBox(6);
        dbBadge.setAlignment(Pos.CENTER);
        dbBadge.setPadding(new Insets(5, 14, 5, 14));
        dbBadge.setStyle(
                "-fx-background-color: " +
                        (connected ? "rgba(26,122,72,0.90)" : "rgba(171,32,32,0.90)") + ";" +
                        "-fx-background-radius: 20;"
        );
        Circle dot = new Circle(4, Color.WHITE);
        Label dbLbl = new Label(connected ? "DB Connected" : "DB Offline");
        dbLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        dbLbl.setStyle("-fx-text-fill: white;");
        dbBadge.getChildren().addAll(dot, dbLbl);

        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> { DatabaseManager.getInstance().closeConnection(); System.exit(0); });
        fileMenu.getItems().add(exitItem);
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About VIS");
        aboutItem.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("About VIS");
            a.setHeaderText("Vehicle Identification System v1.0");
            a.setContentText(
                    "Course: B/DIOP2210 — OOP II\n" +
                            "Backend: Supabase PostgreSQL\n" +
                            "Architecture: MVC JavaFX\n" +
                            "Department: Faculty of ICT"
            );
            a.showAndWait();
        });
        helpMenu.getItems().add(aboutItem);
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        Button logoutBtn = new Button("Logout");
        String logoutBase =
                "-fx-background-color: rgba(255,255,255,0.16);" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: rgba(255,255,255,0.45);" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 7 20;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-family: Verdana;" +
                        "-fx-cursor: hand;";
        String logoutHover =
                "-fx-background-color: rgba(255,255,255,0.28);" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 7 20;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-family: Verdana;" +
                        "-fx-cursor: hand;";
        logoutBtn.setStyle(logoutBase);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutHover));
        logoutBtn.setOnMouseExited(e  -> logoutBtn.setStyle(logoutBase));
        logoutBtn.setOnAction(e -> {
            FadeTransition out = new FadeTransition(Duration.millis(400),
                    primaryStage.getScene().getRoot());
            out.setFromValue(1); out.setToValue(0);
            out.setOnFinished(ev -> showLoginScreen());
            out.play();
        });

        header.getChildren().addAll(logoBox, brandBox, spacer, dbBadge, menuBar, logoutBtn);

        // White nav bar
        HBox navBar = new HBox(0);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(0, 28, 0, 28));
        navBar.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 0 0 1 0;"
        );

        String[]   navLabels  = {"Dashboard","Vehicles","Customers","Police","Insurance"};
        Runnable[] navActions = {
                this::showDashboard, this::showVehicles,
                this::showCustomers, this::showPolice, this::showInsurance
        };
        for (int i = 0; i < navLabels.length; i++) {
            navBar.getChildren().add(makeNavBtn(navLabels[i], navActions[i]));
        }

        top.getChildren().addAll(header, navBar);
        return top;
    }

    private Button makeNavBtn(String text, Runnable action) {
        Button b = new Button(text);
        b.setPadding(new Insets(13, 22, 13, 22));
        b.setFont(Font.font("Verdana", 12));
        b.setStyle(navStyle());
        b.setOnMouseEntered(e -> { if (b != activeNavBtn) b.setStyle(navHoverStyle()); });
        b.setOnMouseExited(e  -> { if (b != activeNavBtn) b.setStyle(navStyle()); });
        b.setOnAction(e -> { setActiveNav(b); action.run(); });
        return b;
    }

    private String navStyle() {
        return "-fx-background-color: transparent;" +
                "-fx-text-fill: " + TEXT_SEC + ";" +
                "-fx-font-family: Verdana;" +
                "-fx-border-color: transparent;" +
                "-fx-border-width: 0 0 3 0;" +
                "-fx-cursor: hand;";
    }

    private String navHoverStyle() {
        return "-fx-background-color: " + ACCENT_BLUE_L + ";" +
                "-fx-text-fill: " + ACCENT_BLUE + ";" +
                "-fx-font-family: Verdana;" +
                "-fx-border-color: transparent transparent " + ACCENT_BLUE_L + " transparent;" +
                "-fx-border-width: 0 0 3 0;" +
                "-fx-cursor: hand;";
    }

    private String navActiveStyle() {
        return "-fx-background-color: " + NAV_ACTIVE_BG + ";" +
                "-fx-text-fill: " + NAV_ACTIVE_TEXT + ";" +
                "-fx-font-family: Verdana;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: transparent transparent " + NAV_ACTIVE_BORDER + " transparent;" +
                "-fx-border-width: 0 0 3 0;" +
                "-fx-cursor: hand;";
    }

    private void setActiveNav(Button b) {
        if (activeNavBtn != null) activeNavBtn.setStyle(navStyle());
        activeNavBtn = b;
        b.setStyle(navActiveStyle());
    }

    // ════════════════════════════════════════════════════════
    //  APP FOOTER
    // ════════════════════════════════════════════════════════
    private HBox buildAppFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 28, 10, 28));
        footer.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-border-color: " + BORDER + " transparent transparent transparent;" +
                        "-fx-border-width: 1 0 0 0;"
        );

        Label left = new Label("© 2024 Vehicle Identification System — Kingdom of Lesotho");
        left.setFont(Font.font("Verdana", 10));
        left.setStyle("-fx-text-fill: " + TEXT_HINT + ";");

        Region s1 = new Region(); HBox.setHgrow(s1, Priority.ALWAYS);

        Label center = new Label("B/DIOP2210 — Object Oriented Programming II  |  Faculty of ICT");
        center.setFont(Font.font("Verdana", 10));
        center.setStyle("-fx-text-fill: " + TEXT_SEC + ";");

        Region s2 = new Region(); HBox.setHgrow(s2, Priority.ALWAYS);

        Label right = new Label("MVC Architecture  |  Supabase PostgreSQL  |  JavaFX");
        right.setFont(Font.font("Verdana", 10));
        right.setStyle("-fx-text-fill: " + TEXT_HINT + ";");

        footer.getChildren().addAll(left, s1, center, s2, right);
        return footer;
    }

    // ════════════════════════════════════════════════════════
    //  NAVIGATION
    // ════════════════════════════════════════════════════════
    private void swap(javafx.scene.Node node) {
        node.setOpacity(0);
        contentArea.getChildren().setAll(node);
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    private void showDashboard() { swap(dashboardController.buildView()); }
    private void showVehicles()  { swap(vehicleController.buildView());   }
    private void showCustomers() { swap(customerController.buildView());  }
    private void showPolice()    { swap(policeController.buildView());     }
    private void showInsurance() { swap(insuranceController.buildView()); }

    // ── Kept for compatibility ────────────────────────────────────────────────
    private void styleLoginInput(TextInputControl tf) { styleField(tf); }

    @Override
    public void stop() { DatabaseManager.getInstance().closeConnection(); }

    public static void main(String[] args) { launch(args); }
}