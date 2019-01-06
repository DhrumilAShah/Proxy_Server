import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Web {
	public static final int HTTP_PORT = 80;
	public static final int MAX_BUFFER_SIZE = 65535;
 public static final int MAX_URL_SIZE = 65535;

	public static void printCharArray(char[] a) {
		for (char b : a)
			System.out.print(b);
	}

	

	public static boolean isBlocked(char[] website,String fileName) {
		File file = new File(fileName);
		int websiteLen=0;
		while(website[websiteLen]!='\0')
			websiteLen++;
		try(FileInputStream fin = new FileInputStream(file)){
			int fileByteCount = (int) file.length();
			byte[] fileContents = new byte[fileByteCount];
			fin.read(fileContents, 0, fileByteCount);
			int i=0;
			int currBlockedUrlLen=0;
			byte[] currBlockedUrl = new byte[65535];
			while(i<fileByteCount)
			{
				if(fileContents[i]==10 || i==fileByteCount-1)
				{
					i++;
					if (currBlockedUrlLen == websiteLen) {
						int j;
						for (j = 0; j < currBlockedUrlLen; j++)
						{
							if ((char)currBlockedUrl[j] != (website[j])) {
								break;
							}
						}
						if (j == websiteLen) {
							return true;
						}
					}
					currBlockedUrl = new byte[65535];
					currBlockedUrlLen=0;
					continue;
				}
				if(currBlockedUrlLen==65535)
					return false;
				currBlockedUrl[currBlockedUrlLen]=fileContents[i];
				currBlockedUrlLen++;
				i++;
			}
		} 
		catch (FileNotFoundException e) {
			System.out.println("File not found. Enter valid file name");
			System.exit(1);
		} 
		catch (IOException e) {
			System.out.println("Something went wrong. Try again.");
			System.exit(1);
		}
		return false;
	}

	public static Object[] doParse(InputStream clientInputStream) throws IOException {
   Object[] objArr  = new Object[2];
		byte[] data = new byte[MAX_BUFFER_SIZE];
		int bytesread = clientInputStream.read(data, 0, data.length);
		int j = 0;
		while (j < bytesread && data[j] != 0
				&& !(data[j] == 13 && data[j + 1] == 10 && data[j + 2] == 13 && data[j + 3] == 10))
			j++;
		bytesread = j;
		char requestChar[] = new char[bytesread];
		byte requestByte[] = new byte[bytesread];
		for (int i = 0; i < requestChar.length; i++) {
			char temp = (char) data[i];
			requestChar[i] = temp;
			requestByte[i] = data[i];
		}
     objArr[0]=requestChar;
     objArr[1]=data;
		return objArr;
	}

	public static void doHttp(byte[] reqObj,OutputStream clientOutputStream,InetAddress host) throws UnknownHostException, IOException {
		Socket s = new Socket();
		s.connect(new InetSocketAddress(host, HTTP_PORT));
		s.getOutputStream().write(reqObj,0,reqObj.length);
		byte[] resObj = new byte[2000];
		int bytesRead;
		while((bytesRead=s.getInputStream().read(resObj, 0, resObj.length))!=-1) {
			clientOutputStream.write(resObj,0,bytesRead);
		}
		s.close();
	}

	public static char[] getHost(char[] reqObj, int bytesRead) {
		char[] host = new char[65535];
		int k = 0;
		for (int z = 0; z < reqObj.length; z++) {
			if (reqObj[z] == 'H' && reqObj[z + 1] == 'o' && reqObj[z + 2] == 's' && reqObj[z + 3] == 't'
					&& reqObj[z + 4] == ':' && reqObj[z + 5] == ' ') {
				int startPosition = z + 6;
				while ((startPosition < bytesRead && reqObj[startPosition] != '\r') && k < 65535) {
					host[k++] = reqObj[startPosition++];
				}
				break;
			}
		}
		return host;
	}

	public static void printURL(char[] reqObj) {
   try{
		char requestedUrl[] = new char[MAX_URL_SIZE];
		int i = 4;
		while (reqObj[i] != ' ') {
			requestedUrl[i - 4] = reqObj[i];
			i++;
		}
		System.out.print("REQUEST : ");
		printCharArray(requestedUrl);
    System.out.println("");
    }catch(ArrayIndexOutOfBoundsException e){
      System.out.println("Array Out Of bound for URL: "+new String(reqObj));
    }
	}

	// main function
	public static void main(String args[]) throws Exception {
		if (args.length < 1) {
			System.err.println("Please enter port number and file name");
			System.exit(1);
		}
		if (args.length < 2) {
			System.err.println("Please enter file name");
			System.exit(1);
		}
		int portNumber = Integer.parseInt(args[0]);
		if (portNumber > 65535 || portNumber <= 1024) {
			System.err.println("Enter a valid port number");
			System.exit(1);
		}
		
        System.out.println("Stage 2 program by (m2-vl73@njit.edu) listening on port ("+args[0]+")");
		ServerSocket server = null;
		Socket clientSocket = null;
		InputStream inputstream = null;
		OutputStream outputstream = null;
		do {
			try {
				server = new ServerSocket();
				InetSocketAddress socketaddress = new InetSocketAddress(portNumber);
				server.bind(socketaddress, 5);
				clientSocket = server.accept();
				inputstream = clientSocket.getInputStream();
				outputstream = clientSocket.getOutputStream();
                Object[] reqObj = doParse(inputstream);
				char request[] = (char[])reqObj[0];
				if (request.length >= 3 && request[0] == 'G' && request[1] == 'E' && request[2] == 'T') {
					printURL(request);
					char host[] = new char[65535];
					host = getHost(request, request.length);
					if (isBlocked(host,args[1])) {
                         outputstream.write("HTTP/1.1\nContent-type: text/html; charset=UTF-8\n\n".getBytes());
						 outputstream.write("<center><br><br><h2>This Website Is Blocked</h2></center>".getBytes());
                         outputstream.flush();
             
					} else {
                         InetAddress inetHost=dns(host);
						 doHttp((byte[])reqObj[1],outputstream,inetHost);
					}
				}
			} catch (UnknownHostException b) {
                outputstream.write("HTTP/1.1\nContent-type:text/html; charset=UTF-8\n\n".getBytes());
                outputstream.write("<center><br><br><h2>Status code: 404 - This site can't be reached.</h2>".getBytes());
				outputstream.write("ERR_NAME_NOT_RESOLVED</center>".getBytes());	
                outputstream.flush();
                b.printStackTrace();
            } catch (BindException b) {
				System.err.println("Address already in use, try some another port number");
				b.printStackTrace();
				System.exit(1);
			} catch (SocketException se) {
				se.printStackTrace();
			} catch (NullPointerException n) {
				System.err.println("Something went wrong, try again");
				n.printStackTrace();
			} catch (IOException e) {
				System.err.println("Exception caught when trying to listen for a connection");
				e.printStackTrace();
			} finally {
				if (null != outputstream)
					outputstream.close();
				if (null != inputstream)
					inputstream.close();
				if (null != clientSocket)
					clientSocket.close();
				if (null != server)
					server.close();
			}
		} while (true);
	}

	private static InetAddress dns(char chars[]) throws UnknownHostException, IOException {
		InetAddress inetAddress[] = InetAddress.getAllByName(new String(chars));

		double diff = 5000, tmp;
		InetAddress preferredIP = null;
		long start, end;

		for (int i = 0; i < inetAddress.length; i++) {
			if (inetAddress[i] instanceof Inet6Address)
				continue;
			else {
				try {
					Socket s = new Socket(); // Fix for improper preferred ip
					InetSocketAddress pref = new InetSocketAddress(inetAddress[i], 80);
					start = System.nanoTime();
					s.connect(pref);
					end = System.nanoTime();
					s.close();
					tmp = (end - start) / 1000000;
					if (tmp < diff) {
						diff = tmp;
						preferredIP = inetAddress[i];
					}
				} catch (IOException e) {
					continue;
				}
			}
		}
		if (preferredIP == null)
			preferredIP = inetAddress[0];
		return preferredIP;
	}
}
