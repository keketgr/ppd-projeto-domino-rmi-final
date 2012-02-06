package Todos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GuiDesenhaPecasDaMesa extends JPanel implements MouseListener{

	//Pe�as que est�o no centro da mesa
	private ArrayList <PecaDomino> pecasDaMesa;

	//Refer�ncia da Classe ClienteThread, onde foi criado um objeto dele
	ClienteThread cliente;

	public GuiDesenhaPecasDaMesa( ClienteThread referencia) {
		pecasDaMesa=new ArrayList<PecaDomino>();
		cliente=referencia;
	}


	public ArrayList<PecaDomino> getPecasDaMesa() {
		return pecasDaMesa;
	}


	public void setPecasDaMesa(ArrayList<PecaDomino> pecasDaMesa) {
		this.pecasDaMesa = pecasDaMesa;
	}


	public void paintComponent(Graphics g){

		//Apaga o que tinha antes
		g.setColor(Color.green);//a cor do proximo objeto
		g.fillRect(0,0,this.getWidth(),this.getHeight());//cria o retangulo

		//Muda a cor para escrever a letra desta cor 
		g.setColor(Color.BLACK);
		g.drawString("Pe�as que os jogadores utilizaram:", 0, 50);

		int distancia=0;

		//Remove todas as pe�as que estavam no Panel Anterior
		this.removeAll();

		this.addMouseListener(this);

		//Adiciona cada um dos pain�is das pe�as que est�o na mesa
		for(int i=0; i<pecasDaMesa.size(); i++){

			//Cria um painel com o desenho de uma das Pe�a da mesa
			GuiDesenhaUmDominoDaMesa umaPeca =new GuiDesenhaUmDominoDaMesa(pecasDaMesa.get(i),10,30);

			//Local onde ser� adicionado o painel com o valor das pe�as
			umaPeca.setBounds(5+distancia, 30+50,120, 80);

			//Adiciona a pe�a a esse JPanel que � o central
			this.add(umaPeca);

			distancia=distancia+122;
		}
	}

	//Trata o evento da escolha do lado a adicionar a pe�a
	public void mouseClicked(MouseEvent e) {
		System.out.println("GuiDesenhaPecasDaMesa: posi��o onde foi clicado:");
		System.out.println("GuiDesenhaPecasDaMesa: X: "+e.getX()+", Y: "+e.getY());

		//Pergunta se tem somente uma pe�a 
		if(pecasDaMesa.size()==1){
			//Pega a metade da 1 pe�a
			if(e.getX()>5&&e.getX()<65&&e.getY()>80&&e.getY()<160){
				//Atualiza o valor da vari�vel lado a jogar
				cliente.ladoAJogar="Esquerdo";				

				try {
					//Avisa o servidor o lado escolhido
					cliente.serv.recebeLadoAJogar("Esquerdo");
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}

			//Seleciona o lado direito da pe�a mais a direita
			if(e.getX()>65&&e.getX()<125&&e.getY()>80&&e.getY()<160){
				//Atualiza o valor da vari�vel lado a jogar
				cliente.ladoAJogar="Direito";		

				try {
					//Avisa o servidor o lado escolhido
					cliente.serv.recebeLadoAJogar("Direito");
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		}
		else{
			if(pecasDaMesa.size()>1){
				//Seleciona o lado esquerdo da pe�a mais a esquerda
				if(e.getX()>5&&e.getX()<65&&e.getY()>80&&e.getY()<160){
					//Atualiza o valor da vari�vel lado a jogar
					cliente.ladoAJogar="Esquerdo";	

					try {
						//Avisa o servidor o lado escolhido
						cliente.serv.recebeLadoAJogar("Esquerdo");
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				}

				//Seleciona o lado direito da pe�a mais a direita
				if(e.getX()>(5+122*(pecasDaMesa.size()-1)+60)&&e.getX()<(5+122*(pecasDaMesa.size()-1)+120)&&e.getY()>80&&e.getY()<160){
					//Atualiza o valor da vari�vel lado a jogar
					cliente.ladoAJogar="Direito";	

					try {
						//Avisa o servidor o lado escolhido
						cliente.serv.recebeLadoAJogar("Direito");
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				}				
			}
		}
		cliente.colorePecasPossiveis();

	}


	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e)  {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e){}

}
