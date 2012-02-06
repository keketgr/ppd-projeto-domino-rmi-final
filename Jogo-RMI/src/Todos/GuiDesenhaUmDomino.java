package Todos;



import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

class GuiDesenhaUmDomino extends JPanel{
	int valorArray;

	//Peça que foi desenhada
	private PecaDomino pecaPitada;

	public GuiDesenhaUmDomino(PecaDomino peca, int x, int y) {
		pecaPitada=peca;
	}

	public void paintComponent(Graphics g){

		//Seleciona a cor que está na peça de dominó
		g.setColor(pecaPitada.corPeca);//a cor do proximo objeto

		//Desenha o retângulo da peça
		g.fillRect(0, 0, 100, 120);

		//Desenha a divisão dos lados do dominó
		g.setColor(Color.white);
		g.drawLine(0, 60, 100, 60);

		//Desenha o número
		//Parte de cima do domino com número 1
		if(pecaPitada.getLadoDireito()==1){
			g.setColor(Color.white);
			g.fillOval(35, 30, 10, 10);}
		//Parte de cima do domino com número 2
		if(pecaPitada.getLadoDireito()==2){
			g.setColor(Color.white);
			g.fillOval(20+5, 30, 10, 10);
			g.fillOval(40+5, 30, 10, 10);
		}
		//Parte de cima do domino com número 3
		if(pecaPitada.getLadoDireito()==3){
			g.setColor(Color.white);
			g.fillOval(15, 30, 10, 10);
			g.fillOval(35, 30, 10, 10);
			g.fillOval(55, 30, 10, 10);
		}
		//Parte de cima do domino com número 4
		if(pecaPitada.getLadoDireito()==4){
			g.setColor(Color.white);
			g.fillOval(15+5, 30-15, 10, 10);
			g.fillOval(45+5, 30-15, 10, 10);
			g.fillOval(15+5, 30+5, 10, 10);
			g.fillOval(45+5, 30+5, 10, 10);
		}
		//Parte de cima do domino com número 5
		if(pecaPitada.getLadoDireito()==5){
			g.setColor(Color.white);
			g.fillOval(15+5, 20-10, 10, 10);
			g.fillOval(45+5, 20-10, 10, 10);
			g.fillOval(15+5, 20+20, 10, 10);
			g.fillOval(45+5, 20+20, 10, 10);
			g.fillOval(30+5, 20+5, 10, 10);
		}
		//Parte de cima do domino com número 6
		if(pecaPitada.getLadoDireito()==6){
			g.setColor(Color.white);
			g.fillOval(15, 20-10, 10, 10);
			g.fillOval(35, 20-10, 10, 10);
			g.fillOval(55, 20-10, 10, 10);
			g.fillOval(15, 20+10, 10, 10);
			g.fillOval(35, 20+10, 10, 10);
			g.fillOval(55, 20+10, 10, 10);
		}


		//Parte de baixo do domino com número 1
		if(pecaPitada.getLadoEsquerdo()==1){
			g.setColor(Color.white);
			g.fillOval(40, 80, 10, 10);}
		//Parte de baixo do domino com número 2
		if(pecaPitada.getLadoEsquerdo()==2){
			g.setColor(Color.white);
			g.fillOval(20+5, 80, 10, 10);
			g.fillOval(40+5, 80, 10, 10);
		}
		//Parte de baixo do domino com número 3
		if(pecaPitada.getLadoEsquerdo()==3){
			g.setColor(Color.white);
			g.fillOval(15, 80, 10, 10);
			g.fillOval(35, 80, 10, 10);
			g.fillOval(55, 80, 10, 10);
		}
		//Parte de baixo do domino com número 4
		if(pecaPitada.getLadoEsquerdo()==4){
			g.setColor(Color.white);
			g.fillOval(15+5, 80-15, 10, 10);
			g.fillOval(45+5, 80-15, 10, 10);
			g.fillOval(15+5, 80+5, 10, 10);
			g.fillOval(45+5, 80+5, 10, 10);
		}
		//Parte de baixo do domino com número 5
		if(pecaPitada.getLadoEsquerdo()==5){
			g.setColor(Color.white);
			g.fillOval(15+5, 80-10, 10, 10);
			g.fillOval(45+5, 80-10, 10, 10);
			g.fillOval(15+5, 80+20, 10, 10);
			g.fillOval(45+5, 80+20, 10, 10);
			g.fillOval(30+5, 80+5, 10, 10);
		}
		//Parte de baixo do domino com número 6
		if(pecaPitada.getLadoEsquerdo()==6){
			g.setColor(Color.white);
			g.fillOval(15, 80-10, 10, 10);
			g.fillOval(35, 80-10, 10, 10);
			g.fillOval(55, 80-10, 10, 10);
			g.fillOval(15, 80+10, 10, 10);
			g.fillOval(35, 80+10, 10, 10);
			g.fillOval(55, 80+10, 10, 10);
		}

	}

}