package Todos;




import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
	////Nome do servidor que quer se conectar
	String nomeServidor;

	//Lado que o jogador clicou para colocar pe�a
	String ladoAJogar="";

	//Pe�a selecionada pelo mouse
	int pecaSelecionada=-1;

	//Pe�as do jogador, ou seja, pe�as que podem ser utilizadas durante o jogo
	private ArrayList <PecaDomino> pecasJogador;
	//Pe�as que est�o no centro da mesa, ou seja, pe�as que os jogadores utilizaram no jogo 
	private ArrayList <PecaDomino> pecasDaMesa;

	//Classe que tem fun��o de desenhar as pe�as do jogador
	GuiDesenhaDomino desenhoPecasDoJogador = new GuiDesenhaDomino(pecasJogador);
	//Barra de rolagem das pe�as no centro da mesa
	JScrollPane painelPecasDoJogador;

	//Classe que tem fun��o de desenhar as pe�as que est�o no centro da mesa
	GuiDesenhaPecasDaMesa desenhoDasPecasDaMesa=new GuiDesenhaPecasDaMesa(this);
	//Barra de rolagem das pe�as no centro da mesa
	JScrollPane painelPecasCentro;

	//Dados de entrada recebidos no teclado
	String teclado = "";

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

	//Nome que ir� registrar caso n�o escolha nada no JOptionPanel
	String nomeJogador="Jogador1";

	//Indica se esse jogador ir� enviar a 1 pe�a
	//Boolean enviarPrimeiraPeca =false;

	//Marca quais pe�as podem ser utilizadas em um array de Boolean
	Boolean [] indicesPecasPossiveis;

	//Pontas do jogo, ou seja, lados poss�veis de jogar
	PecaDomino pecaPossivel;	

	//Comunica��o com servidor
	InterfaceDoServidor serv;

	//Indica se o servidor j� est� lotado
	Boolean servidorLotado=false;

	//Pontua��o dos jogadores 
	Pontuacao pontucaoJogadoresDoJogo;
	
	ArrayList<String> nomeJogadoresNoServidor;
	
	//Combobox com que enviar do chat
	public JComboBox combo;

	public ClienteThread() throws RemoteException{

		super();

		pecasJogador=new ArrayList <PecaDomino>();

		pecasDaMesa=new ArrayList <PecaDomino>();
		
		nomeJogadoresNoServidor = new ArrayList<String>();
		nomeJogadoresNoServidor.add("Todos");
		
		//Recebe o nome do usu�rio que ficar� no t�tulo do Frame
		nomeJogador=JOptionPane.showInputDialog("Escolha o nome do usu�rio","jogador");
		//Coloca os valores do ip e porta do servidor que ir� conectar
		nomeServidor=JOptionPane.showInputDialog("Escolha o nome do servidor","Servidor");

		try {


			System.out.println("Registrara o cliente.");
			//Registra o Jogador no servidor de nomes
			Naming.rebind(nomeJogador,(Remote) this);
			System.out.println("Cliente Registrado!");

			//Busca o servidor que deseja se conectar
			serv =  (InterfaceDoServidor)Naming.lookup("//localhost/"+nomeServidor);

			//M�todo que cria a interface gr�fica do jogador
			criarGUI();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Erro em tentar conectar ao servidor!");
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}		
	}

	//Cria a parte Gr�fica da parte do cliente ou Jogador
	public void criarGUI() {

		//Cria e configura a janela
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

		//Campo que cont�m as mensagens enviadas e recebidas
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

		pontucaoJogadoresDoJogo=new Pontuacao(new ArrayList<String>(),new ArrayList<Integer>());;
		pontucaoJogadoresDoJogo.setLayout(null);		
		pontucaoJogadoresDoJogo.setBounds(260,22,900,25);
		painelDeConteudo.add(pontucaoJogadoresDoJogo);
		
		combo = new JComboBox(new DefaultComboBoxModel( nomeJogadoresNoServidor.toArray()));
		combo.setLayout(null);
		combo.setBounds(900,435,100,30);
		painelDeConteudo.add(combo);
		
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

			//Registra o Jogador no Servidor 
			serv.login(nomeJogador);

			//Condi��o fechar esse jogodor, ou seja, quando o jogador desejar fechar a sua interface gr�fica
			while(!teclado.equals("close")&&servidorLotado==false){

			}

			//Mostra uma mensagem quando o cliente sai
			System.out.println("ClienteThread: "+nomeJogador+" fechou sua conex�o");

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

	//Verifica se o lado selecionado � poss�vel jogar
	public Boolean ladoCorreto(PecaDomino p){


		if(ladoAJogar.equals("Esquerdo")){
			//JOptionPane.showMessageDialog(null,"Voc� escolheu lado "+ladoAJogar);
			if(pecasJogador.get(pecaSelecionada).getLadoDireito()==p.getLadoEsquerdo()||
					pecasJogador.get(pecaSelecionada).getLadoEsquerdo()==p.getLadoEsquerdo()){
				return true;
			}
			else{
				ladoAJogar="";
				//JOptionPane.showMessageDialog(null,"N�o pode jogar no lado Esquerdo!");
			}
			return false;
		}
		else{
			if(ladoAJogar.equals("Direito")){
				//	JOptionPane.showMessageDialog(null,"Voc� escolheu lado "+ladoAJogar);
				if(pecasJogador.get(pecaSelecionada).getLadoDireito()==p.getLadoDireito()||
						pecasJogador.get(pecaSelecionada).getLadoEsquerdo()==p.getLadoDireito()){
					return true;
				}
				else{
					ladoAJogar="";
					//	JOptionPane.showMessageDialog(null,"N�o pode jogar no lado Direito!");
				}
				return false;
			}
			else{//lado nao selecionado
				return false;
			}
		}
	}

	//Vira a pe�a para deixar do lado correto, ou seja, pelo menos um lado jog�vel
	public PecaDomino ordenarPeca(PecaDomino pecaPo, int indice, String lado){
		//Procura na lista a pe�a selecionada pelo jogador
		PecaDomino pecaAEnviarAoServidor = pecasJogador.remove(indice);

		//Pe�a invertida
		if(lado.equals("Esquerdo")&&pecaAEnviarAoServidor.getLadoDireito()!=pecaPo.getLadoEsquerdo() ){
			//Manda o jogador escolher lado
			int troca=pecaAEnviarAoServidor.getLadoDireito();
			pecaAEnviarAoServidor.setLadoDireito(pecaAEnviarAoServidor.getLadoEsquerdo());
			pecaAEnviarAoServidor.setLadoEsquerdo(troca);

		}
		else{//Para tratar pe�a com dois lados iguais e vem uma invertida
			if(lado.equals("Direito")&&pecaAEnviarAoServidor.getLadoEsquerdo()!= pecaPo.getLadoDireito()){
				int troca=pecaAEnviarAoServidor.getLadoDireito();
				pecaAEnviarAoServidor.setLadoDireito(pecaAEnviarAoServidor.getLadoEsquerdo());
				pecaAEnviarAoServidor.setLadoEsquerdo(troca);
			}
		}
		return pecaAEnviarAoServidor;
	}

	//Coloca as 7 pe�as do in�cio do jogo que foram recebidas do servidor
	public void setPecasJogador(ArrayList<PecaDomino> Jogador) {
		this.pecasJogador = Jogador;
	}

	//Mostra as pe�as deste jogador
	void mostrarPecas(){
		System.out.println("ClienteThread: Domino d "+nomeJogador);
		//Mostra as pe�as de um jogador
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
			//Desabilita o check box que confirma se o jogador est� pronto
			jogar.setEnabled(false);
			try {
				//Avisa a todos os jogadores que esse cliente est� pronto para jogar
				serv.enviaATodosClientes(nomeJogador+" est� pronto para receber pe�as!");
				serv.prontoParaJogar(true,nomeJogador);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}

		//Envia mensagem do chat aos outros jogadores
		if(e.getSource() == enviar){
			//Captura o valor do TextField
			String valorTextField=textField.getText();
			//Apaga o valor que est� no TextField
			textField.setText("");

			System.out.println("ClienteThread: "+nomeJogador+" enviou ao servidor o campo - "+valorTextField);

			try {
				String usuario= (String) combo.getSelectedItem();
				if(usuario.equals("Todos")){
				//Envia a todos jogadores o valor que est� no chat
				serv.enviaATodosClientes(nomeJogador+": "+valorTextField);}
				else{
					InterfaceDoCliente cli = (InterfaceDoCliente)Naming.lookup("//localhost/"+usuario);
					cli.recebeMensagemDoChat(nomeJogador+": "+valorTextField);
					recebeMensagemDoChat(nomeJogador+": "+valorTextField);
					
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (NotBoundException e1) {
				e1.printStackTrace();
			}
		}

		//Fecha o jogo e conex�o com o servidor
		if(e.getSource()==itemSair){

			//Coloca para fechar a conex�o antes de sair
			System.out.println("ClienteThread: Jogador "+nomeJogador+" fechou sua conex�o");

			//Fecha os canais de sa�da e entrada
			try {
				serv.enviaATodosClientes(nomeJogador+": Fechei minha janela!");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//fechar janela, ou seja, Frame
			System.exit(1);  
		}

		//Mostra o nome dos autores do Jogo
		if(e.getSource() == autores){
			JOptionPane.showMessageDialog(null,"Wandemberg Rodrigues Gomes");
		}		

	}

	//Trata o evento gerado no JPanel, ou seja, a pe�a selecionada e direita e esquerda 
	public void mouseClicked(MouseEvent e) {
		System.out.println("ClienteThread: posi��o onde foi clicado:");
		System.out.println("ClienteThread: X: "+e.getX()+", Y: "+e.getY());

		//depois de achar a pe�a enviar ao servidor
		int horizontal = this.painelPecasDoJogador.getHorizontalScrollBar().getModel().getValue(); 
		for(int n=1;n<=pecasJogador.size();n++){
			if(e.getX()+horizontal>(10+90*(n-1))&&e.getX()+horizontal<(90*n)&&e.getY()>10&&e.getY()<130){
				//JOptionPane.showMessageDialog(null,"Clicou pe�a "+n);
				pecaSelecionada=n-1;

				colorePecasPossiveis();
				//Muda a cor da pe�a clicada para amarelo
				if(indicesPecasPossiveis!=null&&pecaSelecionada>-1&&indicesPecasPossiveis[pecaSelecionada]==true){
					pecasJogador.get(pecaSelecionada).corPeca=Color.red;
				}

				//desenhoDasPecasDaMesa.repaint();
				//desenhoPecasDoJogador.repaint();

				//painelDeConteudo.repaint();

			}
		}

		System.out.println("Jogador "+nomeJogador+" selecionou: "+pecaSelecionada);
		//System.out.println("Jogador "+nomeJogador+" selecionou lado: "+ladoAJogar);		

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


	//Recebe uma String 
	public void recebeString(String a) throws RemoteException {
		System.out.println(a);
	}

	//Recebe uma String e coloca no TextArea
	public void recebeMensagemDoChat(String s) throws RemoteException {
		areadetexto.append(s+"\n");
		areadetexto.setCaretPosition(areadetexto.getText().length());
	}

	//Recebe uma pe�a, ou seja, quando estiver comprando pe�a
	public void recebeUmaPeca(PecaDomino p) throws RemoteException {

		PecaDomino pecaPuxada=p;

		System.out.println("Cliente: "+nomeJogador+" recebeu esta Pe�a puxando: "+ pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());

		//Adiciona a pe�a que foi puxada ou comprada na lista de pe�as do jogador
		pecasJogador.add(pecaPuxada);

		//Aumenta o tamanho do scroll das pe�as dos jogadores
		desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecasJogador.size(), 210));

		//Desenha a nova pe�a no JPanel
		desenhoPecasDoJogador.repaint();

		desenhoDasPecasDaMesa.repaint();

		painelDeConteudo.repaint();

		frame.validate();

		//Avisa a todos os jogadores que o jogador comprou uma pe�a
		serv.enviaATodosClientes(nomeJogador+": Comprei uma pe�a.");

	}

	//M�todo onde o servidor passa as 7 pe�as ao cliente
	public void recebe7Pecas(ArrayList<PecaDomino> pecas) throws RemoteException {

		System.out.println("Cliente: Recebeu a lista com as pe�as");
		//Atualiza os valores reais das pe�as deste jogador
		this.pecasJogador=null;
		this.pecasJogador=pecas;
		pecaPossivel=null;
		desenhoPecasDoJogador = new GuiDesenhaDomino(pecasJogador);

		System.out.println("Cliente: Atualizou as pe�as do Jogador");

		//Coloquei depois
		mostrarPecas();

		System.out.println("Cliente: "+nomeJogador+" recebeu seu domino");
		System.out.println("Cliente: "+pecas.size());

		//Mostra as pe�as desse jogador
		for(int i=0; i<pecas.size(); i++){
			System.out.println("ClienteThread: "+pecas.get(i).getLadoEsquerdo()+"|"+pecas.get(i).getLadoDireito());
		}

		//Adiciona a parte das pe�as do jogador no Frame
		this.desenhoPecasDoJogador=new GuiDesenhaDomino(this.pecasJogador);

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

		//Indica que � a primeira pe�a a jogar
		//enviarPrimeiraPeca=true;

		//Valor da pe�a que o jogador ir� enviar
		PecaDomino pecaAEnviar = null ;

		System.out.println("Cliente: "+nomeJogador+" tem que selecionar um pe�a e o lado que deseja jogar");
		pecaSelecionada=-1;//isso para dizer que n�o foi selecionada nenhuma pe�a

		//Trata a atualiza��o da vari�vel no evento de mouse
		Boolean sairWhile=false;//erro de atualiza��o
		// Fica no while enquanto n�o selecionar a 1 pe�a
		while(pecaSelecionada<=-1||sairWhile==false){

			if (pecaSelecionada>-1) {

				System.out.println("Cliente: "+nomeJogador+" valor do �ndice da Pe�a selecionada: "+pecaSelecionada);

				//Remove a pe�a selecionada pelo Jogador da lista de pe�as
				pecaAEnviar = pecasJogador.remove(pecaSelecionada);

				System.out.println("Cliente: "+nomeJogador+" ENVIAR� a Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

				//Envia o lado escolhido ao servidor
				serv.recebeLadoAJogar("Esquerdo");

				System.out.println("Cliente: "+nomeJogador+" enviou o lado onde deve ser add a Pe�a: Esquerdo");

				serv.ganhouJogo("N�o ganhou",nomeJogador);

				System.out.println("Cliente: "+nomeJogador+" ENVIOU a Pe�a: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

				sairWhile=true;

				//	enviarPrimeiraPeca=false;

			}
		}

		System.out.println("Cliente: "+nomeJogador+" valor do �ndice da Pe�a: "+pecaSelecionada);
		pecaSelecionada=-1;//isso para deselecionar a pe�a
		System.out.println("Cliente: "+nomeJogador+" enviou 1 pe�a");

		//Avisa a todos Jogadores que o jogador iniciou o jogo
		serv.enviaATodosClientes("Jogou uma pe�a para iniciar o jogo.");

		//Descolore pe�as poss�veis
		pecaPossivel=null;

		return pecaAEnviar;
	}

	//Jogadas ap�s a 1 jogada
	public PecaDomino jogaProximaPeca(PecaDomino pecaPoss) throws RemoteException {

		//Recebe as pontas das pe�as na mesa, ou seja, os valores poss�veis de jogar
		pecaPossivel=pecaPoss;

		//Valor da pe�a que o jogador ir� enviar
		PecaDomino pecaAEnviar=null;

		System.out.println("Cliente: "+nomeJogador+" Recebeu as pontas");

		frame.validate();

		//Vari�vel que indica se existe uma pe�a poss�vel a ser jogada
		Boolean existePecaPossivel=false;

		//Marca quais pe�as podem ser utilizadas em um array de Boolean
		indicesPecasPossiveis=new Boolean[pecasJogador.size()];

		//Verifica se existe pe�a poss�vel a jogar e coloca no "indicesPecasPossiveis[]"					
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

			//Zera o valor do lado que o jogador escolheu
			ladoAJogar="";
			//Zera o valor do �ndice da pe�a que o jogador escolheu
			pecaSelecionada=-1;//isso para dizer que n�o foi selecionada nenhuma pe�a

			//Trata a atualiza��o da vari�vel que cont�m a pe�a escolhida no evento de mouse
			Boolean sairWhile=false;
			while(pecaSelecionada<=-1||indicesPecasPossiveis[pecaSelecionada]==false||sairWhile==false){

				//posso colocar aqui ler strings e esperar at� receber pe�a escolhida
				//se a pe�a foi selecionada e tamb�m � poss�vel jog�-la e escolhido o lado
				if (pecaSelecionada>-1 && indicesPecasPossiveis[pecaSelecionada]==true && 
						ladoCorreto(pecaPossivel)) {//verifica se o lado selecionado pode receber a pe�a

					//antes de enviar objeto dizer se ir� para direita ou esquerda
					pecaAEnviar =ordenarPeca(pecaPossivel,pecaSelecionada,ladoAJogar);

					//Envia o lado selecionado pelo Jogador
					serv.recebeLadoAJogar(ladoAJogar);

					//Pergunta se esse jogador ganhou o jogo
					if(pecasJogador.isEmpty()){
						//Avisa o servidor que o jogador venceu o jogo
						serv.ganhouJogo("venceu essa rodada!",nomeJogador);
					}else{
						//Avisa o servidor que o jogador n�o venceu o jogo
						serv.ganhouJogo("N�o ganhou",nomeJogador);
					}

					//Para sair do while de pe�a escolhida 
					sairWhile=true;

					//enviarPrimeiraPeca=false;
				}else{
					if(pecaSelecionada>-1&&indicesPecasPossiveis[pecaSelecionada]==false){
						JOptionPane.showMessageDialog(null,"Essa pe�a n�o pode ser utilizada! Utilize "+pecaPossivel.getLadoEsquerdo()+" ou "+pecaPossivel.getLadoDireito());
						pecaSelecionada=-1;
					}}
			}
			//Zera o lado e e pe�a selecionada
			pecaSelecionada=-1;//isso para deselecionar a pe�a
			ladoAJogar="";

			System.out.println("ClienteThread: "+nomeJogador+" enviou a pe�a: ");

		}

		//Descolore pe�as poss�veis
		pecaPossivel=null;
		indicesPecasPossiveis=null;

		//Atualiza as novas cores da pe�a desse jogador 
		colorePecasPossiveis();

		return pecaAEnviar;
	}

	public void puxaUmaPeca(PecaDomino p) throws RemoteException {

		//Recebe a pe�a enviada pelo Servidor
		PecaDomino pecaPuxada=p;

		System.out.println("Cliente: "+nomeJogador+" recebeu esta Pe�a puxando: "+ pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());

		//Adiciona a pe�a que foi puxada ou comprada na lista de pe�as do jogador
		pecasJogador.add(pecaPuxada);
		//Aumenta o tamanho do scroll das pe�as dos jogadores
		desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecasJogador.size(), 210));

		//Desenha a nova pe�a no JPanel
		desenhoPecasDoJogador.repaint();

		desenhoDasPecasDaMesa.repaint();

		painelDeConteudo.repaint();

		frame.validate();

		//Avisa todos Jogadores que ele comprou uma pe�a
		serv.enviaATodosClientes(nomeJogador+": Comprei uma pe�a.");
	}

	//Atualiza as pe�as que est�o na mesa e pontua��o
	public void atualizaGUI(ArrayList <PecaDomino> pecaDaMesaLocal,ArrayList<String> nomesDosJogadores,ArrayList<Integer> pontuacoesJogadores) throws RemoteException {
		//Recebe um ArrayList de Pe�as que est�o na mesa

		
		
		System.out.println("Cliente: "+nomeJogador+" Recebeu as pe�as da Mesa");

		pontucaoJogadoresDoJogo.nomeJogador=nomesDosJogadores;
		pontucaoJogadoresDoJogo.pontosJogador=pontuacoesJogadores;
		pontucaoJogadoresDoJogo.repaint();
		
		
		while(nomeJogadoresNoServidor.size()>1) {
			nomeJogadoresNoServidor.remove(0);
		}
		Vector noms=new Vector();
		noms.add("Todos");
		for (int i = 0; i < nomesDosJogadores.size(); i++) {
			noms.add(nomesDosJogadores.get(i));
		}
//		nomeJogadoresNoServidor=nomesDosJogadores;
//		nomeJogadoresNoServidor.add(0, "Todos");
		
      // Vector noms[]=(Vector)nomesDosJogadores;
		//combo.removeAll();
		combo.setModel(new DefaultComboBoxModel(noms));

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

	//Habilita o bot�o de pronto desse Jogador
	public void habilitaPronto() throws RemoteException {
		jogar.setEnabled(true);
		pecasJogador=new ArrayList<PecaDomino>();
	}

	//Faz o somat�rio dos valores das pe�a pra saber quem ganhou
	public int jogoTrancado() throws RemoteException {
		int somaPecas=0;
		for(int i=0;i<pecasJogador.size();i++){
			somaPecas=somaPecas+pecasJogador.get(i).getLadoDireito()+pecasJogador.get(i).getLadoEsquerdo();
		}
		System.out.println("Somat�rio das pe�as desse jogador: "+somaPecas);

		//Avisa a todos o valor da soma das suas pe�as 
		serv.enviaATodosClientes(nomeJogador+": Soma das minhas pe�as = "+somaPecas);

		//Envia a quantidade da soma das pe�as ao servidor
		return somaPecas;
	}

	//Indica se o servidor est� lotado ou n�o
	public void servidorLotador(Boolean a) throws RemoteException {
		servidorLotado=a;		
	}

	//Apaga as pe�as desse jogador
	public void removerTodasPecasJogador() throws RemoteException {
		while( pecasJogador.size()>0) {
			pecasJogador.remove(0);
		}
	}

}