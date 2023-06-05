/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Salinas
 */
public class Decodificador {

    private MapaRegistros mapr;
    private MapaISA mapisa;
    private Memoria RAM;
    private Thread clk;

    /**
     * Constructor de la clase Decodificador
     * @param mapr: Mapa de registros
     * @param mapisa: Mapa ISA
     * @param RAM: Memoria RAM de la máquina
     * @param clk: reloj de la máquina
     */
    public Decodificador(MapaRegistros mapr, MapaISA mapisa, Memoria RAM, Thread clk) {
        this.mapr = mapr;
        this.mapisa = mapisa;
        this.RAM = RAM;
        this.clk = clk;
    }

    /**
     * Obtener parámetros desde la cadena de entrada
     * @param par
     * @return 
     */
    public String[] obtenerParametros(String par) {
        return par.split(",");
    }

    /**
     * Obtener partes (Operación/parámetros) desde la cadena de entrada
     * @param prt
     * @return 
     */
    public String[] obtenerPartes(String prt) {
        return prt.split(" ");
    }

    /**
     * Generar palabra de control de la instrucción entrante. Se estructura así:
     * [func7 (7 bits),rs1(5 bits),rs2(5 bits),selCode(3 bits),rd(5 bits), opCode(7 bits)]
     * @param sent
     * @return 
     */
    public String[] generarPalabraControl(String sent) {
        String[] parts = this.obtenerPartes(sent);
        String[] params;

        if (parts.length > 1) {
            params = this.obtenerParametros(parts[1]);
        } else {
            params = new String[1];
        }

        System.out.println(Arrays.toString(parts));
        System.out.println(Arrays.toString(params));

        ArrayList<String> codes = mapisa.getCodeValues(parts[0]);

        String[] palabraControl = new String[6];

        if (params.length == 3 && (parts[0].equals("lw") || parts[0].equals("sw"))) {
            palabraControl[0] = "0000000";
            try {
                palabraControl[1] = mapr.getRegistro(params[2]).getBinaryID();
            } catch (Exception e) {
                int val = Integer.parseInt(params[1]);
                if (val > 32 || val < 0) {
                    val = 0;
                    System.out.println("Cantidad de bytes superior a la disponible...");
                }
                palabraControl[1] = Integer.toBinaryString(val);
            }
            palabraControl[2] = mapr.getRegistro(params[0]).getBinaryID();
            palabraControl[3] = codes.get(2);
            int offset = Integer.parseInt(params[2]);
            if (offset > 32 || offset < 0) {
                offset = 0;
                System.out.println("Cantidad de bytes superior a la disponible...");
            }
            palabraControl[4] = Integer.toBinaryString(offset);
            palabraControl[5] = codes.get(1);

        }

        if (params.length == 3 && !(parts[0].equals("lw") || parts[0].equals("sw"))) {
            palabraControl[0] = "0000000";
            try {
                palabraControl[1] = mapr.getRegistro(params[2]).getBinaryID();
            } catch (Exception e) {
                int val = Integer.parseInt(params[2]);
                if (val > 32 || val < 0) {
                    val = 0;
                    System.out.println("Cantidad de bytes superior a la disponible...");
                }
                palabraControl[1] = Integer.toBinaryString(val);
            }
            palabraControl[2] = mapr.getRegistro(params[1]).getBinaryID();
            palabraControl[3] = codes.get(2);
            palabraControl[4] = mapr.getRegistro(params[0]).getBinaryID();
            palabraControl[5] = codes.get(1);
        }

        if (params.length == 2) {
            palabraControl[0] = "0000000";
            try {
                palabraControl[1] = mapr.getRegistro(params[2]).getBinaryID();
            } catch (Exception e) {
                int val = Integer.parseInt(params[1]);
                if (val > 32 || val < 0) {
                    val = 0;
                    System.out.println("Cantidad de bytes superior a la disponible...");
                }
                palabraControl[1] = Integer.toBinaryString(val);
            }
            palabraControl[2] = mapr.getRegistro(params[0]).getBinaryID();
            palabraControl[3] = codes.get(2);
            palabraControl[4] = "00000";
            palabraControl[5] = codes.get(1);

        }

        if (params.length == 1) {
            palabraControl[0] = "0000000";
            palabraControl[1] = "00000";
            palabraControl[2] = "00000";
            palabraControl[3] = codes.get(2);
            try{
            palabraControl[4] = mapr.getRegistro(params[0]).getBinaryID();
            }catch(Exception e){
                palabraControl[4] = "00000";
            }
            palabraControl[5] = codes.get(1);
        }
        System.out.println(Arrays.toString(palabraControl));
        return palabraControl;

    }

