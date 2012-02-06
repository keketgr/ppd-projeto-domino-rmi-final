package Todos;


import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;




public class ServidorThread extends UnicastRemoteObject implements Runnable, InterfaceDoServidor,Serializable{
	//Indica se o jogo foi iniciado
	Boolean jogoIniciado = false;

	//Cria o domino do jogo
	JogoDomino jogo;

	//Listas que cont�m todos os Usu�rios e seus Nomes que est�o utilizando o servidor
	ArrayList <String> listaDeNomeUsuarios ;
	ArrayList <TrataCliente> listaDeUsuarios ;

	//Lista com a pontua��o de cada um dos Jogadores
	ArrayList<Integer> listaDePontuacaoUsuarios;

	//Interface gr�fica do Servidor
	GuiServidor referenciaGuiServ;

	//Vari�vel que indica se o jogo terminou
	Boolean fimDoJogo=false;

	//Nome que ir� registrar o servidor no servidor de nomes
	String nomeServidor="Servidor";

	//Vari�vel que cont�m o valor do lado que ir� adicionar a pe�a
	String lado="";

	public ServidorThread(GuiServidor g) throws RemoteException{

		super();

		referenciaGuiServ=g;

		try {
			System.out.println("Registrar� o servidor.");

			//Registra o Servidor no servidor de nomes
			Naming.rebind(nomeServidor,(Remote) this);

			System.out.println("Servidor Registrado!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		//Intancia o jogo De Domino
		jogo=new JogoDomino(this);
		jogo.start();

		//Instancia as listas de Nome usu�rios, Usu�rios e Pontua��o
		listaDeNomeUsuarios= new ArrayList <String>();
		listaDeUsuarios= new ArrayList <TrataCliente>();
		listaDePontuacaoUsuarios=new ArrayList<Integer>();


		System.out.println("ServidorThread: esperando cliente");
		//Escreve no TextArea uma mensagem
		referenciaGuiServ.escreverNoTexto("ServidorThread: esperando cliente");


		while (true) {
			//Servidor n�o faz nada
		}

	}

	//Registra os Jogadores no Servidor
	public void login(String a) throws RemoteException {

		//Limitador do n�mero de usu�rios conectados
		if(listaDeUsuarios.size()<4&&jogoIniciado==false){
			//Adiciona o Nome do Usu�rio e sua pontua��o na Lista
			listaDeNomeUsuarios.add(a);
			listaDePontuacaoUsuarios.add(0);
			//Cria novo TrataCliente e adiciona na lista
			listaDeUsuarios.add(new TrataCliente(a,this));

			System.out.println("ServidorThread: Cliente "+a+" se conectou com Servidor");
			//Mostra no Interface do Servidor que um Jogador se conectou ao Servidor
			referenciaGuiServ.escreverNoTexto("ServidorThread: Cliente "+a+" se conectou com Servidor");

		}
		else{
			//Quando o jogo j� foi iniciado
			if(jogoIniciado==true){
				InterfaceDoCliente cl;
				try {
					cl = (InterfaceDoCliente)Naming.lookup("//localhost/"+a);
					cl.recebeString("Jogo iniciado e n�o pode entrar!");
					//Avisa ao Jogador que ele n�o pode se conectar ao Servidor
					cl.recebeMensagemDoChat("Jogo iniciado e nao pode entrar!");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
				//Mostra no Interface do Servidor que um Jogador tentou se conectar e n�o pode
				referenciaGuiServ.escreverNoTexto("Jogador Tentou entrar, mas jogo j� iniciado");
			}else{

				InterfaceDoCliente cli;
				try {
					cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+a);
					//Avisa ao Jogador que ele n�o pode se conectar ao Servidor
					cli.servidorLotador(true);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}

				//Mostra no Interface do Servidor que ele j� est� lotado
				referenciaGuiServ.escreverNoTexto("Servidor j� est� lotado!");
			}
		}

		System.out.println("ServidorThread: esperando cliente");
		//Mostra no Interface do Servidor que ele est� esperando algum Jogador se conectar
		referenciaGuiServ.escreverNoTexto("ServidorThread: esperando cliente");

	}

	//Envia uma mensagem a todos Jogadores
	public void enviaATodosClientes(String es) throws RemoteException {
		for (int i = 0; i < listaDeNomeUsuarios.size(); i++) {
			try {
				InterfaceDoCliente cl =  (InterfaceDoCliente)Naming.lookup("//localhost/"+listaDeNomeUsuarios.get(i));
				cl.recebeMensagemDoChat(es);
				cl.recebeString(es);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}

	}

	//Recebe o lado que o jogador que adicionar a pe�a na mesa
	public void recebeLadoAJogar(String a) throws RemoteException {
		lado=a;
	}

	//Avisa a todos os Jogadores caso algum dos jogadores tenha vencido o Jogo 
	public void ganhouJogo(String a,String nomeJogador) throws RemoteException {

		if(a.equals("venceu essa rodada!")){

			System.out.println("Entrou no m�todo ganhouJogo");

			for (int i = 0; i < listaDeNomeUsuarios.size(); i++) {

				System.out.println(listaDeNomeUsuarios.get(i));

				//Procura qual o jogador que venceu e adiciona a sua pontua��o
				if(nomeJogador.equals(listaDeNomeUsuarios.get(i))){
					System.out.println("Entrou atualizar pontua��o: somar +1");

					Integer auxp =listaDePontuacaoUsuarios.get(i);
					listaDePontuacaoUsuarios.add(i,auxp+1);

					System.out.println("Atualizei pontua��o");
				}

				try {
					//Escreve na Interface gr�fica dos Jogadores qu o nome de quem venceu
					InterfaceDoCliente cl =  (InterfaceDoCliente)Naming.lookup("//localhost/"+listaDeNomeUsuarios.get(i));
					cl.recebeMensagemDoChat(nomeJogador+" "+a);
					cl.recebeString(nomeJogador+" "+a);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}

			//Sinaliza que o jogo acabou, pois algu�m venceu
			fimDoJogo=true;
		}
		else{
			System.out.println("Jogador jogou, mas n�o ganhou jogo");
		}
	}

	//Atualiza o valor no "TrataCliente" quando � clicado na interface
	public void prontoParaJogar(Boolean a, String nome) throws RemoteException {
		for (int i = 0; i < listaDeNomeUsuarios.size(); i++) {
			if(nome.equals(listaDeNomeUsuarios.get(i))){
				listaDeUsuarios.get(i).prontoParaJogar=a;
			}
		}

	}
}
