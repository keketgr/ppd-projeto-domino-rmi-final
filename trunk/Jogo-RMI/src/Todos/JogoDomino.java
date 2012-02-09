package Todos;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;



//Essa classe irá gerenciar todo o jogo
public class JogoDomino extends Thread implements Serializable{
	//Isso é para o jogo esperar o jogador jogar a peça
	Boolean jogadorAtualJogou=false;

	//Array com boolean que indica se os jogadores passaram a suas vezes
	Boolean [] jogadoresQuePassaramSuaVez;

	//Sinaliza a primeira jogada, ou seja inicio do jogo
	Boolean jogada1=true;

	//Peças já utilizadas no jogo
	ArrayList <PecaDomino> pecasUtilizadas;

	//Domino usado na Rodada
	Domino domino;

	//Referência do Servidor que o criou
	ServidorThread referenciaDoServidor;

	public JogoDomino(ServidorThread referenciaDoServidor){		
		System.out.println("JogoDomino: Iniciou o JOGO Domino");
		domino = new Domino();		
		pecasUtilizadas= new ArrayList<PecaDomino>();
		this.referenciaDoServidor=referenciaDoServidor;
	}

	public void run(){
		//Indica que todos jogadores estão prontos para jogar
		Boolean todosProntos=false;

		//Reinicia Jogo
		while(true){

			//Fica aqui até o jogo terminar, ou seja, o número de peça do jogador acabar
			while(referenciaDoServidor.fimDoJogo==false){

				//Verifica se todos os jogadores enviaram a confirmação de pronto ao servidor
				if(referenciaDoServidor.jogoIniciado==false){
					//Verifica se todos os jogadores estão prontos para jogar
					if(referenciaDoServidor.listaDeUsuarios.size()>1){
						todosProntos=true;
						//Verifica se todos os jogadores estão prontos
						for(int i=0; i<referenciaDoServidor.listaDeUsuarios.size();i++){
							//Acha os jogadores que não estão prontos
							if(referenciaDoServidor.listaDeUsuarios.get(i)!=null && referenciaDoServidor.listaDeUsuarios.get(i).prontoParaJogar==false){

								todosProntos=false;
							}
						}
					}

					//Quando todos os jogadores estão prontos dá as peças aos jogadores
					if(todosProntos==true){
						//Indica quais os jogadores passaram suas vezes e saber se o jogo está trancado
						jogadoresQuePassaramSuaVez=new Boolean[referenciaDoServidor.listaDeUsuarios.size()];

						System.out.println("JogoDomino: colocou as peças na mão do jogador");

						//Todos os jogadores pegam seus dominós
						for(int j=0; j<referenciaDoServidor.listaDeUsuarios.size();j++){
							System.out.println("JogoDomino: Índice da lista de usuários: "+j);
							System.out.println("JogoDomino: colocou as peças na mão do jogador"+referenciaDoServidor.listaDeNomeUsuarios.get(j));
							System.out.println("JogoDomino: Tamanho da lista de usuários:"+referenciaDoServidor.listaDeUsuarios.size());

							String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(j);

							InterfaceDoCliente cli;
							try {
								//Procrura o cliente
								cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);
								//Sorteia as peças para 1 dos Jogadores
								ArrayList<PecaDomino> pec=domino.pecasDeUmJogador();
								//Envia as 7 peças sorteadas a 1 dos Jogadores
								cli.recebe7Pecas(pec);
								//Atualiza no Servidor as peças que estão com o jogador
								referenciaDoServidor.listaDeUsuarios.get(j).pecasJogador=pec;
								//Avisa no TextArea que esse Jogador recebeu suas peças
								cli.recebeMensagemDoChat("Recebi minhas peças.");
							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (RemoteException e) {
								e.printStackTrace();
							} catch (NotBoundException e) {
								e.printStackTrace();
							}

							System.out.println("JogoDomino: Enviou as peças desse jogador");
						}

						System.out.println("JogoDomino: As peças foram dadas a todos os jogadores");
						System.out.println("JogoDomino: Fim envio das peças!");

						//Para não dar as 7 peças novamente aos Jogadores
						todosProntos=false;
						//Sinaliza no Servidor que o jogo foi iniciado
						referenciaDoServidor.jogoIniciado=true;
					}
				}			
				//Jogo iniciado
				else{//jogo iniciado

					//Somente na primeira jogada, ou seja, o jogador poderá escolher qualque peça para iniciar				
					if (jogada1==true) {

						System.out.println("\n**Primeira Jogada**\n");

						//Ordena primeiro jogador, ou seja, ve quem tem maior carroção					
						primeiroJogador();

						//Pega a referência do primeiro jogador da lista
						TrataCliente jogadorAtual=referenciaDoServidor.listaDeUsuarios.get(0);

						//Pega o nome do primeiro jogador da lista
						String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(0);

						//como ele irá jogar, coloca ele no final da lista
						try {
							//Cria a conexão com o primeiro Jogador
							InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);
							//Avisa a todos quem é o primeiro a jogar, ou seja, a vez de quem
							referenciaDoServidor.enviaATodosClientes("Vez do jogador: "+jogadorAtual.nomeUsuario);
							//Recebe a peça escolhida pelo Jogador
							PecaDomino p1=cli.jogaPrimeiraPeca();
							//Verifica qual lado foi escolhido pelo Jogador
							if (referenciaDoServidor.lado.equals("Esquerdo")) {
								//Adiciona a peça no início da lista, ou seja, no lado esquerdo da lista
								referenciaDoServidor.jogo.pecasUtilizadas.add(0,p1);
							} else {//lado direito
								//Adiciona a peça no fim da lista, ou seja, no lado direito da lista
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

						System.out.println("JogoDomino: Nome do jogador que irá iniciar- "+jogadorAtual.nomeUsuario);

						//Sinaliza que já foi jogada a primeira jogada
						jogada1=false;

						System.out.println("JogoDomino: "+jogadorAtual.nomeUsuario+" enviou a 1 peça!");

						//Deixa pronto para a próxima rodada, que o Jogador não jogou 
						jogadorAtualJogou=false;

					} else {//Outras jogadas

						System.out.println("\n**Proxima Jogada**\n");

						//Pega a referência do primeiro jogador da lista
						TrataCliente jogadorAtual=referenciaDoServidor.listaDeUsuarios.get(0);

						//Pega o nome do primeiro jogador da lista
						String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(0);

						try {
							//Atualiza as peça que estão na mesa de todos Jogadores
							for (int i = 0; i < referenciaDoServidor.listaDeNomeUsuarios.size(); i++) {
								InterfaceDoCliente cli2 = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
								cli2.atualizaGUI(referenciaDoServidor.jogo.pecasUtilizadas,referenciaDoServidor.listaDeNomeUsuarios,referenciaDoServidor.listaDePontuacaoUsuarios);
							}

							//Faz a conexão com o primeiro Jogador da Lista
							InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);

							//Avisa a todos de quem é a vez
							referenciaDoServidor.enviaATodosClientes("Vez do jogador: "+jogadorAtual.nomeUsuario);

							//Recebe a peça jogada pelo Jogador
							PecaDomino p1=cli.jogaProximaPeca(buscarPontasDasPecas());

							System.out.println(jogadorAtual.nomeUsuario+" jogou sua peça.");

							//Fica puxando até acabar peças ou pegar uma possível
							while(p1==null&&domino.todasPecas.size()>0){
								//Passa pro Jogador a peça comprada
								cli.puxaUmaPeca(domino.puxarUmaPeca());
								//Avisa a todos que esse Jogador comprou uma peça
								referenciaDoServidor.enviaATodosClientes(jogadorAtual.nomeUsuario+" comprou uma peça.");
								//Recebe a peça jogada pelo Jogador
								p1=cli.jogaProximaPeca(buscarPontasDasPecas());
							}

							System.out.println("Acabou de puxar ou não.");

							//Não tendo peça pra puxar e não tiver recebido uma peça
							if(p1==null&&domino.todasPecas.size()==0){
								System.out.println(jogadorAtual.nomeUsuario+" passou sua vez");
								//Sinaliza que o Jogador passou sua vez
								jogadorAtual.passouVez=true;
								//Sinaliza que o jogador fez sua jogada
								jogadorAtualJogou=true;
							}else{//Se tiver peça

								System.out.println("Puxou peça ou não e ira adicina-la");
								//Verifica qual lado foi escolhido pelo Jogador
								if (referenciaDoServidor.lado.equals("Esquerdo")) {
									//Adiciona a peça no início da lista, ou seja, no lado esquerdo da lista
									referenciaDoServidor.jogo.pecasUtilizadas.add(0,p1);
								} else {//lado direito
									//Adiciona a peça no fim da lista, ou seja, no lado direito da lista
									referenciaDoServidor.jogo.pecasUtilizadas.add(p1); 
								}

								System.out.println("Adicionou peça na mesa");

								//Atualiza as peça que estão na mesa de todos Jogadores
								for (int i = 0; i < referenciaDoServidor.listaDeNomeUsuarios.size(); i++) {
									InterfaceDoCliente cli2 = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
									cli2.atualizaGUI(referenciaDoServidor.jogo.pecasUtilizadas,referenciaDoServidor.listaDeNomeUsuarios,referenciaDoServidor.listaDePontuacaoUsuarios);
								}

								//Sinaliza que o Jogador atual já jogou uma peça
								jogadorAtualJogou=true;
							}

						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (NotBoundException e) {
							e.printStackTrace();
						}

						//Mostra o ArrayList das peças que estão na mesa 
						mostrarPecasUtilizadas();

						System.out.println("JogoDomino: Nome do jogador que irá iniciar- "+jogadorAtual.nomeUsuario);

						while(jogadorAtualJogou==false){
							//Espera até o jogador faz sua jogada
						}

						System.out.println("JogoDomino: O jogador "+jogadorAtual.nomeUsuario+" enviou a 1 peça!");

						//Deixa pronto para a próxima rodada, que o Jogador não jogou 
						jogadorAtualJogou=false;

						//Verifica se o jogo está trancado, se sim entra no IF
						if(estaTrancado()){
							System.out.println(referenciaDoServidor.listaDeNomeUsuarios.get(0)+" trancou o jogo!");

							//Manda todos somarem suas peças
							for (int i = 0; i < referenciaDoServidor.listaDeUsuarios.size(); i++) {
								try {
									//Cria a conexão com o cliente
									InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
									//Obtém o somatório das peças desse Jogador e coloca na lista
									referenciaDoServidor.listaDeUsuarios.get(i).somaDasPecasAposJogoTrancado=cli.jogoTrancado();
									//Sinaliza que o Jogador atualizou o somotário das peças
									referenciaDoServidor.listaDeUsuarios.get(i).atualizouTrancado=true;
								} catch (MalformedURLException e) {
									e.printStackTrace();
								} catch (RemoteException e) {
									e.printStackTrace();
								} catch (NotBoundException e) {
									e.printStackTrace();
								}
							}

							System.out.println("Verificará quem tem menor somatório");

							//Verifica quem tem o menor somatório
							//**falta tratrar quando tiver 2 ou mais ganhadores
							//Variável que verifica se todos jogadores atualizaram a variável do somatório
							Boolean atu=false;
							//Variável que guarda o índice de quem ganhou o jogo
							int quemGanhouJogo=0;
							//Fica no while até que todos jogadores atualizem o somatório das suas peças
							while(atu==false){
								atu=true;
								//Verifica se algum Jogador não atualizou suas peças
								for(int i=0;i<referenciaDoServidor.listaDeUsuarios.size();i++){
									if(referenciaDoServidor.listaDeUsuarios.get(i).atualizouTrancado==false){
										atu=false;
									}
								}

								quemGanhouJogo=0;
								//Verifica o índice do Jogador que ganhou o jogo
								for(int j=1;j<referenciaDoServidor.listaDeUsuarios.size();j++){
									if(referenciaDoServidor.listaDeUsuarios.get(j).somaDasPecasAposJogoTrancado<referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).somaDasPecasAposJogoTrancado){
										quemGanhouJogo=j;
									}
								}
							}

							System.out.println("Verificou quem tem menor somatório e irá dizer quem ganhou");

							try {
								//Avisa a todos os Jogadores quem tem o menor somatório
								referenciaDoServidor.enviaATodosClientes(referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).nomeUsuario+
										" ganhou jogo, pois ele só possui o somatório de "+
										referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).somaDasPecasAposJogoTrancado);


								System.out.println("Entrou atualizar pontuação: somar +1");
								//Obtem o valor da pontuação do Jogador que ganhou o jogo 
								Integer auxp =referenciaDoServidor.listaDePontuacaoUsuarios.remove(quemGanhouJogo);
								//Incrementa a pontuação do Jogador que ganhou o jogo 
								referenciaDoServidor.listaDePontuacaoUsuarios.add(quemGanhouJogo,auxp+1);
								System.out.println("Atualizei pontuação");

								//Fim do jogo
								referenciaDoServidor.fimDoJogo=true;
							} catch (RemoteException e) {
								e.printStackTrace();
							}

						}//fim jogo trancado

					}//Outras jogadas após o 1 jogador

					//Coloca o "TrataCliente" e nome do Jogador no final da Lista
					referenciaDoServidor.listaDeUsuarios.add( referenciaDoServidor.listaDeUsuarios.remove(0));
					referenciaDoServidor.listaDeNomeUsuarios.add( referenciaDoServidor.listaDeNomeUsuarios.remove(0));

					//Atualiza a pontuação no final da Lista também
					referenciaDoServidor.listaDePontuacaoUsuarios.add(referenciaDoServidor.listaDePontuacaoUsuarios.remove(0));

				}//Jogadas após dar as peças aos jogadores 

			}//while(fim de um jogo)

