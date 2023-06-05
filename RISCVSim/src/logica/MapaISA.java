/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.ArrayList;

/**
 *
 * @author Salinas
 */
public class MapaISA {
        private ArrayList<ArrayList<String>> codeOp;
    
    public MapaISA(){
        this.codeOp = new ArrayList<>();
        this.construirMapa();
    }
    /**
     * Construcción de mapa de codigos de operación:
     * Tipo R, Tipo I, Tipo S
     */
    public void construirMapa(){
        ArrayList<String> codeadd = new ArrayList<>();
        codeadd.add("add");
        codeadd.add("0110011");
        codeadd.add("000");
        codeOp.add(codeadd);
        
        ArrayList<String> codesub = new ArrayList<>();
        codesub.add("sub");
        codesub.add("0110011");
        codesub.add("001");
        codeOp.add(codesub);
        
        ArrayList<String> codemul = new ArrayList<>();
        codemul.add("mul");
        codemul.add("0110011");
        codemul.add("010");
        codeOp.add(codemul);
        
        ArrayList<String> codediv = new ArrayList<>();
        codediv.add("div");
        codediv.add("0110011");
        codediv.add("011");
        codeOp.add(codediv);
        
        ArrayList<String> codeand = new ArrayList<>();
        codeand.add("and");
        codeand.add("0110011");
        codeand.add("100");
        codeOp.add(codeand);
        
        ArrayList<String> codeor = new ArrayList<>();
        codeor.add("or");
        codeor.add("0110011");
        codeor.add("101");
        codeOp.add(codeor);
        
        ArrayList<String> codexor = new ArrayList<>();
        codexor.add("xor");
        codexor.add("0110011");
        codexor.add("110");
        codeOp.add(codexor);
        
        ArrayList<String> codenot = new ArrayList<>();
        codenot.add("comp");
        codenot.add("0110011");
        codenot.add("111");
        codeOp.add(codenot);
        
        ArrayList<String> codeaddi = new ArrayList<>();
        codeaddi.add("addi");
        codeaddi.add("0010011");
        codeaddi.add("000");
        codeOp.add(codeaddi);
        
        ArrayList<String> codesubi = new ArrayList<>();
        codesubi.add("subi");
        codesubi.add("0010011");
        codesubi.add("001");
        codeOp.add(codesubi);
        
        ArrayList<String> codemuli = new ArrayList<>();
        codemuli.add("muli");
        codemuli.add("0010011");
        codemuli.add("010");
        codeOp.add(codemuli);
        
        ArrayList<String> codedivi = new ArrayList<>();
        codedivi.add("divi");
        codedivi.add("0010011");
        codedivi.add("011");
        codeOp.add(codedivi);
        
        ArrayList<String> codeandi = new ArrayList<>();
        codeandi.add("andi");
        codeandi.add("0010011");
        codeandi.add("100");
        codeOp.add(codeandi);
        
        ArrayList<String> codeori = new ArrayList<>();
        codeori.add("ori");
        codeori.add("0010011");
        codeori.add("101");
        codeOp.add(codeori);
        
        ArrayList<String> codexori = new ArrayList<>();
        codexori.add("xori");
        codexori.add("0010011");
        codexori.add("110");
        codeOp.add(codexori);     
        
        ArrayList<String> codeout = new ArrayList<>();
        codeout.add("out");
        codeout.add("1111111");
        codeout.add("000");
        codeOp.add(codeout);
        
        ArrayList<String> codehlt = new ArrayList<>();
        codehlt.add("hlt");
        codehlt.add("1111110");
        codehlt.add("000");
        codeOp.add(codehlt);
        
        ArrayList<String> codeload = new ArrayList<>();
        codeload.add("lw");
        codeload.add("0000011");
        codeload.add("000");
        codeOp.add(codeload);
        
        ArrayList<String> codestore = new ArrayList<>();
        codestore.add("sw");
        codestore.add("0100011");
        codestore.add("000");
        codeOp.add(codestore);
        
        ArrayList<String> coderegload = new ArrayList<>();
        coderegload.add("li");
        coderegload.add("0000111");
        coderegload.add("000");
        codeOp.add(coderegload);
       
    }
    
    /**
     * Obtener todo el Mapa ISA
     * @return 
     */
    public ArrayList<ArrayList<String>> getMapa(){
        return this.codeOp;
    }
    
    /**
     * Obtener Código de Operación según su nombre
     * @param code
     * @return 
     */
    public ArrayList<String> getCodeValues(String code){
        for(ArrayList<String> cd: this.codeOp){
            if(cd.contains(code)){
                return cd;
            }
        }
        return null;
    }
    
    /**
     * Obtener nombre en el ISA según su OpCode y su SelCode
     * @param binCode
     * @param binAuxCode
     * @return 
     */
    public String getCodeOp(String binCode, String binAuxCode){
       for(ArrayList<String> cd: this.codeOp){
            if(cd.contains(binCode) && cd.contains(binAuxCode)){
                return cd.get(0);
            }
        }
        return null;
    }
}