    public Thread getClk() {
        return clk;
    }

    public void setClk(Thread clk) {
        this.clk = clk;
    }
    
    /**
     * Ejecutar la instrucción de acuerdo con su palabra de control
     * @param palControl
     * @return 
     */
    public String ejecutarInstruccion(String[] palControl) {
        String codeOp = palControl[5]; //Código de operación
        String BinOp = palControl[3]; //Selector de operación
        String operacion = mapisa.getCodeOp(codeOp, BinOp);
        System.out.println(operacion);
        Registro Rs1 = mapr.getRegistroById(palControl[1]);
        Registro Rs2 = mapr.getRegistroById(palControl[2]);
        Registro Rd = mapr.getRegistroById(palControl[4]);
        int Ivalue = 0;
        int PosMem = 0;
        int Offset = 0;

        if (codeOp.equals("0000011") || codeOp.equals("0100011")) {
            PosMem = Integer.parseInt(palControl[1], 2);
            Offset = Integer.parseInt(palControl[4], 2);
        }

        if (codeOp.equals("0010011") || codeOp.equals("0000111")) {
            Ivalue = Integer.parseInt(palControl[1], 2);
            System.out.println(Ivalue);
        }
        Operaciones opr = new Operaciones();

        //palControl[1] y palControl[2] operandos, palControl[4] almacenamiento
        //palControl[1] posMemoria I/O palControl[2] Registro I/O palControl[4] offSet
        switch (operacion) {
            case "add":
                int add = opr.suma(Rs1.getValor(), Rs2.getValor());
                Rd.setValor(add);
                break;
            case "sub":
                int sub = opr.resta(Rs1.getValor(), Rs2.getValor());
                Rd.setValor(sub);
                break;
            case "mul":
                int mul = opr.multi(Rs1.getValor(), Rs2.getValor());
                Rd.setValor(mul);
                break;
            case "div":
                int div = opr.division(Rs1.getValor(), Rs2.getValor());
                Rd.setValor(div);
                break;
            case "and":
                int and = opr.and(Rs1.getValor(), Rs2.getValor());
                Rd.setValor(and);
                break;
            case "or":
                int or = opr.or(Rs1.getValor(), Rs2.getValor());
                Rd.setValor(or);
                break;
            case "xor":
                int xor = opr.xor(Rs1.getValor(), Rs2.getValor());
                Rd.setValor(xor);
                break;
            case "comp":
                int comp = opr.comp(Rs1.getValor());
                Rd.setValor(comp);
                break;
            case "addi":
                int addi = opr.suma(Ivalue, Rs2.getValor());
                Rd.setValor(addi);
                break;
            case "subi":
                int subi = opr.resta(Ivalue, Rs2.getValor());
                Rd.setValor(subi);
                break;
            case "muli":
                int muli = opr.multi(Ivalue, Rs2.getValor());
                Rd.setValor(muli);
                break;
            case "divi":
                int divi = opr.division(Ivalue, Rs2.getValor());
                Rd.setValor(divi);
                break;
            case "andi":
                int andi = opr.and(Ivalue, Rs2.getValor());
                Rd.setValor(andi);
                break;
            case "ori":
                int ori = opr.or(Ivalue, Rs2.getValor());
                Rd.setValor(ori);
                break;
            case "xori":
                int xori = opr.xor(Ivalue, Rs2.getValor());
                Rd.setValor(xori);
                break;
            case "li":
                Rs2.setValor(Ivalue);
                System.out.println(Rs2.getValor());
                break;
            case "out":
                return Integer.toString(Rd.getValor());
            case "hlt":
                return "HLT";
            case "lw":
                Rs2.setValor(RAM.memoryLoad(PosMem, Offset));
                break;
            case "sw":
                RAM.memoryStoreAt(Rs2.getValor(), PosMem);
                break;
            default:
                System.out.println("Instrucción no reconocida");
                break;
        }
        return "";
    }

}
