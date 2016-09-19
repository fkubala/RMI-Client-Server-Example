package main.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.healthmarketscience.rmiio.RemoteInputStream;

/*
 *  Servers interface , which is available for client app
 */

public interface ServerInterface extends Remote {
	/*
	 * Run script on server
	 */
	public File runAbaqusScript(String script, String database)
			throws RemoteException, FileNotFoundException, IOException;

	/*
	 * Get result file
	 */
	public RemoteInputStream getFile(String fileName) throws RemoteException,
			FileNotFoundException, IOException;

}
