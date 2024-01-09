package com.example.basicshiftmanager;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelloApplication extends Application {
    List<Employee> employees;

    BorderPane root = new BorderPane();
    String companyName="Your Company!";

    LocalDate actualDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    String dateDispaly = actualDate.format(formatter);
    VBox leftContainer=new VBox();
    BorderPane centerContainer=new BorderPane();
    HBox topContainer=new HBox();
    Button button0=new Button("Strona główna");
    Button button1=new Button("Pracownicy");
    Button button2=new Button("Zmiany");
    Button button3=new Button("Wygeneruj grafik");
    Button button4=new Button("Opcje");

    String sceneName;
    @Override
    public void start(Stage primaryStage) throws IOException {
        employees = DataLoader.loadEmployeesFromFile("employeesList.txt");
        for (Employee person : employees) {
            System.out.println(person);
        }
        sceneName="Main";
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bsmlogo.png"))));
        startScene();

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Basic Shift Manager");
        primaryStage.show();
    }
    private void LeftPaneButtonsOnClick(){
        button0.setOnAction(e -> mainScene());
        button1.setOnAction(e -> showEmployees());
        button2.setOnAction(e->showShifts());
        button3.setOnAction(e->printPlan());
        button4.setOnAction(e->showSettings());
    }
    private void contentClear(){
        topContainer.getChildren().clear();
        centerContainer.setCenter(null);
    }
    private void startScene(){

        Image logo = new Image("/bsmlogo.png");
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(200);
        logoView.setFitWidth(200);
        logoView.setPreserveRatio(true);
        mainScene();
        LeftPaneButtonsOnClick();

        leftContainer.getStyleClass().add("left-container");
        root.setLeft(leftContainer);

        centerContainer.getStyleClass().add("main-container");
        root.setCenter(centerContainer);

        topContainer.getStyleClass().add("top-panel");
        centerContainer.setTop(topContainer);

        //LEFT PANEL
        StackPane logoContainer = new StackPane();
        logoContainer.getStyleClass().add("logo-container");
        logoContainer.getChildren().add(logoView);
        leftContainer.getChildren().add(logoContainer);

        leftContainer.getChildren().add(button0);
        leftContainer.getChildren().add(button1);
        leftContainer.getChildren().add(button2);
        leftContainer.getChildren().add(button3);
        leftContainer.getChildren().add(button4);

    }
    private void mainScene(){
//CENTER PANEL
        contentClear();
        topContainer.getChildren().add(new Label(companyName));
        topContainer.getChildren().add(new Label(dateDispaly));
        centerContainer.setCenter(new Label("Work in progress..."));
        //TODO
    }
    private void showEmployees(){
        contentClear();
        LeftPaneButtonsOnClick();
        showEmployeesList();

        Button addButton=new Button("Dodaj pracownika");
        Button loadButton=new Button("Wczytaj listę pracowników");
        Button saveButton=new Button("Zapisz listę pracowników");
        topContainer.getChildren().add(addButton);
        topContainer.getChildren().add(loadButton);
        topContainer.getChildren().add(saveButton);

        addButton.setOnAction(e -> addEmployees());
        loadButton.setOnAction(e -> loadEmployees());
        saveButton.setOnAction(e->saveEmployees());
    }
    private void showShifts(){
        contentClear();
        LeftPaneButtonsOnClick();

        TableView<Employee> table = new TableView<>();
        table.setEditable(false);
        VBox addingContainer = new VBox();
        addingContainer.getStyleClass().add("adding-container");
        centerContainer.setCenter(addingContainer );

        ObservableList<Employee> data = FXCollections.observableArrayList(); // Lista do przechowywania danych

        TableColumn<Employee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Employee, String> firstNameColumn = new TableColumn<>("Imię");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> lastNameColumn = new TableColumn<>("Nazwisko");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, Void> modifyButton = new TableColumn<>("");
        modifyButton.setCellFactory(new Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>>() {
            @Override
            public TableCell<Employee, Void> call(final TableColumn<Employee, Void> param) {
                return new TableCell<Employee, Void>() {
                    private final Button btn = new Button("Wyświetl godziny pracy");

                    {
                        btn.setOnAction(event -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            showEmployeeShifts(employee);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }

        } );
        table.getColumns().addAll(idColumn, firstNameColumn,  lastNameColumn, modifyButton);

        centerContainer.getChildren().addAll(table);

        data.addAll(employees);
        table.setItems(data);
        addingContainer.getChildren().add(table);
    }
    private void showEmployeeShifts(Employee employee) {
        String fileName = employee.getFirstName() + employee.getLastName() + employee.getId() + ".txt";

        employee.readShiftDataFromFile(fileName);

        contentClear();
        LeftPaneButtonsOnClick();

        Button backButton=new Button("Cofnij");
        topContainer.getChildren().add(backButton);
        backButton.setOnAction(e -> showShifts());

        VBox addingContainer = new VBox();
        addingContainer.getStyleClass().add("adding-container");
        centerContainer.setCenter(addingContainer );
        TableView<Shift> shiftTable = new TableView<>();
        ObservableList<Shift> shiftData = FXCollections.observableArrayList(employee.getEmployeeShiftPlan());

        TableColumn<Shift, Integer> dayColumn = new TableColumn<>("Dzień");
        dayColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(shiftTable.getItems().indexOf(cellData.getValue()) + 1));
        TableColumn<Shift, String> startHourColumn = new TableColumn<>("Godzina rozpoczęcia");
        startHourColumn.setCellValueFactory(new PropertyValueFactory<>("formattedStartHour"));

        TableColumn<Shift, String> endHourColumn = new TableColumn<>("Godzina zakończenia");
        endHourColumn.setCellValueFactory(new PropertyValueFactory<>("formattedEndHour"));

        TableColumn<Shift, Void> modifyButtonColumn = new TableColumn<>("Akcje");
        modifyButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edytuj");
            {
                editButton.setOnAction(event -> {
                    Shift selectedShift = getTableView().getItems().get(getIndex());
                    int day=getIndex() + 1;
                    openEditDialog(selectedShift, employee,day );
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });

        shiftTable.getColumns().addAll(dayColumn, startHourColumn, endHourColumn, modifyButtonColumn);
        shiftTable.setItems(shiftData);

        Label hoursCounterLabel=new Label("Suma godzin: "+employee.getMonthlyHours());


        addingContainer.getChildren().add(shiftTable);
        addingContainer.getChildren().add(hoursCounterLabel);
    }
    private void openEditDialog(Shift shift, Employee employee, int day) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edytuj zmianę");

        // Utwórz Spinners dla godzin i minut
        Spinner<Integer> startHourSpinner = new Spinner<>();
        Spinner<Integer> startMinuteSpinner = new Spinner<>();
        Spinner<Integer> endHourSpinner = new Spinner<>();
        Spinner<Integer> endMinuteSpinner = new Spinner<>();

        // Ustaw zakres wartości dla Spinners
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, shift.getStartHour()));
        startMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, shift.getStartMinute()));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, shift.getEndHour()));
        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, shift.getEndMinute()));

        startHourSpinner.setEditable(true);
        startMinuteSpinner.setEditable(true);
        endHourSpinner.setEditable(true);
        endMinuteSpinner.setEditable(true);

        HBox timeFields = new HBox(5, startHourSpinner, startMinuteSpinner, new Label("-"), endHourSpinner, endMinuteSpinner);

        startHourSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue < 0 || newValue > 23) {
                startHourSpinner.getValueFactory().setValue(oldValue != null ? oldValue : 0);
            }
        });

        startMinuteSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue < 0 || newValue > 59) {
                startMinuteSpinner.getValueFactory().setValue(oldValue != null ? oldValue : 0);
            }
        });
        endHourSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue < 0 || newValue > 23) {
                endHourSpinner.getValueFactory().setValue(oldValue != null ? oldValue : 0);
            }
        });

        endMinuteSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue < 0 || newValue > 59) {
                endMinuteSpinner.getValueFactory().setValue(oldValue != null ? oldValue : 0);
            }
        });

        // Przyciski
        Button cancelButton = new Button("Anuluj");
        Button confirmButton = new Button("Zatwierdź");

        cancelButton.setOnAction(event -> dialogStage.close());
        confirmButton.setOnAction(event -> {
            // Pobierz wartości ze Spinners
            int startHour = startHourSpinner.getValue();
            int startMinute = startMinuteSpinner.getValue();
            int endHour = endHourSpinner.getValue();
            int endMinute = endMinuteSpinner.getValue();

            shift.setStartHour(startHour);
            shift.setStartMinute(startMinute);
            shift.setEndHour(endHour);
            shift.setEndMinute(endMinute);

            saveShiftDataToFile(shift, employee, day);
            dialogStage.close();
        });

        VBox layout = new VBox(10, timeFields, cancelButton, confirmButton);
        Scene scene = new Scene(layout, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.show();
    }
    private void saveShiftDataToFile(Shift shift,Employee employee, int dayOfMonth) {
        String fileName = employee.getFirstName() + employee.getLastName() + employee.getId() + ".txt";
        List<String> lines = new ArrayList<>();
        try {
            // Sprawdź, czy plik istnieje, i odczytaj jego zawartość
            if (Files.exists(Paths.get(fileName))) {
                lines = Files.readAllLines(Paths.get(fileName));
            }

            // Uzupełnij listę pustymi liniami, jeśli jest krótsza niż 31 linii
            while (lines.size() < 31) {
                lines.add("\t\t\t");
            }

            // Zaktualizuj odpowiednią linię
            int dayIndex = dayOfMonth-1; // Dzień 1 ma indeks 0 w liście
            String shiftData = String.format("%d\t%d\t%d\t%d", shift.getStartHour(), shift.getStartMinute(), shift.getEndHour(), shift.getEndMinute());
            lines.set(dayIndex, shiftData);

            // Zapisz zaktualizowane dane do pliku
            Files.write(Paths.get(fileName), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showEmployeeShifts(employee);
    }

    private void printPlan(){
        contentClear();
        LeftPaneButtonsOnClick();
        centerContainer.setCenter(new Label("Work in progress..."));
    }
    private void showSettings(){

        contentClear();
        LeftPaneButtonsOnClick();

        Button saveButton=new Button("Save");
        topContainer.getChildren().add(saveButton);

        GridPane addingContainer = new GridPane();
        addingContainer.getStyleClass().add("adding-container");
        centerContainer.setCenter(addingContainer);

        Label companyNameLabel = new Label("Company name");

        TextField companyNameSet = new TextField(companyName);

        addingContainer.add(companyNameLabel,0,0);

        addingContainer.add(companyNameSet,1,0);

        saveButton.setOnAction(e->saveSettings(companyNameSet));

        //TODO DO ROZBUDOWY  I ZAPIS DO PLIKU
    }
    
    private void saveSettings(TextField companyNameSet){
        companyName=companyNameSet.getText();
    }
    private void showEmployeesList(){
        employees = DataLoader.loadEmployeesFromFile("employeesList.txt");
        for (Employee person : employees) {
            System.out.println(person);
        }
       // DataLoader.showEmployeesList(centerContainer);
        TableView<Employee> table = new TableView<>();
        table.setEditable(false);
        VBox addingContainer = new VBox();
        addingContainer.getStyleClass().add("adding-container");
        centerContainer.setCenter(addingContainer );

        ObservableList<Employee> data = FXCollections.observableArrayList(); // Lista do przechowywania danych

        TableColumn<Employee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Employee, String> firstNameColumn = new TableColumn<>("Imię");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> lastNameColumn = new TableColumn<>("Nazwisko");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, String> peselColumn = new TableColumn<>("PESEL");
        peselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));

        TableColumn<Employee, String> cityColumn = new TableColumn<>("Miasto");
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<Employee, String> addressColumn = new TableColumn<>("Adres");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Employee, String> postColumn = new TableColumn<>("Kod pocztowy");
        postColumn.setCellValueFactory(new PropertyValueFactory<>("post"));

        TableColumn<Employee, String> birthDateColumn = new TableColumn<>("Data urodzenia");
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        table.getColumns().addAll(idColumn, firstNameColumn,  lastNameColumn, peselColumn, cityColumn, addressColumn, postColumn, birthDateColumn); // Dodaj wszystkie kolumny do TableView

        centerContainer.getChildren().addAll(table);

        // Załadowanie danych (przykład załadowania z listy wczytanej wcześniej)
        data.addAll(employees); // listaWczytanychPracownikow powinna być listą obiektów Employee wczytaną z pliku
        table.setItems(data);
        addingContainer.getChildren().add(table);

    }
    private void loadEmployees(){
        //TODO
    }
    private void saveEmployees(){
        //TODO
    }
    private void addEmployees(){
        GridPane addingContainer = new GridPane();
        addingContainer.getStyleClass().add("adding-container");
        centerContainer.setCenter(addingContainer);

        Label idLabel = new Label("ID");
        Label firstNameLabel = new Label("First Name");
        Label lastNameLabel = new Label("Last Name");
        Label peselLabel = new Label("PESEL Code");
        Label cityLabel = new Label("City");
        Label addressLabel = new Label("Address");
        Label postLabel = new Label("Post code");
        Label birthdateLabel = new Label("Birthday");

        TextField id = new TextField();
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField pesel = new TextField();
        TextField city = new TextField();
        TextField address = new TextField();
        TextField post = new TextField();
        DatePicker birthDate = new DatePicker();
        birthDate.setEditable(false);

        Button addButton = new Button("OK");
        Button backButton = new Button("Back");
        backButton.setOnAction(e->showEmployees());

            addButton.setOnAction(e -> addToList(id, firstName, lastName, pesel, city, address, post, birthDate));

        addingContainer.add(idLabel,0,0);
        addingContainer.add(firstNameLabel,0,1);
        addingContainer.add(lastNameLabel,0,2);
        addingContainer.add(peselLabel,0,3);
        addingContainer.add(cityLabel,0,4);
        addingContainer.add(addressLabel,0,5);
        addingContainer.add(postLabel,0,6);
        addingContainer.add(birthdateLabel,0,7);

        addingContainer.add(id,1,0);
        addingContainer.add(firstName,1,1);
        addingContainer.add(lastName,1,2);
        addingContainer.add(pesel,1,3);
        addingContainer.add(city,1,4);
        addingContainer.add(address,1,5);
        addingContainer.add(post,1,6);
        addingContainer.add(birthDate,1,7);
        addingContainer.add(addButton,1,8);
        addingContainer.add(backButton,0,8);
    }
    private void addToList(TextField id, TextField firstName, TextField lastName, TextField pesel, TextField city, TextField address, TextField post, DatePicker birthDate){

        if(id.getText()!=null && firstName.getText()!=null && lastName.getText()!=null && pesel.getText()!=null && city.getText()!=null && address.getText()!=null && post.getText()!=null && birthDate.getValue()!=null) {
            String data = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n",
                    id.getText(),
                    firstName.getText(),
                    lastName.getText(),
                    pesel.getText(),
                    city.getText(),
                    address.getText(),
                    post.getText(),
                    birthDate.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            try (FileWriter fileWriter = new FileWriter("employeesList.txt", true)) {
                fileWriter.write(data);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            showEmployees();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}