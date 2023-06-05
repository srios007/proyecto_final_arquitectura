/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import logica.Decodificador;
import logica.MapaISA;
import logica.MapaRegistros;
import logica.Memoria;
import logica.Registro;

/**
 *
 * @author Salinas
 */
public class VistaSim extends javax.swing.JFrame {

    /**
     * Creates new form VistaSim
     */
    private boolean finalizado; //Indica si el reloj está activo o no
    private double tiempoPausa; //Cantidad de tiempo entre pulsos
    private Thread reloj;       //Objeto Thread que representa el reloj
    private MapaRegistros mr;   //Mapa de registros
    private MapaISA mi;         //Mapa ISA de la máquina
    private Memoria RAM;        //Memoria RAM de la máquina
    private Decodificador dc;   //Decodificador de la máquina
    private boolean clk;        //Estado del reloj (true=ALTO, false=BAJO)
    private int programCounter; //Contador de programa de la máquina

    /**
     * Constructor de la clase VistaSim
     */
    public VistaSim() {
        initComponents();
        setTitle("Simulador RISC-V");
        setLocationRelativeTo(null);
        setResizable(false);
        this.BtnEjecutar.setEnabled(false);
        this.BtnDetener.setEnabled(false);
        this.AreaComandos.setEditable(false);
        this.AreaISA.setEditable(false);
        this.programCounter = 0;
        this.finalizado = false;
        this.tiempoPausa = 1000;
        this.mr = new MapaRegistros();
        this.mi = new MapaISA();
        this.RAM = new Memoria();
        this.dc = new Decodificador(mr, mi, RAM, new Thread());
        this.ConstruirTablaRegistros();
        this.ConstruirTablaRAM();
        this.imprimirISA();
        this.clk = false;
    }

    /**
     * Detener el reloj
     */
    public void terminar() {
        this.finalizado = true;
    }

    /**
     * Obtener el tiempo entre pulsos
     * @return 
     */
    public double getTiempoPausa() {
        return this.tiempoPausa;
    }

    /**
     * Imprime el ISA de la máquina en la GUI
     */
    public void imprimirISA() {
        ArrayList<ArrayList<String>> map = this.mi.getMapa();
        this.AreaISA.setText(this.AreaISA.getText()
                + "NAME" + "\t" + "OPCODE" + "\t" + "SELCODE" + "\n");
        for (ArrayList<String> m : map) {
            this.AreaISA.setText(this.AreaISA.getText()
                    + m.get(0) + "\t" + m.get(1) + "\t" + m.get(2) + "\n");
        }
    }

    /**
     * Imprime la palabra de control en la GUI
     * @param pc 
     */
    public void imprimirPalControl(String[] pc) {
        this.PalCtrl0.setText(pc[0]);
        this.PalCtrl1.setText(pc[1]);
        this.PalCtrl2.setText(pc[2]);
        this.PalCtrl3.setText(pc[3]);
        this.PalCtrl4.setText(pc[4]);
        this.PalCtrl5.setText(pc[5]);
    }

    /**
     * Construye la tabla que muestra los registros (inicial y actualizar)
     */
    public void ConstruirTablaRegistros() {
        DefaultTableModel md = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        md.addColumn("Registro");
        md.addColumn("ID");
        md.addColumn("Valor");

        Object[] row = new Object[3];

        ArrayList<Registro> registrosEx = this.mr.getMapa();

        for (int i = 0; i < registrosEx.size(); i++) {
            row[0] = registrosEx.get(i).getName();
            row[1] = registrosEx.get(i).getBinaryID();
            row[2] = registrosEx.get(i).getValor();
            md.addRow(row);
        }

        this.TablaRegistros.setModel(md);

    }

    /**
     * Cambiar estado del reloj
     * Alto -> Bajo, Bajo -> Alto
     */
    public void cambiarEstado() {
        this.clk = !this.clk;
    }

