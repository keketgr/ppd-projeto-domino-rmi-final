package Todos;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;



//Essa classe ir� gerenciar todo o jogo
public class JogoDomino extends Thread implements Serializable{
	//Isso � para o jogo esperar o jogador jogar a pe�a
	Boolean jogadorAtualJogou=false;

	//Array com boolean que indica se os jogadores passaram a suas vezes
	Boolean [] jogadoresQuePassaramSuaVez;

	//Sinaliza a primeira jogada, ou seja inicio do jogo
	Boolean jogada1=true;

	//Pe�as j� utilizadas no jogo
	ArrayList <PecaDomino> pecasUtilizadas;

	//Domino usado na Rodada
	Domino domino;

	//Refer�ncia do Servidor que o criou
	ServidorThread referenciaDoServidor;

	public JogoDomino(ServidorThread referenciaDoServidor){		
		System.out.println("JogoDomino: Iniciou o JOGO Domino");
		domino = new Domino();		
		pecasUtilizadas= new ArrayList<PecaDomino>();
		this.referenciaDoServidor=referenciaDoServidor;
	}

	public void run(){
		//Indica que todos jogadores est�o prontos para jogar
		Boolean todosProntos=false;

		//Reinicia Jogo
		while(true){

			//Fica aqui at� o jogo terminar, ou seja, o n�mero de pe�a do jogador acabar
			while(referenciaDoServidor.fimDoJogo==false){

				//Verifica se todos os jogadores enviaram a confirma��o de pronto ao servidor
				if(referenciaDoServidor.jogoIniciado==false){
					//Verifica se todos os jogadores est�o prontos para jogar
					if(referenciaDoServidor.listaDeUsuarios.size()>1){
						todosProntos=true;
						//Verifica se todos os jogadores est�o prontos
						for(int i=0; i<referenciaDoServidor.listaDeUsuarios.size();i++){
							//Acha os jogadores que n�o est�o prontos
							if(referenciaDoServidor.listaDeUsuarios.get(i)!=null && referenciaDoServidor.listaDeUsuarios.get(i).prontoParaJogar==false){

								todosProntos=false;
							}
						}
					}

					//Quando todos os jogadores est�o prontos d� as pe�as aos jogadores
					if(todosProntos==true){
						//Indica quais os jogadores passaram suas vezes e saber se o jogo est� trancado
						jogadoresQuePassaramSuaVez=new Boolean[referenciaDoServidor.listaDeUsuarios.size()];

						System.out.println("JogoDomino: colocou as pe�as na m�o do jogador");

						//Todos os jogadores pegam seus domin�s
						for(int j=0; j<referenciaDoServidor.listaDeUsuarios.size();j++){
							System.out.println("JogoDomino: �ndice da lista de usu�rios: "+j);
							System.out.println("JogoDomino: colocou as pe�as na m�o do jogador"+referenciaDoServidor.listaDeNomeUsuarios.get(j));
							System.out.println("JogoDomino: Tamanho da lista de usu�rios:"+referenciaDoServidor.listaDeUsuarios.size());

							String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(j);

							InterfaceDoCliente cli;
							try {
								//Procrura o cliente
								cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);
								//Sorteia as pe�as para 1 dos Jogadores
								ArrayList<PecaDomino> pec=domino.pecasDeUmJogador();
								//Envia as 7 pe�as sorteadas a 1 dos Jogadores
								cli.recebe7Pecas(pec);
								//Atualiza no Servidor as pe�as que est�o com o jogador
								referenciaDoServidor.listaDeUsuarios.get(j).pecasJogador=pec;
								//Avisa no TextArea que esse Jogador recebeu suas pe�as
								cli.recebeMensagemDoChat("Recebi minhas pe�as.");
							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (NotBoundException e) {
								e.printStackTrace();
							}

							System.out.println("JogoDomino: Enviou as pe�as desse jogador");
						}

						System.out.println("JogoDomino: As pe�as foram dadas a todos os jogadores");
						System.out.println("JogoDomino: Fim envio das pe�as!");

						//Para n�o dar as 7 pe�as novamente aos Jogadores
						todosProntos=false;
						//Sinaliza no Servidor que o jogo foi iniciado
						referenciaDoServidor.jogoIniciado=true;
					}
				}			
				//Jogo iniciado
				else{//jogo iniciado

					//Somente na primeira jogada, ou seja, o jogador poder� escolher qualque pe�a para iniciar				
					if (jogada1==true) {

						System.out.println("\n**Primeira Jogada**\n");

						//Ordena primeiro jogador, ou seja, ve quem tem maior carro��o					
						primeiroJogador();

						//Pega a refer�ncia do primeiro jogador da lista
						TrataCliente jogadorAtual=referenciaDoServidor.listaDeUsuarios.get(0);

						//Pega o nome do primeiro jogador da lista
						String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(0);

						//como ele ir� jogar, coloca ele no final da lista
						try {
							//Cria a conex�o com o primeiro Jogador
							InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);
							//Avisa a todos quem � o primeiro a jogar, ou seja, a vez de quem
							referenciaDoServidor.enviaATodosClientes("Vez do jogador: "+jogadorAtual.nomeUsuario);
							//Recebe a pe�a escolhida pelo Jogador
							PecaDomino p1=cli.jogaPrimeiraPeca();
							//Verifica qual lado foi escolhido pelo Jogador
							if (referenciaDoServidor.lado.equals("Esquerdo")) {
								//Adiciona a pe�a no in�cio da lista, ou seja, no lado esquerdo da lista
								referenciaDoServidor.jogo.pecasUtilizadas.add(0,p1);
							} else {//lado direito
								//Adiciona a pe�a no fim da lista, ou seja, no lado direito da lista
								referenciaDoServidor.jogo.pecasUtilizadas.add(p1);}

							//Atualiza a GUI desse jogador
							cli.atualizaGUI(referenciaDoServidor.jogo.pecasUtilizadas,referenciaDoServidor.listaDeNomeUsuarios,referenciaDoServidor.listaDePontuacaoUsuarios);

						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (NotBoundException e) {
							e.printStackTrace();
						}

						System.out.println("JogoDomino: Nome do jogador que ir� iniciar- "+jogadorAtual.nomeUsuario);

						//Sinaliza que j� foi jogada a primeira jogada
						jogada1=false;

						System.out.println("JogoDomino: "+jogadorAtual.nomeUsuario+" enviou a 1 pe�a!");

						//Deixa pronto para a pr�xima rodada, que o Jogador n�o jogou 
						jogadorAtualJogou=false;

					} else {//Outras jogadas

						System.out.println("\n**Proxima Jogada**\n");

						//Pega a refer�ncia do primeiro jogador da lista
						TrataCliente jogadorAtual=referenciaDoServidor.listaDeUsuarios.get(0);

						//Pega o nome do primeiro jogador da lista
						String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(0);

						try {
							//Atualiza as pe�a que est�o na mesa de todos Jogadores
							for (int i = 0; i < referenciaDoServidor.listaDeNomeUsuarios.size(); i++) {
								InterfaceDoCliente cli2 = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
								cli2.atualizaGUI(referenciaDoServidor.jogo.pecasUtilizadas,referenciaDoServidor.listaDeNomeUsuarios,referenciaDoServidor.listaDePontuacaoUsuarios);
							}

							//Faz a conex�o com o primeiro Jogador da Lista
							InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);

							//Avisa a todos de quem � a vez
							referenciaDoServidor.enviaATodosClientes("Vez do jogador: "+jogadorAtual.nomeUsuario);

							//Recebe a pe�a jogada pelo Jogador
							PecaDomino p1=cli.jogaProximaPeca(buscarPontasDasPecas());

							System.out.println(jogadorAtual.nomeUsuario+" jogou sua pe�a.");

							//Fica puxando at� acabar pe�as ou pegar uma poss�vel
							while(p1==null&&domino.todasPecas.size()>0){
								//Passa pro Jogador a pe�a comprada
								cli.puxaUmaPeca(domino.puxarUmaPeca());
								//Avisa a todos que esse Jogador comprou uma pe�a
								referenciaDoServidor.enviaATodosClientes(jogadorAtual.nomeUsuario+" comprou uma pe�a.");
								//Recebe a pe�a jogada pelo Jogador
								p1=cli.jogaProximaPeca(buscarPontasDasPecas());
							}

							System.out.println("Acabou de puxar ou n�o.");

							//N�o tendo pe�a pra puxar e n�o tiver recebido uma pe�a
							if(p1==null&&domino.todasPecas.size()==0){
								System.out.println(jogadorAtual.nomeUsuario+" passou sua vez");
								//Sinaliza que o Jogador passou sua vez
								jogadorAtual.passouVez=true;
								//Sinaliza que o jogador fez sua jogada
								jogadorAtualJogou=true;
							}else{//Se tiver pe�a

								System.out.println("Puxou pe�a ou n�o e ira adicina-la");
								//Verifica qual lado foi escolhido pelo Jogador
								if (referenciaDoServidor.lado.equals("Esquerdo")) {
									//Adiciona a pe�a no in�cio da lista, ou seja, no lado esquerdo da lista
									referenciaDoServidor.jogo.pecasUtilizadas.add(0,p1);
								} else {//lado direito
									//Adiciona a pe�a no fim da lista, ou seja, no lado direito da lista
									referenciaDoServidor.jogo.pecasUtilizadas.add(p1); 
								}

								System.out.println("Adicionou pe�a na mesa");

								//Atualiza as pe�a que est�o na mesa de todos Jogadores
								for (int i = 0; i < referenciaDoServidor.listaDeNomeUsuarios.size(); i++) {
									InterfaceDoCliente cli2 = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
									cli2.atualizaGUI(referenciaDoServidor.jogo.pecasUtilizadas,referenciaDoServidor.listaDeNomeUsuarios,referenciaDoServidor.listaDePontuacaoUsuarios);
								}

								//Sinaliza que o Jogador atual j� jogou uma pe�a
								jogadorAtualJogou=true;
							}

						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (NotBoundException e) {
							e.printStackTrace();
						}

						//Mostra o ArrayList das pe�as que est�o na mesa 
						mostrarPecasUtilizadas();

						System.out.println("JogoDomino: Nome do jogador que ir� iniciar- "+jogadorAtual.nomeUsuario);

						while(jogadorAtualJogou==false){
							//Espera at� o jogador faz sua jogada
						}

						System.out.println("JogoDomino: O jogador "+jogadorAtual.nomeUsuario+" enviou a 1 pe�a!");

						//Deixa pronto para a pr�xima rodada, que o Jogador n�o jogou 
						jogadorAtualJogou=false;

						//Verifica se o jogo est� trancado, se sim entra no IF
						if(estaTrancado()){
							System.out.println(referenciaDoServidor.listaDeNomeUsuarios.get(0)+" trancou o jogo!");

							//Manda todos somarem suas pe�as
							for (int i = 0; i < referenciaDoServidor.listaDeUsuarios.size(); i++) {
								try {
									//Cria a conex�o com o cliente
									InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
									//Obt�m o somat�rio das pe�as desse Jogador e coloca na lista
									referenciaDoServidor.listaDeUsuarios.get(i).somaDasPecasAposJogoTrancado=cli.jogoTrancado();
									//Sinaliza que o Jogador atualizou o somot�rio das pe�as
									referenciaDoServidor.listaDeUsuarios.get(i).atualizouTrancado=true;
								} catch (MalformedURLException e) {
									e.printStackTrace();
								} catch (RemoteException e) {
									e.printStackTrace();
								} catch (NotBoundException e) {
									e.printStackTrace();
								}
							}

							System.out.println("Verificar� quem tem menor somat�rio");

							//Verifica quem tem o menor somat�rio
							//**falta tratrar quando tiver 2 ou mais ganhadores
							//Vari�vel que verifica se todos jogadores atualizaram a vari�vel do somat�rio
							Boolean atu=false;
							//Vari�vel que guarda o �ndice de quem ganhou o jogo
							int quemGanhouJogo=0;
							//Fica no while at� que todos jogadores atualizem o somat�rio das suas pe�as
							while(atu==false){
								atu=true;
								//Verifica se algum Jogador n�o atualizou suas pe�as
								for(int i=0;i<referenciaDoServidor.listaDeUsuarios.size();i++){
									if(referenciaDoServidor.listaDeUsuarios.get(i).atualizouTrancado==false){
										atu=false;
									}
								}

								quemGanhouJogo=0;
								//Verifica o �ndice do Jogador que ganhou o jogo
								for(int j=1;j<referenciaDoServidor.listaDeUsuarios.size();j++){
									if(referenciaDoServidor.listaDeUsuarios.get(j).somaDasPecasAposJogoTrancado<referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).somaDasPecasAposJogoTrancado){
										quemGanhouJogo=j;
									}
								}
							}

							System.out.println("Verificou quem tem menor somat�rio e ir� dizer quem ganhou");

							try {
								//Avisa a todos os Jogadores quem tem o menor somat�rio
								referenciaDoServidor.enviaATodosClientes(referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).nomeUsuario+
										" ganhou jogo, pois ele s� possui o somat�rio de "+
										referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).somaDasPecasAposJogoTrancado);


								System.out.println("Entrou atualizar pontua��o: somar +1");
								//Obtem o valor da pontua��o do Jogador que ganhou o jogo 
								Integer auxp =referenciaDoServidor.listaDePontuacaoUsuarios.remove(quemGanhouJogo);
								//Incrementa a pontua��o do Jogador que ganhou o jogo 
								referenciaDoServidor.listaDePontuacaoUsuarios.add(quemGanhouJogo,auxp+1);
								System.out.println("Atualizei pontua��o");

								//Fim do jogo
								referenciaDoServidor.fimDoJogo=true;
							} catch (RemoteException e) {
								e.printStackTrace();
							}

						}//fim jogo trancado

					}//Outras jogadas ap�s o 1 jogador

					//Coloca o "TrataCliente" e nome do Jogador no final da Lista
					referenciaDoServidor.listaDeUsuarios.add( referenciaDoServidor.listaDeUsuarios.remove(0));
					referenciaDoServidor.listaDeNomeUsuarios.add( referenciaDoServidor.listaDeNomeUsuarios.remove(0));

					//Atualiza a pontua��o no final da Lista tamb�m
					referenciaDoServidor.listaDePontuacaoUsuarios.add(referenciaDoServidor.listaDePontuacaoUsuarios.remove(0));

				}//Jogadas ap�s dar as pe�as aos jogadores 

			}//while(fim de um jogo)

			//Mostra a pontua��o atual
			//	mostrarPontuacao();

			System.out.println("Acabou o Jogo!!!");

			//Informa o Servidor que acabou uma rodada
			referenciaDoServidor.referenciaGuiServ.escreverNoTexto("Acabou o Jogo!!!");

			//Atualiza todos Jogadores para um novo jogo
			for (int i = 0; i < referenciaDoServidor.listaDeNomeUsuarios.size(); i++) {
				//TrataCliente desse jogador
				TrataCliente tr=referenciaDoServidor.listaDeUsuarios.get(i);
				//Atualiza que n�o passou a vez
				tr.passouVez=false;
				//Atualiza que o Jogador n�o est� pronto para jogar
				tr.prontoParaJogar=false;
				tr.atualizouTrancado=false;
				//tr.pecasJogador=new ArrayList<PecaDomino>();

				//Conex�o com o cliente
				InterfaceDoCliente cli;

				try {
					cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
					//Remove todas as pe�as deste Jogador
					cli.removerTodasPecasJogador();
					//Habilita o checkbox para uma nova rodada deste Jogador
					cli.habilitaPronto();
					//Apaga a mesa do cliente
					cli.atualizaGUI(new ArrayList<PecaDomino>(),referenciaDoServidor.listaDeNomeUsuarios,referenciaDoServidor.listaDePontuacaoUsuarios);
					//Apaga as pe�as do jogador
					cli.recebe7Pecas(new ArrayList<PecaDomino>());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}

			//Sinaliza que n�o aconteceu a primeira jogada
			jogada1=true;
			//Sinaliza que iniciou um novo jogo, ou seja, diz que n�o � fim
			referenciaDoServidor.fimDoJogo=false;
			//Cria um novo Domino
			domino=new Domino();
			//Sinaliza que nenhuma pe�a foi utilizada
			pecasUtilizadas= new ArrayList<PecaDomino>();
			//Sinaliza que o jogo n�o iniciou
			referenciaDoServidor.jogoIniciado=false;

		}//while que reinicia jogo
	}

	//Ordena a Lista dos jogadores, ou seja, deixa em primeiro o que tiver o maior carro��o
	public void primeiroJogador(){
		//aqui nao sabemos se s�o iguais os lados

		//Guarda o �ndice do primeiro Jogador
		int indiceDeQuemInicia=0;
		//Pergunta se essa pe�a tem lados iguais
		boolean iguais=pecaMaiorIguais(indiceDeQuemInicia);

		//Busca a maior pe�a 
		for (int i = 1; i < referenciaDoServidor.listaDeUsuarios.size(); i++) {
			//Indica que as duas pe�as comparadas s�o iguais ou diferentes
			//,ou seja, pega o maior somat�rio
			if(iguais==pecaMaiorIguais(i)){
				if(somaDoisLados(i)>somaDoisLados(indiceDeQuemInicia)){
					//Atualiza o �ndice do Jogador que inicia o jogo
					indiceDeQuemInicia=i;
					//Atualiza se a maior pe�a � Carro��o ou N�o
					iguais=pecaMaiorIguais(indiceDeQuemInicia);
				}
			}
			else{
				//Pegunta se a pe�a que ir� comparar � carro��o e a outra n�o						
				if(iguais!=true&&pecaMaiorIguais(i)){
					//Atualiza o �ndice do Jogador que inicia o jogo
					indiceDeQuemInicia=i;
					//Atualiza se a maior pe�a � Carro��o ou N�o
					iguais=pecaMaiorIguais(indiceDeQuemInicia);
				}
			}
		}

		//Coloca atr�s da lista at� o primeiro maior
		for (int i = 0; i < indiceDeQuemInicia; i++) {
			//Coloca o "TrataCliente" no final da Lista
			TrataCliente aux= referenciaDoServidor.listaDeUsuarios.remove(i);
			referenciaDoServidor.listaDeUsuarios.add(aux);

			//Coloca o nome do Jogador no final da Lista
			String auxNome=referenciaDoServidor.listaDeNomeUsuarios.remove(i);
			referenciaDoServidor.listaDeNomeUsuarios.add(auxNome);

			//Atualiza o valor da pontua��o tamb�m
			Integer auxPont=referenciaDoServidor.listaDePontuacaoUsuarios.remove(i);
			referenciaDoServidor.listaDePontuacaoUsuarios.add(auxPont);
		}

		System.out.println("Maior Pe�a entre os jogadores:"+referenciaDoServidor.listaDeUsuarios.get(0).maiorPecaDoJogador().getLadoDireito()+"|"+
				referenciaDoServidor.listaDeUsuarios.get(0).maiorPecaDoJogador().getLadoEsquerdo());


	}

	//Soma os dois lados da maior pe�a do Jogador
	public int somaDoisLados(int indice){
		return referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoDireito()+
		referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoEsquerdo();
	}

	//Retorna a maior pe�a do Jogador. O �ndice do jogador � passado como par�mentro  
	public PecaDomino maiorPeca(int indice){
		return referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador();
	}

	//Verifica se a maior pe�a do Jogador � Carro��o ou N�o. O �ndice do jogador � passado como par�metro
	public Boolean pecaMaiorIguais(int indice){
		if(referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoDireito()==
			referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoEsquerdo()	){
			return true;
		}
		else{
			return false;
		}
	}

	//Verifica se o jogo est� trancado
	public Boolean estaTrancado(){
		//Vari�vel que cont�m o resultado do m�todo
		Boolean resultado=true;

		//Verifica se todos jogadores passaram sua vez 
		for(int i=0;i<referenciaDoServidor.listaDeUsuarios.size();i++){
			//Se pelo menos um jogador n�o tiver passado ent�o o jogo continua
			if(!referenciaDoServidor.listaDeUsuarios.get(i).passouVez){
				resultado=false;
			}			
		}
		return resultado;
	}

	//Mostra as pe�as utilizadas, ou seja, est� na mesa
	public void mostrarPecasUtilizadas(){
		System.out.println("Pe�as utilizadas:");
		for(int i=0; i<pecasUtilizadas.size();i++){
			System.out.print(pecasUtilizadas.get(i).getLadoEsquerdo()+"|"+pecasUtilizadas.get(i).getLadoDireito()+" ");
		}
		System.out.println();
	}

	//Monta uma pe�a com os lados poss�veis
	public PecaDomino buscarPontasDasPecas(){
		//Ponta do jogo
		PecaDomino pecamontada;

		//Verifica se tem mais de uma pe�a na mesa
		if(pecasUtilizadas.size()>1){
			//Monta a pe�a com a esquerda do primeiro da lista da mesa
			//e com a direita do �ltimo da lista de pe�as da mesa
			pecamontada=new PecaDomino(pecasUtilizadas.get(pecasUtilizadas.size()-1).getLadoDireito(), pecasUtilizadas.get(0).getLadoEsquerdo());
		}
		else{//Se tiver s� tem uma pe�a na mesa
			pecamontada=pecasUtilizadas.get(0);
		}

		//Mostra as pontas 
		System.out.println("Pontas: "+pecamontada.getLadoEsquerdo()+"|"+pecamontada.getLadoDireito());

		return pecamontada;

	}

	//Mostra a pontua��o de cada um dos jogadores
	public void mostrarPontuacao(){
		System.out.println("Pontua��es:");
		for (int i = 0; i < referenciaDoServidor.listaDePontuacaoUsuarios.size()-1; i++) {
			System.out.println(referenciaDoServidor.listaDeNomeUsuarios.get(i)+": "+referenciaDoServidor.listaDePontuacaoUsuarios.get(i));

		}	
	}

}
