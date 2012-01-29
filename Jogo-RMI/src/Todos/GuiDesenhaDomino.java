package Todos;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GuiDesenhaDomino extends JPanel{


	int valorArray;

	//peças a desenhar
	public ArrayList <PecaDomino> pecasPitadas;
	public ArrayList<GuiDesenhaUmDomino> desenhosDomino;

	public GuiDesenhaDomino(ArrayList <PecaDomino> pecas) {
		pecasPitadas=pecas;
		desenhosDomino= new ArrayList<GuiDesenhaUmDomino>();
	}
	public void paintComponent(Graphics g){

		Dimension sz = getSize();

		g.setColor(Color.gray);//a cor do proximo objeto
		g.fillRect(0,0,this.getWidth(),this.getHeight());//cria o retangulo

		int distancia=0;
		//removendo as peças que estavam no Panel Anterior
		this.removeAll();
		for(int i=0; i<pecasPitadas.size(); i++){

			GuiDesenhaUmDomino umaPeca =new GuiDesenhaUmDomino(pecasPitadas.get(i),10,30);			

			umaPeca.setBounds(10+distancia, 10, 80, 120);

			//guardando todas as peças pintadas em uma lista nessa classe
			this.desenhosDomino.add(umaPeca);

			this.add(umaPeca);

			//	ta crescendo de 90 em 90
			distancia=distancia+90;
		}		
	}

}