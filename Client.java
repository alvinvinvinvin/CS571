import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;


public class Client 
{
	
	public static String ip;
	public static String portNum;
	static String username;
	static String password;
	static Socket conToServerSocket;
	static String cmdQuit ;
	
	static Date currentTime = new Date();
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	static String strDate = formatter.format(currentTime);
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args)
	{
		while(true){
			System.out.println("welcome to use! whenever you wanna quit, please enter \"quit\" and go back to previous menu please enter \"back\"");
			System.out.println("please input ip:");
			Scanner scanIp = new Scanner(System.in);
			cmdQuit = scanIp.next();
			if(cmdQuit.equalsIgnoreCase("quit"))
				System.exit(0);
			ip = cmdQuit;
			System.out.println("please input port number:");
			Scanner scanPort = new Scanner(System.in);
			cmdQuit = scanPort.next();
			if(cmdQuit.equalsIgnoreCase("quit"))
				System.exit(0);
			portNum = cmdQuit;
			System.out.println("Connecting to the server.......");
			
			try
			{

					while(true)
					{
						conToServerSocket = new Socket(InetAddress.getByName(ip),Integer.parseInt(portNum));
						OutputStream ops = conToServerSocket.getOutputStream();
						PrintStream psOutput = new PrintStream(ops);
						BufferedReader inSock = new BufferedReader( new 
								InputStreamReader(conToServerSocket.getInputStream()));
						System.out.println("Connection is successed! Press 1 to log in, 2 to register, 3 to quit....");
						
						Scanner scanCmd1 = new Scanner(System.in);
						String strCmd = scanCmd1.next();
						if(strCmd.equalsIgnoreCase("quit"))
							break;
						int cmdLogin = Integer.parseInt(strCmd);			
						
						//log in
						if(cmdLogin == 1){
							System.out.println("please input username: ");
							Scanner scanUsername = new Scanner(System.in);
							username = scanUsername.next();
							if(username.equalsIgnoreCase("quit")){
								break;
							}
							
							System.out.println("please input password");
							Scanner scanPwd = new Scanner(System.in);
							password = scanPwd.next();
							if(password.equalsIgnoreCase("quit")){
								break;
							}
							
							psOutput.println(MsgCodes.LOGIN+"|"+username+"|"+password);
							//System.out.println(username+","+password+","+MsgCodes.LOGIN);
							//wait for replying
							
							String strFromServer = inSock.readLine();
							//System.out.println(strFromServer);
							
							if(strFromServer.equals(MsgCodes.SUCCEED)){
								
								//Scanner scanFS = new Scanner(inSock.readLine());
								System.out.println("logging in successful!");
								//
								//operations in the next menu
								operation(psOutput, inSock);
								//end of operations
								
								break;
							}
							else{
								System.out.println("Information is wrong or some one is using your account!");
							}
						}
						
						//registering
						else if (cmdLogin == 2)
						{
							while(true)
							{
								System.out.println("please input username for registering: ");
								Scanner scanUsername = new Scanner(System.in);
								username = scanUsername.next();
								if(username.equalsIgnoreCase("back")){
									break;
								}
								
								System.out.println("please input password for registering: ");
								Scanner scanPwd = new Scanner(System.in);
								password = scanPwd.next();
								if(password.equalsIgnoreCase("back")){
									break;
								}
								
								psOutput.println(MsgCodes.INSERT+"|"+username+"|"+password);
								//waiting
								
								String strFromServer = inSock.readLine();
								
								if(strFromServer.equals(MsgCodes.SUCCEED)){
									System.out.println("registering is successful! Now you can use your new account to log in!");
									break;
								}
								else{
									System.out.println("failed! accout is already existed or other error, please try again or go back to previous menu");
								}
							}
						}
						
						//quit
						else{
							System.out.println("quit");
							inSock.close();
							conToServerSocket.close();
							break;
						}
					}
			}catch(IOException e){System.out.println("Connection is failed");}
		}
	}
	@SuppressWarnings("resource")
	public static void operation( PrintStream psOut, BufferedReader inSocket)
	{
		while(true)
		{
			
			System.out.println("enter 0 to log out, enter 1 to check messages to you from subscribed one, "
					+ "2 to check users who can be subscribed, 3 to send message,"
					+ "4 to subscribe one, 5 to unsubscribe one, *6 to delete an user*");
			Scanner scan = new Scanner(System.in);
			String strCmd = scan.next();
			if(strCmd.equalsIgnoreCase("back"))
				return;
			int cmd = Integer.parseInt(strCmd);
			//0quit
			if(cmd == 0){
				return;
			}
			
			//1check messages
			else if(cmd == 1){
				try{
					while(true)
					{
						System.out.println("please enter the date (yyyyMMdd) for getting message after it: ");
						Scanner scanDate = new Scanner(System.in);
						String date = scanDate.next();
						if(date.equalsIgnoreCase("back"))
							break;
						psOut.println(MsgCodes.GETCHATLOG+"|"+username+"|"+date);
						
						String strFS = inSocket.readLine();
						
						if(strFS.length() > 0){
							//split reply into logs 1 by 1
							String[] strLogs = strFS.split(",");				
							//add explanation to each log
							for(int i = 0; i<strLogs.length; i++)
							{		
								String[] logs = strLogs[i].split("\\|");
								System.out.println("from: "+logs[0]+"; content:\" "+logs[1]+"\"; sent date: "+logs[2]);
								logs = null;
							}
							break;
						}else System.out.println("no messages after that date..");
					}
				}
				catch(IOException e){System.out.println(e.getMessage());}
			}
			
			//2list users
			else if(cmd == 2){
				try{
					psOut.println(MsgCodes.GETNAME);
					//System.out.println("here1");
					
					String strFS = inSocket.readLine();
					
					System.out.println(strFS);
					
				}
				catch(IOException e){System.out.println(e);}
			}
			
			//3send message
			else if(cmd == 3){
				try{
					
					while(true)
					{
						psOut.println(MsgCodes.GETSUBSCRIBE+"|"+username);
						String sbUsers = inSocket.readLine();
						System.out.println("These are all the people have subscribed you:");
						System.out.println(sbUsers);
						
						
						System.out.println("please enter message(press \"enter\" to send):");
						Scanner scanMsg = new Scanner(System.in);
						String msg = scanMsg.next();
						if(msg.equalsIgnoreCase("back"))
							break;
						
						psOut.println(MsgCodes.SENDMSG+"|"+username+"|"+msg+"|"+strDate);
						
						String strFS = inSocket.readLine();
						
						if(strFS.equals(MsgCodes.SUCCEED)){
							System.out.println("sending is successful. enter 1 to go on, 0 to quit");
							Scanner scanCmd = new Scanner(System.in);
							if(scanCmd.nextInt() == 1){
								
							}
							else break;
						}else System.out.println("sending failed, please try again");
					}
					
				}
				catch(IOException e){System.out.println(e);}
			}
			
			//4subscribe
			else if (cmd == 4){
				try{
					while(true){
						psOut.println(MsgCodes.GETNAME);
						
						String strFS = inSocket.readLine();
						System.out.println("which author do you want to subscribe?");
						System.out.println(strFS);
						Scanner scanSbName = new Scanner(System.in);
						String author = scanSbName.next();
						if(author.equalsIgnoreCase("back"))
							break;
						
						psOut.println(MsgCodes.SUBSCRIBE+"|"+username+"|"+author);
						
						String rsFS = inSocket.readLine();
						if(rsFS.equals(MsgCodes.SUCCEED)){
							System.out.println("successful subscribing! enter 1 to go on, 0 to quit");
							Scanner scanCmd = new Scanner(System.in);
							if(scanCmd.nextInt() == 1){
					
							}
							else break;
						}else System.out.println("subscribing failed or you have already subscribed him/her, please try again");
					}
				}
				catch(IOException e){System.out.println(e);}
			}
			
			//5unsubscribe
			else if(cmd == 5){
				try{
					while(true){
						psOut.println(MsgCodes.GETSUBSCRIBE+"|"+username);
						
						String strFS = inSocket.readLine();
						System.out.println("which author do you want to unsubscribe?");
						System.out.println(strFS);
						
						Scanner scanName = new Scanner(System.in);
						String author = scanName.next();
						if(author.equalsIgnoreCase("back"))
							break;
						
						psOut.println(MsgCodes.UNSUBSCRIBE+"|"+author+"|"+username);
						
						String rsFS = inSocket.readLine();
						if(rsFS.equals(MsgCodes.SUCCEED)){
							System.out.println("successful unsubscribing! enter 1 to go on, 0 to quit");
							Scanner scanCmd = new Scanner(System.in);
							if(scanCmd.nextInt() == 1){
								
							}
							else break;
						}else System.out.println("unsubscribing failed, please try again");
					}
				}catch(IOException e){System.out.println(e.getMessage());}
			}
			
			//6delete an user
			else if(cmd == 6){
				try{
					while(true){
						psOut.println(MsgCodes.GETNAME);
						
						String strFS = inSocket.readLine();
						System.out.println("which user do you want to delete?");
						System.out.println(strFS);
						
						Scanner scanName = new Scanner(System.in);
						String nameDeleted = scanName.next();
						if(nameDeleted.equalsIgnoreCase("back"))
							break;
						
						psOut.println(MsgCodes.DELETE+"|"+nameDeleted);
						
						String rsFS = inSocket.readLine();
						if(rsFS.equals(MsgCodes.SUCCEED)){
							System.out.println("successful deleting! enter 1 to go on, 0 to quit");
							Scanner scanCmd = new Scanner(System.in);
							if(scanCmd.nextInt() == 1){
								
							}
							else break;
						}else System.out.println("failed deleting, please try again");
					}
				}catch(IOException e){System.out.println(e.getMessage());}
			}
		}
	}
	
}
