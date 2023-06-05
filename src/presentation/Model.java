package presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

public class Model extends JFrame {

    public Model() {
        // Configuración básica del JFrame
        setTitle("Proyecto final arquitectura");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1300, 800));

        // Crear paneles para los módulos
        JPanel inputPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();
        JPanel tablePanel = new JPanel();
        JPanel controlWordsAndClock = new JPanel();
        JPanel textArea = new JPanel();
        JPanel clockOutput = new JPanel();
        JPanel controlWords = new JPanel();

        // Establecer el tamaño de los paneles
        inputPanel.setPreferredSize(new Dimension(400, 400));
        buttonsPanel.setPreferredSize(new Dimension(200, 400));
        tablePanel.setPreferredSize(new Dimension(600, 400));
        controlWordsAndClock.setPreferredSize(new Dimension(600, 310));
        textArea.setPreferredSize(new Dimension(600, 310));
        clockOutput.setPreferredSize(new Dimension(600, 150));
        controlWords.setPreferredSize(new Dimension(600, 150));

        // Crear un borde personalizado
        javax.swing.border.Border borde = BorderFactory.createLineBorder(Color.RED, 2);

        // Establecer el borde de los paneles
        inputPanel.setBorder(borde);
        buttonsPanel.setBorder(borde);
        tablePanel.setBorder(borde);
        textArea.setBorder(borde);
        clockOutput.setBorder(borde);
        controlWords.setBorder(borde);

        // Agregar componentes a los paneles
//        inputPanel.add(new JButton("Botón 1"));
//        buttonsPanel.add(new JLabel("Etiqueta 1"));
        // Agregar los paneles al JFrame
        getContentPane().add(inputPanel);
        getContentPane().add(buttonsPanel);
        getContentPane().add(tablePanel);
        getContentPane().add(controlWordsAndClock);
        getContentPane().add(textArea);
        controlWordsAndClock.add(clockOutput);
        controlWordsAndClock.add(controlWords);

        // Establecer un administrador de diseño (layout) para los paneles
        getContentPane().setLayout(new FlowLayout());

        // Centrar el JFrame en la pantalla
        setLocationRelativeTo(null);
        pack();

        // Mostrar el JFrame
        setVisible(true);
    }

}
