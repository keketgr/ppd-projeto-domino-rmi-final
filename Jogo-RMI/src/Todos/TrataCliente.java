package Todos;

import java.io.Serializable;
import java.util.ArrayList;



public class TrataCliente extends Thread implements Serializable{
	//Indica que esse jogador passou a sua vez
	Boolean passouVez=false;

	//Somatório das peças após jogo trancado
	int somaDasPecasAposJogoTrancado=0;

	//Sinaliza que esse jogador atualizou a variável com valor das soma das suas peças 
	Boolean atualizouTrancado=false;

	//Nome do Jogador
	String nomeUsuario;

	//Variável que sinaliza se o cliente está pronto para jogar
	Boolean prontoParaJogar=false;

	//Peças do jogador
	public ArrayList <PecaDomino> pecasJogador;

	//Referência do Servidor
	ServidorThread servidor;

	//Sinaliza que o jogador quer sair do Jogo
	Boolean sairJogo=false;

	public TrataCliente(String nome ,ServidorThread referenciaDoServidor){
		//Referencia do servidor principal
		this.servidor=referenciaDoServidor;
		//Nome do Jogador
		this.nomeUsuario=nome;

		//Lista com as peças deste Jogador
		pecasJogador = new ArrayList <PecaDomino> ();

		//Inicia a Thread
		this.start();
	}

	public void run(){

		//Condição do fechamento da conexão
		while(sairJogo==false){////!dadosRecebidos.equals("close")){
			//Não Faz Nada
		}
	}


	//Remove o jogador da lista de jogadores, após ele apertar em sair
	public void removerDaListaDeUsuario(){
		//Remove ele da lista 
		servidor.listaDeUsuarios.remove(this);

		//Finaliza o método RUN da Thread
		sairJogo=true;
	}

	//Retorna o maior carroção desse Jogador, caso não tenha o maior somatório
	public PecaDomino maiorPecaDoJogador(){
		PecaDomino maior=null;

		//Procura a maior peça igual, ou seja, carroção
		for (int i = 0; i < pecasJogador.size(); i++) {
			//Quando os dois lados são iguais
			if(pecasJogador.get(i).getLadoDireito()==pecasJogador.get(i).getLadoEsquerdo()){
				//Primeira peça com dois lados iguais
				if(maior==null){
					maior=pecasJogador.get(i);
				}
				else{//Já existe um carroção no maior e vai comparar o maior
					if(pecasJogador.get(i).getLadoDireito()>maior.getLadoDireito()){
						maior=pecasJogador.get(i);
					}
				}
			}
		}

		//Caso não exista peças iguais, ou seja, carroção		
		if(maior==null){
			//Inicia primeira peça	
			int indiceMa=0;
			maior=pecasJogador.get(0);
			for (int i = 1; i < pecasJogador.size(); i++) {
				//Obtem o maior somatório dos dois lados
				if(somaDoisLados(i)>somaDoisLados(indiceMa)){
					maior=pecasJogador.get(i);
					indiceMa=i;
				}
			}
		}

		return maior;

	}

	//Soma os valores dos lados de uma peça
	public int somaDoisLados(int indice){
		System.out.println("soma das duas peças:"+(pecasJogador.get(indice).getLadoDireito()+pecasJogador.get(indice).getLadoDireito()));
		return pecasJogador.get(indice).getLadoDireito()+pecasJogador.get(indice).getLadoDireito();
	}

}
