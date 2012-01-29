package Todos;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class GuiDesenhaUmDominoDaMesa extends JPanel{
	int valorArray;

	//peças a desenhar
	private PecaDomino pecaPitada;

	public GuiDesenhaUmDominoDaMesa(PecaDomino peca, int x, int y) {		

		pecaPitada=peca;
	}
	public void paintComponent(Graphics g){
		g.setColor(Color.black);//a cor do proximo objeto

		//não precisa dá distância pois o Desenha Domino Já faz
		g.fillRect(0, 0, 120,80);

		//desenhar a divisão dos lados do dominó
		g.setColor(Color.gray);
		g.drawLine( 60, 0, 60,100);

		//desenhar o número
		//parte de cima do domino com número 1
		if(pecaPitada.getLadoDireito()==1){
			g.setColor(Color.gray);
			g.fillOval(90, 40, 10, 10);}
		//parte de cima do domino com número 2
		if(pecaPitada.getLadoDireito()==2){
			g.setColor(Color.gray);
			g.fillOval(90+5, 30, 10, 10);
			g.fillOval(90+5, 50, 10, 10);
		}
		//parte de cima do domino com número 3
		if(pecaPitada.getLadoDireito()==3){
			g.setColor(Color.gray);
			g.fillOval(90, 20, 10, 10);
			g.fillOval(90, 35, 10, 10);
			g.fillOval(90, 50, 10, 10);
		}
		//parte de cima do domino com número 4
		if(pecaPitada.getLadoDireito()==4){
			g.setColor(Color.gray);
			g.fillOval(90-15, 15+5, 10, 10);
			g.fillOval(90-15, 45+5, 10, 10);
			g.fillOval(90+5, 15+5, 10, 10);
			g.fillOval(90+5, 45+5, 10, 10);
		}
		//parte de cima do domino com número 5
		if(pecaPitada.getLadoDireito()==5){
			g.setColor(Color.gray);
			g.fillOval(90-15, 15+5, 10, 10);
			g.fillOval(90-15, 45+5, 10, 10);
			g.fillOval(90+15, 15+5, 10, 10);
			g.fillOval(90+15, 45+5, 10, 10);
			g.fillOval(90, 30+5, 10, 10);
		}
		//parte de cima do domino com número 6
		if(pecaPitada.getLadoDireito()==6){
			g.setColor(Color.gray);
			g.fillOval(90-10, 15, 10, 10);
			g.fillOval(90-10,35, 10, 10);
			g.fillOval(90-10,55, 10, 10);
			g.fillOval(90+10,15, 10, 10);
			g.fillOval(90+10,35, 10, 10);
			g.fillOval(90+10,55, 10, 10);
		}


		//parte de baixo do domino com número 1
		if(pecaPitada.getLadoEsquerdo()==1){
			g.setColor(Color.gray);
			g.fillOval(30, 40, 10, 10);}
		//parte de baixo do domino com número 2
		if(pecaPitada.getLadoEsquerdo()==2){
			g.setColor(Color.gray);
			g.fillOval(30+5, 30, 10, 10);
			g.fillOval(30+5, 50, 10, 10);
		}
		//parte de baixo do domino com número 3
		if(pecaPitada.getLadoEsquerdo()==3){
			g.setColor(Color.gray);
			g.fillOval(30, 20, 10, 10);
			g.fillOval(30, 35, 10, 10);
			g.fillOval(30, 50, 10, 10);
		}
		//parte de baixo do domino com número 4
		if(pecaPitada.getLadoEsquerdo()==4){
			g.setColor(Color.gray);
			g.fillOval(30-15, 15+5, 10, 10);
			g.fillOval(30-15, 45+5, 10, 10);
			g.fillOval(30+5, 15+5, 10, 10);
			g.fillOval(30+5, 45+5, 10, 10);
		}
		//parte de baixo do domino com número 5
		if(pecaPitada.getLadoEsquerdo()==5){
			g.setColor(Color.gray);
			g.fillOval(30-15, 15+5, 10, 10);
			g.fillOval(30-15, 45+5, 10, 10);
			g.fillOval(30+15, 15+5, 10, 10);
			g.fillOval(30+15, 45+5, 10, 10);
			g.fillOval(30, 30+5, 10, 10);
		}
		//parte de baixo do domino com número 6
		if(pecaPitada.getLadoEsquerdo()==6){
			g.setColor(Color.gray);
			g.fillOval(30-10, 15, 10, 10);
			g.fillOval(30-10,35, 10, 10);
			g.fillOval(30-10,55, 10, 10);
			g.fillOval(30+10,15, 10, 10);
			g.fillOval(30+10,35, 10, 10);
			g.fillOval(30+10,55, 10, 10);

		}


	}
	

}
