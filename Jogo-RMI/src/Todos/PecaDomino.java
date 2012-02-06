package Todos;

import java.awt.Color;
import java.io.Serializable;

public class PecaDomino implements Serializable {
	//Cor que deve pintar a peça	
	public Color corPeca;

	//Valores dos lados desta peça
	private int ladoDireito;
	private int ladoEsquerdo;

	public PecaDomino(int direita, int esquerda){
		corPeca=Color.black;
		this.ladoDireito=direita;
		this.ladoEsquerdo=esquerda;
	}

	public int getLadoDireito() {
		return ladoDireito;
	}

	public int getLadoEsquerdo() {
		return ladoEsquerdo;
	}
	public void setLadoDireito(int ladoDireito) {
		this.ladoDireito = ladoDireito;
	}

	public void setLadoEsquerdo(int ladoEsquerdo) {
		this.ladoEsquerdo = ladoEsquerdo;
	}
}
