/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

/**
 *
 * @author Salinas
 */
public class Operaciones {

    public Operaciones() {
    }

    /**
     * Operación Suma
     *
     * @param valA: valor del registro A
     * @param valB: Valor del registro B
     * @return
     */
    public int suma(int valA, int valB) {
        return valA + valB;
    }

    /**
     * Operación Resta
     *
     * @param valA: valor del registro A
     * @param valB: Valor del registro B
     * @return
     */
    public int resta(int valA, int valB) {
        return valA - valB;
    }

    /**
     * Operación Multiplicación
     *
     * @param valA: valor del registro A
     * @param valB: Valor del registro B
     * @return
     */
    public int multi(int valA, int valB) {
        return valA * valB;
    }

    /**
     * Operación División
     *
     * @param valA: valor del registro A
     * @param valB: Valor del registro B
     * @return
     */
    public int division(int valA, int valB) {
        return valA / valB;
    }

    /**
     * Operación And
     *
     * @param valA: valor del registro A
     * @param valB: Valor del registro B
     * @return
     */
    public int and(int valA, int valB) {
        return valA & valB;
    }

    /**
     * Operación Or
     *
     * @param valA: valor del registro A
     * @param valB: Valor del registro B
     * @return
     */
    public int or(int valA, int valB) {
        return valA | valB;
    }

    /**
     * Operación Xor
     *
     * @param valA: valor del registro A
     * @param valB: Valor del registro B
     * @return
     */
    public int xor(int valA, int valB) {
        return valA ^ valB;
    }

    /**
     * Operación Complemento (Negación)
     *
     * @param A: valor del registro A
     * @return
     */
    public int comp(int A) {
        return ~A;
    }

}
