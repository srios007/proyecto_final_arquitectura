/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Salinas
 */
public class MapaRegistros {
    private ArrayList<Registro> registros;
    
    public MapaRegistros(){
        this.registros = new ArrayList<>();
        this.construirMapa();
    }
    /**
     * Construcción de mapa de registros:
     *  zero: registro con valor cero
     *  ra: dirección de retorno a la función actual
     *  sp: puntero a la cabeza de la pila
     *  gp: puntero a la sección global de la data
     *  tp: puntero a thread
     *  tX: registros temporales
     *  aX: registros para enviar argumentos
     *  sX: registros seguros
     */
    public void construirMapa(){
        registros.add(new Registro("zero","00000"));
        registros.add(new Registro("ra","00001"));
        registros.add(new Registro("sp","00010"));
        registros.add(new Registro("gp","00011"));
        registros.add(new Registro("tp","00100"));
        registros.add(new Registro("t0","00101"));
        registros.add(new Registro("t1","00110"));
        registros.add(new Registro("t2","00111"));
        registros.add(new Registro("s0","01000"));
        registros.add(new Registro("s1","01001"));
        registros.add(new Registro("a0","01010"));
        registros.add(new Registro("a1","01011"));
        registros.add(new Registro("a2","01100"));
        registros.add(new Registro("a3","01101"));
        registros.add(new Registro("a4","01110"));
        registros.add(new Registro("a5","01111"));
        registros.add(new Registro("a6","10000"));
        registros.add(new Registro("a7","10001"));
        registros.add(new Registro("s2","10010"));
        registros.add(new Registro("s3","10011"));
        registros.add(new Registro("s4","10100"));
        registros.add(new Registro("s5","10101"));
        registros.add(new Registro("s6","10110"));
        registros.add(new Registro("s7","10111"));
        registros.add(new Registro("s8","11000"));
        registros.add(new Registro("s9","11001"));
        registros.add(new Registro("s10","11010"));
        registros.add(new Registro("s11","11011"));
        registros.add(new Registro("t3","11100"));
        registros.add(new Registro("t4","11101"));
        registros.add(new Registro("t5","11110"));
        registros.add(new Registro("t6","11111"));


        
    }
    
    /**
     * Obtener mapa de registros
     * @return 
     */
    public ArrayList<Registro> getMapa(){
        return this.registros;
    }
    
    /**
     * Obtener registro por su nombre
     * @param reg: nombre del registro
     * @return 
     */
    public Registro getRegistro(String reg){
        for(Registro cd: this.registros){
            if(cd.getName().equals(reg)){
                return cd;
            }
        }
        return null;
    }
    
    /**
     * Obtener registro por su ID binaria
     * @param BinID: ID binaria del registro
     * @return 
     */
    public Registro getRegistroById(String BinID){
        for(Registro cd: this.registros){
            if(cd.getBinaryID().equals(BinID)){
                return cd;
            }
        }
        return null;
    }
}
