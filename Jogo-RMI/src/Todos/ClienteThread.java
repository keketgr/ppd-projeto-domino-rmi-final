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

	//Porta que o servidor está usando
	////int portaUtilizada;

	//Lado que o jogador clicou para colocar peça
	String ladoAJogar="";

	//Peça selecionada pelo mouse
	int pecaSelecionada=-1;

	//Indica se deve adicionar a mensagem no textArea
	////Boolean mostrarTexto=true;

	//Peças do jogador, ou seja, peças que podem ser utilizadas durante o jogo
	private ArrayList <PecaDomino> pecasJogador;
	//Peças que estão no centro da mesa, ou seja, peças que os jogadores utilizaram no jogo 
	private ArrayList <PecaDomino> pecasDaMesa;

	//Classe que tem função de desenhar as peças do jogador
	GuiDesenhaDomino desenhoPecasDoJogador;
	//Barra de rolagem das peças no centro da mesa
	JScrollPane painelPecasDoJogador;

	//Classe que tem função de desenhar as peças que estão no centro da mesa
	GuiDesenhaPecasDaMesa desenhoDasPecasDaMesa=new GuiDesenhaPecasDaMesa(this);
	//Barra de rolagem das peças no centro da mesa
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
	//Check com a função de confirmar que o jogador está pronto para jogar
	private JCheckBox jogar;
	private JLabel confirmar;
	private JLabel nomePecas; //texto MINHAS PEÇAS 

	String nomeJogador="Jogador1";

	//para receber Objetos
	//Stream que recebe Objetos
	////ObjectInputStream recObjeto;
	//Stream que envia Objetos
	////ObjectOutputStream envObjeto;

	Boolean enviarPrimeiraPeca =false;

	//Marca quais peças podem ser utilizadas em um array de Boolean
	Boolean [] indicesPecasPossiveis;

	//Pontas do jogo, ou seja, lados possíveis de jogar
	PecaDomino pecaPossivel;	

	//Comunicação com servidor
	InterfaceDoServidor serv;

	Boolean servidorLotado=false;

	public ClienteThread() throws RemoteException{

		super();

		pecasJogador=new ArrayList <PecaDomino>();

		pecasDaMesa=new ArrayList <PecaDomino>();

		//Recebe o nome do usuário que ficará no título do Frame
		nomeJogador=JOptionPane.showInputDialog("Escolha o nome do usuário","jogador");
		//Coloca os valores do ip e porta do servidor que irá conectar
		nomeServidor=JOptionPane.showInputDialog("Escolha o nome do servidor","Servidor");
		////portaUtilizada=Integer.parseInt(JOptionPane.showInputDialog("Escolha o Porta do servidor","5080"));

		//Cria o socket, ou seja, faz a conexão com o servidor
		try {
			//criando a conexão com o servidor
			////socket = new Socket (ipServidor, portaUtilizada);

			System.out.println("Registrara o cliente.");
			Naming.rebind(nomeJogador,(Remote) this);
			System.out.println("Cliente Registrado!");

			serv =  (InterfaceDoServidor)Naming.lookup("//localhost/"+nomeServidor);


			//Método que cria a interface gráfica do jogador
			criarGUI();

			//Executar a comunicação, ou seja, inicia a Thread
			//this.start();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Erro em tentar conectar ao servidor!");
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	//Cria a parte Gráfica da parte do cliente ou Jogador
	public void criarGUI() {

		//Criar e configurar a janela
		frame = new JFrame(nomeJogador);
		frame.setSize(1200, 700);
		//não redimensionar
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

		//Botão que enviaa as informações do chat ao servidor
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

		//campo que contém as mensagens enviadas e recebidas
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

		//Desenha Peças que estão na mesa		
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

			//Coloca no servidor o nome do usuário ou jogador
			////dadosEnviados.writeUTF(nomeJogador);
			////dadosEnviados.flush();

			serv.login(nomeJogador);


			//Variável que contém a string recebida do servidor
			////String rec="";

			//Condição fechar esse jogodor, ou seja, quando o jogador desejar fechar a sua interface gráfica
			while(!teclado.equals("close")&&servidorLotado==false){

				////mostrarTexto=true;

				//System.out.println("ClienteThread: Esperando para ler String");
				//Recebe uma nova string do servidor
				////rec=receberMsg();
				////System.out.println("ClienteThread: Recebeu a string "+rec);



				//desconsiderar mensagem repetidas
				////if(rec.equals("Continuar, pois a peça foi escolhida.")){
				////mostrarTexto=false;
				////}

				//Início do jogo e o jogador irá receber suas peças que é um objeto
				////if(rec.equals("Receber Lista de Peças")){

				//					//Recebe um ArrayList com as peças desse jogador
				//					recObjeto = new ObjectInputStream(socket.getInputStream());  
				//					ArrayList<PecaDomino> pecaJogador=(ArrayList)recObjeto.readObject();
				//					System.out.println("ClienteThread: Recebeu a lista com as peças");
				//
				//					//Volta para o canal de de receber String
				//					dadosRecebidos = new DataInputStream(socket.getInputStream());
				//
				//					//Atualiza os valores reais das peças deste jogador
				//					pecasJogador=pecaJogador;
				//					System.out.println("ClienteThread: Atualizou as peças do Jogador");
				//
				//					System.out.println("ClienteThread: Jogador "+nomeJogador+" recebeu seu domino");
				//					System.out.println("ClienteThread: "+pecaJogador.size());
				//
				//					//Mostra as peças desse jogador
				//					for(int i=0; i<pecaJogador.size(); i++){
				//						System.out.println("ClienteThread: "+pecaJogador.get(i).getLadoEsquerdo()+"|"+pecaJogador.get(i).getLadoDireito());
				//					}
				//
				//					//Adiciona a parte das peças do jogador no Frame
				//					desenhoPecasDoJogador=new GuiDesenhaDomino(this.pecasJogador);
				//
				//					nomePecas = new JLabel("Minhas Peças: ");
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

				//Esse jogador inicia o jogo, ou seja, ele pode enviar qualquer peça, já que é a primeira peça
				////	if(rec.equals("primeiraPeca")){

				//					System.out.println("ClienteTread: Jogador "+nomeJogador+" irá escolher primeira peça");
				//
				//					enviarPrimeiraPeca=true;
				//
				//					//Manda o servidor(TrataCliente) esperar por um objeto, ou seja, a peça escolhida
				//					enviarMsg("peca Escolhida");//trocou pelo de cima
				//
				//					System.out.println("ClienteTread: Jogador "+nomeJogador+" tem que selecionar um peça e o lado que deseja jogar");
				//					pecaSelecionada=-1;//isso para dizer que não foi selecionada nenhuma peça
				//					//Trata a atualização da variável no evento de mouse
				//					Boolean sairWhile=false;//erro de atualização
				//					// Fica no while enquanto não selecionar a 1 peça
				//					while(pecaSelecionada<=-1||sairWhile==false){
				//
				//						if (pecaSelecionada>-1) {
				//							//out.writeUTF("recebaObjeto");
				//							System.out.println("Jogador "+nomeJogador+" valor Peça selecionada1: "+pecaSelecionada);
				//
				//							//Remove a peça selecionada pelo Jogador da lista de peças
				//							PecaDomino pecaAEnviar = pecasJogador.remove(pecaSelecionada);
				//
				//							System.out.println("Jogador "+nomeJogador+" ENVIARÁ a Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
				//
				//							//Indica que a peça deve ser insirida no início da lista de peças na mesa
				//							enviarMsg("Esquerdo");
				//
				//							envObjeto = new ObjectOutputStream(socket.getOutputStream());
				//							envObjeto.writeObject(pecaAEnviar);
				//
				//							//Volta a enviar String 
				//							dadosEnviados = new DataOutputStream(socket.getOutputStream());
				//
				//							//Diz que esse jogador não venceu o jogo, pois é o início do jogo
				//							enviarMsg("Não ganhou");
				//
				//							System.out.println("Jogador "+nomeJogador+" ENVIOU a Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
				//
				//							sairWhile=true;
				//
				//							enviarPrimeiraPeca=false;
				//
				//						}
				//					}
				//					System.out.println("Jogador "+nomeJogador+" valor Peça selecionada2: "+pecaSelecionada);
				//					pecaSelecionada=-1;//isso para deselecionar a peça
				//					System.out.println("ClienteThread: Jogador "+nomeJogador+" enviou 1 peça");
				//
				//					enviarMsg("Jogou uma peça para iniciar o jogo.");
				//
				//					mostrarTexto=false;
				////	}

				//Quando não possuir peça possível de jogar, compra uma peça não utilizadas
				////if(rec.equals("Receber Uma Peça Puxada")){
				//					//Recebe uma nova Peça
				//					PecaDomino pecaPuxada=(PecaDomino)recObjeto.readObject();
				//					System.out.println("Jogador "+nomeJogador+" recebeu esta Peça puxando: "+ pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());
				//					//Volta para o canal que recebe String
				//					dadosRecebidos = new DataInputStream(socket.getInputStream());
				//					//Adiciona a peça que foi puxada ou comprada na lista de peças do jogador
				//					pecasJogador.add(pecaPuxada);
				//					//Aumenta o tamanho do scroll das peças dos jogadores
				//					desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecasJogador.size(), 210));
				//
				//					//Desenha a nova peça no JPanel
				//					desenhoPecasDoJogador.repaint();
				//
				//					desenhoDasPecasDaMesa.repaint();
				//
				//					painelDeConteudo.repaint();
				//
				//					frame.validate();
				//
				//					enviarMsg("Comprei uma peça.");
				//
				//					mostrarTexto=false;
			}

			//Atualiza as peças que estão no centro da Mesa
			////	if(rec.equals("atualizar mesa")){
			//					//Recebe um ArrayList de Peças que estão na mesa
			//					pecasDaMesa=receberPecasDaMesa();
			//					mostrarPecasDaMesa();//Imprime no console as peças recebidas					
			//					desenhoDasPecasDaMesa.setPecasDaMesa(pecasDaMesa);//Atualiza na parte gráfica os novos valores
			//					//Aumenta o tamango do Scroll das peças da mesa
			//					desenhoDasPecasDaMesa.setPreferredSize(new Dimension (100+140*pecasDaMesa.size(), 300));
			//					//Desenha os novos valores de peças na mesa
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

			//No inicio de um novo jogo, habilitar todos botões de pronto
			////	if(rec.equals("Habilita botão de pronto")){
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
			//					System.out.println("Somatório das peças desse jogador: "+somaPecas);
			//					//envia a quantidade da soma das peças ao servidor
			//					enviarMsg("Soma das peças");
			//					enviarMsg(""+somaPecas);
			//
			//					mostrarTexto=false;
			////		}

			//Vez do jogador enviar a sua peça escolhida no jogo, ou seja, chegou sua vez
			//				if(rec.equals("proximoAJogar")){
			//
			//					recObjeto = new ObjectInputStream(socket.getInputStream());  
			//					//Recebe as pontas das peças na mesa, ou seja, os valores possíveis de jogar
			//					pecaPossivel=(PecaDomino)recObjeto.readObject();
			//					System.out.println("Jogador "+nomeJogador+" Recebeu as pontas");
			//					//Volta para o canal que recebe String
			//					dadosRecebidos = new DataInputStream(socket.getInputStream());
			//
			//					frame.validate();
			//
			//					//Variável que indica se existe uma peça possível a ser jogada
			//					Boolean existePecaPossivel=false;
			//					//Marca quais peças podem ser utilizadas em um array de Boolean
			//					indicesPecasPossiveis=new Boolean[pecasJogador.size()];
			//					//Verifica se existe peça possível a jogar					
			//					for(int i=0; i<pecasJogador.size(); i++){
			//						//Verificando se o lado direito do domino do jogador é possível
			//						if(pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoEsquerdo()){
			//							//Marca a peça possível de jogar na mesa 
			//							pecasJogador.get(i).corPeca=Color.blue;
			//							existePecaPossivel=true;
			//							indicesPecasPossiveis[i]=true;
			//						}
			//						else{
			//							//Verificando se o lado esquerdo do domino do jogador é possível
			//							if(pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoEsquerdo()){
			//								//Marca a peça possível de jogar na mesa 
			//								pecasJogador.get(i).corPeca=Color.blue;
			//								existePecaPossivel=true;
			//								indicesPecasPossiveis[i]=true;
			//
			//							}else{
			//								//Marca a peça possível de jogar na mesa 
			//								pecasJogador.get(i).corPeca=Color.black;
			//								indicesPecasPossiveis[i]=false;
			//							}
			//						}
			//					}
			//
			//					frame.validate();
			//
			//					//Pergunta se existe uma peça a ser jogada
			//					if(existePecaPossivel==true){
			//
			//
			//
			//						ladoAJogar="";
			//						pecaSelecionada=-1;//isso para dizer que não foi selecionada nenhuma peça
			//						//Trata a atualização da variável que contém a peça escolhida no evento de mouse
			//						Boolean sairWhile=false;
			//						while(pecaSelecionada<=-1||indicesPecasPossiveis[pecaSelecionada]==false||sairWhile==false){
			//							//posso colocar aqui ler strings e esperar até receber peça escolhida
			//							//para tratar o chat que não recebe enquando não 
			//							//se a peça foi selecionada e também é possível jogá-la e escolhido o lado
			//							String confirmacao=receberMsg();
			//							while (!confirmacao.equals("Continuar, pois a peça foi escolhida.")){
			//								areadetexto.append(confirmacao+"\n");
			//								areadetexto.setCaretPosition(areadetexto.getText().length());
			//								confirmacao=receberMsg();
			//							}
			//
			//							if (pecaSelecionada>-1 && indicesPecasPossiveis[pecaSelecionada]==true && 
			//									ladoCorreto(pecaPossivel)) {//verifica se o lado selecionado pode receber a peça
			//
			//								//Manda o servidor esperar que o jogador envie a peça escolhida
			//								dadosEnviados.writeUTF("peca Escolhida");
			//								dadosEnviados.flush();
			//
			//								//antes de enviar objeto dizer se irá para direita ou esquerda
			//								//		ordenarPeca
			//
			//								//saber se o lado é possível de jogar
			//								//envia o lado
			//
			//								PecaDomino pecaAEnviar =ordenarPeca(pecaPossivel,pecaSelecionada,ladoAJogar);
			//								enviarMsg(ladoAJogar);
			//
			//								envObjeto = new ObjectOutputStream(socket.getOutputStream());
			//
			//								System.out.println("Jogador "+nomeJogador+" ENVIARÁ esta Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
			//								envObjeto.writeObject(pecaAEnviar);
			//								System.out.println("Jogador "+nomeJogador+" ENVIOU esta Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
			//
			//								//voltar a enviar String 
			//								dadosEnviados = new DataOutputStream(socket.getOutputStream());
			//
			//								//perguntar se esse jogador ganhou o jogo
			//								if(pecasJogador.isEmpty()){
			//									enviarMsg("Jogador Venceu essa rodada!");
			//								}else{
			//									enviarMsg("Não ganhou");
			//
			//								}
			//
			//								sairWhile=true;
			//
			//								enviarPrimeiraPeca=false;
			//							}else{
			//								if(pecaSelecionada>-1&&indicesPecasPossiveis[pecaSelecionada]==false){
			//									JOptionPane.showMessageDialog(null,"Essa peça não pode ser utilizada! Utilize "+pecaPossivel.getLadoEsquerdo()+" ou "+pecaPossivel.getLadoDireito());
			//									pecaSelecionada=-1;
			//								}}
			//						}
			//						//zerar o lado e e peça selecionada
			//						pecaSelecionada=-1;//isso para deselecionar a peça
			//						ladoAJogar="";
			//						System.out.println("ClienteThread: "+nomeJogador+" enviou a peça: ");
			//					}
			//					else{//não existe peça possível a ser jogada
			//						dadosEnviados.writeUTF("Não existe peça possível a ser jogada.");
			//						dadosEnviados.flush();
			//					}
			//
			//					//Quando terminar a jogada deixar todas as peças pretas					
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
			//					//Não pintar a peça de amarelo
			//					indicesPecasPossiveis=new Boolean[pecasJogador.size()];
			//					//Verifica se existe peça possível a jogar					
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
			//				if(rec.equals("Servidor já está lotado!")){
			//					JOptionPane.showMessageDialog(null,"Servidor já está lotado!");
			//					System.exit(1); }
			//
			//				//Jogo Iniciado
			//				if(rec.equals("Jogo iniciado e nao pode entrar!")){
			//					JOptionPane.showMessageDialog(null,"Jogo já foi iniciado, não pode adicionar novos jogadores!");
			//					System.exit(1); }				
			//
			//			}

			System.out.println("ClienteThread: "+nomeJogador+" fechou sua conexão");

			//fecha os canais de saída e entrada
			////dadosEnviados.close();
			////dadosRecebidos.close();
			//fecha o socket
			////socket.close();



		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Mostrar no console as peças  que estão na Mesa, ou seja, foram utilizadas
	public void mostrarPecasDaMesa(){
		System.out.println("ClienteThread: INICIO Peças que estão no centro da mesa: ");
		//mostrar as peças de um jogador
		for(int i=0; i<pecasDaMesa.size(); i++){
			System.out.print(" "+pecasDaMesa.get(i).getLadoEsquerdo()+"|"+pecasDaMesa.get(i).getLadoDireito());
		}
		System.out.println();
		System.out.println("ClienteThread: FIM Peças que estão no centro da mesa: ");
	}

	//Receber peças já utilizadas e estão no centro da mesa
	//	public ArrayList <PecaDomino> receberPecasDaMesa(){
	//
	//		ArrayList <PecaDomino> pecaDaMesaLocal=null;
	//		//já tem que está pronto para para receber antes deles enviarem
	//		try {
	//			recObjeto = new ObjectInputStream(socket.getInputStream());
	//			//valores que podem ser jogados
	//			pecaDaMesaLocal=(ArrayList <PecaDomino>)recObjeto.readObject();
	//			System.out.println("Jogador "+nomeJogador+" Recebeu as peças da Mesa");
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

	//Verifica se o lado selecionado é possível jogar
	public Boolean ladoCorreto(PecaDomino p){


		if(ladoAJogar.equals("Esquerdo")){
			JOptionPane.showMessageDialog(null,"Você escolheu lado "+ladoAJogar);
			if(pecasJogador.get(pecaSelecionada).getLadoDireito()==p.getLadoEsquerdo()||
					pecasJogador.get(pecaSelecionada).getLadoEsquerdo()==p.getLadoEsquerdo()){
				return true;
			}
			else{
				ladoAJogar="";
				JOptionPane.showMessageDialog(null,"Não pode jogar no lado Esquerdo!");}
			return false;
		}
		else{
			if(ladoAJogar.equals("Direito")){
				JOptionPane.showMessageDialog(null,"Você escolheu lado "+ladoAJogar);
				if(pecasJogador.get(pecaSelecionada).getLadoDireito()==p.getLadoDireito()||
						pecasJogador.get(pecaSelecionada).getLadoEsquerdo()==p.getLadoDireito()){
					return true;
				}
				else{
					ladoAJogar="";
					JOptionPane.showMessageDialog(null,"Não pode jogar no lado Direito!");}
				return false;
			}
			else{//lado nao selecionado
				return false;
			}
		}
	}

	//Recebe as peças que estão no centro mesa, ou seja, já foram utlizadas pelos jogadores
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

	//Vira a peça para deixar do lado correto, ou seja, pelo menos um lado jogável
	public PecaDomino ordenarPeca(PecaDomino pecaPo, int indice, String lado){
		//procura na lista a peça selecionada pelo jogador
		PecaDomino pecaAEnviarAoServidor = pecasJogador.remove(indice);

		//Peça invertida
		if(lado.equals("Esquerdo")&&pecaAEnviarAoServidor.getLadoDireito()!=pecaPo.getLadoEsquerdo() ){
			//mandar o jogador escolher lado
			int troca=pecaAEnviarAoServidor.getLadoDireito();
			pecaAEnviarAoServidor.setLadoDireito(pecaAEnviarAoServidor.getLadoEsquerdo());
			pecaAEnviarAoServidor.setLadoEsquerdo(troca);

		}
		else{//para tratar peça com dois lados iguais e vem uma invertida
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

	//colocar as 7 peças do início do jogo que foram recebidas do servidor
	public void setPecasJogador(ArrayList<PecaDomino> Jogador) {
		this.pecasJogador = Jogador;
	}

	//Mostra as peças deste jogador
	void mostrarPecas(){
		System.out.println("ClienteThread: Domino jogador "+nomeJogador);
		//mostrar as peças de um jogador
		for(int i=0; i<pecasJogador.size(); i++){
			System.out.println("ClienteThread: "+pecasJogador.get(i).getLadoEsquerdo()+"|"+pecasJogador.get(i).getLadoDireito());
		}
		desenharPecas();
	}

	//Desenha Graficamento as peças
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

	//Colore as peças possíveis de jogar de vermelhor
	public void colorePecasPossiveis(){

		for(int i=0; i<pecasJogador.size(); i++){
			if(pecaPossivel==null){
				//Marca a peça possível de jogar na mesa 
				pecasJogador.get(i).corPeca=Color.black;
			}
			else{
				//Verificando se o lado direito do domino do jogador é possível
				if(pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoEsquerdo()){
					//Marca a peça possível de jogar na mesa 
					pecasJogador.get(i).corPeca=Color.blue;			
				}
				else{
					//Verificando se o lado esquerdo do domino do jogador é possível
					if(pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoEsquerdo()){
						//Marca a peça possível de jogar na mesa 
						pecasJogador.get(i).corPeca=Color.blue;					

					}else{
						//Marca a peça possível de jogar na mesa 
						pecasJogador.get(i).corPeca=Color.black;
					}
				}
			}
		}
		frame.validate();
	}

	//Trata o evento dos botões
	public void actionPerformed(ActionEvent e){

		//se o botão de pronto tiver selecionado, então envia a confirmação ao servidor
		//que coloca o valor em uma variavel no trataCliente e quantos todos tiver confirmado começa o jogo
		if(e.getSource()==jogar){
			//desabilitar o check box que confirma se o jogador está pronto
			jogar.setEnabled(false);
			////enviarMsg("Estou pronto para receber peças!");
			try {
				serv.enviaATodosClientes("Estou pronto para receber peças!");
				serv.prontoParaJogar(true,nomeJogador);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			areadetexto.append("Estou pronto para receber peças!\n");
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

		//fechar o jogo e conexão com o servidor
		if(e.getSource()==itemSair){

			//colocar para fechar a conexão antes de sair
			System.out.println("ClienteThread: Jogador "+nomeJogador+" fechou sua conexão");

			//fecha os canais de saída e entrada
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

	//Trata o evento gerado no JPanel, ou seja, a peça selecionada e direita e esquerda 
	public void mouseClicked(MouseEvent e) {
		System.out.println("ClienteThread: posição onde foi clicado:");
		System.out.println("ClienteThread: X: "+e.getX()+", Y: "+e.getY());

		//depois de achar a peça enviar ao servidor
		int horizontal = painelPecasDoJogador.getHorizontalScrollBar().getModel().getValue(); 
		for(int n=1;n<=pecasJogador.size();n++){
			if(e.getX()+horizontal>(10+90*(n-1))&&e.getX()+horizontal<(90*n)&&e.getY()>10&&e.getY()<130){
				//JOptionPane.showMessageDialog(null,"Clicou peça "+n);
				pecaSelecionada=n-1;

				colorePecasPossiveis();
				//Muda a cor da peça clicada para amarelo
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
		//Recebe uma nova Peça
		////PecaDomino pecaPuxada=(PecaDomino)recObjeto.readObject();

		PecaDomino pecaPuxada=p;

		System.out.println("Cliente: "+nomeJogador+" recebeu esta Peça puxando: "+ pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());
		//Volta para o canal que recebe String
		////dadosRecebidos = new DataInputStream(socket.getInputStream());

		//Adiciona a peça que foi puxada ou comprada na lista de peças do jogador
		pecasJogador.add(pecaPuxada);
		//Aumenta o tamanho do scroll das peças dos jogadores
		desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecasJogador.size(), 210));

		//Desenha a nova peça no JPanel
		desenhoPecasDoJogador.repaint();

		desenhoDasPecasDaMesa.repaint();

		painelDeConteudo.repaint();

		frame.validate();

		////enviarMsg("Comprei uma peça.");

		serv.enviaATodosClientes(nomeJogador+": Comprei uma peça.");

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

	//Método onde o servidor passa as 7 peças ao cliente
	public void recebe7Pecas(ArrayList<PecaDomino> pecas) throws RemoteException {

		System.out.println("Cliente: Recebeu a lista com as peças");
		//Atualiza os valores reais das peças deste jogador
		pecasJogador=(ArrayList<PecaDomino>)pecas;
		System.out.println("Cliente: Atualizou as peças do Jogador");

		System.out.println("Cliente: "+nomeJogador+" recebeu seu domino");
		System.out.println("Cliente: "+pecas.size());

		//Mostra as peças desse jogador
		for(int i=0; i<pecas.size(); i++){
			System.out.println("ClienteThread: "+pecas.get(i).getLadoEsquerdo()+"|"+pecas.get(i).getLadoDireito());
		}

		//Adiciona a parte das peças do jogador no Frame
		desenhoPecasDoJogador=new GuiDesenhaDomino(pecasJogador);

		nomePecas = new JLabel("Minhas Peças: ");
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

		System.out.println("Cliente: "+nomeJogador+" irá escolher primeira peça");

		enviarPrimeiraPeca=true;

		//Manda o servidor(TrataCliente) esperar por um objeto, ou seja, a peça escolhida
		////enviarMsg("peca Escolhida");//trocou pelo de cima


		PecaDomino pecaAEnviar = null ;
		System.out.println("Cliente: "+nomeJogador+" tem que selecionar um peça e o lado que deseja jogar");
		pecaSelecionada=-1;//isso para dizer que não foi selecionada nenhuma peça
		//Trata a atualização da variável no evento de mouse
		Boolean sairWhile=false;//erro de atualização
		// Fica no while enquanto não selecionar a 1 peça
		while(pecaSelecionada<=-1||sairWhile==false){

			if (pecaSelecionada>-1) {
				//out.writeUTF("recebaObjeto");
				System.out.println("Cliente: "+nomeJogador+" valor do índice da Peça selecionada: "+pecaSelecionada);

				//Remove a peça selecionada pelo Jogador da lista de peças
				pecaAEnviar = pecasJogador.remove(pecaSelecionada);

				System.out.println("Cliente: "+nomeJogador+" ENVIARÁ a Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

				//Indica que a peça deve ser insirida no início da lista de peças na mesa
				///enviarMsg("Esquerdo");


				//criar um método que implementa o lado escolhido
				serv.recebeLadoAJogar("Esquerdo");

				System.out.println("Cliente: "+nomeJogador+" enviou o lado onde deve ser add a Peça: Esquerdo");

				////envObjeto = new ObjectOutputStream(socket.getOutputStream());
				////envObjeto.writeObject(pecaAEnviar);

				//Volta a enviar String 
				////dadosEnviados = new DataOutputStream(socket.getOutputStream());

				//Diz que esse jogador não venceu o jogo, pois é o início do jogo
				////enviarMsg("Não ganhou");

				serv.ganhouJogo("Não ganhou",nomeJogador);

				System.out.println("Cliente: "+nomeJogador+" ENVIOU a Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

				sairWhile=true;

				enviarPrimeiraPeca=false;

			}
		}
		System.out.println("Cliente: "+nomeJogador+" valor do índice da Peça: "+pecaSelecionada);
		pecaSelecionada=-1;//isso para deselecionar a peça
		System.out.println("Cliente: "+nomeJogador+" enviou 1 peça");

		////enviarMsg("Jogou uma peça para iniciar o jogo.");
		serv.enviaATodosClientes("Jogou uma peça para iniciar o jogo.");

		//descolorir peças possíveis
		pecaPossivel=null;

		return pecaAEnviar;
	}


	public PecaDomino jogaProximaPeca(PecaDomino pecaPoss) throws RemoteException {
		////recObjeto = new ObjectInputStream(socket.getInputStream());  
		//Recebe as pontas das peças na mesa, ou seja, os valores possíveis de jogar
		////pecaPossivel=(PecaDomino)recObjeto.readObject();

		pecaPossivel=pecaPoss;

		PecaDomino pecaAEnviar=null;

		System.out.println("Cliente: "+nomeJogador+" Recebeu as pontas");
		//Volta para o canal que recebe String
		////dadosRecebidos = new DataInputStream(socket.getInputStream());

		frame.validate();

		//Variável que indica se existe uma peça possível a ser jogada
		Boolean existePecaPossivel=false;
		//Marca quais peças podem ser utilizadas em um array de Boolean
		indicesPecasPossiveis=new Boolean[pecasJogador.size()];
		//Verifica se existe peça possível a jogar					
		for(int i=0; i<pecasJogador.size(); i++){
			//Verificando se o lado direito do domino do jogador é possível
			if(pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoDireito()==pecaPossivel.getLadoEsquerdo()){
				//Marca a peça possível de jogar na mesa 
				pecasJogador.get(i).corPeca=Color.blue;
				existePecaPossivel=true;
				indicesPecasPossiveis[i]=true;
			}
			else{
				//Verificando se o lado esquerdo do domino do jogador é possível
				if(pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoDireito()||pecasJogador.get(i).getLadoEsquerdo()==pecaPossivel.getLadoEsquerdo()){
					//Marca a peça possível de jogar na mesa 
					pecasJogador.get(i).corPeca=Color.blue;
					existePecaPossivel=true;
					indicesPecasPossiveis[i]=true;

				}else{
					//Marca a peça possível de jogar na mesa 
					pecasJogador.get(i).corPeca=Color.black;
					indicesPecasPossiveis[i]=false;
				}
			}
		}

		frame.validate();

		//Pergunta se existe uma peça a ser jogada
		if(existePecaPossivel==true){



			ladoAJogar="";
			pecaSelecionada=-1;//isso para dizer que não foi selecionada nenhuma peça
			//Trata a atualização da variável que contém a peça escolhida no evento de mouse
			Boolean sairWhile=false;
			while(pecaSelecionada<=-1||indicesPecasPossiveis[pecaSelecionada]==false||sairWhile==false){
				//posso colocar aqui ler strings e esperar até receber peça escolhida
				//para tratar o chat que não recebe enquando não 
				//se a peça foi selecionada e também é possível jogá-la e escolhido o lado

				////era pro chat o que está em baixo
				//				String confirmacao=receberMsg();
				//				while (!confirmacao.equals("Continuar, pois a peça foi escolhida.")){
				//					areadetexto.append(confirmacao+"\n");
				//					areadetexto.setCaretPosition(areadetexto.getText().length());
				//					confirmacao=receberMsg();
				//				}

				if (pecaSelecionada>-1 && indicesPecasPossiveis[pecaSelecionada]==true && 
						ladoCorreto(pecaPossivel)) {//verifica se o lado selecionado pode receber a peça

					//Manda o servidor esperar que o jogador envie a peça escolhida
					////dadosEnviados.writeUTF("peca Escolhida");
					////dadosEnviados.flush();

					//antes de enviar objeto dizer se irá para direita ou esquerda
					//		ordenarPeca

					//saber se o lado é possível de jogar
					//envia o lado

					pecaAEnviar =ordenarPeca(pecaPossivel,pecaSelecionada,ladoAJogar);

					////enviarMsg(ladoAJogar);
					serv.recebeLadoAJogar(ladoAJogar);


					////envObjeto = new ObjectOutputStream(socket.getOutputStream());

					System.out.println("Clienete: "+nomeJogador+" ENVIARÁ esta Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());
					////envObjeto.writeObject(pecaAEnviar);

					System.out.println("Cliente: "+nomeJogador+" ENVIOU esta Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

					//voltar a enviar String 
					////dadosEnviados = new DataOutputStream(socket.getOutputStream());

					//perguntar se esse jogador ganhou o jogo
					if(pecasJogador.isEmpty()){
						////enviarMsg("Jogador Venceu essa rodada!");
						serv.ganhouJogo("venceu essa rodada!",nomeJogador);
					}else{
						////enviarMsg("Não ganhou");
						serv.ganhouJogo("Não ganhou",nomeJogador);
					}

					sairWhile=true;

					enviarPrimeiraPeca=false;
				}else{
					if(pecaSelecionada>-1&&indicesPecasPossiveis[pecaSelecionada]==false){
						JOptionPane.showMessageDialog(null,"Essa peça não pode ser utilizada! Utilize "+pecaPossivel.getLadoEsquerdo()+" ou "+pecaPossivel.getLadoDireito());
						pecaSelecionada=-1;
					}}
			}
			//zerar o lado e e peça selecionada
			pecaSelecionada=-1;//isso para deselecionar a peça
			ladoAJogar="";
			System.out.println("ClienteThread: "+nomeJogador+" enviou a peça: ");

		}
		else{
			//puxar peça
		}

		//descolorir peças possíveis
		pecaPossivel=null;
		indicesPecasPossiveis=null;

		return pecaAEnviar;
	}

	public void puxaUmaPeca(PecaDomino p) throws RemoteException {
		pecasJogador.add(p);
	}

	public void atualizaGUI(ArrayList <PecaDomino> pecaDaMesaLocal) throws RemoteException {
		//Recebe um ArrayList de Peças que estão na mesa

		System.out.println("Cliente: "+nomeJogador+" Recebeu as peças da Mesa");

		pecasDaMesa=pecaDaMesaLocal;

		mostrarPecasDaMesa();//Imprime no console as peças recebidas	

		desenhoDasPecasDaMesa.setPecasDaMesa(pecasDaMesa);//Atualiza na parte gráfica os novos valores
		//Aumenta o tamango do Scroll das peças da mesa
		desenhoDasPecasDaMesa.setPreferredSize(new Dimension (100+140*pecasDaMesa.size(), 300));
		//Desenha os novos valores de peças na mesa
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
		System.out.println("Somatório das peças desse jogador: "+somaPecas);
		//envia a quantidade da soma das peças ao servidor
		////enviarMsg("Soma das peças");
		serv.enviaATodosClientes(nomeJogador+": Soma das minhas peças = "+somaPecas);

		////enviarMsg(""+somaPecas);

		////mostrarTexto=false;

		return somaPecas;
	}

	@Override
	public void servidorLotador(Boolean a) throws RemoteException {
		servidorLotado=a;		
	}

}