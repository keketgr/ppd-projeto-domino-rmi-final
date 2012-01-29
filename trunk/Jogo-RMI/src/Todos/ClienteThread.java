package Todos;




import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class ClienteThread extends UnicastRemoteObject implements Runnable, InterfaceDoCliente, ActionListener, MouseListener, Serializable{
	//IP do servidor
	////Nome do servidor que quer se conectar
	String nomeServidor;

	//Porta que o servidor est� usando
	////int portaUtilizada;

	//Lado que o jogador clicou para colocar pe�a
	String ladoAJogar="";

	//Pe�a selecionada pelo mouse
	int pecaSelecionada=-1;

	//Indica se deve adicionar a mensagem no textArea
	////Boolean mostrarTexto=true;

	//Pe�as do jogador, ou seja, pe�as que podem ser utilizadas durante o jogo
	private ArrayList <PecaDomino> pecasJogador;
	//Pe�as que est�o no centro da mesa, ou seja, pe�as que os jogadores utilizaram no jogo 
	private ArrayList <PecaDomino> pecasDaMesa;

	//Classe que tem fun��o de desenhar as pe�as do jogador
	GuiDesenhaDomino desenhoPecasDoJogador;
	//Barra de rolagem das pe�as no centro da mesa
	JScrollPane painelPecasDoJogador;

	//Classe que tem fun��o de desenhar as pe�as que est�o no centro da mesa
	GuiDesenhaPecasDaMesa desenhoDasPecasDaMesa=new GuiDesenhaPecasDaMesa(this);
	//Barra de rolagem das pe�as no centro da mesa
	JScrollPane painelPecasCentro;

	//Dados de entrada recebidos no teclado
	String teclado = "";

	//Cria um socket conectado ao servidor
	////Socket socket=null;

	//Stream que envia as Strings
	////DataOutputStream dadosEnviados=null;
	//Stream que receb as Strings
	////DataInputStream dadosRecebidos=null;
	//----------------------------------
	JFrame frame;
	Container painelDeConteudo ;	

	public JTextArea areadetexto;
	private JMenuBar bar;
	private JMenu menu;
	private JMenuItem itemSair, autores;
	public  JButton enviar;
	private JTextField textField;
	//Check com a fun��o de confirmar que o jogador est� pronto para jogar
	private JCheckBox jogar;
	private JLabel confirmar;
	private JLabel nomePecas; //texto MINHAS PE�AS 

	String nomeJogador="Jogador1";

	//para receber Objetos
	//Stream que recebe Objetos
	////ObjectInputStream recObjeto;
	//Stream que envia Objetos
	////ObjectOutputStream envObjeto;

	Boolean enviarPrimeiraPeca =false;

	//Marca quais pe�as podem ser utilizadas em um array de Boolean
	Boolean [] indicesPecasPossiveis;

	//Pontas do jogo, ou seja, lados poss�veis de jogar
	PecaDomino pecaPossivel;	

	//Comunica��o com servidor
	InterfaceDoServidor serv;

	Boolean servidorLotado=false;

	public ClienteThread() throws RemoteException{

		super();

		pecasJogador=new ArrayList <PecaDomino>();

		pecasDaMesa=new ArrayList <PecaDomino>();

		//Recebe o nome do usu�rio que ficar� no t�tulo do Frame
		nomeJogador=JOptionPane.showInputDialog("Escolha o nome do usu�rio","jogador");
		//Coloca os valores do ip e porta do servidor que ir� conectar
		nomeServidor=JOptionPane.showInputDialog("Escolha o nome do servidor","Servidor");
		////portaUtilizada=Integer.parseInt(JOptionPane.showInputDialog("Escolha o Porta do servidor","5080"));

		//Cria o socket, ou seja, faz a conex�o com o servidor
		try {
			//criando a conex�o com o servidor
			////socket = new Socket (ipServidor, portaUtilizada);

			System.out.println("Registrara o cliente.");
			Naming.rebind(nomeJogador,(Remote) this);
			System.out.println("Cliente Registrado!");

			serv =  (InterfaceDoServidor)Naming.lookup("//localhost/"+nomeServidor);


			//M�todo que cria a interface gr�fica do jogador
			criarGUI();

			//Executar a comunica��o, ou seja, inicia a Thread
			//this.start();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Erro em tentar conectar ao servidor!");
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	//Cria a parte Gr�fica da parte do cliente ou Jogador
	public void criarGUI() {

		//Criar e configurar a janela
		frame = new JFrame(nomeJogador);
		frame.setSize(1200, 700);
		//n�o redimensionar
		frame.setResizable(false);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(null);

		//Constroi o painel de conteudo
		painelDeConteudo = frame.getContentPane();
		painelDeConteudo.setBackground(Color.gray);

		autores = new JMenuItem("Autores");
		autores.addActionListener(this);

		itemSair = new JMenuItem("Sair");
		itemSair.addActionListener(this);

		menu = new JMenu("Arquivo");

		menu.add(autores);
		menu.add(itemSair);

		bar = new JMenuBar();
		//x,y,largura e altura
		bar.setBounds(0, 0, 1200, 20);
		bar.add(menu);

		painelDeConteudo.add(bar);

		//Bot�o que enviaa as informa��es do chat ao servidor
		enviar=new JButton("Enviar");
		enviar.setBackground(Color.WHITE);
		enviar.setToolTipText("Enviar mensagem");
		enviar.setBounds(1100, 470, 80, 25);
		enviar.addActionListener(this);  
		painelDeConteudo.add(enviar);

		//Nome Chat
		JLabel label = new JLabel("Chat: ");
		label.setBackground(Color.DARK_GRAY);
		label.setBounds(830,435,100,30);
		painelDeConteudo.add(label);

		textField = new JTextField("Digite seu texto aqui", 20);		
		textField.setBounds(830, 470, 260, 25);		
		painelDeConteudo.add(textField);

		//campo que cont�m as mensagens enviadas e recebidas
		areadetexto=new JTextArea();
		areadetexto.setEditable(false);
		JScrollPane ps2 = new JScrollPane(areadetexto);
		ps2.setBounds(830,500,350, 150); 
		painelDeConteudo.add(ps2);

		confirmar = new JLabel("Pronto para Jogar: ");
		confirmar.setBounds(0,20,150,30);
		painelDeConteudo.add(confirmar);

		jogar=new JCheckBox();
		jogar.setBackground(Color.gray);
		jogar.setToolTipText("Jogador pronto para jogar");
		jogar.setBounds(160, 20, 20, 25);
		jogar.addActionListener(this);  
		painelDeConteudo.add(jogar);

		frame.setVisible(true);

	}

	public void run(){

		//Desenha Pe�as que est�o na mesa		
		desenhoDasPecasDaMesa.setPreferredSize(new Dimension (100+140*pecasDaMesa.size(), 300));
		painelPecasCentro = new JScrollPane(desenhoDasPecasDaMesa);
		painelPecasCentro.setBounds(0, 50, 1195, 300);	
		painelPecasCentro.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		painelDeConteudo.add(painelPecasCentro);

		desenhoDasPecasDaMesa.repaint();
		painelDeConteudo.repaint();

		frame.validate();

		try {

			//Cria um canal para enviar dados do cliente
			////dadosEnviados = new DataOutputStream(socket.getOutputStream());

			//Cria um canal para receber dados do cliente
			////dadosRecebidos = new DataInputStream(socket.getInputStream());

			//Coloca no servidor o nome do usu�rio ou jogador
			////dadosEnviados.writeUTF(nomeJogador);
			////dadosEnviados.flush();

			serv.login(nomeJogador);


			//Vari�vel que cont�m a string recebida do servidor
			////String rec="";

			//Condi��o fechar esse jogodor, ou seja, quando o jogador desejar fechar a sua interface gr�fica
			while(!teclado.equals("close")&&servidorLotado==false){

				////mostrarTexto=true;

				//System.out.println("ClienteThread: Esperando para ler String");
				//Recebe uma nova string do servidor
				////rec=receberMsg();
				////System.out.println("ClienteThread: Recebeu a string "+rec);



				//desconsiderar mensagem repetidas
				////if(rec.equals("Continuar, pois a pe�a foi escolhida.")){
				////mostrarTexto=false;
				////}

				//In�cio do jogo e o jogador ir� receber suas pe�as que � um objeto
				////if(rec.equals("Receber Lista de Pe�as")){

				//					//Recebe um ArrayList com as pe�as desse jogador
				//					recObjeto = new ObjectInputStream(socket.getInputStream());  
				//					ArrayList<PecaDomino> pecaJogador=(ArrayList)recObjeto.readObject();
				//					System.out.println("ClienteThread: Recebeu a lista com as pe�as");
				//
				//					//Volta para o canal de de receber String
				//					dadosRecebidos = new DataInputStream(socket.getInputStream());
				//
				//					//Atualiza os valores reais das pe�as deste jogador
				//					pecasJogador=pecaJogador;
				//					System.out.println("ClienteThread: Atualizou as pe�as do Jogador");
				//
				//					System.out.println("ClienteThread: Jogador "+nomeJogador+" recebeu seu domino");
				//					System.out.println("ClienteThread: "+pecaJogador.size());
				//
				//					//Mostra as pe�as desse jogador
				//					for(int i=0; i<pecaJogador.size(); i++){
				//						System.out.println("ClienteThread: "+pecaJogador.get(i).getLadoEsquerdo()+"|"+pecaJogador.get(i).getLadoDireito());
				//					}
				//
				//					//Adiciona a parte das pe�as do jogador no Frame
				//					desenhoPecasDoJogador=new GuiDesenhaDomino(this.pecasJogador);
				//
				//					nomePecas = new JLabel("Minhas Pe�as: ");
				//					nomePecas.setBounds(0,420,150,30);
				//					painelDeConteudo.add(nomePecas);
				//
				//					desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecaJogador.size(), 210));
				//					painelPecasDoJogador = new JScrollPane(desenhoPecasDoJogador);
				//					painelPecasDoJogador.setBounds(0, 450, 750, 210);	
				//					painelPecasDoJogador.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				//					painelPecasDoJogador.addMouseListener(this);
				//					painelDeConteudo.add(painelPecasDoJogador);
				//
				//					desenhoDasPecasDaMesa.repaint();
				//					desenhoPecasDoJogador.repaint();
				//
				//
				//					//Atualiza o Frame
				//					painelDeConteudo.repaint();
				//
				//					frame.validate();
				//
				//					mostrarTexto=false;
				////	}

				//Esse jogador inicia o jogo, ou seja, ele pode enviar qualquer pe�a, j� que � a primeira pe�a
				////	if(rec.equals("primeiraPeca")){

				//					System.out.println("ClienteTread: Jogador "+nomeJogador+" ir� escolher primeira pe�a");
				//
				//					enviarPrimeiraPeca=true;
				//
				//					//Manda o servidor(TrataCliente) esperar por um objeto, ou seja, a pe�a escolhida
				//					enviarMsg("peca Escolhida");//trocou pelo de cima
				//
				//					System.out.println("ClienteTread: Jogador "+nomeJogador+" tem que selecionar um pe�a e o lado que deseja jogar");
				//					pecaSelecionada=-1;//isso para dizer que n�o foi selecionada nenhuma pe�a
				//					//Trata a atualiza��o da vari�vel no evento de mouse
				//					Boolean sairWhile=false;//erro de atualiza��o
				//					// Fica no while enquanto n�o selecionar a 1 pe�a
				//					while(pecaSelecionada<=-1||sairWhile==false){
				//
				//						if (pecaSelecionada>-1) {
				//							//out.writeUTF("recebaObjeto");
				//							System.out.println("Jogador "+nomeJogador+" valor Pe�a selecionada1: "+pecaSelecionada);
				//
				//							//Remove a pe�a selecionada pelo Jogador da lista de pe�as
				//							PecaDomino pecaAEnviar = pecasJogador.remove(pecaSelecionada);
				//
				//							System.out.println("Jogador "+nomeJogador+" ENVIAR� a Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
				//
				//							//Indica que a pe�a deve ser insirida no in�cio da lista de pe�as na mesa
				//							enviarMsg("Esquerdo");
				//
				//							envObjeto = new ObjectOutputStream(socket.getOutputStream());
				//							envObjeto.writeObject(pecaAEnviar);
				//
				//							//Volta a enviar String 
				//							dadosEnviados = new DataOutputStream(socket.getOutputStream());
				//
				//							//Diz que esse jogador n�o venceu o jogo, pois � o in�cio do jogo
				//							enviarMsg("N�o ganhou");
				//
				//							System.out.println("Jogador "+nomeJogador+" ENVIOU a Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
				//
				//							sairWhile=true;
				//
				//							enviarPrimeiraPeca=false;
				//
				//						}
				//					}
				//					System.out.println("Jogador "+nomeJogador+" valor Pe�a selecionada2: "+pecaSelecionada);
				//					pecaSelecionada=-1;//isso para deselecionar a pe�a
				//					System.out.println("ClienteThread: Jogador "+nomeJogador+" enviou 1 pe�a");
				//
				//					enviarMsg("Jogou uma pe�a para iniciar o jogo.");
				//
				//					mostrarTexto=false;
				////	}

				//Quando n�o possuir pe�a poss�vel de jogar, compra uma pe�a n�o utilizadas
				////if(rec.equals("Receber Uma Pe�a Puxada")){
				//					//Recebe uma nova Pe�a
				//					PecaDomino pecaPuxada=(PecaDomino)recObjeto.readObject();
				//					System.out.println("Jogador "+nomeJogador+" recebeu esta Pe�a puxando: "+ pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());
				//					//Volta para o canal que recebe String
				//					dadosRecebidos = new DataInputStream(socket.getInputStream());
				//					//Adiciona a pe�a que foi puxada ou comprada na lista de pe�as do jogador
				//					pecasJogador.add(pecaPuxada);
				//					//Aumenta o tamanho do scroll das pe�as dos jogadores
				//					desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecasJogador.size(), 210));
				//
				//					//Desenha a nova pe�a no JPanel
				//					desenhoPecasDoJogador.repaint();
				//
				//					desenhoDasPecasDaMesa.repaint();
				//
				//					painelDeConteudo.repaint();
				//
				//					frame.validate();
				//
				//					enviarMsg("Comprei uma pe�a.");
				//
				//					mostrarTexto=false;
			}

			//Atualiza as pe�as que est�o no centro da Mesa
			////	if(rec.equals("atualizar mesa")){
			//					//Recebe um ArrayList de Pe�as que est�o na mesa
			//					pecasDaMesa=receberPecasDaMesa();
			//					mostrarPecasDaMesa();//Imprime no console as pe�as recebidas					
			//					desenhoDasPecasDaMesa.setPecasDaMesa(pecasDaMesa);//Atualiza na parte gr�fica os novos valores
			//					//Aumenta o tamango do Scroll das pe�as da mesa
			//					desenhoDasPecasDaMesa.setPreferredSize(new Dimension (100+140*pecasDaMesa.size(), 300));
			//					//Desenha os novos valores de pe�as na mesa
			//					desenhoDasPecasDaMesa.repaint();
			//
			//					desenhoPecasDoJogador.repaint();
			//
			//					painelDeConteudo.repaint();
			//
			//					frame.validate();
			//
			//					mostrarTexto=false;
			////		}

			//No inicio de um novo jogo, habilitar todos bot�es de pronto
			////	if(rec.equals("Habilita bot�o de pronto")){
			//					jogar.setEnabled(true);
			//					mostrarTexto=false;
			//					pecasJogador=new ArrayList<PecaDomino>();
			////	}

			//Jogo trancado
			////	if(rec.equals("Jogo Trancado!")){
			//					int somaPecas=0;
			//					for(int i=0;i<pecasJogador.size();i++){
			//						somaPecas=somaPecas+pecasJogador.get(i).getLadoDireito()+pecasJogador.get(i).getLadoEsquerdo();
			//					}
			//					System.out.println("Somat�rio das pe�as desse jogador: "+somaPecas);
			//					//envia a quantidade da soma das pe�as ao servidor
			//					enviarMsg("Soma das pe�as");
			//					enviarMsg(""+somaPecas);
			//
			//					mostrarTexto=false;
			////		}

			//Vez do jogador enviar a sua pe�a escolhida no jogo, ou seja, chegou sua vez
			//				if(rec.equals("proximoAJogar")){
			//
			//					recObjeto = new ObjectInputStream(socket.getInputStream());  
			//					//Recebe as pontas das pe�as na mesa, ou seja, os valores poss�veis de jogar
			//					pecaPossivel=(PecaDomino)recObjeto.readObject();
			//					System.out.println("Jogador "+nomeJogador+" Recebeu as pontas");
			//					//Volta para o canal que recebe String
			//					dadosRecebidos = new DataInputStream(socket.getInputStream());
			//
			//					frame.validate();
			//
			//					//Vari�vel que indica se existe uma pe�a poss�vel a ser jogada
			//					Boolean existePecaPossivel=false;
			//					//Marca quais pe�as podem ser utilizadas em um array de Boolean
			//					indicesPecasPossiveis=new Boolean[pecasJogador.size()];
			//					//Verifica se existe pe�a poss�vel a jogar					
			//					for(int i=0; i<pecasJogador.size(); i++){
			//						//Verificando se o lado direito do domino do jogador � poss�vel
			//						if(pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoEsquerdo()){
			//							//Marca a pe�a poss�vel de jogar na mesa 
			//							pecasJogador.get(i).corPeca=Color.blue;
			//							existePecaPossivel=true;
			//							indicesPecasPossiveis[i]=true;
			//						}
			//						else{
			//							//Verificando se o lado esquerdo do domino do jogador � poss�vel
			//							if(pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoEsquerdo()){
			//								//Marca a pe�a poss�vel de jogar na mesa 
			//								pecasJogador.get(i).corPeca=Color.blue;
			//								existePecaPossivel=true;
			//								indicesPecasPossiveis[i]=true;
			//
			//							}else{
			//								//Marca a pe�a poss�vel de jogar na mesa 
			//								pecasJogador.get(i).corPeca=Color.black;
			//								indicesPecasPossiveis[i]=false;
			//							}
			//						}
			//					}
			//
			//					frame.validate();
			//
			//					//Pergunta se existe uma pe�a a ser jogada
			//					if(existePecaPossivel==true){
			//
			//
			//
			//						ladoAJogar="";
			//						pecaSelecionada=-1;//isso para dizer que n�o foi selecionada nenhuma pe�a
			//						//Trata a atualiza��o da vari�vel que cont�m a pe�a escolhida no evento de mouse
			//						Boolean sairWhile=false;
			//						while(pecaSelecionada<=-1||indicesPecasPossiveis[pecaSelecionada]==false||sairWhile==false){
			//							//posso colocar aqui ler strings e esperar at� receber pe�a escolhida
			//							//para tratar o chat que n�o recebe enquando n�o 
			//							//se a pe�a foi selecionada e tamb�m � poss�vel jog�-la e escolhido o lado
			//							String confirmacao=receberMsg();
			//							while (!confirmacao.equals("Continuar, pois a pe�a foi escolhida.")){
			//								areadetexto.append(confirmacao+"\n");
			//								areadetexto.setCaretPosition(areadetexto.getText().length());
			//								confirmacao=receberMsg();
			//							}
			//
			//							if (pecaSelecionada>-1 && indicesPecasPossiveis[pecaSelecionada]==true && 
			//									ladoCorreto(pecaPossivel)) {//verifica se o lado selecionado pode receber a pe�a
			//
			//								//Manda o servidor esperar que o jogador envie a pe�a escolhida
			//								dadosEnviados.writeUTF("peca Escolhida");
			//								dadosEnviados.flush();
			//
			//								//antes de enviar objeto dizer se ir� para direita ou esquerda
			//								//		ordenarPeca
			//
			//								//saber se o lado � poss�vel de jogar
			//								//envia o lado
			//
			//								PecaDomino pecaAEnviar =ordenarPeca(pecaPossivel,pecaSelecionada,ladoAJogar);
			//								enviarMsg(ladoAJogar);
			//
			//								envObjeto = new ObjectOutputStream(socket.getOutputStream());
			//
			//								System.out.println("Jogador "+nomeJogador+" ENVIAR� esta Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
			//								envObjeto.writeObject(pecaAEnviar);
			//								System.out.println("Jogador "+nomeJogador+" ENVIOU esta Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
			//
			//								//voltar a enviar String 
			//								dadosEnviados = new DataOutputStream(socket.getOutputStream());
			//
			//								//perguntar se esse jogador ganhou o jogo
			//								if(pecasJogador.isEmpty()){
			//									enviarMsg("Jogador Venceu essa rodada!");
			//								}else{
			//									enviarMsg("N�o ganhou");
			//
			//								}
			//
			//								sairWhile=true;
			//
			//								enviarPrimeiraPeca=false;
			//							}else{
			//								if(pecaSelecionada>-1&&indicesPecasPossiveis[pecaSelecionada]==false){
			//									JOptionPane.showMessageDialog(null,"Essa pe�a n�o pode ser utilizada! Utilize "+pecaPossivel.getLadoEsquerdo()+" ou "+pecaPossivel.getLadoDireito());
			//									pecaSelecionada=-1;
			//								}}
			//						}
			//						//zerar o lado e e pe�a selecionada
			//						pecaSelecionada=-1;//isso para deselecionar a pe�a
			//						ladoAJogar="";
			//						System.out.println("ClienteThread: "+nomeJogador+" enviou a pe�a: ");
			//					}
			//					else{//n�o existe pe�a poss�vel a ser jogada
			//						dadosEnviados.writeUTF("N�o existe pe�a poss�vel a ser jogada.");
			//						dadosEnviados.flush();
			//					}
			//
			//					//Quando terminar a jogada deixar todas as pe�as pretas					
			//					for (int i = 0; i < pecasJogador.size(); i++) {
			//						pecasJogador.get(i).corPeca=Color.black;
			//					}
			//
			//					desenhoDasPecasDaMesa.repaint();
			//					desenhoPecasDoJogador.repaint();
			//
			//					painelDeConteudo.repaint();
			//
			//					frame.validate();
			//
			//					mostrarTexto=false;
			//
			//					//N�o pintar a pe�a de amarelo
			//					indicesPecasPossiveis=new Boolean[pecasJogador.size()];
			//					//Verifica se existe pe�a poss�vel a jogar					
			//					for(int i=0; i<pecasJogador.size(); i++){
			//						indicesPecasPossiveis[i]=false;
			//					}
			//
			//
			//				}
			//				//Receber Objeto
			//				if(mostrarTexto==true){
			//					areadetexto.append(rec+"\n");
			//					areadetexto.setCaretPosition(areadetexto.getText().length());
			//				}
			//				System.out.println("ClienteThread: Jogador "+nomeJogador+" escreveu no textArea - "+rec);
			//
			//				//Servidor com limite de jogadores 
			//				if(rec.equals("Servidor j� est� lotado!")){
			//					JOptionPane.showMessageDialog(null,"Servidor j� est� lotado!");
			//					System.exit(1); }
			//
			//				//Jogo Iniciado
			//				if(rec.equals("Jogo iniciado e nao pode entrar!")){
			//					JOptionPane.showMessageDialog(null,"Jogo j� foi iniciado, n�o pode adicionar novos jogadores!");
			//					System.exit(1); }				
			//
			//			}

			System.out.println("ClienteThread: "+nomeJogador+" fechou sua conex�o");

			//fecha os canais de sa�da e entrada
			////dadosEnviados.close();
			////dadosRecebidos.close();
			//fecha o socket
			////socket.close();



		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Mostrar no console as pe�as  que est�o na Mesa, ou seja, foram utilizadas
	public void mostrarPecasDaMesa(){
		System.out.println("ClienteThread: INICIO Pe�as que est�o no centro da mesa: ");
		//mostrar as pe�as de um jogador
		for(int i=0; i<pecasDaMesa.size(); i++){
			System.out.print(" "+pecasDaMesa.get(i).getLadoEsquerdo()+"|"+pecasDaMesa.get(i).getLadoDireito());
		}
		System.out.println();
		System.out.println("ClienteThread: FIM Pe�as que est�o no centro da mesa: ");
	}

	//Receber pe�as j� utilizadas e est�o no centro da mesa
	//	public ArrayList <PecaDomino> receberPecasDaMesa(){
	//
	//		ArrayList <PecaDomino> pecaDaMesaLocal=null;
	//		//j� tem que est� pronto para para receber antes deles enviarem
	//		try {
	//			recObjeto = new ObjectInputStream(socket.getInputStream());
	//			//valores que podem ser jogados
	//			pecaDaMesaLocal=(ArrayList <PecaDomino>)recObjeto.readObject();
	//			System.out.println("Jogador "+nomeJogador+" Recebeu as pe�as da Mesa");
	//			//voltar para o canal de string
	//			dadosRecebidos = new DataInputStream(socket.getInputStream());
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		} catch (ClassNotFoundException e) {
	//			e.printStackTrace();
	//		}  
	//
	//		return pecaDaMesaLocal;
	//	}

	//Verifica se o lado selecionado � poss�vel jogar
	public Boolean ladoCorreto(PecaDomino p){


		if(ladoAJogar.equals("Esquerdo")){
			JOptionPane.showMessageDialog(null,"Voc� escolheu lado "+ladoAJogar);
			if(pecasJogador.get(pecaSelecionada).getLadoDireito()==p.getLadoEsquerdo()||
					pecasJogador.get(pecaSelecionada).getLadoEsquerdo()==p.getLadoEsquerdo()){
				return true;
			}
			else{
				ladoAJogar="";
				JOptionPane.showMessageDialog(null,"N�o pode jogar no lado Esquerdo!");}
			return false;
		}
		else{
			if(ladoAJogar.equals("Direito")){
				JOptionPane.showMessageDialog(null,"Voc� escolheu lado "+ladoAJogar);
				if(pecasJogador.get(pecaSelecionada).getLadoDireito()==p.getLadoDireito()||
						pecasJogador.get(pecaSelecionada).getLadoEsquerdo()==p.getLadoDireito()){
					return true;
				}
				else{
					ladoAJogar="";
					JOptionPane.showMessageDialog(null,"N�o pode jogar no lado Direito!");}
				return false;
			}
			else{//lado nao selecionado
				return false;
			}
		}
	}

	//Recebe as pe�as que est�o no centro mesa, ou seja, j� foram utlizadas pelos jogadores
	//	public void setPecasDaMesa(){
	//		try {
	//			recObjeto = new ObjectInputStream(socket.getInputStream());  
	//			pecasDaMesa=(ArrayList)recObjeto.readObject();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		} catch (ClassNotFoundException e) {
	//			e.printStackTrace();
	//		}		
	//	}

	//Vira a pe�a para deixar do lado correto, ou seja, pelo menos um lado jog�vel
	public PecaDomino ordenarPeca(PecaDomino pecaPo, int indice, String lado){
		//procura na lista a pe�a selecionada pelo jogador
		PecaDomino pecaAEnviarAoServidor = pecasJogador.remove(indice);

		//Pe�a invertida
		if(lado.equals("Esquerdo")&&pecaAEnviarAoServidor.getLadoDireito()!=pecaPo.getLadoEsquerdo() ){
			//mandar o jogador escolher lado
			int troca=pecaAEnviarAoServidor.getLadoDireito();
			pecaAEnviarAoServidor.setLadoDireito(pecaAEnviarAoServidor.getLadoEsquerdo());
			pecaAEnviarAoServidor.setLadoEsquerdo(troca);

		}
		else{//para tratar pe�a com dois lados iguais e vem uma invertida
			if(lado.equals("Direito")&&pecaAEnviarAoServidor.getLadoEsquerdo()!= pecaPo.getLadoDireito()){
				int troca=pecaAEnviarAoServidor.getLadoDireito();
				pecaAEnviarAoServidor.setLadoDireito(pecaAEnviarAoServidor.getLadoEsquerdo());
				pecaAEnviarAoServidor.setLadoEsquerdo(troca);
			}
		}
		return pecaAEnviarAoServidor;
	}

	//Recebe uma String do Servidor
	//	public String receberMsg(){
	//		String valorRecebido=null;
	//		try {
	//			valorRecebido = dadosRecebidos.readUTF();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//		return valorRecebido;
	//	}

	//Envia uma String ao Servidor
	//	public void enviarMsg(String str){
	//		try {
	//			dadosEnviados.writeUTF(str);
	//			dadosEnviados.flush();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}

	//colocar as 7 pe�as do in�cio do jogo que foram recebidas do servidor
	public void setPecasJogador(ArrayList<PecaDomino> Jogador) {
		this.pecasJogador = Jogador;
	}

	//Mostra as pe�as deste jogador
	void mostrarPecas(){
		System.out.println("ClienteThread: Domino jogador "+nomeJogador);
		//mostrar as pe�as de um jogador
		for(int i=0; i<pecasJogador.size(); i++){
			System.out.println("ClienteThread: "+pecasJogador.get(i).getLadoEsquerdo()+"|"+pecasJogador.get(i).getLadoDireito());
		}
		desenharPecas();
	}

	//Desenha Graficamento as pe�as
	void desenharPecas(){
		System.out.println("ClienteThread: pintando domino");
		System.out.println("ClienteThread: "+pecasJogador.size());
		for(int i=0; i<pecasJogador.size(); i++){
			System.out.println("ClienteThread: "+pecasJogador.get(i).getLadoEsquerdo()+"|"+pecasJogador.get(i).getLadoDireito());

		}

		desenhoDasPecasDaMesa.repaint();
		desenhoPecasDoJogador.repaint();

		painelDeConteudo.repaint();

		frame.validate();
	}

	//Colore as pe�as poss�veis de jogar de vermelhor
	public void colorePecasPossiveis(){

		for(int i=0; i<pecasJogador.size(); i++){
			if(pecaPossivel==null){
				//Marca a pe�a poss�vel de jogar na mesa 
				pecasJogador.get(i).corPeca=Color.black;
			}
			else{
				//Verificando se o lado direito do domino do jogador � poss�vel
				if(pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoEsquerdo()){
					//Marca a pe�a poss�vel de jogar na mesa 
					pecasJogador.get(i).corPeca=Color.blue;			
				}
				else{
					//Verificando se o lado esquerdo do domino do jogador � poss�vel
					if(pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoEsquerdo()){
						//Marca a pe�a poss�vel de jogar na mesa 
						pecasJogador.get(i).corPeca=Color.blue;					

					}else{
						//Marca a pe�a poss�vel de jogar na mesa 
						pecasJogador.get(i).corPeca=Color.black;
					}
				}
			}
		}
		frame.validate();
	}

	//Trata o evento dos bot�es
	public void actionPerformed(ActionEvent e){

		//se o bot�o de pronto tiver selecionado, ent�o envia a confirma��o ao servidor
		//que coloca o valor em uma variavel no trataCliente e quantos todos tiver confirmado come�a o jogo
		if(e.getSource()==jogar){
			//desabilitar o check box que confirma se o jogador est� pronto
			jogar.setEnabled(false);
			////enviarMsg("Estou pronto para receber pe�as!");
			try {
				serv.enviaATodosClientes("Estou pronto para receber pe�as!");
				serv.prontoParaJogar(true,nomeJogador);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			areadetexto.append("Estou pronto para receber pe�as!\n");
			areadetexto.setCaretPosition(areadetexto.getText().length());
		}

		//enviar mensagem aos outros jogadores
		if(e.getSource() == enviar){
			String valorTextField=textField.getText(); 
			textField.setText("");
			//enviar uma string

			System.out.println("ClienteThread: "+nomeJogador+" enviou ao servidor o campo - "+valorTextField);
			////dadosEnviados.writeUTF(valorTextField);
			////dadosEnviados.flush();
			try {
				serv.enviaATodosClientes(nomeJogador+": "+valorTextField);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		}

		//fechar o jogo e conex�o com o servidor
		if(e.getSource()==itemSair){

			//colocar para fechar a conex�o antes de sair
			System.out.println("ClienteThread: Jogador "+nomeJogador+" fechou sua conex�o");

			//fecha os canais de sa�da e entrada
			try {
				////dadosEnviados.writeUTF("close");
				serv.enviaATodosClientes(nomeJogador+": Fechei minha janela!");


			} catch (IOException e1) {
				e1.printStackTrace();
			}

			//fechar janela, ou seja, Frame
			System.exit(1);  
		}

		//mostrar o nome dos autores do Jogo
		if(e.getSource() == autores){
			JOptionPane.showMessageDialog(null,"Wandemberg Rodrigues Gomes");
		}		

	}

	//Trata o evento gerado no JPanel, ou seja, a pe�a selecionada e direita e esquerda 
	public void mouseClicked(MouseEvent e) {
		System.out.println("ClienteThread: posi��o onde foi clicado:");
		System.out.println("ClienteThread: X: "+e.getX()+", Y: "+e.getY());

		//depois de achar a pe�a enviar ao servidor
		int horizontal = painelPecasDoJogador.getHorizontalScrollBar().getModel().getValue(); 
		for(int n=1;n<=pecasJogador.size();n++){
			if(e.getX()+horizontal>(10+90*(n-1))&&e.getX()+horizontal<(90*n)&&e.getY()>10&&e.getY()<130){
				//JOptionPane.showMessageDialog(null,"Clicou pe�a "+n);
				pecaSelecionada=n-1;

				colorePecasPossiveis();
				//Muda a cor da pe�a clicada para amarelo
				if(indicesPecasPossiveis!=null&&pecaSelecionada>-1&&indicesPecasPossiveis[pecaSelecionada]==true){
					pecasJogador.get(pecaSelecionada).corPeca=Color.red;
				}

				desenhoDasPecasDaMesa.repaint();
				desenhoPecasDoJogador.repaint();

				painelDeConteudo.repaint();

			}
		}

		System.out.println("Jogador "+nomeJogador+" selecionou: "+pecaSelecionada);
		System.out.println("Jogador "+nomeJogador+" selecionou lado: "+ladoAJogar);		

	}


	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e)  {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e){}


	public static void main(String[] args) {

		Thread cliente1;
		try {
			cliente1 = new Thread(new ClienteThread());			
			cliente1.start();			
		} 
		catch (RemoteException e) {
			e.printStackTrace();
		}



	}

	@Override
	public ArrayList<PecaDomino> pecasJogador() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recebeString(String a) throws RemoteException {
		System.out.println(a);
	}

	public void recebeMensagemDoChat(String s) throws RemoteException {
		areadetexto.append(s+"\n");
		areadetexto.setCaretPosition(areadetexto.getText().length());
	}

	@Override
	public void recebeUmaPeca(PecaDomino p) throws RemoteException {
		//Recebe uma nova Pe�a
		////PecaDomino pecaPuxada=(PecaDomino)recObjeto.readObject();

		PecaDomino pecaPuxada=p;

		System.out.println("Cliente: "+nomeJogador+" recebeu esta Pe�a puxando: "+ pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());
		//Volta para o canal que recebe String
		////dadosRecebidos = new DataInputStream(socket.getInputStream());

		//Adiciona a pe�a que foi puxada ou comprada na lista de pe�as do jogador
		pecasJogador.add(pecaPuxada);
		//Aumenta o tamanho do scroll das pe�as dos jogadores
		desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecasJogador.size(), 210));

		//Desenha a nova pe�a no JPanel
		desenhoPecasDoJogador.repaint();

		desenhoDasPecasDaMesa.repaint();

		painelDeConteudo.repaint();

		frame.validate();

		////enviarMsg("Comprei uma pe�a.");

		serv.enviaATodosClientes(nomeJogador+": Comprei uma pe�a.");

		////mostrarTexto=false;

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

	//M�todo onde o servidor passa as 7 pe�as ao cliente
	public void recebe7Pecas(ArrayList<PecaDomino> pecas) throws RemoteException {

		System.out.println("Cliente: Recebeu a lista com as pe�as");
		//Atualiza os valores reais das pe�as deste jogador
		pecasJogador=(ArrayList<PecaDomino>)pecas;
		System.out.println("Cliente: Atualizou as pe�as do Jogador");

		System.out.println("Cliente: "+nomeJogador+" recebeu seu domino");
		System.out.println("Cliente: "+pecas.size());

		//Mostra as pe�as desse jogador
		for(int i=0; i<pecas.size(); i++){
			System.out.println("ClienteThread: "+pecas.get(i).getLadoEsquerdo()+"|"+pecas.get(i).getLadoDireito());
		}

		//Adiciona a parte das pe�as do jogador no Frame
		desenhoPecasDoJogador=new GuiDesenhaDomino(pecasJogador);

		nomePecas = new JLabel("Minhas Pe�as: ");
		nomePecas.setBounds(0,420,150,30);
		painelDeConteudo.add(nomePecas);

		desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecas.size(), 210));
		painelPecasDoJogador = new JScrollPane(desenhoPecasDoJogador);
		painelPecasDoJogador.setBounds(0, 450, 750, 210);	
		painelPecasDoJogador.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		painelPecasDoJogador.addMouseListener(this);
		painelDeConteudo.add(painelPecasDoJogador);

		desenhoDasPecasDaMesa.repaint();
		desenhoPecasDoJogador.repaint();

		//Atualiza o Frame
		painelDeConteudo.repaint();

		frame.validate();


	}

	@Override
	public PecaDomino jogaPrimeiraPeca() throws RemoteException {

		System.out.println("Cliente: "+nomeJogador+" ir� escolher primeira pe�a");

		enviarPrimeiraPeca=true;

		//Manda o servidor(TrataCliente) esperar por um objeto, ou seja, a pe�a escolhida
		////enviarMsg("peca Escolhida");//trocou pelo de cima


		PecaDomino pecaAEnviar = null ;
		System.out.println("Cliente: "+nomeJogador+" tem que selecionar um pe�a e o lado que deseja jogar");
		pecaSelecionada=-1;//isso para dizer que n�o foi selecionada nenhuma pe�a
		//Trata a atualiza��o da vari�vel no evento de mouse
		Boolean sairWhile=false;//erro de atualiza��o
		// Fica no while enquanto n�o selecionar a 1 pe�a
		while(pecaSelecionada<=-1||sairWhile==false){

			if (pecaSelecionada>-1) {
				//out.writeUTF("recebaObjeto");
				System.out.println("Cliente: "+nomeJogador+" valor do �ndice da Pe�a selecionada: "+pecaSelecionada);

				//Remove a pe�a selecionada pelo Jogador da lista de pe�as
				pecaAEnviar = pecasJogador.remove(pecaSelecionada);

				System.out.println("Cliente: "+nomeJogador+" ENVIAR� a Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

				//Indica que a pe�a deve ser insirida no in�cio da lista de pe�as na mesa
				///enviarMsg("Esquerdo");


				//criar um m�todo que implementa o lado escolhido
				serv.recebeLadoAJogar("Esquerdo");

				System.out.println("Cliente: "+nomeJogador+" enviou o lado onde deve ser add a Pe�a: Esquerdo");

				////envObjeto = new ObjectOutputStream(socket.getOutputStream());
				////envObjeto.writeObject(pecaAEnviar);

				//Volta a enviar String 
				////dadosEnviados = new DataOutputStream(socket.getOutputStream());

				//Diz que esse jogador n�o venceu o jogo, pois � o in�cio do jogo
				////enviarMsg("N�o ganhou");

				serv.ganhouJogo("N�o ganhou",nomeJogador);

				System.out.println("Cliente: "+nomeJogador+" ENVIOU a Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

				sairWhile=true;

				enviarPrimeiraPeca=false;

			}
		}
		System.out.println("Cliente: "+nomeJogador+" valor do �ndice da Pe�a: "+pecaSelecionada);
		pecaSelecionada=-1;//isso para deselecionar a pe�a
		System.out.println("Cliente: "+nomeJogador+" enviou 1 pe�a");

		////enviarMsg("Jogou uma pe�a para iniciar o jogo.");
		serv.enviaATodosClientes("Jogou uma pe�a para iniciar o jogo.");

		//descolorir pe�as poss�veis
		pecaPossivel=null;

		return pecaAEnviar;
	}


	public PecaDomino jogaProximaPeca(PecaDomino pecaPoss) throws RemoteException {
		////recObjeto = new ObjectInputStream(socket.getInputStream());  
		//Recebe as pontas das pe�as na mesa, ou seja, os valores poss�veis de jogar
		////pecaPossivel=(PecaDomino)recObjeto.readObject();

		pecaPossivel=pecaPoss;

		PecaDomino pecaAEnviar=null;

		System.out.println("Cliente: "+nomeJogador+" Recebeu as pontas");
		//Volta para o canal que recebe String
		////dadosRecebidos = new DataInputStream(socket.getInputStream());

		frame.validate();

		//Vari�vel que indica se existe uma pe�a poss�vel a ser jogada
		Boolean existePecaPossivel=false;
		//Marca quais pe�as podem ser utilizadas em um array de Boolean
		indicesPecasPossiveis=new Boolean[pecasJogador.size()];
		//Verifica se existe pe�a poss�vel a jogar					
		for(int i=0; i<pecasJogador.size(); i++){
			//Verificando se o lado direito do domino do jogador � poss�vel
			if(pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoEsquerdo()){
				//Marca a pe�a poss�vel de jogar na mesa 
				pecasJogador.get(i).corPeca=Color.blue;
				existePecaPossivel=true;
				indicesPecasPossiveis[i]=true;
			}
			else{
				//Verificando se o lado esquerdo do domino do jogador � poss�vel
				if(pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoEsquerdo()){
					//Marca a pe�a poss�vel de jogar na mesa 
					pecasJogador.get(i).corPeca=Color.blue;
					existePecaPossivel=true;
					indicesPecasPossiveis[i]=true;

				}else{
					//Marca a pe�a poss�vel de jogar na mesa 
					pecasJogador.get(i).corPeca=Color.black;
					indicesPecasPossiveis[i]=false;
				}
			}
		}

		frame.validate();

		//Pergunta se existe uma pe�a a ser jogada
		if(existePecaPossivel==true){



			ladoAJogar="";
			pecaSelecionada=-1;//isso para dizer que n�o foi selecionada nenhuma pe�a
			//Trata a atualiza��o da vari�vel que cont�m a pe�a escolhida no evento de mouse
			Boolean sairWhile=false;
			while(pecaSelecionada<=-1||indicesPecasPossiveis[pecaSelecionada]==false||sairWhile==false){
				//posso colocar aqui ler strings e esperar at� receber pe�a escolhida
				//para tratar o chat que n�o recebe enquando n�o 
				//se a pe�a foi selecionada e tamb�m � poss�vel jog�-la e escolhido o lado

				////era pro chat o que est� em baixo
				//				String confirmacao=receberMsg();
				//				while (!confirmacao.equals("Continuar, pois a pe�a foi escolhida.")){
				//					areadetexto.append(confirmacao+"\n");
				//					areadetexto.setCaretPosition(areadetexto.getText().length());
				//					confirmacao=receberMsg();
				//				}

				if (pecaSelecionada>-1 && indicesPecasPossiveis[pecaSelecionada]==true && 
						ladoCorreto(pecaPossivel)) {//verifica se o lado selecionado pode receber a pe�a

					//Manda o servidor esperar que o jogador envie a pe�a escolhida
					////dadosEnviados.writeUTF("peca Escolhida");
					////dadosEnviados.flush();

					//antes de enviar objeto dizer se ir� para direita ou esquerda
					//		ordenarPeca

					//saber se o lado � poss�vel de jogar
					//envia o lado

					pecaAEnviar =ordenarPeca(pecaPossivel,pecaSelecionada,ladoAJogar);

					////enviarMsg(ladoAJogar);
					serv.recebeLadoAJogar(ladoAJogar);


					////envObjeto = new ObjectOutputStream(socket.getOutputStream());

					System.out.println("Clienete: "+nomeJogador+" ENVIAR� esta Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
					////envObjeto.writeObject(pecaAEnviar);

					System.out.println("Cliente: "+nomeJogador+" ENVIOU esta Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

					//voltar a enviar String 
					////dadosEnviados = new DataOutputStream(socket.getOutputStream());

					//perguntar se esse jogador ganhou o jogo
					if(pecasJogador.isEmpty()){
						////enviarMsg("Jogador Venceu essa rodada!");
						serv.ganhouJogo("venceu essa rodada!",nomeJogador);
					}else{
						////enviarMsg("N�o ganhou");
						serv.ganhouJogo("N�o ganhou",nomeJogador);
					}

					sairWhile=true;

					enviarPrimeiraPeca=false;
				}else{
					if(pecaSelecionada>-1&&indicesPecasPossiveis[pecaSelecionada]==false){
						JOptionPane.showMessageDialog(null,"Essa pe�a n�o pode ser utilizada! Utilize "+pecaPossivel.getLadoEsquerdo()+" ou "+pecaPossivel.getLadoDireito());
						pecaSelecionada=-1;
					}}
			}
			//zerar o lado e e pe�a selecionada
			pecaSelecionada=-1;//isso para deselecionar a pe�a
			ladoAJogar="";
			System.out.println("ClienteThread: "+nomeJogador+" enviou a pe�a: ");

		}
		else{
			//puxar pe�a
		}

		//descolorir pe�as poss�veis
		pecaPossivel=null;
		indicesPecasPossiveis=null;

		return pecaAEnviar;
	}

	public void puxaUmaPeca(PecaDomino p) throws RemoteException {
		pecasJogador.add(p);
	}

	public void atualizaGUI(ArrayList <PecaDomino> pecaDaMesaLocal) throws RemoteException {
		//Recebe um ArrayList de Pe�as que est�o na mesa

		System.out.println("Cliente: "+nomeJogador+" Recebeu as pe�as da Mesa");

		pecasDaMesa=pecaDaMesaLocal;

		mostrarPecasDaMesa();//Imprime no console as pe�as recebidas	

		desenhoDasPecasDaMesa.setPecasDaMesa(pecasDaMesa);//Atualiza na parte gr�fica os novos valores
		//Aumenta o tamango do Scroll das pe�as da mesa
		desenhoDasPecasDaMesa.setPreferredSize(new Dimension (100+140*pecasDaMesa.size(), 300));
		//Desenha os novos valores de pe�as na mesa
		desenhoDasPecasDaMesa.repaint();

		desenhoPecasDoJogador.repaint();

		painelDeConteudo.repaint();

		frame.validate();

		////mostrarTexto=false;

	}


	public void habilitaPronto() throws RemoteException {
		jogar.setEnabled(true);
		////mostrarTexto=false;
		pecasJogador=new ArrayList<PecaDomino>();
	}

	@Override
	public int jogoTrancado() throws RemoteException {
		int somaPecas=0;
		for(int i=0;i<pecasJogador.size();i++){
			somaPecas=somaPecas+pecasJogador.get(i).getLadoDireito()+pecasJogador.get(i).getLadoEsquerdo();
		}
		System.out.println("Somat�rio das pe�as desse jogador: "+somaPecas);
		//envia a quantidade da soma das pe�as ao servidor
		////enviarMsg("Soma das pe�as");
		serv.enviaATodosClientes(nomeJogador+": Soma das minhas pe�as = "+somaPecas);

		////enviarMsg(""+somaPecas);

		////mostrarTexto=false;

		return somaPecas;
	}

	@Override
	public void servidorLotador(Boolean a) throws RemoteException {
		servidorLotado=a;		
	}

}