    /**
     * Construye la tabla que muestra la memoria RAM con sus primeros 65535
     * registros (inicial y actualizar)
     */
    public void ConstruirTablaRAM() {
        DefaultTableModel md = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        md.addColumn("Posición");
        md.addColumn("Valor");

        Object[] row = new Object[2];

        String[] mem = this.RAM.getData();

        for (int i = 0; i < 65535; i++) {
            row[0] = Integer.toHexString(i);
            String[] spl = mem[i].split("(?<=\\G.{2})");
            row[1] = spl[0] + " " + spl[1] + " " + spl[2] + " " + spl[3];
            md.addRow(row);
        }

        this.TablaRAM.setModel(md);
    }

    /**
     * Actualizar el estado del reloj en la GUI
     */
    public void actualizarClk() {
        boolean estado = this.clk;
        if (estado) {
            this.PanelCLK.setBackground(new Color(102, 204, 255));
            this.PanelCLK.setText("Alto //");
        } else {
            this.PanelCLK.setBackground(new Color(255, 102, 102));
            this.PanelCLK.setText("Bajo //");
        }
    }

    /**
     * Ejecutar las instrucciones entrantes desde el panel de entrada
     * Lee una línea a la vez
     * @throws InterruptedException 
     */
    public void ejecutarInstrucciones() throws InterruptedException {
        String[] instrucciones = this.AreaComandos.getText().split("\n");
        try {
            String[] palControl = this.dc.generarPalabraControl(instrucciones[programCounter]);
            String out = this.dc.ejecutarInstruccion(palControl);
            this.ConstruirTablaRegistros();
            this.ConstruirTablaRAM();
            if (!out.equals("") && !out.equals("HLT")) {
                this.PanelOUT.setText(out);
            }
            if (out.equals("HLT")) {
                this.finalizado = true;
                this.reloj.interrupt();
                this.BtnCargar.setEnabled(true);
                this.BtnLimpiar.setEnabled(true);
                this.BtnDetener.setEnabled(false);
            }
            this.imprimirPalControl(palControl);
            programCounter++;
            Thread.sleep((long) tiempoPausa);
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Hilo detenido...", "Aviso", 1);
        } catch (ArrayIndexOutOfBoundsException a) {
        }

    }

