package Todos;

import java.util.ArrayList;


public class Domino {
	//Esse ArrayList contém todas as peças não utilizadas no jogo
	ArrayList <PecaDomino> todasPecas=new ArrayList <PecaDomino>();

	public  Domino() {
		//criando todas as peças do jogo
		todasPecas.add(new PecaDomino(0, 0));	//01
		todasPecas.add(new PecaDomino(0, 1));	//02
		todasPecas.add(new PecaDomino(0, 2));	//03
		todasPecas.add(new PecaDomino(0, 3));	//04
		todasPecas.add(new PecaDomino(0, 4));	//05
		todasPecas.add(new PecaDomino(0, 5));	//06
		todasPecas.add(new PecaDomino(0, 6));	//07
		todasPecas.add(new PecaDomino(1, 1));	//08
		todasPecas.add(new PecaDomino(1, 2));	//09
		todasPecas.add(new PecaDomino(1, 3));	//10
		todasPecas.add(new PecaDomino(1, 4));	//11
		todasPecas.add(new PecaDomino(1, 5));	//12
		todasPecas.add(new PecaDomino(1, 6));	//13
		todasPecas.add(new PecaDomino(2, 2));	//14
		todasPecas.add(new PecaDomino(2, 3));	//15
		todasPecas.add(new PecaDomino(2, 4));	//16
		todasPecas.add(new PecaDomino(2, 5));	//17
		todasPecas.add(new PecaDomino(2, 6));	//18
		todasPecas.add(new PecaDomino(3, 3));	//19
		todasPecas.add(new PecaDomino(3, 4));	//20
		todasPecas.add(new PecaDomino(3, 5));	//21
		todasPecas.add(new PecaDomino(3, 6));	//22
		todasPecas.add(new PecaDomino(4, 4));	//23
		todasPecas.add(new PecaDomino(4, 5));	//24
		todasPecas.add(new PecaDomino(4, 6));	//25
		todasPecas.add(new PecaDomino(5, 5));	//26
		todasPecas.add(new PecaDomino(5, 6));	//27
		todasPecas.add(new PecaDomino(6, 6));	//28

	}

	//sorteio de peças para 1 jogador
	public ArrayList pecasDeUmJogador(){
		//peças  deste jogador
		ArrayList <PecaDomino> pecaDoJogador=new ArrayList <PecaDomino>();
		int indice;

		//dar 7 peças a um jogador
		for(int i=0;i<3;i++){
			//sorteio de uma peça do jogador, ou seja, o indice dela na lista
			indice=(int) (Math.random() * todasPecas.size());
			//agrupando as peças do jogador
			pecaDoJogador.add(todasPecas.remove(indice));
		}

		return pecaDoJogador;
	}

	//puxar uma peça das que sobraram

	public PecaDomino puxarUmaPeca(){

		double numeroDePecas =todasPecas.size();

		if(numeroDePecas>0){
			//sorteando a peça a ser puxada pelo jogador
			int indice=(int) (Math.random() * todasPecas.size()-1);

			return todasPecas.remove(indice);
		}
		else{
			return null;
		}
	}

}
