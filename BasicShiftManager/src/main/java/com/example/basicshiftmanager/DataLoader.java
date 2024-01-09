package com.example.basicshiftmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataLoader {

    public static List<Employee> loadEmployeesFromFile(String filePath) {
        List<Employee> employees = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                if (data.length == 8) {
                    Date birthDate = dateFormat.parse(data[7]);
                    Employee employee = new Employee(
                            data[0], data[1], data[2], data[3], data[4], data[5], data[6], birthDate
                    );
                    employees.add(employee);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return employees;
    }

}
