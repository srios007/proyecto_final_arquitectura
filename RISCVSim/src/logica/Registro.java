/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import interfaces.IRegistro;

/**
 *
 * @author sebas
 */
public class Registro implements IRegistro {

    private int valor; //Valor interno del registro (32 bits).
    private String binaryID; //ID Binaria del registro
    private String name; //Nombre del registro

    /**
     * Constructor de la clase Registro
     *
     * @param name: identificador del registro
     * @param binaryID: id del registro en binario
     */
    public Registro(String name, String binaryID) {
        this.valor = 0;
        this.name = name;
        this.binaryID = binaryID;
    }

    /**
     * Obtener id binaria del registro
     *
     * @return id del registro
     */
    public String getBinaryID() {
        return binaryID;
    }

    /**
     * Obtener nombre del registro
     *
     * @return nombre del registro
     */
    public String getName() {
        return name;
    }

    /**
     * Asignar nombre del registro
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Asignar ID binaria del registro
     *
     * @param binaryID
     */
    public void setBinaryID(String binaryID) {
        this.binaryID = binaryID;
    }

    @Override
    // Reemplaza el valor actual almacenado en el registro con newVal	 
    public void setValor(int newVal) {
        this.valor = newVal;
    }

    @Override
    // Devuelve el valor almacenado actualmente en el registro, como un byte
    public int getValor() {
        return this.valor;
    }

    @Override
    // Borra el contenido del registro.
    public void clear() {
        this.valor = 0;
    }

}
