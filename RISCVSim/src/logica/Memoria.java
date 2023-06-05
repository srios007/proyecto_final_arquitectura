package logica;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import interfaces.IRAMObserver;
import interfaces.IRegistro;
import java.util.Arrays;

public class Memoria {

    private int capacidadMemoria; // capacidad de la memoria en bytes

    // Mantener los valores de RAM como un arreglo de bytes
    private String[] data;

    // La memoria mantiene el direccionamiento como una referencia al registro de direcciones de memoria.
    // La memoria es observable, por lo que debe mantener una lista de observadores
    private List<IRAMObserver> observers;

    /**
     * Constructor de la clase memoria
     */
    public Memoria() {
        capacidadMemoria = 16 * 1024 * 1024;

        this.data = new String[capacidadMemoria];
        this.observers = new ArrayList<>();

        // Cargar valores basura en la memoria (para probar)
        for (int i = 0; i < capacidadMemoria; i++) {
            this.data[i] = "00000000";
        }

    }

    /**
     * Cargar un valor de manera secuencial en la memoria RAM
     * @param val: valor a insertar
     */
    public void memoryStore(int val) {
        String stringVal = Integer.toBinaryString(val);
        String zeros = "";

        for (int i = 0; i < 32 - stringVal.length(); i++) {
            zeros += 0;
        }
        String fullString = zeros + stringVal;
        String[] splittedVal = fullString.split("(?<=\\G.{8})");

        System.out.println(Arrays.toString(splittedVal));

        for (int k = 0; k < this.data.length; k += 4) {
            if (this.data[k].equals("00000000") && this.data[k + 1].equals("00000000")
                    && this.data[k + 2].equals("00000000") && this.data[k + 3].equals("00000000")) {
                this.data[k] = splittedVal[0];
                this.data[k + 1] = splittedVal[1];
                this.data[k + 2] = splittedVal[2];
                this.data[k + 3] = splittedVal[3];
                break;
            }

        }
        /*
       for(int q = 0; q < 80; q++){
           System.out.print(this.data[q]+" ");
       }
         */
    }

    /**
     * Cargar un valor en la dirección seleccionada en la memoria RAM
     * @param val: valor a cargar
     * @param dir: dirección dentro de la memoria RAM
     */
    public void memoryStoreAt(int val, int dir) {
        String stringVal = Integer.toBinaryString(val);
        String zeros = "";

        for (int i = 0; i < 32 - stringVal.length(); i++) {
            zeros += 0;
        }
        String fullString = zeros + stringVal;
        String[] splittedVal = fullString.split("(?<=\\G.{8})");

        System.out.println(Arrays.toString(splittedVal));

        if (dir % 4 != 0) {
            System.out.println("Dirección de memoria no válida...");
        } else {
            if (this.data[dir].equals("00000000") && this.data[dir + 1].equals("00000000")
                    && this.data[dir + 2].equals("00000000") && this.data[dir + 3].equals("00000000")) {
                this.data[dir] = splittedVal[0];
                this.data[dir + 1] = splittedVal[1];
                this.data[dir + 2] = splittedVal[2];
                this.data[dir + 3] = splittedVal[3];
            } else {
                System.out.println("La dirección de memoria " + dir + "sobreescribe información previamente almacenada.");
            }
        }

        /*
       for(int q = 0; q < 80; q++){
           System.out.print(this.data[q]+" ");
       }
         */
    }

    /**
     * Retorna el valor almacenado en la memoria RAM según una dirección
     * @param dir: dirección dentro de la memoria RAM
     * @param offset: Desplazamiento dentro de las direcciones
     * @return 
     */
    public int memoryLoad(int dir, int offset) {
        
        String loadedValue = "";
        if(offset < 0){
            offset = 0;
        }
        int trValue = offset*4;
        dir += trValue;

        if(dir % 4 == 0){
        loadedValue += this.data[dir];
        loadedValue += this.data[dir + 1];
        loadedValue += this.data[dir + 2];
        loadedValue += this.data[dir + 3];
        }
        else{
            System.out.println("Dirección de memoria incorrecta");
            return -1;
        }
        
        return Integer.parseInt(loadedValue,2);
    }

    // Cambia manualmente una dirección de memoria (utilizada en WidgetRAM)
    public void cambiarValor(int address, String newVal) {
        this.data[address] = newVal;
        this.notifyObservers(address);
    }

    // Devuleve el contenido de la memoria
    public String[] getData() {
        return this.data;
    }

    //Retorna las posiciones de memoria especificadas
    public void showMemory(int pos) {

        for (int q = 0; q < pos; q++) {
            System.out.print(this.data[q] + " ");
        }

    }

    // Métodos para implementar el patrón de diseño Observer
    public void addRAMObserver(IRAMObserver o) {
        if (o == null) {
            return;
        }
        this.observers.add(o);
    }

    public void removeRAMObserver(IRAMObserver o) {
        if (o == null) {
            return;
        }
        this.observers.remove(o);
    }

    private void notifyObservers(int address) {
        for (IRAMObserver o : observers) {
            o.cambiaValorRAM(address);
        }
    }

}
