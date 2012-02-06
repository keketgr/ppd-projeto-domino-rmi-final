package Todos;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface InterfaceDoServidor extends Remote {

	//	serviços que o servidor disponibiliza a todos
	//String inverter(String msg) throws  RemoteException;

	//Cadastra Cliente no servidor de nomes 
	void login(String a) throws  RemoteException;

	//Envia String a todos clientes, ou seja, serviço usado para o chat
	void enviaATodosClientes(String es) throws  RemoteException;

	//Recebe String enviada do cliente
	//void recebeString(String a) throws  RemoteException;	

	//O servidor receber uma ArrayList de Peças do Cliente
	//void recebeListaDePecas(ArrayList<PecaDomino> pecas) throws RemoteException;

	//Recebe Object de um cliente
	//void recebeObject(Object o) throws  RemoteException;

	void recebeLadoAJogar(String a)throws RemoteException;

	void ganhouJogo(String a,String nomeJogador)throws RemoteException;

	void prontoParaJogar(Boolean a,String nomeCliente)throws RemoteException;

	//PecaDomino pontasJogo()throws RemoteException;


}