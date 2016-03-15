package model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {
	public String communication(String array) throws RemoteException;

	public String phase_1(int K) throws RemoteException;

	public String phase_2(int K, String s) throws RemoteException;
}
