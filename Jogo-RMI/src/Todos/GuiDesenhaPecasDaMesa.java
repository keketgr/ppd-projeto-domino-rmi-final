package Todos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GuiDesenhaPecasDaMesa extends JPanel implements MouseListener{

	//pe�as que est�o no centro da mesa
	private ArrayList <PecaDomino> pecasDaMesa;

	//Refer�ncia da Classe ClienteThread que instanciou essa classa
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


		Dimension sz = getSize();

		g.setColor(Color.green);//a cor do proximo objeto
		g.fillRect(0,0,this.getWidth(),this.getHeight());//cria o retangulo

		g.setColor(Color.BLACK);
		g.drawString("Pe�as que os jogadores utilizaram:", 0, 50);

		int distancia=0;
		//removendo as pe�as que estavam no Panel Anterior
		this.removeAll();

		this.addMouseListener(this);

		for(int i=0; i<pecasDaMesa.size(); i++){

			GuiDesenhaUmDominoDaMesa umaPeca =new GuiDesenhaUmDominoDaMesa(pecasDaMesa.get(i),10,30);
			umaPeca.setBounds(5+distancia, 30+50,120, 80);

			this.add(umaPeca);

			distancia=distancia+122;
		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("GuiDesenhaPecasDaMesa: posi��o onde foi clicado:");
		System.out.println("GuiDesenhaPecasDaMesa: X: "+e.getX()+", Y: "+e.getY());

		//pergunta se tem somente uma pe�a 
		if(pecasDaMesa.size()==1){
			//pegar a metade da 1 pe�a
			if(e.getX()>5&&e.getX()<65&&e.getY()>80&&e.getY()<160){
				cliente.ladoAJogar="Esquerdo";				
				////cliente.enviarMsg("Escolhi pe�a!");
				
				try {
					cliente.serv.recebeLadoAJogar("Esquerdo");
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}

			//seleciona o lado direito da pe�a mais a direita
			if(e.getX()>65&&e.getX()<125&&e.getY()>80&&e.getY()<160){
				cliente.ladoAJogar="Direito";		
				////cliente.enviarMsg("Escolhi pe�a!");
				
				try {
					cliente.serv.recebeLadoAJogar("Direito");
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else{
			if(pecasDaMesa.size()>1){
				//seleciona o lado esquerdo da pe�a mais a esquerda
				if(e.getX()>5&&e.getX()<65&&e.getY()>80&&e.getY()<160){
					cliente.ladoAJogar="Esquerdo";	
					////cliente.enviarMsg("Escolhi pe�a!");
					
					try {
						cliente.serv.recebeLadoAJogar("Esquerdo");
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				//seleciona o lado direito da pe�a mais a direita
				if(e.getX()>(5+122*(pecasDaMesa.size()-1)+60)&&e.getX()<(5+122*(pecasDaMesa.size()-1)+120)&&e.getY()>80&&e.getY()<160){
					cliente.ladoAJogar="Direito";	
					////cliente.enviarMsg("Escolhi pe�a!");
					
					try {
						cliente.serv.recebeLadoAJogar("Direito");
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}				
			}
		}


	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
