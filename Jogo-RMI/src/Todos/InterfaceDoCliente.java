package Todos;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import Todos.PecaDomino;

//Servi�os dispon�veis ao cliente
public interface InterfaceDoCliente extends Remote{

	//Retorna as pe�as da m�o do jogador
	ArrayList<PecaDomino> pecasJogador() throws RemoteException;

	//Recebe uma String do Servidor
	void recebeString(String a)throws RemoteException;

	//Coloca o valor recebido do chat na GUI do chat
	void recebeMensagemDoChat(String s) throws RemoteException;

	//Recebe uma pe�a do Servidor
	void recebeUmaPeca(PecaDomino p)throws RemoteException;

	//Recebe uma ArrayList de Pe�as do Servidor
	void recebeListaDePecas(ArrayList<PecaDomino> pecas) throws RemoteException;

	//Recebe um Object do servidor
	void recebeObject(Object o) throws  RemoteException;

	//Recebe uma ArrayList de Pe�as do Servidor, ou seja, pe�as iniciais
	void recebe7Pecas(ArrayList<PecaDomino> pecas) throws RemoteException;

	//Espera que o jogador envia 1 pe�a
	PecaDomino jogaPrimeiraPeca() throws RemoteException;

	//Coloca na mesa a pe�a que o jogador escolheu 
	PecaDomino jogaProximaPeca(PecaDomino pontas) throws RemoteException;

	//Adiciona uma pe�a nesse cliente, pois ele n�o tem pe�a poss�vel a jogar
	void puxaUmaPeca(PecaDomino p) throws RemoteException;

	//Atualiza a GUI do Cliente
	void atualizaGUI(ArrayList<PecaDomino> a) throws RemoteException;

	//Habilita o bot�o de pronto para jogar uma nova partida
	void habilitaPronto() throws RemoteException;

	//Retorna a soma das pe�as que est�o na m�o do jogador
	int jogoTrancado() throws RemoteException;

	void servidorLotador(Boolean a) throws RemoteException;
	
	void removerTodasPecasJogador() throws RemoteException;

}
