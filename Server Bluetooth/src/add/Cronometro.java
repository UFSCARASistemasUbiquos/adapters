/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package add;

/**
 *
 * @author virtual
 */
public class Cronometro {

    private long inicio = 0;

    // Construtor - também ativa o cronometro.  

    public Cronometro() {
        inicio = System.currentTimeMillis();
    }
        // retorna tempo em segundos   
    // não interrompe o cronometro, pode ser chamado várias vezes  

    public long getAtual() {
        long mili = System.currentTimeMillis() - inicio;
        //return Math.round(mili / 1000.0);  
        return mili;
    }
}
