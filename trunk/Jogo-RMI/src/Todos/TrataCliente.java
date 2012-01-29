package Todos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;



public class TrataCliente extends Thread implements Serializable{
	//Indica que esse jogador passou a sua vez
	Boolean passouVez=false;
	//Soma das pe�as ap�s jogo trancado
	int somaDasPecasAposJogoTrancado=0;
	Boolean atualizouTrancado=false;

	//nome do usu�rio
	String nomeUsuario;

	//vari�vel que sinaliza se o cliente est� pronto para jogar
	Boolean prontoParaJogar=false;

	//Pe�as do jogador
	public ArrayList <PecaDomino> pecasJogador;

	//nome do cliente tratado
	////int nomeCliente;
	//conex�o com o cliente 
	////Socket cliente;
	//canal para receber dados
	////DataInputStream receberMensagem;
	//canal para enviar dados
	////DataOutputStream out;

	ServidorThread servidor;

	//enviar e receber Objeto
	////ObjectOutputStream enviarObjeto;
	////ObjectInputStream receberObjeto;

	public TrataCliente(String nome ,ServidorThread referenciaDoServidor){
		//referencia do servidor principal
		this.servidor=referenciaDoServidor;
		this.nomeUsuario=nome;
		

		////this.cliente=cliente;
		////this.nomeCliente=idCliente;
		//instanciando as pe�as deste jogador
		pecasJogador = new ArrayList <PecaDomino> ();

//		try {
//			this.receberMensagem=new DataInputStream(cliente.getInputStream());
//			this.out=new DataOutputStream(cliente.getOutputStream());
//
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		//inicia a Thread
		this.start();

	}
	public void run(){
	///	Boolean enviarATodos=true;
		//condi��o do fechamento da conex�o
		while(true){////!dadosRecebidos.equals("close")){

		////	enviarATodos=true;

		//	System.out.println("TrataCliente "+nomeUsuario+" : Esperando receber string");
			//receber um dado do cliente
			////dadosRecebidos= receberMensagem();
			////System.out.println("TrataCliente "+nomeUsuario+" : String recebida- "+dadosRecebidos);

			
		   
		  
		    
//				//se receber a confirma��o de escolhimento das pe�as
//				if(dadosRecebidos.equals("Escolhi pe�a!")){
//					//isso para ativar JogoDomino enviar as pe�as aos jogadores
//					//System.out.println("TrataCliente "+nomeUsuario+" : Jogador "+nomeUsuario+" selecionou o checkbox");
//
//					enviarMensagem("Continuar, pois a pe�a foi escolhida.");
//					
//					enviarATodos=false;
//				}
//				
//				//Entra aqui quando o checkbox for selecionado e ativa a variavel de pronto
//				if(dadosRecebidos.equals("Estou pronto para receber pe�as!")){
//					//isso para ativar JogoDomino enviar as pe�as aos jogadores
//					prontoParaJogar=true;
//					System.out.println("TrataCliente "+nomeUsuario+" : Jogador "+nomeUsuario+" selecionou o checkbox");
//
//					enviarATodos=false;
//				}
//				
//				//quando o jogador n�o possuir mais pe�as para jogar
//				if(dadosRecebidos.equals("N�o existe pe�a poss�vel a ser jogada.")){
//					PecaDomino pecaPuxada= servidor.jogo.domino.puxarUmaPeca();
//					//tratar se ainda existir pe�as a serem puxadas
//					if(pecaPuxada!=null){			
//						System.out.println("Pe�a que o servidor ir� enviar: "+pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());
//						enviarMensagem("Receber Uma Pe�a Puxada");
//						enviarObjeto.writeObject(pecaPuxada);
//						//sinalizar que ele j� pode jogar
//						enviarMensagem("proximoAJogar");
//						System.out.println("TrataCliente "+nomeUsuario+" : Enviando ao jogador as pontas das das pe�as.");
//						enviarObjeto = new ObjectOutputStream(cliente.getOutputStream());					
//						enviarObjeto.writeObject(servidor.jogo.buscarPontasDasPecas());
//
//					}
//					else{//passar a vez
//						enviarMensagemTodosUsuarios("Passei minha vez!");
//						System.out.println("Jogador passou a vez por n�o existir pe�a a ser puxada");
//						servidor.jogo.jogadorAtualJogou=true;
//						passouVez=true;//quando todos passarem a vez para o jogo e conta as pe�as
//					}
//
//					enviarATodos=false;
//				}
//
//				//atualizar soma das pe�as ap�s trancar jogo
//				if(dadosRecebidos.equals("Soma das pe�as")){
//					somaDasPecasAposJogoTrancado= Integer.parseInt(receberMensagem());
//					System.out.println("Valor recebido do jogador "+nomeUsuario+": "+somaDasPecasAposJogoTrancado);
//					atualizouTrancado=true;
//
//				}
//
////				//para tratar quando algu�m terminou suas pe�as 
////				if(dadosRecebidos.equals("Jogador Venceu")){
////
////					enviarMensagemTodosUsuarios(nomeUsuario+" ganhou o Jogo! ");
////				}
//
//				//recebeu a confirma��o do jogador que ir� enviar um objeto
//				if(dadosRecebidos.equals("peca Escolhida")){
//					System.out.println("TrataCliente "+nomeUsuario+" : O jogador ir� receber uma pe�a");
//					String lado="";
//					//enquanto est� esperando o lado ser escolhido
//					//isso para o jogador que est� jogando poder enviar msg via chat
//					while(!lado.equals("Esquerdo")&&!lado.equals("Direito")){
//						//lado onde ser� inserido a pe�a
//						lado=receberMensagem();
//
//						System.out.println("Lado a ADD:"+lado);
//						if (lado.equals("Esquerdo")) {
//							servidor.jogo.pecasUtilizadas.add(0,receberPeca());
//						} else {//lado direito
//							if(lado.equals("Direito")){
//								servidor.jogo.pecasUtilizadas.add(receberPeca());}
//							else{
//								enviarMensagemTodosUsuarios(lado);
//							}
//						}
//					}
//					//receber novos dados
//					dadosRecebidos=receberMensagem();
//					//perguntar se esse jogador venceu o jogo
//
//
//					if(dadosRecebidos.equals("Jogador Venceu essa rodada!")){
//						
//						
//						System.out.println("Atualizou a variavel de fim de jogo para: "+servidor.fimDoJogo);
//						
//						enviarMensagemATodos("atualizar mesa");
//						enviarPecasDaMesaATodosJogadores(servidor.jogo.pecasUtilizadas);
//						enviarMensagemATodos(nomeUsuario+" ganhou o Jogo! ");
//						
//						//Sinaliza o inicio de um outro jogo
//						servidor.fimDoJogo=true;
//						
//						//Sinaliza que o jogo terminou
//						
//							System.out.println("jogo terminou");
//							//colocar que o jogador n�o est� pronto
//							for (int i = 0; i < servidor.listaDeUsuarios.size(); i++) {
//								servidor.listaDeUsuarios.get(i).prontoParaJogar=false;
//								//Envia um sinal para habilitar o bot�o de pronto
//								servidor.listaDeUsuarios.get(i).enviarMensagem("Habilita bot�o de pronto");
//														
//							}
//							//Crio um novo domin� para outra partida
//							servidor.jogo.domino = new Domino();	
//							servidor.jogo.pecasUtilizadas= new ArrayList<PecaDomino>();
//							//Dizer que o jogo nao foi iniciado
//							servidor.jogoIniciado=false;
//							//N�o entrar novamente no fim do jogo somente quando acabar novamente
//							servidor.fimDoJogo=false;
//						//Quando o jogo termindou
//						
//
//					}
//
//					else{//n�o deixa o pr�ximo jogar
//
//						//Indica que o jogador n�o passou a vez
//						passouVez=false;
//
//						enviarMensagemATodos("atualizar mesa");
//
//						enviarPecasDaMesaATodosJogadores(servidor.jogo.pecasUtilizadas);
//
//						servidor.jogo.jogadorAtualJogou=true;
//					}
//
//					System.out.println("TrataCliente "+nomeUsuario+" : Recebeu uma pe�a");
//					System.out.println("TrataCliente "+nomeUsuario+" : "+servidor.jogo.pecasUtilizadas.get(0).getLadoEsquerdo()+"|"+servidor.jogo.pecasUtilizadas.get(0).getLadoDireito());
//
//					enviarATodos=false;
//				}
//				if(enviarATodos==true){
//					//depois que recebeu uma mensagem envia a todos usu�rios
//					enviarMensagemTodosUsuarios(dadosRecebidos);
//				}
//
//				System.out.println("TrataCliente "+nomeUsuario+" : Dados recedidos: '"+dadosRecebidos + "' pelo Tratador de Cliente: "+nomeUsuario);

		}
	}

