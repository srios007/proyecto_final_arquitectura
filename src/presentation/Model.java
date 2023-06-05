package presentation;

import java.awt.Dimension;
import javax.swing.JFrame;

public class Model {

    private JFrame principalWindow;
    private boolean runApplication;

    public Model() {}

    public void init() {
        getPrincipalWindow().pack();
        getPrincipalWindow().setVisible(true);
    }

    // genera la ventana principal, que contendrá a los diferentes paneles que 
    // mostrarán el resultado de la ejecución del simulador
    public JFrame getPrincipalWindow() {
        if (principalWindow == null) {
            principalWindow = new JFrame();
            principalWindow.setTitle("Proyect final arquitectura");
            principalWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            principalWindow.setPreferredSize(new Dimension(1200, 768));
            principalWindow.setResizable(true);
        }
        return principalWindow;
    }

    public boolean isApplicationRunning() {
        return runApplication;
    }

    public void setIsRunningApplication(boolean auto) {
        this.runApplication = auto;
    }

}
