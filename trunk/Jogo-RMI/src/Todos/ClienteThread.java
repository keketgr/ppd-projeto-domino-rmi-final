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

	//Lado que o jogador clicou para colocar peça
	String ladoAJogar="";

	//Peça selecionada pelo mouse
	int pecaSelecionada=-1;

	//Peças do jogador, ou seja, peças que podem ser utilizadas durante o jogo
	private ArrayList <PecaDomino> pecasJogador;
	//Peças que estão no centro da mesa, ou seja, peças que os jogadores utilizaram no jogo 
	private ArrayList <PecaDomino> pecasDaMesa;

	//Classe que tem função de desenhar as peças do jogador
	GuiDesenhaDomino desenhoPecasDoJogador = new GuiDesenhaDomino(pecasJogador);
	//Barra de rolagem das peças no centro da mesa
	JScrollPane painelPecasDoJogador;

	//Classe que tem função de desenhar as peças que estão no centro da mesa
	GuiDesenhaPecasDaMesa desenhoDasPecasDaMesa=new GuiDesenhaPecasDaMesa(this);
	//Barra de rolagem das peças no centro da mesa
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
	//Check com a função de confirmar que o jogador está pronto para jogar
	private JCheckBox jogar;
	private JLabel confirmar;
	private JLabel nomePecas; //texto MINHAS PEÇAS 

	//Nome que irá registrar caso não escolha nada no JOptionPanel
	String nomeJogador="Jogador1";

	//Indica se esse jogador irá enviar a 1 peça
	//Boolean enviarPrimeiraPeca =false;

	//Marca quais peças podem ser utilizadas em um array de Boolean
	Boolean [] indicesPecasPossiveis;

	//Pontas do jogo, ou seja, lados possíveis de jogar
	PecaDomino pecaPossivel;	

	//Comunicação com servidor
	InterfaceDoServidor serv;

	//Indica se o servidor já está lotado
	Boolean servidorLotado=false;

	//Pontuação dos jogadores 
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
		
		//Recebe o nome do usuário que ficará no título do Frame
		nomeJogador=JOptionPane.showInputDialog("Escolha o nome do usuário","jogador");
		//Coloca os valores do ip e porta do servidor que irá conectar
		nomeServidor=JOptionPane.showInputDialog("Escolha o nome do servidor","Servidor");

		try {


			System.out.println("Registrara o cliente.");
			//Registra o Jogador no servidor de nomes
			Naming.rebind(nomeJogador,(Remote) this);
			System.out.println("Cliente Registrado!");

			//Busca o servidor que deseja se conectar
			serv =  (InterfaceDoServidor)Naming.lookup("//localhost/"+nomeServidor);

			//Método que cria a interface gráfica do jogador
			criarGUI();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Erro em tentar conectar ao servidor!");
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}		
	}

	//Cria a parte Gráfica da parte do cliente ou Jogador
	public void criarGUI() {

		//Cria e configura a janela
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

		//Campo que contém as mensagens enviadas e recebidas
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

			//Registra o Jogador no Servidor 
			serv.login(nomeJogador);

			//Condição fechar esse jogodor, ou seja, quando o jogador desejar fechar a sua interface gráfica
			while(!teclado.equals("close")&&servidorLotado==false){

			}

			//Mostra uma mensagem quando o cliente sai
			System.out.println("ClienteThread: "+nomeJogador+" fechou sua conexão");

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

	//Verifica se o lado selecionado é possível jogar
	public Boolean ladoCorreto(PecaDomino p){


		if(ladoAJogar.equals("Esquerdo")){
			//JOptionPane.showMessageDialog(null,"Você escolheu lado "+ladoAJogar);
			if(pecasJogador.get(pecaSelecionada).getLadoDireito()==p.getLadoEsquerdo()||
					pecasJogador.get(pecaSelecionada).getLadoEsquerdo()==p.getLadoEsquerdo()){
				return true;
			}
			else{
				ladoAJogar="";
				//JOptionPane.showMessageDialog(null,"Não pode jogar no lado Esquerdo!");
			}
			return false;
		}
		else{
			if(ladoAJogar.equals("Direito")){
				//	JOptionPane.showMessageDialog(null,"Você escolheu lado "+ladoAJogar);
				if(pecasJogador.get(pecaSelecionada).getLadoDireito()==p.getLadoDireito()||
						pecasJogador.get(pecaSelecionada).getLadoEsquerdo()==p.getLadoDireito()){
					return true;
				}
				else{
					ladoAJogar="";
					//	JOptionPane.showMessageDialog(null,"Não pode jogar no lado Direito!");
				}
				return false;
			}
			else{//lado nao selecionado
				return false;
			}
		}
	}

	//Vira a peça para deixar do lado correto, ou seja, pelo menos um lado jogável
	public PecaDomino ordenarPeca(PecaDomino pecaPo, int indice, String lado){
		//Procura na lista a peça selecionada pelo jogador
		PecaDomino pecaAEnviarAoServidor = pecasJogador.remove(indice);

		//Peça invertida
		if(lado.equals("Esquerdo")&&pecaAEnviarAoServidor.getLadoDireito()!=pecaPo.getLadoEsquerdo() ){
			//Manda o jogador escolher lado
			int troca=pecaAEnviarAoServidor.getLadoDireito();
			pecaAEnviarAoServidor.setLadoDireito(pecaAEnviarAoServidor.getLadoEsquerdo());
			pecaAEnviarAoServidor.setLadoEsquerdo(troca);

		}
		else{//Para tratar peça com dois lados iguais e vem uma invertida
			if(lado.equals("Direito")&&pecaAEnviarAoServidor.getLadoEsquerdo()!= pecaPo.getLadoDireito()){
				int troca=pecaAEnviarAoServidor.getLadoDireito();
				pecaAEnviarAoServidor.setLadoDireito(pecaAEnviarAoServidor.getLadoEsquerdo());
				pecaAEnviarAoServidor.setLadoEsquerdo(troca);
			}
		}
		return pecaAEnviarAoServidor;
	}

	//Coloca as 7 peças do início do jogo que foram recebidas do servidor
	public void setPecasJogador(ArrayList<PecaDomino> Jogador) {
		this.pecasJogador = Jogador;
	}

	//Mostra as peças deste jogador
	void mostrarPecas(){
		System.out.println("ClienteThread: Domino d "+nomeJogador);
		//Mostra as peças de um jogador
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
			//Desabilita o check box que confirma se o jogador está pronto
			jogar.setEnabled(false);
			try {
				//Avisa a todos os jogadores que esse cliente está pronto para jogar
				serv.enviaATodosClientes(nomeJogador+" está pronto para receber peças!");
				serv.prontoParaJogar(true,nomeJogador);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}

		//Envia mensagem do chat aos outros jogadores
		if(e.getSource() == enviar){
			//Captura o valor do TextField
			String valorTextField=textField.getText();
			//Apaga o valor que está no TextField
			textField.setText("");

			System.out.println("ClienteThread: "+nomeJogador+" enviou ao servidor o campo - "+valorTextField);

			try {
				String usuario= (String) combo.getSelectedItem();
				if(usuario.equals("Todos")){
				//Envia a todos jogadores o valor que está no chat
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

		//Fecha o jogo e conexão com o servidor
		if(e.getSource()==itemSair){

			//Coloca para fechar a conexão antes de sair
			System.out.println("ClienteThread: Jogador "+nomeJogador+" fechou sua conexão");

			//Fecha os canais de saída e entrada
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

	//Trata o evento gerado no JPanel, ou seja, a peça selecionada e direita e esquerda 
	public void mouseClicked(MouseEvent e) {
		System.out.println("ClienteThread: posição onde foi clicado:");
		System.out.println("ClienteThread: X: "+e.getX()+", Y: "+e.getY());

		//depois de achar a peça enviar ao servidor
		int horizontal = this.painelPecasDoJogador.getHorizontalScrollBar().getModel().getValue(); 
		for(int n=1;n<=pecasJogador.size();n++){
			if(e.getX()+horizontal>(10+90*(n-1))&&e.getX()+horizontal<(90*n)&&e.getY()>10&&e.getY()<130){
				//JOptionPane.showMessageDialog(null,"Clicou peça "+n);
				pecaSelecionada=n-1;

				colorePecasPossiveis();
				//Muda a cor da peça clicada para amarelo
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

	//Recebe uma peça, ou seja, quando estiver comprando peça
	public void recebeUmaPeca(PecaDomino p) throws RemoteException {

		PecaDomino pecaPuxada=p;

		System.out.println("Cliente: "+nomeJogador+" recebeu esta Peça puxando: "+ pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());

		//Adiciona a peça que foi puxada ou comprada na lista de peças do jogador
		pecasJogador.add(pecaPuxada);

		//Aumenta o tamanho do scroll das peças dos jogadores
		desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecasJogador.size(), 210));

		//Desenha a nova peça no JPanel
		desenhoPecasDoJogador.repaint();

		desenhoDasPecasDaMesa.repaint();

		painelDeConteudo.repaint();

		frame.validate();

		//Avisa a todos os jogadores que o jogador comprou uma peça
		serv.enviaATodosClientes(nomeJogador+": Comprei uma peça.");

	}

	//Método onde o servidor passa as 7 peças ao cliente
	public void recebe7Pecas(ArrayList<PecaDomino> pecas) throws RemoteException {

		System.out.println("Cliente: Recebeu a lista com as peças");
		//Atualiza os valores reais das peças deste jogador
		this.pecasJogador=null;
		this.pecasJogador=pecas;
		pecaPossivel=null;
		desenhoPecasDoJogador = new GuiDesenhaDomino(pecasJogador);

		System.out.println("Cliente: Atualizou as peças do Jogador");

		//Coloquei depois
		mostrarPecas();

		System.out.println("Cliente: "+nomeJogador+" recebeu seu domino");
		System.out.println("Cliente: "+pecas.size());

		//Mostra as peças desse jogador
		for(int i=0; i<pecas.size(); i++){
			System.out.println("ClienteThread: "+pecas.get(i).getLadoEsquerdo()+"|"+pecas.get(i).getLadoDireito());
		}

		//Adiciona a parte das peças do jogador no Frame
		this.desenhoPecasDoJogador=new GuiDesenhaDomino(this.pecasJogador);

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

		//Indica que é a primeira peça a jogar
		//enviarPrimeiraPeca=true;

		//Valor da peça que o jogador irá enviar
		PecaDomino pecaAEnviar = null ;

		System.out.println("Cliente: "+nomeJogador+" tem que selecionar um peça e o lado que deseja jogar");
		pecaSelecionada=-1;//isso para dizer que não foi selecionada nenhuma peça

		//Trata a atualização da variável no evento de mouse
		Boolean sairWhile=false;//erro de atualização
		// Fica no while enquanto não selecionar a 1 peça
		while(pecaSelecionada<=-1||sairWhile==false){

			if (pecaSelecionada>-1) {

				System.out.println("Cliente: "+nomeJogador+" valor do índice da Peça selecionada: "+pecaSelecionada);

				//Remove a peça selecionada pelo Jogador da lista de peças
				pecaAEnviar = pecasJogador.remove(pecaSelecionada);

				System.out.println("Cliente: "+nomeJogador+" ENVIARÁ a Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

				//Envia o lado escolhido ao servidor
				serv.recebeLadoAJogar("Esquerdo");

				System.out.println("Cliente: "+nomeJogador+" enviou o lado onde deve ser add a Peça: Esquerdo");

				serv.ganhouJogo("Não ganhou",nomeJogador);

				System.out.println("Cliente: "+nomeJogador+" ENVIOU a Peça: "+pecaAEnviar.getLadoEsquerdo()+"|"+pecaAEnviar.getLadoDireito());

				sairWhile=true;

				//	enviarPrimeiraPeca=false;

			}
		}

		System.out.println("Cliente: "+nomeJogador+" valor do índice da Peça: "+pecaSelecionada);
		pecaSelecionada=-1;//isso para deselecionar a peça
		System.out.println("Cliente: "+nomeJogador+" enviou 1 peça");

		//Avisa a todos Jogadores que o jogador iniciou o jogo
		serv.enviaATodosClientes("Jogou uma peça para iniciar o jogo.");

		//Descolore peças possíveis
		pecaPossivel=null;

		return pecaAEnviar;
	}

	//Jogadas após a 1 jogada
	public PecaDomino jogaProximaPeca(PecaDomino pecaPoss) throws RemoteException {

		//Recebe as pontas das peças na mesa, ou seja, os valores possíveis de jogar
		pecaPossivel=pecaPoss;

		//Valor da peça que o jogador irá enviar
		PecaDomino pecaAEnviar=null;

		System.out.println("Cliente: "+nomeJogador+" Recebeu as pontas");

		frame.validate();

		//Variável que indica se existe uma peça possível a ser jogada
		Boolean existePecaPossivel=false;

		//Marca quais peças podem ser utilizadas em um array de Boolean
		indicesPecasPossiveis=new Boolean[pecasJogador.size()];

		//Verifica se existe peça possível a jogar e coloca no "indicesPecasPossiveis[]"					
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

			//Zera o valor do lado que o jogador escolheu
			ladoAJogar="";
			//Zera o valor do índice da peça que o jogador escolheu
			pecaSelecionada=-1;//isso para dizer que não foi selecionada nenhuma peça

			//Trata a atualização da variável que contém a peça escolhida no evento de mouse
			Boolean sairWhile=false;
			while(pecaSelecionada<=-1||indicesPecasPossiveis[pecaSelecionada]==false||sairWhile==false){

				//posso colocar aqui ler strings e esperar até receber peça escolhida
				//se a peça foi selecionada e também é possível jogá-la e escolhido o lado
				if (pecaSelecionada>-1 && indicesPecasPossiveis[pecaSelecionada]==true && 
						ladoCorreto(pecaPossivel)) {//verifica se o lado selecionado pode receber a peça

					//antes de enviar objeto dizer se irá para direita ou esquerda
					pecaAEnviar =ordenarPeca(pecaPossivel,pecaSelecionada,ladoAJogar);

					//Envia o lado selecionado pelo Jogador
					serv.recebeLadoAJogar(ladoAJogar);

					//Pergunta se esse jogador ganhou o jogo
					if(pecasJogador.isEmpty()){
						//Avisa o servidor que o jogador venceu o jogo
						serv.ganhouJogo("venceu essa rodada!",nomeJogador);
					}else{
						//Avisa o servidor que o jogador não venceu o jogo
						serv.ganhouJogo("Não ganhou",nomeJogador);
					}

					//Para sair do while de peça escolhida 
					sairWhile=true;

					//enviarPrimeiraPeca=false;
				}else{
					if(pecaSelecionada>-1&&indicesPecasPossiveis[pecaSelecionada]==false){
						JOptionPane.showMessageDialog(null,"Essa peça não pode ser utilizada! Utilize "+pecaPossivel.getLadoEsquerdo()+" ou "+pecaPossivel.getLadoDireito());
						pecaSelecionada=-1;
					}}
			}
			//Zera o lado e e peça selecionada
			pecaSelecionada=-1;//isso para deselecionar a peça
			ladoAJogar="";

			System.out.println("ClienteThread: "+nomeJogador+" enviou a peça: ");

		}

		//Descolore peças possíveis
		pecaPossivel=null;
		indicesPecasPossiveis=null;

		//Atualiza as novas cores da peça desse jogador 
		colorePecasPossiveis();

		return pecaAEnviar;
	}

	public void puxaUmaPeca(PecaDomino p) throws RemoteException {

		//Recebe a peça enviada pelo Servidor
		PecaDomino pecaPuxada=p;

		System.out.println("Cliente: "+nomeJogador+" recebeu esta Peça puxando: "+ pecaPuxada.getLadoEsquerdo()+"|"+pecaPuxada.getLadoDireito());

		//Adiciona a peça que foi puxada ou comprada na lista de peças do jogador
		pecasJogador.add(pecaPuxada);
		//Aumenta o tamanho do scroll das peças dos jogadores
		desenhoPecasDoJogador.setPreferredSize(new Dimension (100+90*pecasJogador.size(), 210));

		//Desenha a nova peça no JPanel
		desenhoPecasDoJogador.repaint();

		desenhoDasPecasDaMesa.repaint();

		painelDeConteudo.repaint();

		frame.validate();

		//Avisa todos Jogadores que ele comprou uma peça
		serv.enviaATodosClientes(nomeJogador+": Comprei uma peça.");
	}

	//Atualiza as peças que estão na mesa e pontuação
	public void atualizaGUI(ArrayList <PecaDomino> pecaDaMesaLocal,ArrayList<String> nomesDosJogadores,ArrayList<Integer> pontuacoesJogadores) throws RemoteException {
		//Recebe um ArrayList de Peças que estão na mesa

		
		
		System.out.println("Cliente: "+nomeJogador+" Recebeu as peças da Mesa");

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

	//Habilita o botão de pronto desse Jogador
	public void habilitaPronto() throws RemoteException {
		jogar.setEnabled(true);
		pecasJogador=new ArrayList<PecaDomino>();
	}

	//Faz o somatório dos valores das peça pra saber quem ganhou
	public int jogoTrancado() throws RemoteException {
		int somaPecas=0;
		for(int i=0;i<pecasJogador.size();i++){
			somaPecas=somaPecas+pecasJogador.get(i).getLadoDireito()+pecasJogador.get(i).getLadoEsquerdo();
		}
		System.out.println("Somatório das peças desse jogador: "+somaPecas);

		//Avisa a todos o valor da soma das suas peças 
		serv.enviaATodosClientes(nomeJogador+": Soma das minhas peças = "+somaPecas);

		//Envia a quantidade da soma das peças ao servidor
		return somaPecas;
	}

	//Indica se o servidor está lotado ou não
	public void servidorLotador(Boolean a) throws RemoteException {
		servidorLotado=a;		
	}

	//Apaga as peças desse jogador
	public void removerTodasPecasJogador() throws RemoteException {
		while( pecasJogador.size()>0) {
			pecasJogador.remove(0);
		}
	}

}