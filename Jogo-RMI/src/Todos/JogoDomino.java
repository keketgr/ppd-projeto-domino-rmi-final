package Todos;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;



//esse classe ir� gerenciar todo o jogo
public class JogoDomino extends Thread implements Serializable{
	//isso para o jogo esperar o jogador jogar a pe�a
	Boolean jogadorAtualJogou=false;

	//Array com boolean que indica se os jogadores passaram a suas vezes
	Boolean [] jogadoresQuePassaramSuaVez;

	//Sinaliza a primeira jogada, ou seja inicio do jogo
	Boolean jogada1=true;

	//pe�as j� utilizadas no jogo
	ArrayList <PecaDomino> pecasUtilizadas;

	Domino domino;
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

		//tem que ficar aqui at� o jogo terminar, ou seja, o n�mero de pe�a do jogador acabar
		while(referenciaDoServidor.fimDoJogo==false){

			//verificar se todos os jogadores enviaram a confirma��o de pronto ao servidor
			if(referenciaDoServidor.jogoIniciado==false){
				//verifica se todos os jogadores est�o prontos para jogar
				if(referenciaDoServidor.listaDeUsuarios.size()>1){
					todosProntos=true;
					//verificar se todos os jogadores est�o prontos
					for(int i=0; i<referenciaDoServidor.listaDeUsuarios.size();i++){
						//para achar um jogador nao pronto
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
					//todos os jogadores pegam seus domin�s
					for(int j=0; j<referenciaDoServidor.listaDeUsuarios.size();j++){
						System.out.println("JogoDomino: valor de J:"+j);
						System.out.println("JogoDomino: Tamanho da lista de usu�rio:"+referenciaDoServidor.listaDeUsuarios.size());
						//passando pro servidor as pe�as sorteadas do jogador
						////referenciaDoServidor.listaDeUsuarios.get(j).setPecasJogador(domino.pecasDeUmJogador());


						String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(j);

						//como ele ir� jogar, coloca ele no final da lista
						////jogadorAtual.enviarMensagemATodos("Vez do jogador: "+jogadorAtual.nomeUsuario);

						InterfaceDoCliente cli;
						try {
							cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);
							ArrayList<PecaDomino> pec=domino.pecasDeUmJogador();
							cli.recebe7Pecas(pec);
							referenciaDoServidor.listaDeUsuarios.get(j).pecasJogador=pec;
							cli.recebeMensagemDoChat("Recebi minhas pe�as.");
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NotBoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						//dizer o cliente que vai um objeto
						////referenciaDoServidor.listaDeUsuarios.get(j).enviarMensagem("Receber Lista de Pe�as");


						//tem que esperar uns segundos para enviar pe�as
						////	referenciaDoServidor.listaDeUsuarios.get(j).enviarPecas();
						System.out.println("JogoDomino: Enviou as pe�as desse jogador");
					}
					System.out.println("JogoDomino: As pe�as foram dadas a todos os jogadores");
					System.out.println("JogoDomino: Fim envio das pe�as!");

					todosProntos=false;
					referenciaDoServidor.jogoIniciado=true;
				}
			}			
			//jogo iniciado
			else{//jogo iniciado

				//				//aqui nao sabemos se s�o iguais os lados				
				//				int indiceDeQuemInicia=0;
				//				boolean iguais=pecaMaiorIguais(indiceDeQuemInicia);
				//
				//				//buscar a maior pe�a
				//				for (int i = 1; i < referenciaDoServidor.listaDeUsuarios.size(); i++) {
				//					//Isso indica que os dois s�o iguais ou diferentes
				//					if(iguais==pecaMaiorIguais(i)){
				//						if(somaDoisLados(i)>somaDoisLados(indiceDeQuemInicia)){
				//							indiceDeQuemInicia=i;
				//							iguais=pecaMaiorIguais(indiceDeQuemInicia);
				//						}
				//
				//					}
				//					else{
				//						//lados iguais na outra pe�a						
				//						if(iguais!=true&&pecaMaiorIguais(i)){
				//							indiceDeQuemInicia=i;
				//							iguais=pecaMaiorIguais(indiceDeQuemInicia);
				//						}
				//					}
				//				}
				//
				//				//colocar atr�s da lista
				//				for (int i = 0; i < indiceDeQuemInicia; i++) {
				//					TrataCliente aux= referenciaDoServidor.listaDeUsuarios.remove(i);
				//					referenciaDoServidor.listaDeUsuarios.add(aux);
				//				}

				//				//retira o primeiro jogador da lista e coloca no final
				//				TrataCliente jogadorAtual=referenciaDoServidor.listaDeUsuarios.get(0);
				//				//como ele ir� jogar, coloca ele no final da lista
				//				jogadorAtual.enviarMensagemATodos("Vez do jogador: "+jogadorAtual.nomeUsuario);


				//somente na primeira jogada, ou seja, o jogador poder� escolher qualque pe�a para iniciar				
				if (jogada1==true) {
					
					System.out.println("\n**Primeira Jogada**\n");

					//ordenar primeiro jogador					
					primeiroJogador();

					//retira o primeiro jogador da lista e coloca no final
					TrataCliente jogadorAtual=referenciaDoServidor.listaDeUsuarios.get(0);

					String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(0);

					//como ele ir� jogar, coloca ele no final da lista
					////jogadorAtual.enviarMensagemATodos("Vez do jogador: "+jogadorAtual.nomeUsuario);
					try {
						InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);
						//	cli.recebeMensagemDoChat("Vez do jogador: "+jogadorAtual.nomeUsuario);
						referenciaDoServidor.enviaATodosClientes("Vez do jogador: "+jogadorAtual.nomeUsuario);

						PecaDomino p1=cli.jogaPrimeiraPeca();
						if (referenciaDoServidor.lado.equals("Esquerdo")) {
							referenciaDoServidor.jogo.pecasUtilizadas.add(0,p1);
						} else {//lado direito
							referenciaDoServidor.jogo.pecasUtilizadas.add(p1);}
						
						//isso tem que ser para todos clientes
						cli.atualizaGUI(referenciaDoServidor.jogo.pecasUtilizadas);


					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println("JogoDomino: Nome do jogador que ir� iniciar- "+jogadorAtual.nomeUsuario);
					////jogadorAtual.enviarMensagem("primeiraPeca");

					jogada1=false;
					//					while(jogadorAtualJogou==false){
					//						//Espera at� o jogador faz sua jogada
					//					}
					System.out.println("JogoDomino: "+jogadorAtual.nomeUsuario+" enviou a 1 pe�a!");
					jogadorAtualJogou=false;

				} else {//outras jogadas
					
					System.out.println("\n**Proxima Jogada**\n");
					
					//retira o primeiro jogador da lista e coloca no final
					TrataCliente jogadorAtual=referenciaDoServidor.listaDeUsuarios.get(0);
					String nomeJ=referenciaDoServidor.listaDeNomeUsuarios.get(0);

					//como ele ir� jogar, coloca ele no final da lista
					////jogadorAtual.enviarMensagemATodos("Vez do jogador: "+jogadorAtual.nomeUsuario);

					try {
						
						
						//tem que ser para todos clientes
						for (int i = 0; i < referenciaDoServidor.listaDeNomeUsuarios.size(); i++) {
							InterfaceDoCliente cli2 = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
							cli2.atualizaGUI(referenciaDoServidor.jogo.pecasUtilizadas);
						}
						
						
						InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+nomeJ);
						//	cli.recebeMensagemDoChat("Vez do jogador: "+jogadorAtual.nomeUsuario);
						referenciaDoServidor.enviaATodosClientes("Vez do jogador: "+jogadorAtual.nomeUsuario);

						PecaDomino p1=cli.jogaProximaPeca(buscarPontasDasPecas());
						
						System.out.println(jogadorAtual.nomeUsuario+" jogou sua pe�a.");
						
						//fica puxando at� acabar pe�as ou pegar uma poss�vel
						while(p1==null&&domino.todasPecas.size()>0){
							cli.puxaUmaPeca(domino.puxarUmaPeca());
							referenciaDoServidor.enviaATodosClientes(jogadorAtual.nomeUsuario+" comprou uma pe�a.");
							p1=cli.jogaProximaPeca(buscarPontasDasPecas());
						}
						
						System.out.println("Acabou de puxar ou n�o.");
						
						//n�o tem pe�a pra puxar
						if(p1==null&&domino.todasPecas.size()==0){
							System.out.println(jogadorAtual.nomeUsuario+" passou sua vez");
							jogadorAtual.passouVez=true;
							jogadorAtualJogou=true;
						}else{//se tiver pe�a
							System.out.println("Puxou pe�a ou n�o e ira adicina-la");
							if (referenciaDoServidor.lado.equals("Esquerdo")) {
								referenciaDoServidor.jogo.pecasUtilizadas.add(0,p1);
							} else {//lado direito
								referenciaDoServidor.jogo.pecasUtilizadas.add(p1);}
							System.out.println("Adicionou pe�a na mesa");
							
							//tem que ser para todos clientes
							for (int i = 0; i < referenciaDoServidor.listaDeNomeUsuarios.size(); i++) {
								InterfaceDoCliente cli2 = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
								cli2.atualizaGUI(referenciaDoServidor.jogo.pecasUtilizadas);
							}
							jogadorAtualJogou=true;
						}

					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					//isso para o 1 ap�s o in�cio do jogo
					mostrarPecasUtilizadas();

					System.out.println("JogoDomino: Nome do jogador que ir� iniciar- "+jogadorAtual.nomeUsuario);
					//jogadorAtual.enviarMensagem("proximoAJogar");
					//jogadorAtual.enviarPeca(buscarPontasDasPecas());//trocou a instru��o de cima
					while(jogadorAtualJogou==false){
						//Espera at� o jogador faz sua jogada
					}
					System.out.println("JogoDomino: O jogador "+jogadorAtual.nomeUsuario+" enviou a 1 pe�a!");
					jogadorAtualJogou=false;

					//verifica se o jogo est� trancado
					if(estaTrancado()){
						System.out.println(referenciaDoServidor.listaDeNomeUsuarios.get(0)+" trancou o jogo!");
						//Manda todos somarem suas pe�as
						////referenciaDoServidor.listaDeUsuarios.get(0).enviarMensagemATodos("Jogo Trancado!");

						for (int i = 0; i < referenciaDoServidor.listaDeUsuarios.size(); i++) {
							try {
								InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+referenciaDoServidor.listaDeNomeUsuarios.get(i));
								referenciaDoServidor.listaDeUsuarios.get(i).somaDasPecasAposJogoTrancado=cli.jogoTrancado();
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
						//falta tratrar quando tiver 2 ou mais ganhadores
						Boolean atu=false;
						int quemGanhouJogo=0;
						while(atu==false){
							atu=true;
							for(int i=0;i<referenciaDoServidor.listaDeUsuarios.size();i++){
								if(referenciaDoServidor.listaDeUsuarios.get(i).atualizouTrancado==false){
									atu=false;
								}
							}
							quemGanhouJogo=0;
							for(int j=1;j<referenciaDoServidor.listaDeUsuarios.size();j++){
								if(referenciaDoServidor.listaDeUsuarios.get(j).somaDasPecasAposJogoTrancado<referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).somaDasPecasAposJogoTrancado){
									quemGanhouJogo=j;
								}
							}
						}
						
						System.out.println("Verificou quem tem menor somat�rio e ir� dizer quem ganhou");
						
						try {
							referenciaDoServidor.enviaATodosClientes(referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).nomeUsuario+
									" ganhou jogo, pois ele s� possui o somat�rio de "+
									referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).somaDasPecasAposJogoTrancado);
							
							//Fim do jogo
							referenciaDoServidor.fimDoJogo=true;
						} catch (RemoteException e) {
							e.printStackTrace();
						}

						//						referenciaDoServidor.listaDeUsuarios.get(0).enviarMensagemATodos(
						//								referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).nomeUsuario+
						//								" ganhou jogo, pois ele s� possui o somat�rio de "+
						//								referenciaDoServidor.listaDeUsuarios.get(quemGanhouJogo).somaDasPecasAposJogoTrancado);

						//contar quantas pe�as cada um tem para d� o resultado
//						while(true){
//
//						}
					}//fim jogo trancado

				}//Outras jogadas ap�s o 1 jogador

				//Coloca o jogador no final da fila
				referenciaDoServidor.listaDeUsuarios.add( referenciaDoServidor.listaDeUsuarios.remove(0));
				referenciaDoServidor.listaDeNomeUsuarios.add( referenciaDoServidor.listaDeNomeUsuarios.remove(0));

				//				//Sinaliza que o jogo terminou
				//				if(referenciaDoServidor.fimDoJogo==true){
				//					System.out.println("jogo terminou");
				//					//colocar que o jogador n�o est� pronto
				//					for (int i = 0; i < referenciaDoServidor.listaDeUsuarios.size(); i++) {
				//						referenciaDoServidor.listaDeUsuarios.get(i).prontoParaJogar=false;
				//						//Envia um sinal para habilitar o bot�o de pronto
				//						referenciaDoServidor.listaDeUsuarios.get(i).enviarMensagem("Habilita bot�o de pronto");
				//												
				//					}
				//					//Crio um novo domin� para outra partida
				//					domino = new Domino();	
				//					pecasUtilizadas= new ArrayList<PecaDomino>();
				//					//Dizer que o jogo nao foi iniciado
				//					referenciaDoServidor.jogoIniciado=false;
				//					//N�o entrar novamente no fim do jogo somente quando acabar novamente
				//					referenciaDoServidor.fimDoJogo=false;
				//				}//Quando o jogo termindou

			}//Jogadas ap�s dar as pe�as aos jogadores 

		}//while(true)
		
		System.out.println("Acabou o Jogo!!!");
		referenciaDoServidor.referenciaGuiServ.escreverNoTexto("Acabou o Jogo!!!");
	}


	public void primeiroJogador(){
		//aqui nao sabemos se s�o iguais os lados				
		int indiceDeQuemInicia=0;
		boolean iguais=pecaMaiorIguais(indiceDeQuemInicia);

		//buscar a maior pe�a
		for (int i = 1; i < referenciaDoServidor.listaDeUsuarios.size(); i++) {
			//Isso indica que os dois s�o iguais ou diferentes
			if(iguais==pecaMaiorIguais(i)){
				if(somaDoisLados(i)>somaDoisLados(indiceDeQuemInicia)){
					indiceDeQuemInicia=i;
					iguais=pecaMaiorIguais(indiceDeQuemInicia);
				}

			}
			else{
				//lados iguais na outra pe�a						
				if(iguais!=true&&pecaMaiorIguais(i)){
					indiceDeQuemInicia=i;
					iguais=pecaMaiorIguais(indiceDeQuemInicia);
				}
			}
		}

		//colocar atr�s da lista at� o primeiro maior
		for (int i = 0; i < indiceDeQuemInicia; i++) {
			TrataCliente aux= referenciaDoServidor.listaDeUsuarios.remove(i);
			referenciaDoServidor.listaDeUsuarios.add(aux);

			String auxNome=referenciaDoServidor.listaDeNomeUsuarios.remove(i);
			referenciaDoServidor.listaDeNomeUsuarios.add(auxNome);
		}

		System.out.println("Maior Pe�a entre os jogadores:"+referenciaDoServidor.listaDeUsuarios.get(0).maiorPecaDoJogador().getLadoDireito()+"|"+
				referenciaDoServidor.listaDeUsuarios.get(0).maiorPecaDoJogador().getLadoEsquerdo());


	}

	public int somaDoisLados(int indice){
		return referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoDireito()+
		referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador().getLadoEsquerdo();
	}

	//	indice do jogador e passado e retorna a maior pe�a 
	public PecaDomino maiorPeca(int indice){
		return referenciaDoServidor.listaDeUsuarios.get(indice).maiorPecaDoJogador();
	}

	//	indice do jogador e retorna se sua maior pe�a � igual
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
		Boolean resultado=true;
		for(int i=0;i<referenciaDoServidor.listaDeUsuarios.size();i++){
			if(!referenciaDoServidor.listaDeUsuarios.get(i).passouVez){
				resultado=false;
			}			
		}
		return resultado;
	}

	//mostrar pe�as utilizadas
	void mostrarPecasUtilizadas(){
		System.out.println("Pe�as utilizadas:");
		for(int i=0; i<pecasUtilizadas.size();i++){
			//para achar um jogador nao pronto
			System.out.print(pecasUtilizadas.get(i).getLadoEsquerdo()+"|"+pecasUtilizadas.get(i).getLadoDireito()+" ");
		}
		System.out.println();
	}

	//monta uma pe�a com as pe�as poss�veis
	PecaDomino buscarPontasDasPecas(){
		PecaDomino pecamontada;

		if(pecasUtilizadas.size()>1){

			//pegar a direita do �ltimo �ndice(size-1)
			pecamontada=new PecaDomino(pecasUtilizadas.get(pecasUtilizadas.size()-1).getLadoDireito(), pecasUtilizadas.get(0).getLadoEsquerdo());

		}
		else{//s� tem uma pe�a
			pecamontada=pecasUtilizadas.get(0);
		}

		//mostrar as pontas 
		System.out.println("Pontas: "+pecamontada.getLadoEsquerdo()+"|"+pecamontada.getLadoDireito());

		return pecamontada;

	}
}
