package co.edu.uniquindio.poo.nequii;

public class CategoriaFactory {
    public Categoria crearCategoria(String tipo, double presupuesto) {
        return switch (tipo.toLowerCase()) {
            case "comida" -> new Categoria("CAT-COM", "Comida", presupuesto);
            case "transporte" -> new Categoria("CAT-TRA", "Transporte", presupuesto);
            default -> new Categoria("CAT-GEN", tipo, presupuesto);
        };
    }
}
