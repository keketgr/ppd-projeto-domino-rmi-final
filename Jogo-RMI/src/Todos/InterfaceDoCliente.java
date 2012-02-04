package Todos;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import Todos.PecaDomino;

//Serviços disponíveis ao cliente
public interface InterfaceDoCliente extends Remote{

	//Retorna as peças da mão do jogador
	ArrayList<PecaDomino> pecasJogador() throws RemoteException;

	//Recebe uma String do Servidor
	void recebeString(String a)throws RemoteException;

	//Coloca o valor recebido do chat na GUI do chat
	void recebeMensagemDoChat(String s) throws RemoteException;

	//Recebe uma peça do Servidor
	void recebeUmaPeca(PecaDomino p)throws RemoteException;

	//Recebe uma ArrayList de Peças do Servidor
	void recebeListaDePecas(ArrayList<PecaDomino> pecas) throws RemoteException;

	//Recebe um Object do servidor
	void recebeObject(Object o) throws  RemoteException;

	//Recebe uma ArrayList de Peças do Servidor, ou seja, peças iniciais
	void recebe7Pecas(ArrayList<PecaDomino> pecas) throws RemoteException;

	//Espera que o jogador envia 1 peça
	PecaDomino jogaPrimeiraPeca() throws RemoteException;

	//Coloca na mesa a peça que o jogador escolheu 
	PecaDomino jogaProximaPeca(PecaDomino pontas) throws RemoteException;

	//Adiciona uma peça nesse cliente, pois ele não tem peça possível a jogar
	void puxaUmaPeca(PecaDomino p) throws RemoteException;

	//Atualiza a GUI do Cliente
	void atualizaGUI(ArrayList<PecaDomino> a) throws RemoteException;

	//Habilita o botão de pronto para jogar uma nova partida
	void habilitaPronto() throws RemoteException;

	//Retorna a soma das peças que estão na mão do jogador
	int jogoTrancado() throws RemoteException;

	void servidorLotador(Boolean a) throws RemoteException;
	
	void removerTodasPecasJogador() throws RemoteException;

}
