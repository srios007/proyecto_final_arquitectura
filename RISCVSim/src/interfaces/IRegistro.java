package interfaces;

public interface IRegistro {

    // Reemplaza el valor actual almacenado en el registro con newVal	 
    public void setValor(int newVal);

    // Devuelve el valor almacenado actualmente en el registro, como un byte
    public int getValor();

    // Borra el contenido del registro.
    public void clear();
}
