package Todos;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.JOptionPane;
//import java.util.List;




public class ServidorThread extends UnicastRemoteObject implements Runnable, InterfaceDoServidor,Serializable{
	//jogo iniciado
	Boolean jogoIniciado = false;

	//Criar o domino
	JogoDomino jogo;

	//lista que contém todos os usuários que estão utilizando o servidor
	ArrayList <String> listaDeNomeUsuarios ;
	ArrayList <TrataCliente> listaDeUsuarios ;



	//String com conteudo
	////String conteudo= new String();

	////static int contador =0;

	// Cria um ServerSocket escutando à porta 1024
	////ServerSocket server;

	////DataInputStream dadosRecebidos=null;
	////DataOutputStream dadosEnviados=null;
	GuiServidor referenciaGuiServ;

	////int porta=5080;

	Boolean fimDoJogo=false;

	String nomeServidor="Servidor";

	String lado="";

	public ServidorThread(GuiServidor g) throws RemoteException{

		super();

		referenciaGuiServ=g;
		//trocar porta por 5080
		//porta=Integer.parseInt(JOptionPane.showInputDialog("Escolha o Porta do servidor",""));
		try {
			//cria um socket servidor na por especificada
			////	server = new ServerSocket(porta);

			System.out.println("Registrará o servidor.");
			Naming.rebind(nomeServidor,(Remote) this);
			System.out.println("Servidor Registrado!");



		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public void run(){
		//jogo De Domino
		jogo=new JogoDomino(this);
		jogo.start();

		//a instância da lista de usuários
		listaDeNomeUsuarios= new ArrayList <String>();
		listaDeUsuarios= new ArrayList <TrataCliente>();
		//try {
		////Socket cliente;

		System.out.println("ServidorThread: esperando cliente");
		referenciaGuiServ.escreverNoTexto("ServidorThread: esperando cliente");
		//para aceitar a conexão com o cliente
		//depois disso cria o canal de comunicação entre cliente e servidor
		////cliente = server.accept();

		//confirmando que o servidor conectou com algum cliente
		////System.out.println("ServidorThread: Cliente "+cliente.getLocalAddress()+" se conectou com Servidor");
		////referenciaGuiServ.escreverNoTexto("ServidorThread: Cliente "+cliente.getLocalAddress()+" se conectou com Servidor");

		//número de clientes acessando
		////	contador++;

		while (true) {

			//				//limitador do número de usuários conectados
			//				if(listaDeUsuarios.size()<4&&jogoIniciado==false){
			//					//chamando o trador de cliente, ou seja, cria um para tratar daquele cliente e
			//					//coloca essa comunicação com esse usuário na lista de usuários
			//					listaDeUsuarios.add(new TrataCliente(cliente,contador,this));
			//				}
			//				else{
			//					if(jogoIniciado==true){
			//						DataOutputStream out=new DataOutputStream(cliente.getOutputStream());
			//						out.writeUTF("Jogo iniciado e nao pode entrar!");
			//						referenciaGuiServ.escreverNoTexto("Jogador Tentou entrar, mas jogo já iniciado");
			//					}else{
			//					DataOutputStream out=new DataOutputStream(cliente.getOutputStream());
			//					out.writeUTF("Servidor já está lotado!");
			//					referenciaGuiServ.escreverNoTexto("Servidor já está lotado!");
			//					}
			//				}
			//
			//				System.out.println("ServidorThread: esperando cliente");
			//				referenciaGuiServ.escreverNoTexto("ServidorThread: esperando cliente");
			//				
			//				//para aceitar a conexão com o cliente
			//				//depois disso cria o canal de comunicação entre cliente e servidor
			//
			//				//esperar uma nova coneção
			//				cliente = server.accept();
			//				//confirmando que o servidor conectou com algum cliente
			//				System.out.println("ServidorThread: Cliente "+cliente.getLocalAddress()+" se conectou com Servidor");
			//				referenciaGuiServ.escreverNoTexto("ServidorThread: Cliente "+cliente.getLocalAddress()+" se conectou com Servidor");
			//			
			//
			//			////	contador++;

		}
		//		}
		//		catch (IOException e) {
		//			e.printStackTrace();
		//		}
	}

	@Override
	public void login(String a) throws RemoteException {

		//limitador do número de usuários conectados
		if(listaDeUsuarios.size()<4&&jogoIniciado==false){
			//chamando o trador de cliente, ou seja, cria um para tratar daquele cliente e
			//coloca essa comunicação com esse usuário na lista de usuários
			listaDeNomeUsuarios.add(a);
			listaDeUsuarios.add(new TrataCliente(a,this));
		}
		else{
			if(jogoIniciado==true){
				////DataOutputStream out=new DataOutputStream(cliente.getOutputStream());
				InterfaceDoCliente cl;
				try {
					cl = (InterfaceDoCliente)Naming.lookup("//localhost/"+a);
					cl.recebeString("Jogo iniciado e nao pode entrar!");
					cl.recebeMensagemDoChat("Jogo iniciado e nao pode entrar!");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
				////out.writeUTF("Jogo iniciado e nao pode entrar!");

				referenciaGuiServ.escreverNoTexto("Jogador Tentou entrar, mas jogo já iniciado");
			}else{
				////DataOutputStream out=new DataOutputStream(cliente.getOutputStream());				
				////out.writeUTF("Servidor já está lotado!");
				InterfaceDoCliente cli;
				try {
					cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+a);
					cli.servidorLotador(true);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}


				referenciaGuiServ.escreverNoTexto("Servidor já está lotado!");
			}
		}

		System.out.println("ServidorThread: esperando cliente");
		referenciaGuiServ.escreverNoTexto("ServidorThread: esperando cliente");

		//para aceitar a conexão com o cliente
		//depois disso cria o canal de comunicação entre cliente e servidor

		//esperar uma nova coneção
		////	cliente = server.accept();
		//confirmando que o servidor conectou com algum cliente
		System.out.println("ServidorThread: Cliente "+a+" se conectou com Servidor");
		referenciaGuiServ.escreverNoTexto("ServidorThread: Cliente "+a+" se conectou com Servidor");



	}

	@Override
	public void enviaATodosClientes(String es) throws RemoteException {
		for (int i = 0; i < listaDeNomeUsuarios.size(); i++) {
			try {
				InterfaceDoCliente cl =  (InterfaceDoCliente)Naming.lookup("//localhost/"+listaDeNomeUsuarios.get(i));
				cl.recebeMensagemDoChat(es);
				cl.recebeString(es);
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
		}

	}

	@Override
	public void recebeString(String a) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void recebeListaDePecas(ArrayList<PecaDomino> pecas)
	throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void recebeObject(Object o) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public void recebeLadoAJogar(String a) throws RemoteException {
		lado=a;
	}

	@Override
	public void ganhouJogo(String a,String nomeJogador) throws RemoteException {

		if(a.equals("venceu essa rodada!")){
			

			for (int i = 0; i < listaDeNomeUsuarios.size(); i++) {
				try {
					InterfaceDoCliente cl =  (InterfaceDoCliente)Naming.lookup("//localhost/"+listaDeNomeUsuarios.get(i));
					cl.recebeMensagemDoChat(nomeJogador+" "+a);
					cl.recebeString(nomeJogador+" "+a);
					//cl.atualizaGUI(jogo.pecasUtilizadas);
					
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
			}
			
			fimDoJogo=true;
			//jogo.stop();
		}
		else{
		System.out.println("Jogador jogou, mas não ganhou jogo");
		}
	}

	//atualiza o valor no tratar cliente quando é clicado na interface
	public void prontoParaJogar(Boolean a, String nome) throws RemoteException {
		for (int i = 0; i < listaDeNomeUsuarios.size(); i++) {
			if(nome.equals(listaDeNomeUsuarios.get(i))){
				listaDeUsuarios.get(i).prontoParaJogar=a;
			}
		}

	}
}
