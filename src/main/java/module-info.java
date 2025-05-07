module co.edu.uniquindio.poo.nequii {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.poo.nequii to javafx.fxml;
    exports co.edu.uniquindio.poo.nequii;
}