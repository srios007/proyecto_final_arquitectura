

import presentation.Model;

public class Launcher {

    private Model miApp;

    public Launcher() {
        miApp = new Model();
        miApp.init();
    }
    
    public static void main(String[] args) {      
        new Launcher();
        //
    }
}
