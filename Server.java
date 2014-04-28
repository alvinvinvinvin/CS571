import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public class Server implements Runnable {
	Socket clientSocket;
	DataStore data;
	static int cmdQuitPostion;

	public Server(Socket s, DataStore d) {
		clientSocket = s;
		data = d;
	}

	public void run() {
		try {
			BufferedReader inSock = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			PrintStream psToClient = new PrintStream(
					clientSocket.getOutputStream());
			// Scanner scanInSock = new Scanner(inSock.readLine());
			String msgFromC = inSock.readLine();
			System.out.println(msgFromC);

			String[] msgFromCs = msgFromC.split("\\|");

			while (!msgFromCs[0].equalsIgnoreCase("quit")) 
			{
			
				// get all users' names
				if (msgFromCs[0].equals(MsgCodes.GETNAME))
				{
					//System.out.println("here2");
					String usernames = data.getUsernames();
					System.out.println("here3" + usernames);
					psToClient.println(usernames);
				}
				
				//log in
				else
				if (msgFromCs[0].equals(MsgCodes.LOGIN)) 
				{
					String username = msgFromCs[1];
					// String statusTest = data.getStatus(username);
					// System.out.println(username+"'s status is "+statusTest);
					String password = msgFromCs[2];
					if (data.loginCheck(username, password)) 
					{
						System.out.println(msgFromCs[2] + msgFromCs[0]);
						// data.onlineUpdate(username);
						System.out.println(username + " is online...");
						psToClient.println(MsgCodes.SUCCEED);
						//

					} else {
						psToClient.println(MsgCodes.FAIL);
					}

				}
				//insert user
				else
				if (msgFromCs[0].equals(MsgCodes.INSERT)) 
				{
					String username = msgFromCs[1];
					String password = msgFromCs[2];
					if (data.checkUserExisted(username)) 
					{
						psToClient.println(MsgCodes.FAIL);
					} else {
						data.insertUser(username, password);
						psToClient.println(MsgCodes.SUCCEED);
					}
				}
				
				//get chatlog by username after "date"
				else if(msgFromCs[0].equals(MsgCodes.GETCHATLOG)){
					String reader = msgFromCs[1];
					String date = msgFromCs[2];
					String chatlog = data.getChatlog(reader, date);
					psToClient.println(chatlog);
				}
				
				//send "message" to all subscribers from "author" when "date"
				else if(msgFromCs[0].equals(MsgCodes.SENDMSG)){
					String author =msgFromCs[1];
					String content = msgFromCs[2];
					String date = msgFromCs[3];
					if(data.createChatlog(author, content, date))
					{
						psToClient.println(MsgCodes.SUCCEED);
					}
					else psToClient.println(MsgCodes.FAIL);
				}
				
				//get subscribe
				else if(msgFromCs[0].equals(MsgCodes.GETSUBSCRIBE)){
					String reader = msgFromCs[1];
					String authors = data.getSubscribe(reader);
					psToClient.println(authors);
				}
				
				
				
				//subscribes
				else if(msgFromCs[0].equals(MsgCodes.SUBSCRIBE)){
					String reader = msgFromCs[1];
					String author = msgFromCs[2];
					if(data.existedSubscribe(reader, author)){
						psToClient.println(MsgCodes.FAIL);
					}else{
						if(data.createSubscribe(reader, author))
							psToClient.println(MsgCodes.SUCCEED);
						else psToClient.println(MsgCodes.FAIL);
					}
				}
				
				//unsubscribes
				else if(msgFromCs[0].equals(MsgCodes.UNSUBSCRIBE)){
					String author = msgFromCs[1];
					String reader = msgFromCs[2];
					if(data.unSubscribe(author, reader)){
						psToClient.println(MsgCodes.SUCCEED);
					}else psToClient.println(MsgCodes.FAIL);
					
					
				}
				
				//delete an user
				else if(msgFromCs[0].equals(MsgCodes.DELETE)){
					String username = msgFromCs[1];
					if(data.deleteUser(username)){
						psToClient.println(MsgCodes.SUCCEED);
					}else psToClient.println(MsgCodes.FAIL);
				}

				msgFromC = inSock.readLine();
				msgFromCs = msgFromC.split("\\|");

			}
			
			// data.offlineUpdate(msgFromCs[0]);
			psToClient.close();
			inSock.close();

			// logincheck

		} catch (Exception e) {

			System.out.println("err");
			System.out.println(clientSocket.getInetAddress().toString() + "is offline...");
		}
	}

	static Date currentTime = new Date();
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	static String strDate = formatter.format(currentTime);

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			System.out.println("local IP is: " + addr.toString());
		} catch (Exception e) {
			System.out.println("can't get IP");
			e.printStackTrace();
		}

		System.out.println("today's date is: " + strDate);
		System.out.println("Server is started!");
		System.out.println("please input SeverSocket port number: ");
		Scanner scanSSPort = new Scanner(System.in);
		int serverSocketPort = scanSSPort.nextInt();

		
		try {
			DataStore d = new DataStore("data.db");
			System.out.println("Connected with database!");
			ServerSocket myServerSock = new ServerSocket(serverSocketPort);
			while (true) {
				Socket client = myServerSock.accept();
				System.out.println("client: " + client.getInetAddress()
						+ " is connected with server!");
				Server s = new Server(client, d);
				Thread t = new Thread(s);
				t.start();
			}
		} catch (Exception e) {
			System.out.print("I am wrong");
			e.printStackTrace();
		}
	}

}
