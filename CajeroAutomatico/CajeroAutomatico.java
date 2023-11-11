package CajeroAutomatico;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CajeroAutomatico {
    private static final String BILLETES_FILE = "billetes.dat";
    private static final String LOGS_FILE = "logs.txt";

    private Usuario usuario;
    private List<Billete> billetes;

    public CajeroAutomatico() {
        cargarBilletesIniciales();
    }

    public void cargarBilletesIniciales() {
        File file = new File(BILLETES_FILE);

        if (file.exists() && !file.isDirectory()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                billetes = (List<Billete>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // Si el archivo no existe, crea billetes iniciales y guárdalos
            billetes = new ArrayList<>();
            billetes.add(new Billete(100, 100));
            billetes.add(new Billete(200, 100));
            billetes.add(new Billete(500, 20));
            billetes.add(new Billete(1000, 10));

            guardarBilletes();
        }
    }

    public void guardarBilletes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BILLETES_FILE))) {
            oos.writeObject(billetes);
        } catch (IOException e) {
            //Stacktracem: Permite mostrar el nombre de una excepción junto con el mensaje que devuelve getMessage()
            e.printStackTrace();
        }
    }

    public void consultarSaldo() {
        if (usuario != null) {
            //Getter de usuario.getSaldo
            //System.out.println("Saldo actual: $" + usuario.getSaldo());
        } else {
            System.out.println("No hay usuario registrado. Ingrese al modo cajero primero.");
        }
    }

    public void retirarEfectivo(double cantidad) {
        if (usuario != null) {
            if (cantidad > 0 && cantidad <= usuario.getSaldo()) {
                if (verificarDisponibilidadBilletes(cantidad)) {
                    // Realizar la operación de retiro
                    usuario.retirarSaldo(cantidad);
                    actualizarBilletesDisponibles(cantidad);

                    System.out.println("Retiro exitoso. Nuevo saldo: $" + usuario.getSaldo());
                    registrarLog("retirar", usuario.getNombre(), cantidad, true);
                } else {
                    System.out.println("No hay suficientes billetes disponibles para el monto solicitado.");
                    registrarLog("retirar", usuario.getNombre(), cantidad, false);
                }
            } else {
                System.out.println("No se puede realizar el retiro. Verifique el monto ingresado.");
                registrarLog("retirar", usuario.getNombre(), cantidad, false);
            }
        } else {
            System.out.println("No hay usuario registrado. Ingrese al modo cajero primero.");
        }
    }

    private boolean verificarDisponibilidadBilletes(double cantidad) {
        int montoRestante = (int) cantidad;

        for (Billete billete : billetes) {
            int denominacion = billete.getDenominacion();
            int cantidadBilletes = billete.getCantidad();

            int billetesNecesarios = montoRestante / denominacion;

            int billetesAUsar = Math.min(billetesNecesarios, cantidadBilletes);

            if (billetesAUsar > 0) {
                montoRestante -= billetesAUsar * denominacion;
            }

            if (montoRestante == 0) {
                // Combinación de billetes 
                billete.setCantidad(cantidadBilletes - billetesAUsar);
                return true;
            }
        }
        return false;
    }

    public void registrarLog(String accion, String usuario, double saldo, boolean seRealizo) {
    }

    public void mostrarLogs() {
    }

    public void mostrarBilletesDisponibles() {
    }
}