	//Envia a mensagem a esse jogador
//	public void enviarMensagem(String msg){
//		try {	
//			this.out.writeUTF(msg);
//			this.out.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	//Recebe uma String de um Jogador
//	public String receberMensagem(){
//		String dadosRecebidos=null;
//		try {
//			dadosRecebidos= receberMensagem.readUTF();
//			return dadosRecebidos;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return dadosRecebidos;
//	}

	//envia a mensagem com o "nome do Jogador:"
//	public void enviarMensagemTodosUsuarios(String msgATodos){
//		int indice=0;
//		System.out.println("ClienteThread "+nomeUsuario+" : Enviando Mensagem a todos jogadores.");
//		while(servidor.listaDeUsuarios.size()>indice){
//
//			//para mostrar o eu disse:
//			if(!servidor.listaDeUsuarios.get(indice).nomeUsuario.equals(nomeUsuario)){
//				servidor.listaDeUsuarios.get(indice).enviarMensagem(nomeUsuario+": "+msgATodos);}
//			else{
//				//enviar pro cliente atual com EU 
//				enviarMensagem("Eu: "+msgATodos);
//			}
//
//			indice++;
//		}
//	}

	//envia a mensagem com o "nome do Jogador:"
//	public void enviarMensagemATodos(String msgATodos){
//		for(int indice=0;indice<servidor.listaDeUsuarios.size();indice++){
//			servidor.listaDeUsuarios.get(indice).enviarMensagem(msgATodos);
//		}				
//	}

