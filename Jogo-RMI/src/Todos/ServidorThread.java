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

	//Listas que contém todos os Usuários e seus Nomes que estão utilizando o servidor
	ArrayList <String> listaDeNomeUsuarios ;
	ArrayList <TrataCliente> listaDeUsuarios ;

	//Lista com a pontuação de cada um dos Jogadores
	ArrayList<Integer> listaDePontuacaoUsuarios;

	//Interface gráfica do Servidor
	GuiServidor referenciaGuiServ;

	//Variável que indica se o jogo terminou
	Boolean fimDoJogo=false;

	//Nome que irá registrar o servidor no servidor de nomes
	String nomeServidor="Servidor";

	//Variável que contém o valor do lado que irá adicionar a peça
	String lado="";

	public ServidorThread(GuiServidor g) throws RemoteException{

		super();

		referenciaGuiServ=g;

		try {
			System.out.println("Registrará o servidor.");

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

		//Instancia as listas de Nome usuários, Usuários e Pontuação
		listaDeNomeUsuarios= new ArrayList <String>();
		listaDeUsuarios= new ArrayList <TrataCliente>();
		listaDePontuacaoUsuarios=new ArrayList<Integer>();


		System.out.println("ServidorThread: esperando cliente");
		//Escreve no TextArea uma mensagem
		referenciaGuiServ.escreverNoTexto("ServidorThread: esperando cliente");


		while (true) {
			//Servidor não faz nada
		}

	}

	//Registra os Jogadores no Servidor
	public void login(String a) throws RemoteException {

		//Limitador do número de usuários conectados
		if(listaDeUsuarios.size()<4&&jogoIniciado==false){
			//Adiciona o Nome do Usuário e sua pontuação na Lista
			listaDeNomeUsuarios.add(a);
			listaDePontuacaoUsuarios.add(0);
			//Cria novo TrataCliente e adiciona na lista
			listaDeUsuarios.add(new TrataCliente(a,this));

			System.out.println("ServidorThread: Cliente "+a+" se conectou com Servidor");
			//Mostra no Interface do Servidor que um Jogador se conectou ao Servidor
			referenciaGuiServ.escreverNoTexto("ServidorThread: Cliente "+a+" se conectou com Servidor");

		}
		else{
			//Quando o jogo já foi iniciado
			if(jogoIniciado==true){
				InterfaceDoCliente cl;
				try {
					cl = (InterfaceDoCliente)Naming.lookup("//localhost/"+a);
					cl.recebeString("Jogo iniciado e não pode entrar!");
					//Avisa ao Jogador que ele não pode se conectar ao Servidor
					cl.recebeMensagemDoChat("Jogo iniciado e nao pode entrar!");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
				//Mostra no Interface do Servidor que um Jogador tentou se conectar e não pode
				referenciaGuiServ.escreverNoTexto("Jogador Tentou entrar, mas jogo já iniciado");
			}else{

				InterfaceDoCliente cli;
				try {
					cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+a);
					//Avisa ao Jogador que ele não pode se conectar ao Servidor
					cli.servidorLotador(true);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}

				//Mostra no Interface do Servidor que ele já está lotado
				referenciaGuiServ.escreverNoTexto("Servidor já está lotado!");
			}
		}

		System.out.println("ServidorThread: esperando cliente");
		//Mostra no Interface do Servidor que ele está esperando algum Jogador se conectar
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

	//Recebe o lado que o jogador que adicionar a peça na mesa
	public void recebeLadoAJogar(String a) throws RemoteException {
		lado=a;
	}

	//Avisa a todos os Jogadores caso algum dos jogadores tenha vencido o Jogo 
	public void ganhouJogo(String a,String nomeJogador) throws RemoteException {

		if(a.equals("venceu essa rodada!")){

			System.out.println("Entrou no método ganhouJogo");

			for (int i = 0; i < listaDeNomeUsuarios.size(); i++) {

				System.out.println(listaDeNomeUsuarios.get(i));

				//Procura qual o jogador que venceu e adiciona a sua pontuação
				if(nomeJogador.equals(listaDeNomeUsuarios.get(i))){
					System.out.println("Entrou atualizar pontuação: somar +1");

					Integer auxp =listaDePontuacaoUsuarios.get(i);
					listaDePontuacaoUsuarios.add(i,auxp+1);

					System.out.println("Atualizei pontuação");
				}

				try {
					//Escreve na Interface gráfica dos Jogadores qu o nome de quem venceu
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

			//Sinaliza que o jogo acabou, pois alguém venceu
			fimDoJogo=true;
		}
		else{
			System.out.println("Jogador jogou, mas não ganhou jogo");
		}
	}

	//Atualiza o valor no "TrataCliente" quando é clicado na interface
	public void prontoParaJogar(Boolean a, String nome) throws RemoteException {
		for (int i = 0; i < listaDeNomeUsuarios.size(); i++) {
			if(nome.equals(listaDeNomeUsuarios.get(i))){
				listaDeUsuarios.get(i).prontoParaJogar=a;
			}
		}

	}
}
