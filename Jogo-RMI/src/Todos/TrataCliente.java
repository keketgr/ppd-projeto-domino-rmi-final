package Todos;

import java.io.Serializable;
import java.util.ArrayList;



public class TrataCliente extends Thread implements Serializable{
	//Indica que esse jogador passou a sua vez
	Boolean passouVez=false;

	//Somat�rio das pe�as ap�s jogo trancado
	int somaDasPecasAposJogoTrancado=0;

	//Sinaliza que esse jogador atualizou a vari�vel com valor das soma das suas pe�as 
	Boolean atualizouTrancado=false;

	//Nome do Jogador
	String nomeUsuario;

	//Vari�vel que sinaliza se o cliente est� pronto para jogar
	Boolean prontoParaJogar=false;

	//Pe�as do jogador
	public ArrayList <PecaDomino> pecasJogador;

	//Refer�ncia do Servidor
	ServidorThread servidor;

	//Sinaliza que o jogador quer sair do Jogo
	Boolean sairJogo=false;

	public TrataCliente(String nome ,ServidorThread referenciaDoServidor){
		//Referencia do servidor principal
		this.servidor=referenciaDoServidor;
		//Nome do Jogador
		this.nomeUsuario=nome;

		//Lista com as pe�as deste Jogador
		pecasJogador = new ArrayList <PecaDomino> ();

		//Inicia a Thread
		this.start();
	}

	public void run(){

		//Condi��o do fechamento da conex�o
		while(sairJogo==false){////!dadosRecebidos.equals("close")){
			//N�o Faz Nada
		}
	}


	//Remove o jogador da lista de jogadores, ap�s ele apertar em sair
	public void removerDaListaDeUsuario(){
		//Remove ele da lista 
		servidor.listaDeUsuarios.remove(this);

		//Finaliza o m�todo RUN da Thread
		sairJogo=true;
	}

	//Retorna o maior carro��o desse Jogador, caso n�o tenha o maior somat�rio
	public PecaDomino maiorPecaDoJogador(){
		PecaDomino maior=null;

		//Procura a maior pe�a igual, ou seja, carro��o
		for (int i = 0; i < pecasJogador.size(); i++) {
			//Quando os dois lados s�o iguais
			if(pecasJogador.get(i).getLadoDireito()==pecasJogador.get(i).getLadoEsquerdo()){
				//Primeira pe�a com dois lados iguais
				if(maior==null){
					maior=pecasJogador.get(i);
				}
				else{//J� existe um carro��o no maior e vai comparar o maior
					if(pecasJogador.get(i).getLadoDireito()>maior.getLadoDireito()){
						maior=pecasJogador.get(i);
					}
				}
			}
		}

		//Caso n�o exista pe�as iguais, ou seja, carro��o		
		if(maior==null){
			//Inicia primeira pe�a	
			int indiceMa=0;
			maior=pecasJogador.get(0);
			for (int i = 1; i < pecasJogador.size(); i++) {
				//Obtem o maior somat�rio dos dois lados
				if(somaDoisLados(i)>somaDoisLados(indiceMa)){
					maior=pecasJogador.get(i);
					indiceMa=i;
				}
			}
		}

		return maior;

	}

	//Soma os valores dos lados de uma pe�a
	public int somaDoisLados(int indice){
		System.out.println("soma das duas pe�as:"+(pecasJogador.get(indice).getLadoDireito()+pecasJogador.get(indice).getLadoDireito()));
		return pecasJogador.get(indice).getLadoDireito()+pecasJogador.get(indice).getLadoDireito();
	}

}