			//Mostra a pontuação atual
			//	mostrarPontuacao();

			System.out.println("Acabou o Jogo!!!");

			//Informa o Servidor que acabou uma rodada
			referenciaDoServidor.referenciaGuiServ.escreverNoTexto("Acabou o Jogo!!!");

			//Atualiza todos Jogadores para um novo jogo
			for (int i = 0; i < referenciaDoServidor.listaDeNomeUsuarios.size(); i++) {
				//TrataCliente desse jogador
				TrataCliente tr=referenciaDoServidor.listaDeUsuarios.get(i);
				//Atualiza que não passou a vez
				tr.passouVez=false;
				//Atualiza que o Jogador não está pronto para jogar
				tr.prontoParaJogar=false;
				tr.atualizouTrancado=false;
				//tr.pecasJogador=new ArrayList<PecaDomino>();

				//Conexão com o cliente
				InterfaceDoCliente cli;

				try {
					cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
					//Remove todas as peças deste Jogador
					cli.removerTodasPecasJogador();
					//Habilita o checkbox para uma nova rodada deste Jogador
					cli.habilitaPronto();
					//Apaga a mesa do cliente
					cli.atualizaGUI(new ArrayList<PecaDomino>(),referenciaDoServidor.listaDeNomeUsuarios,referenciaDoServidor.listaDePontuacaoUsuarios);
					//Apaga as peças do jogador
					cli.recebe7Pecas(new ArrayList<PecaDomino>());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}

			//Sinaliza que não aconteceu a primeira jogada
			jogada1=true;
			//Sinaliza que iniciou um novo jogo, ou seja, diz que não é fim
			referenciaDoServidor.fimDoJogo=false;
			//Cria um novo Domino
			domino=new Domino();
			//Sinaliza que nenhuma peça foi utilizada
			pecasUtilizadas= new ArrayList<PecaDomino>();
			//Sinaliza que o jogo não iniciou
			referenciaDoServidor.jogoIniciado=false;

		}//while que reinicia jogo
	}

	//Ordena a Lista dos jogadores, ou seja, deixa em primeiro o que tiver o maior carroção
	public void primeiroJogador(){
		//aqui nao sabemos se são iguais os lados

		//Guarda o índice do primeiro Jogador
		int indiceDeQuemInicia=0;
		//Pergunta se essa peça tem lados iguais
		boolean iguais=pecaMaiorIguais(indiceDeQuemInicia);

		//Busca a maior peça 
		for (int i = 1; i < referenciaDoServidor.listaDeUsuarios.size(); i++) {
			//Indica que as duas peças comparadas são iguais ou diferentes
			//,ou seja, pega o maior somatório
			if(iguais==pecaMaiorIguais(i)){
				if(somaDoisLados(i)>somaDoisLados(indiceDeQuemInicia)){
					//Atualiza o índice do Jogador que inicia o jogo
					indiceDeQuemInicia=i;
					//Atualiza se a maior peça é Carroção ou Não
					iguais=pecaMaiorIguais(indiceDeQuemInicia);
				}
			}
			else{
				//Pegunta se a peça que irá comparar é carroção e a outra não						
				if(iguais!=true&&pecaMaiorIguais(i)){
					//Atualiza o índice do Jogador que inicia o jogo
					indiceDeQuemInicia=i;
					//Atualiza se a maior peça é Carroção ou Não
					iguais=pecaMaiorIguais(indiceDeQuemInicia);
				}
			}
		}

		//Coloca atrás da lista até o primeiro maior
		for (int i = 0; i < indiceDeQuemInicia; i++) {
			//Coloca o "TrataCliente" no final da Lista
			TrataCliente aux= referenciaDoServidor.listaDeUsuarios.remove(i);
			referenciaDoServidor.listaDeUsuarios.add(aux);

			//Coloca o nome do Jogador no final da Lista
			String auxNome=referenciaDoServidor.listaDeNomeUsuarios.remove(i);
			referenciaDoServidor.listaDeNomeUsuarios.add(auxNome);

			//Atualiza o valor da pontuação também
			Integer auxPont=referenciaDoServidor.listaDePontuacaoUsuarios.remove(i);
			referenciaDoServidor.listaDePontuacaoUsuarios.add(auxPont);
		}

		System.out.println("Maior Peça entre os jogadores:"+referenciaDoServidor.listaDeUsuarios.get(0).maiorPecaDoJogador().getLadoDireito()+"|"+
				referenciaDoServidor.listaDeUsuarios.get(0).maiorPecaDoJogador().getLadoEsquerdo());


	}

	//Soma os dois lados da maior peça do Jogador
	public int somaDoisLados(int indice){
		return referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoDireito()+
		referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoEsquerdo();
	}

	//Retorna a maior peça do Jogador. O índice do jogador é passado como parâmentro  
	public PecaDomino maiorPeca(int indice){
		return referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador();
	}

	//Verifica se a maior peça do Jogador é Carroção ou Não. O índice do jogador é passado como parâmetro
	public Boolean pecaMaiorIguais(int indice){
		if(referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoDireito()==
			referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoEsquerdo()	){
			return true;
		}
		else{
			return false;
		}
	}

	//Verifica se o jogo está trancado
	public Boolean estaTrancado(){
		//Variável que contém o resultado do método
		Boolean resultado=true;

		//Verifica se todos jogadores passaram sua vez 
		for(int i=0;i<referenciaDoServidor.listaDeUsuarios.size();i++){
			//Se pelo menos um jogador não tiver passado então o jogo continua
			if(!referenciaDoServidor.listaDeUsuarios.get(i).passouVez){
				resultado=false;
			}			
		}
		return resultado;
	}

	//Mostra as peças utilizadas, ou seja, está na mesa
	public void mostrarPecasUtilizadas(){
		System.out.println("Peças utilizadas:");
		for(int i=0; i<pecasUtilizadas.size();i++){
			System.out.print(pecasUtilizadas.get(i).getLadoEsquerdo()+"|"+pecasUtilizadas.get(i).getLadoDireito()+" ");
		}
		System.out.println();
	}

	//Monta uma peça com os lados possíveis
	public PecaDomino buscarPontasDasPecas(){
		//Ponta do jogo
		PecaDomino pecamontada;

		//Verifica se tem mais de uma peça na mesa
		if(pecasUtilizadas.size()>1){
			//Monta a peça com a esquerda do primeiro da lista da mesa
			//e com a direita do último da lista de peças da mesa
			pecamontada=new PecaDomino(pecasUtilizadas.get(pecasUtilizadas.size()-1).getLadoDireito(), pecasUtilizadas.get(0).getLadoEsquerdo());
		}
		else{//Se tiver só tem uma peça na mesa
			pecamontada=pecasUtilizadas.get(0);
		}

		//Mostra as pontas 
		System.out.println("Pontas: "+pecamontada.getLadoEsquerdo()+"|"+pecamontada.getLadoDireito());

		return pecamontada;

	}

	//Mostra a pontuação de cada um dos jogadores
	public void mostrarPontuacao(){
		System.out.println("Pontuações:");
		for (int i = 0; i < referenciaDoServidor.listaDePontuacaoUsuarios.size()-1; i++) {
			System.out.println(referenciaDoServidor.listaDeNomeUsuarios.get(i)+": "+referenciaDoServidor.listaDePontuacaoUsuarios.get(i));

		}	
	}

}
