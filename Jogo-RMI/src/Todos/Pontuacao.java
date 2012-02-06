package Todos;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Pontuacao extends JPanel{
	//Texto "Pontuação"
	JLabel pont;

	//Pontuação de cada um dos Jogadores 
	public ArrayList <Integer> pontosJogador;
	//Nome de cada um dos Jogadores
	public ArrayList<String> nomeJogador;

	public Pontuacao(ArrayList<String> nomeJogador, ArrayList <Integer> pontosJogador){

		super();

		this.pontosJogador=pontosJogador;
		this.nomeJogador=nomeJogador;

		this.setLayout(null);


		pont = new JLabel("Pontuação: ");
		pont.setBounds(0,0,70,30);
		this.add(pont);

	}
	public void paintComponent(Graphics g){
		System.out.println("Atualizou a PONTUAÇÃO do jogador");

		//Apaga tudo que tinha escrito nesse JPanel
		g.setColor(Color.gray);//a cor do proximo objeto
		g.fillRect(0,0,this.getWidth(),this.getHeight());//cria o retangulo

		//Remove as peças que estavam no Panel Anterior
		this.removeAll();

		pont = new JLabel("Pontuação: ");
		pont.setBounds(0,0,70,30);
		this.add(pont);

		//Adiciona a pontuação de cada um dos Jogadores
		for (int i = 0; i < nomeJogador.size(); i++) {
			JLabel pontJogador = new JLabel(nomeJogador.get(i)+": "+pontosJogador.get(i));
			pontJogador.setBounds(80+120*i,0,100,30);
			this.add(pontJogador);
		}
	}
}