    /**
     * Administra el hilo que representa al reloj
     */
    public void HiloReloj() {
        this.reloj = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!finalizado) {
                    try {
                        cambiarEstado();
                        actualizarClk();
                        System.out.println(clk);
                        if (clk) {
                            ejecutarInstrucciones();
                        } else {
                            Thread.sleep((long) tiempoPausa);
                        }
                    } catch (InterruptedException ex) {
                        JOptionPane.showMessageDialog(null, "Error en hilo...", "Error", 0);
                    }
                }
            }
        });
        this.dc.setClk(reloj);
        this.reloj.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelOpciones = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        AreaComandos = new javax.swing.JTextArea();
        PanelBotones = new javax.swing.JPanel();
        BtnEjecutar = new javax.swing.JButton();
        BtnCargar = new javax.swing.JButton();
        BtnDetener = new javax.swing.JButton();
        BtnLimpiar = new javax.swing.JButton();
        PanelRegistros = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TablaRegistros = new javax.swing.JTable();
        PanelISA = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        AreaISA = new javax.swing.JTextArea();
        PanelPalabraControl = new javax.swing.JPanel();
        PalCtrl0 = new javax.swing.JLabel();
        PalCtrl1 = new javax.swing.JLabel();
        PalCtrl2 = new javax.swing.JLabel();
        PalCtrl3 = new javax.swing.JLabel();
        PalCtrl4 = new javax.swing.JLabel();
        PalCtrl5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        PanelRAM = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TablaRAM = new javax.swing.JTable();
        PanelSalida = new javax.swing.JPanel();
        LblOUT = new javax.swing.JLabel();
        LblCLK = new javax.swing.JLabel();
        PanelOUT = new javax.swing.JLabel();
        PanelCLK = new javax.swing.JLabel();
        SliderTime = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        PanelOpciones.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Panel", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Berlin Sans FB", 0, 12))); // NOI18N

        AreaComandos.setColumns(20);
        AreaComandos.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        AreaComandos.setRows(5);
        jScrollPane1.setViewportView(AreaComandos);

        PanelBotones.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Opciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Berlin Sans FB", 0, 12))); // NOI18N

        BtnEjecutar.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        BtnEjecutar.setText("Ejecutar");
        BtnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEjecutarActionPerformed(evt);
            }
        });

        BtnCargar.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        BtnCargar.setText("Cargar");
        BtnCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCargarActionPerformed(evt);
            }
        });

        BtnDetener.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        BtnDetener.setText("Detener");
        BtnDetener.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDetenerActionPerformed(evt);
            }
        });

        BtnLimpiar.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        BtnLimpiar.setText("Limpiar");
        BtnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelBotonesLayout = new javax.swing.GroupLayout(PanelBotones);
        PanelBotones.setLayout(PanelBotonesLayout);
        PanelBotonesLayout.setHorizontalGroup(
            PanelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnDetener, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                    .addComponent(BtnEjecutar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnCargar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelBotonesLayout.setVerticalGroup(
            PanelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBotonesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(BtnEjecutar)
                .addGap(18, 18, 18)
                .addComponent(BtnCargar)
                .addGap(18, 18, 18)
                .addComponent(BtnDetener)
                .addGap(18, 18, 18)
                .addComponent(BtnLimpiar)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PanelOpcionesLayout = new javax.swing.GroupLayout(PanelOpciones);
        PanelOpciones.setLayout(PanelOpcionesLayout);
        PanelOpcionesLayout.setHorizontalGroup(
            PanelOpcionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOpcionesLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        PanelOpcionesLayout.setVerticalGroup(
            PanelOpcionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOpcionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelOpcionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(PanelBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        PanelRegistros.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Registros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Berlin Sans FB", 0, 12))); // NOI18N

        TablaRegistros.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        TablaRegistros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        TablaRegistros.setCellSelectionEnabled(true);
        jScrollPane2.setViewportView(TablaRegistros);

        PanelISA.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "ISA de la máquina", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Berlin Sans FB", 0, 12))); // NOI18N

        AreaISA.setColumns(20);
        AreaISA.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        AreaISA.setRows(5);
        jScrollPane4.setViewportView(AreaISA);

        javax.swing.GroupLayout PanelISALayout = new javax.swing.GroupLayout(PanelISA);
        PanelISA.setLayout(PanelISALayout);
        PanelISALayout.setHorizontalGroup(
            PanelISALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );
        PanelISALayout.setVerticalGroup(
            PanelISALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );

        javax.swing.GroupLayout PanelRegistrosLayout = new javax.swing.GroupLayout(PanelRegistros);
        PanelRegistros.setLayout(PanelRegistrosLayout);
        PanelRegistrosLayout.setHorizontalGroup(
            PanelRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRegistrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                    .addComponent(PanelISA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelRegistrosLayout.setVerticalGroup(
            PanelRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRegistrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelISA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        PanelPalabraControl.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Palabra de Control", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Berlin Sans FB", 0, 12))); // NOI18N

        PalCtrl0.setBackground(new java.awt.Color(102, 255, 102));
        PalCtrl0.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        PalCtrl0.setText("0000000");
        PalCtrl0.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PalCtrl0.setOpaque(true);

        PalCtrl1.setBackground(new java.awt.Color(153, 102, 255));
        PalCtrl1.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        PalCtrl1.setText("00000");
        PalCtrl1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PalCtrl1.setOpaque(true);

        PalCtrl2.setBackground(new java.awt.Color(153, 102, 255));
        PalCtrl2.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        PalCtrl2.setText("00000");
        PalCtrl2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PalCtrl2.setOpaque(true);

        PalCtrl3.setBackground(new java.awt.Color(102, 204, 255));
        PalCtrl3.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        PalCtrl3.setText("000");
        PalCtrl3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PalCtrl3.setOpaque(true);

        PalCtrl4.setBackground(new java.awt.Color(255, 102, 102));
        PalCtrl4.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        PalCtrl4.setText("00000");
        PalCtrl4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PalCtrl4.setOpaque(true);

        PalCtrl5.setBackground(new java.awt.Color(102, 204, 255));
        PalCtrl5.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        PalCtrl5.setText("0000000");
        PalCtrl5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PalCtrl5.setOpaque(true);

        jLabel1.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("func7");

        jLabel2.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("rs1");

        jLabel3.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("rs2");

        jLabel4.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("selCode");

        jLabel5.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("rd");

        jLabel6.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("opCode");

        javax.swing.GroupLayout PanelPalabraControlLayout = new javax.swing.GroupLayout(PanelPalabraControl);
        PanelPalabraControl.setLayout(PanelPalabraControlLayout);
        PanelPalabraControlLayout.setHorizontalGroup(
            PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPalabraControlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PalCtrl0, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PalCtrl1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelPalabraControlLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(PalCtrl2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelPalabraControlLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PalCtrl3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PalCtrl4, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PalCtrl5, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        PanelPalabraControlLayout.setVerticalGroup(
            PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPalabraControlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PalCtrl1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PalCtrl0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PalCtrl2)
                    .addComponent(PalCtrl3)
                    .addComponent(PalCtrl4)
                    .addComponent(PalCtrl5))
                .addGap(2, 2, 2)
                .addGroup(PanelPalabraControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)))
        );

        PanelRAM.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Memoria RAM", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Berlin Sans FB", 0, 12))); // NOI18N

        TablaRAM.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        TablaRAM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        TablaRAM.setCellSelectionEnabled(true);
        jScrollPane3.setViewportView(TablaRAM);

        javax.swing.GroupLayout PanelRAMLayout = new javax.swing.GroupLayout(PanelRAM);
        PanelRAM.setLayout(PanelRAMLayout);
        PanelRAMLayout.setHorizontalGroup(
            PanelRAMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRAMLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        PanelRAMLayout.setVerticalGroup(
            PanelRAMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRAMLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addContainerGap())
        );

        PanelSalida.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Salida", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Berlin Sans FB", 0, 12))); // NOI18N

        LblOUT.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        LblOUT.setText("OUT");

        LblCLK.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        LblCLK.setText("CLK");

        PanelOUT.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        PanelOUT.setText("[Número Entero]");
        PanelOUT.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        PanelCLK.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        PanelCLK.setText("Alto/Bajo");
        PanelCLK.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PanelCLK.setOpaque(true);

        SliderTime.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        SliderTime.setMaximum(2000);
        SliderTime.setMinimum(50);
        SliderTime.setPaintLabels(true);
        SliderTime.setPaintTicks(true);
        SliderTime.setValue(1000);
        SliderTime.setBorder(javax.swing.BorderFactory.createTitledBorder("Velocidad"));
        SliderTime.setName("Velocidad"); // NOI18N

        javax.swing.GroupLayout PanelSalidaLayout = new javax.swing.GroupLayout(PanelSalida);
        PanelSalida.setLayout(PanelSalidaLayout);
        PanelSalidaLayout.setHorizontalGroup(
            PanelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSalidaLayout.createSequentialGroup()
                .addGroup(PanelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelSalidaLayout.createSequentialGroup()
                        .addGroup(PanelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(PanelOUT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                            .addComponent(LblOUT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LblCLK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PanelCLK, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)))
                    .addComponent(SliderTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelSalidaLayout.setVerticalGroup(
            PanelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSalidaLayout.createSequentialGroup()
                .addGroup(PanelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LblOUT)
                    .addComponent(LblCLK))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PanelOUT)
                    .addComponent(PanelCLK))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(SliderTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(PanelPalabraControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(PanelOpciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelRAM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelSalida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelRegistros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelOpciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelPalabraControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelRAM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelSalida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(PanelRegistros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Acciones del boton detener
     * @param evt 
     */
    private void BtnDetenerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDetenerActionPerformed
        this.reloj.interrupt();
        JOptionPane.showMessageDialog(null, "Máquina detenida manualmente...", "Aviso", 1);
    }//GEN-LAST:event_BtnDetenerActionPerformed

    /**
     * Acciones del boton limpiar
     * @param evt 
     */
    private void BtnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLimpiarActionPerformed
        this.AreaComandos.setText(null);
        this.mr = new MapaRegistros();
        this.mi = new MapaISA();
        this.RAM = new Memoria();
        this.dc = new Decodificador(mr, mi, RAM, new Thread());
        this.programCounter = 0;
        this.PanelOUT.setText("[Número entero]");
        this.ConstruirTablaRAM();
        this.ConstruirTablaRegistros();
        this.finalizado = false;
    }//GEN-LAST:event_BtnLimpiarActionPerformed

    /**
     * Acciones del boton cargar
     * @param evt 
     */
    private void BtnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCargarActionPerformed
        this.AreaComandos.setText(null);
        this.BtnEjecutar.setEnabled(true);
        int rand = (int) Math.floor(Math.random() * 3);

        switch (rand) {
            case 0:
                String[] defaultComms = {"li a1,5", "li a2,9", "add a3,a1,a2", "xor a5,a1,a3", "sw a3,0,0", "out a3", "hlt"};
                for (int z = 0; z < defaultComms.length; z++) {
                    this.AreaComandos.setText(this.AreaComandos.getText() + defaultComms[z] + "\n");
                }
                break;
            case 1:
                String[] defaultComms1 = {"li a5,1", "li a6,3", "sub a7,a6,a5", "andi a0,a7,10", "sw a0,0,0", "out a7", "hlt"};
                for (int z = 0; z < defaultComms1.length; z++) {
                    this.AreaComandos.setText(this.AreaComandos.getText() + defaultComms1[z] + "\n");
                }
                break;
            default:
                String[] defaultComms2 = {"li t1,4", "muli a2,t1,20", "xor a3,a2,t1", "sw a2,0,0", "out a2", "hlt"};
                for (int z = 0; z < defaultComms2.length; z++) {
                    this.AreaComandos.setText(this.AreaComandos.getText() + defaultComms2[z] + "\n");
                }
                break;
        }


    }//GEN-LAST:event_BtnCargarActionPerformed

    /**
     * Acciones del boton ejecutar
     * @param evt 
     */
    private void BtnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEjecutarActionPerformed
        try {
            this.BtnDetener.setEnabled(true);
            this.BtnCargar.setEnabled(false);
            this.BtnLimpiar.setEnabled(false);
            this.tiempoPausa = this.SliderTime.getValue();
            this.HiloReloj();
        } catch (NullPointerException np) {
            System.out.println("Fin...");
        }
    }//GEN-LAST:event_BtnEjecutarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VistaSim.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VistaSim.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VistaSim.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VistaSim.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VistaSim().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea AreaComandos;
    private javax.swing.JTextArea AreaISA;
    private javax.swing.JButton BtnCargar;
    private javax.swing.JButton BtnDetener;
    private javax.swing.JButton BtnEjecutar;
    private javax.swing.JButton BtnLimpiar;
    private javax.swing.JLabel LblCLK;
    private javax.swing.JLabel LblOUT;
    private javax.swing.JLabel PalCtrl0;
    private javax.swing.JLabel PalCtrl1;
    private javax.swing.JLabel PalCtrl2;
    private javax.swing.JLabel PalCtrl3;
    private javax.swing.JLabel PalCtrl4;
    private javax.swing.JLabel PalCtrl5;
    private javax.swing.JPanel PanelBotones;
    private javax.swing.JLabel PanelCLK;
    private javax.swing.JPanel PanelISA;
    private javax.swing.JLabel PanelOUT;
    private javax.swing.JPanel PanelOpciones;
    private javax.swing.JPanel PanelPalabraControl;
    private javax.swing.JPanel PanelRAM;
    private javax.swing.JPanel PanelRegistros;
    private javax.swing.JPanel PanelSalida;
    private javax.swing.JSlider SliderTime;
    private javax.swing.JTable TablaRAM;
    private javax.swing.JTable TablaRegistros;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    // End of variables declaration//GEN-END:variables
}
