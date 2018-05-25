/**
 * Author: Connor Gephart
 * COSC 457 Final Project
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class DynamicTables extends Application{

    //TABLE VIEW AND DATA
    private ObservableList<ObservableList> data;
    private TableView tableview;
    private String username; //mmarti42
    private String password; //Cosc*pqq5
    //Connection
    private static Connection connection = null;

    //MAIN EXECUTOR
    public static void main(String[] args) {
        launch(args);
    }

    //CONNECTION DATABASE
    public void selectTables(String tableName){
        data = FXCollections.observableArrayList();
        try{
            //SQL FOR SELECTING ALL OF CUSTOMER
            String SQL = "SELECT * FROM " + tableName;
            //ResultSet
            ResultSet rs = connection.createStatement().executeQuery(SQL);

            buildTables(rs);

            //FINALLY ADDED TO TableView
            tableview.setItems(data);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    public void executeList(String query){
        data = FXCollections.observableArrayList();
        try{
            //connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/mmarti42db", "mmarti42", "Cosc*pqq5");

            //ResultSet
            ResultSet rs = connection.createStatement().executeQuery(query);

            buildTables(rs);

            //FINALLY ADDED TO TableView
            tableview.setItems(data);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    public void executeManipulate(String query){
        data = FXCollections.observableArrayList();
        try{
            connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/mmarti42db", "mmarti42", "Cosc*pqq5");

            //ResultSet
            connection.createStatement().executeUpdate(query);

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error Manipulation Data");
        }
    }

    public void buildTables(ResultSet rs) throws SQLException {
        /**********************************
         * TABLE COLUMN ADDED DYNAMICALLY *
         **********************************/
        for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
            //We are using non property style for making dynamic table
            final int j = i;
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });

            tableview.getColumns().addAll(col);
        }

        /********************************
         * Data added to ObservableList *
         ********************************/
        while(rs.next()){
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                //Iterate Column
                row.add(rs.getString(i));
            }
            data.add(row);

        }
    }

    public boolean connect(String username, String password){
        try{
            connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/mmarti42db", username, password);
            return true;
        }
        catch(SQLException e){
            return false;
        }
    }

    public Scene getDatabaseScene(Stage stage){
        stage.setMinWidth(1000);
        BorderPane border = new BorderPane();
        VBox vb = new VBox();
        TextArea sqlInput = new TextArea();
        sqlInput.setMaxSize(300, 200);
        vb.setPadding(new Insets(10, 50, 50, 50));
        vb.setSpacing(20);

        Label title = new Label("Options");
        title.setFont(Font.font("Amble CN", FontWeight.BOLD, 24));
        vb.getChildren().add(title);

        final ComboBox tableOptions = new ComboBox();
        tableOptions.getItems().addAll(
                "Employee",
                "Company",
                "Escalator",
                "Station",
                "TeamInfo",
                "WorkWeek",
                "WorksOn"
        );

        final ComboBox queryOptions = new ComboBox();
        queryOptions.getItems().addAll(
                "Show Tables",
                "Update, Insert, or Delete"
        );
        Label optionsLabel = new Label("Select a Table");
        vb.getChildren().add(optionsLabel);
        vb.getChildren().add(tableOptions);

        // Buttons
        Button submit = new Button();
        submit.setText("Submit");
        vb.getChildren().add(submit);

        Label sqlLabel = new Label("Choose Type of Query");
        vb.getChildren().add(sqlLabel);
        vb.getChildren().add(queryOptions);
        Label sqlLabel2 = new Label("Enter Query");
        vb.getChildren().add(sqlLabel2);
        vb.getChildren().add(sqlInput);

        Button executeSQL = new Button();
        executeSQL.setText("Execute SQL");
        vb.getChildren().add(executeSQL);


        //TableView
        tableview = new TableView();


        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                tableview.getItems().clear();
                tableview.getColumns().clear();
                String selection = (String) tableOptions.getValue();
                selectTables(selection);
            }
        });

        executeSQL.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(queryOptions.getValue().equals("Update, Insert, or Delete")){
                    tableview.getItems().clear();
                    tableview.getColumns().clear();
                    String query = sqlInput.getText();
                    executeManipulate(query);
                }
                else {
                    tableview.getItems().clear();
                    tableview.getColumns().clear();
                    String query = sqlInput.getText();
                    executeList(query);
                }
            }
        });

        border.setCenter(tableview);
        border.setLeft(vb);
        //Main Scene
        Scene scene = new Scene(border);
        return scene;
    }

    public Scene getLoginScene(Stage stage){
        BorderPane border = new BorderPane();
        VBox vb = new VBox();
        vb.setPadding(new Insets(10, 50, 50, 50));
        vb.setSpacing(20);
        vb.setAlignment(Pos.CENTER);
        TextField usernameInput = new TextField();
        PasswordField passwordInput = new PasswordField();
        Text actiontarget = new Text();
        Image kone = new Image("kone.png");
        ImageView koneView = new ImageView(kone);
        Image admiral = new Image("admiral.png");
        ImageView admiralView = new ImageView(admiral);
        vb.getChildren().add(koneView);
        vb.getChildren().add(admiralView);

        Label usernameLabel = new Label("Enter Username");
        vb.getChildren().addAll(usernameLabel, usernameInput);
        Label passwordLabel = new Label("Enter Password");
        vb.getChildren().addAll(passwordLabel, passwordInput);

        Button login = new Button("Login");
        vb.getChildren().add(login);

        border.setCenter(vb);
        vb.getChildren().add(actiontarget);

        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(connect(usernameInput.getText(), passwordInput.getText())){
                    stage.setScene(getDatabaseScene(stage));
                }
                else{
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Incorrect Login");
                }
            }
        });

        Scene scene = new Scene(border);
        return scene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setMinWidth(400);
        stage.setTitle("Kone/Admiral");
        stage.setResizable(true);

        stage.setScene(getLoginScene(stage));
        stage.show();
    }
}