	//remover o jogador da lista de jogadores, ap�s ele apertar em sair
	public void removerDaListaDeUsuario(){
		//remover ele da lista 
		servidor.listaDeUsuarios.remove(this);
	}

	//sortear as pe�as do jogador
//	public void setPecasJogador(ArrayList <PecaDomino> pecasJogadorSorteadas){
//		this.pecasJogador= pecasJogadorSorteadas;
//		//desabilitar para ele n�o pegar mais pe�as
//		prontoParaJogar=false;
//
//		//mostrar as pe�as de um jogador
//		for(int i=0; i<pecasJogador.size(); i++){
//			System.out.println("TrataCliente "+nomeUsuario+" : "+pecasJogador.get(i).getLadoEsquerdo()+"|"+pecasJogador.get(i).getLadoDireito());
//		}
//	}
//	
	//Pegar o �ndice de qual string corresponde 
//	public static int returnIndex(String toIndex, String... args) {  
//        for (int i=0; i<args.length; i++) {  
//            if (toIndex.equals(args[i] ) )   
//                return i;  
//        }  
//        return -1;  
//    }  

	//enviar pe�as utilizadas na mesa a todos
//	public void enviarPecasDaMesaATodosJogadores(ArrayList <PecaDomino> pecasUtilizadas){
//		System.out.println("ClienteThread "+nomeUsuario+" : Enviando Mensagem as pe�as utilizadas a todos a todos jogadores.");
//
//		for(int indice=0;indice<servidor.listaDeUsuarios.size();indice++){			
//			servidor.listaDeUsuarios.get(indice).enviarPecasDaMesa(pecasUtilizadas);
//		}
//	}

	//enviar pe�as utilizadas na mesa
//	public void enviarPecasDaMesa(ArrayList <PecaDomino> pecasUtilizadas){
//		try {
//			System.out.println("TrataCliente "+nomeUsuario+" : Enviando ao jogador as pe�as da mesa");
//
//			enviarObjeto = new ObjectOutputStream(cliente.getOutputStream());  
//
//			enviarObjeto.writeObject(pecasUtilizadas);
//
//			//voltar a enviar string
//			out=new DataOutputStream(cliente.getOutputStream());
//
//			System.out.println("TrataCliente "+nomeUsuario+" : Pe�as da mesa enviadas");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	//enviar as pe�as sorteadas ao jogador
//	void enviarPecas(){
//
//		try {
//			System.out.println("TrataCliente "+nomeUsuario+" : Enviando ao jogador as pe�as");
//
//			enviarObjeto = new ObjectOutputStream(cliente.getOutputStream());  
//
//			enviarObjeto.writeObject(pecasJogador);
//
//			//voltar a enviar string
//			out=new DataOutputStream(cliente.getOutputStream());
//
//			System.out.println("TrataCliente "+nomeUsuario+" : Pe�as do jogador enviadas");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	//enviar uma pe�a ao jogador
//	void enviarPeca(PecaDomino peca){
//		try {
//			enviarObjeto = new ObjectOutputStream(cliente.getOutputStream());
//
//			enviarObjeto.writeObject(peca);
//
//			//voltar a enviar string
//			out=new DataOutputStream(cliente.getOutputStream());
//			//receberObjeto
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	//receber uma pe�a do jogador
//	PecaDomino receberPeca(){
//		try {
//
//			receberObjeto = new ObjectInputStream(cliente.getInputStream());
//
//			PecaDomino peRec=(PecaDomino) receberObjeto.readUnshared();
//
//			//receber String novamente
//			receberMensagem=new DataInputStream(cliente.getInputStream());
//
//			//receberObjeto
//			return peRec;
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null; 
//	}

	public PecaDomino maiorPecaDoJogador(){
		PecaDomino maior=null;

		for (int i = 0; i < pecasJogador.size(); i++) {
			//Quando os dois lados s�o iguais
			if(pecasJogador.get(i).getLadoDireito()==pecasJogador.get(i).getLadoEsquerdo()){
				//primeira pe�a com dois lados iguais
				if(maior==null){
					maior=pecasJogador.get(i);
				}
				else{
					if(pecasJogador.get(i).getLadoDireito()>maior.getLadoDireito()){
						maior=pecasJogador.get(i);
					}
				}

			}
		}

		//para quando n�o existir pe�as iguais		
		if(maior==null){
			//inicia primeira pe�a	
			int indiceMa=0;
			maior=pecasJogador.get(0);
			for (int i = 1; i < pecasJogador.size(); i++) {
				if(somaDoisLados(i)>somaDoisLados(indiceMa)){
					maior=pecasJogador.get(i);
					indiceMa=i;
				}
			}
		}

		return maior;

	}


	public int somaDoisLados(int indice){
		System.out.println("soma das duas pe�as:"+(pecasJogador.get(indice).getLadoDireito()+pecasJogador.get(indice).getLadoDireito()));
		return pecasJogador.get(indice).getLadoDireito()+pecasJogador.get(indice).getLadoDireito();
	}

}
