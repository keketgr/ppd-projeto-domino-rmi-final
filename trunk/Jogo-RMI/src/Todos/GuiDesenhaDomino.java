package Todos;


import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GuiDesenhaDomino extends JPanel{


	int valorArray;

	//Valor das peças a desenhar
	public ArrayList <PecaDomino> pecasPitadas;
	//Painel de cada uma das peças
	public ArrayList<GuiDesenhaUmDomino> desenhosDomino;

	public GuiDesenhaDomino(ArrayList <PecaDomino> pecas) {
		pecasPitadas=pecas;
		desenhosDomino= new ArrayList<GuiDesenhaUmDomino>();
	}

	public void paintComponent(Graphics g){
		System.out.println("Atualizou a tela do jogador");

		//Apaga o que tinha antes
		g.setColor(Color.gray);//a cor do proximo objeto
		g.fillRect(0,0,this.getWidth(),this.getHeight());//cria o retangulo

		int distancia=0;
		//Remove as peças que estavam no Panel Anterior
		this.removeAll();

		//Adiciona cada um dos painéis das peças
		for(int i=0; i<pecasPitadas.size(); i++){

			//Cria um painel com o desenho da Peça
			GuiDesenhaUmDomino umaPeca =new GuiDesenhaUmDomino(pecasPitadas.get(i),10,30);			

			//Local onde será adicionado o painel com o valor das peças
			umaPeca.setBounds(10+distancia, 10, 80, 120);

			//Guarda todas as peças que foram pintadas em uma lista
			this.desenhosDomino.add(umaPeca);

			//Adiciona a peça a esse JPanel que é o central
			this.add(umaPeca);

			//	ta crescendo de 90 em 90
			distancia=distancia+90;
		}		
	}

}