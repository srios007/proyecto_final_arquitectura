/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

/**
 *
 * @author Salinas
 */
public class main {
    public static void main(String[] args) {
        MapaRegistros mp = new MapaRegistros();
        System.out.println(mp.getMapa());
        MapaISA mi = new MapaISA();
        System.out.println(mi.getMapa());
        Memoria RAM = new Memoria();
        Decodificador dec = new Decodificador(mp,mi,RAM, new Thread());
        String[] palcl1 = dec.generarPalabraControl("li a5,11");
        dec.ejecutarInstruccion(palcl1);
        String[] palcl2 = dec.generarPalabraControl("li a6,14");
        dec.ejecutarInstruccion(palcl2);
        String[] palcl = dec.generarPalabraControl("addi a7,a5,4");
        dec.ejecutarInstruccion(palcl);

        String[] palcl3 = dec.generarPalabraControl("out a7");
        System.out.println(dec.ejecutarInstruccion(palcl3));   
        
        String[] palcl4 = dec.generarPalabraControl("hlt");
        System.out.println(dec.ejecutarInstruccion(palcl4)); 
    }
}
