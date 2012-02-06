package Todos;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GuiServidor implements ActionListener {

	JFrame frame;
	Container painelDeConteudo;
	JMenuItem autores;
	JMenuItem itemSair;
	JMenu menu;
	JMenuBar bar;
	JTextArea areadetexto;

	public GuiServidor() {
		//Cria e configura a janela do Servidor
		frame = new JFrame("Servidor");
		frame.setSize(400, 300);
		//Não redimensionar
		frame.setResizable(false);

		//Para fechar o programa quado fechar a GUI
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
		bar.setBounds(0, 0, 400, 20);
		bar.add(menu);

		painelDeConteudo.add(bar);

		//Nome Chat
		JLabel label = new JLabel("Conexões: ");
		label.setBackground(Color.DARK_GRAY);
		label.setBounds(0,22,100,30);
		painelDeConteudo.add(label);

		//Campo que contém as mensagens enviadas e recebidas
		areadetexto=new JTextArea();
		areadetexto.setEditable(false);//não deixar escrever onde as mensagens do chat chegam	
		JScrollPane ps2 = new JScrollPane(areadetexto);
		ps2.setBounds(0,60,390, 200); 

		painelDeConteudo.add(ps2);

		frame.setVisible(true);	
	}

	//Escreve no TextArea da GUI do Servidor
	public void escreverNoTexto(String msg){
		areadetexto.append(msg+"\n");
		areadetexto.setCaretPosition(areadetexto.getText().length());
	}

	public void actionPerformed(ActionEvent e) {
		//Fecha o jogo e conexão com o servidor
		if(e.getSource()==itemSair){
			System.exit(1);  
		}

		//Mostra o nome dos autores do Jogo
		if(e.getSource() == autores){
			JOptionPane.showMessageDialog(null,"Wandemberg Rodrigues Gomes");
		}				
	}

	public static void main(String[] args) {
		//Cria a parte gráfica do Servidor
		GuiServidor parteGrafica=new GuiServidor();

		//Cria a parte lógica do Servidor 
		Thread servidor;
		try {
			servidor = new Thread(new ServidorThread(parteGrafica));
			servidor.start();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}


}
