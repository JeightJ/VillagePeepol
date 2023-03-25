package com.app.villagepeepol;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Optional;

/**
 * Tämä luokka on mökkivarausjärjestelmän pääluokka.
 * Se sisältää mökkien ja varauksien tiedot, lukee ja tallentaa tiedot tiedostoihin,
 * ja käynnistää käyttöliittymän.
 */
public class CottageBookingSystem extends Application {
    // Tiedostojen nimet
    private static final String COTTAGES_FILE = "cottages.txt";
    private static final String BOOKINGS_FILE = "varaukset.txt";
    // ObservableListit mökeille ja varauksille
    private ObservableList<Cottage> cottageList = FXCollections.observableArrayList();
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();


    /**
     * Sovelluksen käynnistysmetodi.
     * Luo tiedostot, jos niitä ei ole olemassa.
     * Lukee mökit ja varaukset tiedostoista.
     * Näyttää tervetulosivun.
     *
     * @param primaryStage sovelluksen ensisijainen näyttöikkuna
     */
    @Override
    public void start(Stage primaryStage) {
        // Luo tiedostot, jos niitä ei ole olemassa
        createFilesIfNotExist();
        // Lue mökit ja varaukset tiedostoista
        readCottagesFile();
        readBookingsFile();

        // Tervetuloa-teksti ja sen tyyli
        Text welcomeText = new Text("Tervetuloa mökkivarausjärjestelmään!");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Käynnistä-painike ja sen tapahtumankäsittelijä
        Button startButton = new Button("Käynnistä");
        startButton.setOnAction(e -> {
            showMainPage(primaryStage);
        });

        // Lopeta-painike ja sen tapahtumankäsittelijä
        Button exitButton = new Button("Lopeta");
        exitButton.setOnAction(e -> {
            primaryStage.close();
        });

        // Asettelu tervetuloa-näkymälle
        VBox welcomeLayout = new VBox(20, welcomeText, startButton, exitButton);
        welcomeLayout.setAlignment(Pos.CENTER);
        welcomeLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Taustakuva, jos se löytyy
        File backgroundImageFile = new File("taustakuva.jpg");
        if (backgroundImageFile.exists()) {
            Image backgroundImage = new Image("file:taustakuva.jpg");
            BackgroundSize backgroundSize = new BackgroundSize(1200, 800, false, false, false, false);
            BackgroundImage backgroundImg = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            welcomeLayout.setBackground(new Background(backgroundImg));
        }

        // Näkymä tervetuloa-näkymälle
        Scene scene = new Scene(welcomeLayout, 1200, 800);
        primaryStage.setTitle("Mökkivarausjärjestelmä - Tervetuloa!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Luo pääsivun ja sen komponentit, kuten TableView:t mökeille ja varauksille,
     * painikkeet mökkien ja varausten lisäämiseen ja poistamiseen sekä taustakuvan.
     * Asettaa myös tapahtumankäsittelijät painikkeille ja asettaa näytettävän näkymän.
     *
     * @param primaryStage päänäyttämö, johon pääsivu lisätään.
     */
    private void showMainPage(Stage primaryStage) {
        // Luo TableView:t mökeille ja varauksille
        TableView<Cottage> cottageTableView = createCottageTableView();
        TableView<Booking> bookingTableView = createBookingTableView();

        // Lisää mökki -painike ja sen tapahtumankäsittelijä
        Button addCottageButton = new Button("Lisää mökki");
        addCottageButton.setOnAction(e -> {
            Dialog<Cottage> addCottageDialog = createAddCottageDialog();
            Optional<Cottage> result = addCottageDialog.showAndWait();
            result.ifPresent(cottage -> {
                cottageList.add(cottage);
                saveCottagesToFile();
            });
        });

        // Poista mökki -painike ja sen tapahtumankäsittelijä
        Button removeCottageButton = new Button("Poista mökki");
        removeCottageButton.setOnAction(e -> {
            int selectedIndex = cottageTableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                cottageList.remove(selectedIndex);
                saveCottagesToFile();
            }
        });

        //Lisää varaus -painike ja sen tapahtumakäsittelijä
        Button addBookingButton = new Button("Lisää varaus");
        addBookingButton.setOnAction(e -> {
            Dialog<Booking> addBookingDialog = createAddBookingDialog();
            Optional<Booking> result = addBookingDialog.showAndWait();
            result.ifPresent(booking -> {
                bookingList.add(booking);
                saveBookingsToFile();
            });
        });

        // Poista varaus -painike ja sen tapahtumankäsittelijä
        Button removeBookingButton = new Button("Poista varaus");
        removeBookingButton.setOnAction(e -> {
            int selectedIndex = bookingTableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                bookingList.remove(selectedIndex);
                saveBookingsToFile();
            }
        });

        // Luodaan painikkeille hboxi
        HBox buttons = new HBox(10, addCottageButton, removeCottageButton, addBookingButton, removeBookingButton);
        buttons.setPadding(new Insets(10));

        // Luodaan vboxi elementeille
        VBox mainLayout = new VBox(10, cottageTableView, buttons, bookingTableView);
        mainLayout.setPadding(new Insets(10));

        // Lisätään taustakuva projektin juuresta, varmistetaan että se löytyy, lisätään koko ja speksit
        File backgroundImageFile = new File("taustakuva.jpg");
        if (backgroundImageFile.exists()) {
            Image backgroundImage = new Image("file:taustakuva.jpg");
            BackgroundSize backgroundSize = new BackgroundSize(1200, 800, false, false, false, false);
            BackgroundImage backgroundImg = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            mainLayout.setBackground(new Background(backgroundImg));
        }

        // Luodaan kaikesta tästä scene
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setTitle("Mökkivarausjärjestelmä");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * Luo tiedostot, jos niitä ei ole olemassa.
     */
    private void createFilesIfNotExist() {
        try {
            Files.createDirectories(Paths.get("").toAbsolutePath());
            Files.createFile(Paths.get(COTTAGES_FILE));
            Files.createFile(Paths.get(BOOKINGS_FILE));
        } catch (IOException e) {
            System.out.println("Tiedostoja ei voitu luoda");
        }
    }

    /**
     * Luo TableView:n mökeille, joka sisältää kolme saraketta: nimi, koko ja kapasiteetti.
     * Asettaa myös sarakkeiden leveydet ja solujen arvot.
     *
     * @return TableView, joka näyttää mökkilistauksen.
     */
    private TableView<Cottage> createCottageTableView() {
        // Luo mökin nimen sisältävä sarake
        TableColumn<Cottage, String> nameColumn = new TableColumn<>("Nimi");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        nameColumn.setMinWidth(150);

        // Luo mökin koon sisältävä sarake
        TableColumn<Cottage, String> sizeColumn = new TableColumn<>("Koko");
        sizeColumn.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getSize())));
        sizeColumn.setMinWidth(100);

        // Luo mökin kapasiteetin sisältävä sarake
        TableColumn<Cottage, String> capacityColumn = new TableColumn<>("Kapasiteetti");
        capacityColumn.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getCapacity())));
        capacityColumn.setMinWidth(100);

        // Luo mökkinäkymän taulukon ja asettaa sarakkeet
        TableView<Cottage> cottageTableView = new TableView<>();
        cottageTableView.setItems(cottageList);
        cottageTableView.getColumns().addAll(nameColumn, sizeColumn, capacityColumn);

        // Palauttaa näkymän
        return cottageTableView;
    }

    /**
     * Luo TableView:n mökeille, joka sisältää kolme saraketta: Valitun mökin nimi, asiakkaan nimi, varauspäivämäärä ja varauksen kesto
     * Asettaa myös sarakkeiden leveydet ja solujen arvot.
     *
     * @return TableView, joka näyttää varauslistauksen
     */
    private TableView<Booking> createBookingTableView() {
        // Luo mökin nimen sisältävä sarake
        TableColumn<Booking, String> cottageNameColumn = new TableColumn<>("Mökin nimi");
        cottageNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCottage().getName()));
        cottageNameColumn.setMinWidth(150);

        // Luo asiakkaan nimen sisältävä sarake
        TableColumn<Booking, String> customerNameColumn = new TableColumn<>("Asiakkaan nimi");
        customerNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCustomerName()));
        customerNameColumn.setMinWidth(150);

        // Luo varauspäivämäärän sisältävä sarake
        TableColumn<Booking, String> startDateColumn = new TableColumn<>("Varauspäivämäärä");
        startDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        startDateColumn.setMinWidth(150);

        // Luo varattujen päivien määrän sisältävä sarake
        TableColumn<Booking, String> daysColumn = new TableColumn<>("Varattujen päivien määrä");
        daysColumn.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getDays())));
        daysColumn.setMinWidth(150);

        // Luo varausnäkymän taulukon ja asettaa sarakkeet
        TableView<Booking> bookingTableView = new TableView<>();
        bookingTableView.setItems(bookingList);
        bookingTableView.getColumns().addAll(cottageNameColumn, customerNameColumn, startDateColumn, daysColumn);

        // Palauttaa näkymän
        return bookingTableView;
    }

    /**
     * Luo uuden dialogin, jossa käyttäjä voi lisätä uuden mökin. Dialogi sisältää
     * kentät mökin nimen, koon ja kapasiteetin syöttämiseen. Lisäksi dialogissa on
     * painikkeet mökin lisäämiseksi ja toiminnon peruuttamiseksi.
     *
     * @return Dialog, joka palauttaa Cottage-olion, jos käyttäjä painaa Lisää-painiketta,
     *         muuten palauttaa null.
     */
    private Dialog<Cottage> createAddCottageDialog() {
        Dialog<Cottage> addCottageDialog = new Dialog<>();
        addCottageDialog.setTitle("Lisää mökki");

        // Luo dialogin sisältö
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Mökin nimi");
        TextField sizeField = new TextField();
        sizeField.setPromptText("Koko");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Kapasiteetti");

        // Lisää kentät ja niiden selitteet dialogiin
        grid.add(new Label("Nimi:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Koko:"), 0, 1);
        grid.add(sizeField, 1, 1);
        grid.add(new Label("Kapasiteetti:"), 0, 2);
        grid.add(capacityField, 1, 2);
        addCottageDialog.getDialogPane().setContent(grid);

        // Luo lisäys- ja peruutuspainikkeet
        ButtonType addButton = new ButtonType("Lisää", ButtonBar.ButtonData.OK_DONE);
        addCottageDialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Määritä, mitä tapahtuu, kun painiketta painetaan

        // Aseta muunnin dialogin tulokselle
        addCottageDialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                // Lue mökin tiedot tekstikentistä ja luo uusi Cottage-olio
                String name = nameField.getText();
                int size = Integer.parseInt(sizeField.getText());
                int capacity = Integer.parseInt(capacityField.getText());
                return new Cottage(name, size, capacity);
            }
            return null;
        });

        return addCottageDialog;
    }

    /**
     * Luo uuden dialogin, jossa käyttäjä voi lisätä uuden varauksen. Dialogi sisältää
     * mökin valintaelementin, asiakkaan nimen tekstikentän, aloituspäivämäärän
     * tekstikentän ja varattujen päivien määrän tekstikentän. Lisäksi dialogissa on
     * painikkeet varauksen lisäämiseksi ja toiminnon peruuttamiseksi.
     *
     * @return Dialog, joka palauttaa Booking-olion, jos käyttäjä painaa Lisää-painiketta,
     *         muuten palauttaa null.
     */
    private Dialog<Booking> createAddBookingDialog() {
        Dialog<Booking> addBookingDialog = new Dialog<>();
        addBookingDialog.setTitle("Lisää varaus");

        // Luo ruudukko dialogin sisällölle
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Luo mökkien valintaelementti, asiakkaan nimen tekstikenttä, aloituspäivämäärän tekstikenttä ja varattujen päivien määrän tekstikenttä
        ComboBox<Cottage> cottageComboBox = new ComboBox<>(cottageList);
        cottageComboBox.setPromptText("Valitse mökki");
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Asiakkaan nimi");
        TextField startDateField = new TextField();
        startDateField.setPromptText("Aloituspäivämäärä (dd.MM.yyyy)");
        TextField daysField = new TextField();
        daysField.setPromptText("Varattujen päivien määrä");

        // Lisää elementit ruudukkoon
        grid.add(new Label("Mökki:"), 0, 0);
        grid.add(cottageComboBox, 1, 0);
        grid.add(new Label("Asiakkaan nimi:"), 0, 1);
        grid.add(customerNameField, 1, 1);
        grid.add(new Label("Aloituspäivämäärä:"), 0, 2);
        grid.add(startDateField, 1, 2);
        grid.add(new Label("Varattujen päivien määrä:"), 0, 3);
        grid.add(daysField, 1, 3);

        // Aseta ruudukko dialogin sisällöksi
        addBookingDialog.getDialogPane().setContent(grid);

        // Lisää Lisää- ja Peruuta-painikkeet dialogiin
        ButtonType addButton = new ButtonType("Lisää", ButtonBar.ButtonData.OK_DONE);
        addBookingDialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Aseta muunnin dialogin tulokselle
        addBookingDialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                // Lue varauksen tiedot ja luo uusi Booking-olio
                Cottage cottage = cottageComboBox.getSelectionModel().getSelectedItem();
                String customerName = customerNameField.getText();
                LocalDate startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                int days = Integer.parseInt(daysField.getText());
                return new Booking(cottage, customerName, startDate, days);
            }
            return null;
        });

        // Palautetaan luotu dialog

        return addBookingDialog;
    }

    /**
     * Lue mökit tiedostosta ja lisää ne cottageList-olioon. Jos tiedoston lukemisessa
     * ilmenee ongelmia, tulostetaan virheilmoitus.
     */
    private void readCottagesFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(COTTAGES_FILE))) {
            // Tyhjennä mökkilista
            cottageList.clear();
            String line;
            // Käy läpi tiedoston rivit ja luo Cottage-olio jokaisesta rivistä
            while ((line = br.readLine()) != null) {
                cottageList.add(Cottage.fromFileString(line));
            }
        } catch (IOException e) {
            // Tulosta virheilmoitus, jos tiedoston lukemisessa ilmenee ongelmia
            System.out.println("Virhe cottages.txt-tiedoston lukemisessa: " + e.getMessage());
        }
    }

    /**
     * Lue varaukset tiedostosta ja lisää ne bookingList-olioon. Jos tiedoston lukemisessa
     * ilmenee ongelmia, tulostetaan virheilmoitus.
     */
    private void readBookingsFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            // Tyhjennä varauslista
            bookingList.clear();
            String line;
            // Käy läpi tiedoston rivit ja luo Booking-olio jokaisesta rivistä
            while ((line = br.readLine()) != null) {
                bookingList.add(Booking.fromFileString(line, cottageList));
            }
        } catch (IOException e) {
            // Tulosta virheilmoitus, jos tiedoston lukemisessa ilmenee ongelmia
            System.out.println("Virhe varaukset.txt-tiedoston lukemisessa: " + e.getMessage());
        }
    }

    /**
     * Tallenna cottageList-olion sisältämät mökit tiedostoon. Jos tiedoston kirjoittamisessa
     * ilmenee ongelmia, tulostetaan virheilmoitus.
     */
    private void saveCottagesToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COTTAGES_FILE))) {
            // Käy läpi mökkilista ja kirjoita jokainen mökki tiedostoon
            for (Cottage cottage : cottageList) {
                bw.write(cottage.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            // Tulosta virheilmoitus, jos tiedoston kirjoittamisessa ilmenee ongelmia
            System.out.println("Virhe cottages.txt-tiedoston kirjoittamisessa: " + e.getMessage());
        }
    }

    /**
     * Tallenna bookingList-olion sisältämät varaukset tiedostoon. Jos tiedoston kirjoittamisessa
     * ilmenee ongelmia, tulostetaan virheilmoitus.
     */
    private void saveBookingsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            // Käy läpi varauslista ja kirjoita jokainen varaus tiedostoon
            for (Booking booking : bookingList) {
                bw.write(booking.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            // Tulosta virheilmoitus, jos tiedoston kirjoittamisessa ilmenee ongelmia
            System.out.println("Virhe varaukset.txt-tiedoston kirjoittamisessa: " + e.getMessage());
        }
    }
}


