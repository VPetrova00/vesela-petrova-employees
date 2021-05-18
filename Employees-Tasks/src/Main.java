import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static boolean checkIfWorkInSameTime(Date firstEDateFrom, Date firstEDateTo, Date secondEDateFrom, Date secondEDateTo) {
        if (firstEDateTo == null && secondEDateTo != null) {
            if ((firstEDateFrom.after(secondEDateFrom) && firstEDateFrom.before(secondEDateTo)) ||
                    firstEDateFrom.before(secondEDateFrom)) {
                return true;
            }
            else {
                return false;
            }
        }
        else if (firstEDateTo != null && secondEDateTo == null) {
            if ((firstEDateFrom.before(secondEDateFrom) && firstEDateTo.after(secondEDateFrom)) ||
                    firstEDateFrom.after(secondEDateFrom)) {
                return true;
            }
            else {
                return false;
            }
        }
        else if (firstEDateTo == null && secondEDateTo == null) {
            return true;
        }
        else {
            if ((firstEDateFrom.before(secondEDateFrom) && secondEDateFrom.before(firstEDateTo) && firstEDateTo.before(secondEDateTo)) ||
                    (secondEDateFrom.before(firstEDateFrom) && firstEDateFrom.before(secondEDateTo) && secondEDateTo.before(firstEDateTo)) ||
                    (firstEDateFrom.before(secondEDateFrom) && secondEDateTo.before(firstEDateTo)) ||
                    (secondEDateFrom.before(firstEDateFrom) && firstEDateTo.before(secondEDateTo))) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employees who worked together the longest");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        Button button = new Button("Choose text file");
        Button cancelButton = new Button("Cancel");
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        GridPane pane = new GridPane();

        pane.setHgap(10);
        pane.setVgap(5);
        pane.setPadding(new Insets(15,15,15,15));

        Label h1 = new Label("Employee ID #1");
        Label h2 = new Label("Employee ID #2");
        Label h3 = new Label("Project ID");
        Label h4 = new Label("Days worked");
        pane.add(h1, 0, 0, 1, 1);
        pane.add(h2, 1, 0, 1, 1);
        pane.add(h3, 2, 0, 1, 1);
        pane.add(h4, 3, 0, 1, 1);

        AtomicInteger rowIndex = new AtomicInteger(1);
        button.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                textArea.appendText("Selected file: " + selectedFile.getName() + "\n");
            }

            Scanner inputStream = null;

            try {
                inputStream = new Scanner(new File(selectedFile.getAbsolutePath())).useDelimiter(", |\\n");
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
            }

            Vector<Employee> employees = new Vector<>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while (inputStream.hasNext()) {
                String eId = inputStream.next();
                String pId = inputStream.next();
                String dateFrom = inputStream.next();
                String dateTo = inputStream.next();

                Employee employee = new Employee(Integer.parseInt(eId),
                        Integer.parseInt(pId));

                Date from = null;
                Date to = null;

                try {
                    from = (Date) dateFormat.parse(dateFrom);
                    to = (Date) dateFormat.parse(dateTo);
                } catch (ParseException ignored) { }

                employee.setDateFrom(from);
                employee.setDateTo(to);

                employees.add(employee);
            }

            long maxDiff = Long.MIN_VALUE;
            Duration maxDays = Duration.ZERO;
            Employee first = null;
            Employee second = null;
            for(int i = 0; i < employees.size() - 1; i++) {
                for(int j = i; j < employees.size(); j++) {
                    if (employees.get(i).getProjectId() != employees.get(j).getProjectId()) continue;
                    if (i == j) continue;

                    boolean workTogether = checkIfWorkInSameTime(employees.get(i).getDateFrom(),
                            employees.get(i).getDateTo(),
                            employees.get(j).getDateFrom(),
                            employees.get(j).getDateTo());

                    if (workTogether) {
                        Date workTogetherFrom = null;

                        if (employees.get(i).getDateFrom().before(employees.get(j).getDateFrom())) {
                            workTogetherFrom = employees.get(j).getDateFrom();
                        }
                        else {
                            workTogetherFrom = employees.get(i).getDateFrom();
                        }

                        long difference = 0;
                        Duration days = Duration.ZERO;
                        if (employees.get(i).getDateTo() == null || employees.get(j).getDateTo() == null) {
                            if (employees.get(i).getDateTo() == employees.get(j).getDateTo() && employees.get(i).getDateTo() == null) {
                                difference = new Date().getTime() - workTogetherFrom.getTime();
                                days = Duration.between(workTogetherFrom.toInstant(), new Date().toInstant());
                            }
                            else {
                                if (employees.get(i).getDateTo() != null) {
                                    difference = employees.get(i).getDateTo().getTime() - workTogetherFrom.getTime();
                                    days = Duration.between(workTogetherFrom.toInstant(), employees.get(i).getDateTo().toInstant());
                                }
                                else {
                                    difference = employees.get(j).getDateTo().getTime() - workTogetherFrom.getTime();
                                    days = Duration.between(workTogetherFrom.toInstant(), employees.get(j).getDateTo().toInstant());
                                }
                            }
                        }
                        else {
                            Date workTogetherTo = null;
                            if (employees.get(i).getDateTo().before(employees.get(j).getDateTo())) {
                                workTogetherTo = employees.get(i).getDateTo();
                            }
                            else {
                                workTogetherTo = employees.get(j).getDateTo();
                            }

                            difference = workTogetherTo.getTime() - workTogetherFrom.getTime();
                            days = Duration.between(workTogetherFrom.toInstant(), workTogetherTo.toInstant());
                        }

                        if (days.compareTo(maxDays) > 0) {
                            maxDays = days;
                        }

                        if (difference > maxDiff) {
                            maxDiff = difference;
                            first = employees.get(i);
                            second = employees.get(j);
                        }
                    }
                }
            }

            long differenceInYears = (maxDiff / (1000L * 60 * 60 * 24 * 365));
            long differenceInDays = (maxDiff / (1000L * 60 * 60 * 24)) % 365;
            long differenceInMonths = differenceInDays / 30;
            differenceInDays %= 30;

            Label val1 = new Label(String.valueOf(first.getEmployeeId()));
            Label val2 = new Label(String.valueOf(second.getEmployeeId()));
            Label val3 = new Label(String.valueOf(first.getProjectId()));
            Label val4 = new Label(String.valueOf(maxDays.toDays()));

            pane.add(val1, 0, rowIndex.get(), 1, 1);
            pane.add(val2, 1, rowIndex.get(), 1, 1);
            pane.add(val3, 2, rowIndex.get(), 1, 1);
            pane.add(val4, 3, rowIndex.get(), 1, 1);

            rowIndex.getAndIncrement();

            System.out.println("Years, months and days worked: " + differenceInYears + " years " + differenceInMonths + " months "
                    + differenceInDays + " days");
        });

        cancelButton.setOnAction(event -> {
            System.exit(0);
        });

        VBox vBox = new VBox();
        VBox.setMargin(button, new Insets(15, 15, 15, 15));
        VBox.setMargin(cancelButton, new Insets(15, 15, 15, 15));

        ScrollPane sp = new ScrollPane();
        sp.setContent(pane);
        vBox.getChildren().add(button);
        vBox.getChildren().add(textArea);
        vBox.getChildren().add(sp);
        vBox.getChildren().add(cancelButton);
        vBox.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(vBox, 500, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}