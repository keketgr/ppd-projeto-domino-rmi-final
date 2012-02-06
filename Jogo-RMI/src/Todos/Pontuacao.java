package Todos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Pontuacao extends JPanel{
	JLabel pont;

	public ArrayList <Integer> pontosJogador;
	public ArrayList<String> nomeJogador;

	public Pontuacao(ArrayList<String> nomeJogador, ArrayList <Integer> pontosJogador){

		super();

		this.pontosJogador=pontosJogador;
		this.nomeJogador=nomeJogador;

		this.setLayout(null);


		pont = new JLabel("Pontuação: ");
		pont.setBounds(0,0,70,30);
		this.add(pont);

		//		JLabel jog1 = new JLabel("Jogador1: ");
		//		jog1.setBounds(80,0,100,30);
		//		this.add(jog1);
		//
		//		JLabel jog2 = new JLabel("Jogador2: ");
		//		jog2.setBounds(200,0,100,30);
		//		this.add(jog2);
		//
		//		JLabel jog3 = new JLabel("Jogador3: ");
		//		jog3.setBounds(320,0,100,30);
		//		pontucaoJogadoresDoJogo.add(jog3);
		//
		//		JLabel jog4 = new JLabel("Jogador4: ");
		//		jog4.setBounds(440,0,100,30);
		//		pontucaoJogadoresDoJogo.add(jog4);

//		for (int i = 0; i < nomeJogador.size(); i++) {
//			JLabel pontJogador = new JLabel(nomeJogador.get(i)+": "+pontosJogador.get(i));
//			pontJogador.setBounds(80+120*i,0,100,30);
//			this.add(pontJogador);
//		}
	}
	public void paintComponent(Graphics g){
		System.out.println("Atualizou a PONTUAÇÃO do jogador");

		g.setColor(Color.gray);//a cor do proximo objeto
		g.fillRect(0,0,this.getWidth(),this.getHeight());//cria o retangulo

		//removendo as peças que estavam no Panel Anterior
		this.removeAll();
		
		pont = new JLabel("Pontuação: ");
		pont.setBounds(0,0,70,30);
		this.add(pont);
		
		for (int i = 0; i < nomeJogador.size(); i++) {
			JLabel pontJogador = new JLabel(nomeJogador.get(i)+": "+pontosJogador.get(i));
			pontJogador.setBounds(80+120*i,0,100,30);
			this.add(pontJogador);
		}
	}
}
