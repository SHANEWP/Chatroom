import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This is a server that will allow clients to connect and send messages to
 * one another.
 *
 * To view all clients who are currently online, type "ONLINE" and press
 * enter. A set of the current online users will be printed to the screen.
 *
 * To send a message use the following format:
 * "NAME_OF_RECIPIENT MESSAGE_TO_BE_SENT"
 * The senders name and the message
 * will be sent to both the user and the recipient in the following format:
 * "SENDER_NAME: MESSAGE_SENT
 *
 * If the user enters input that is not formatted correctly, the following
 * message will be sent to the user's screen:
 * "/Invalid input/"
 * @author Shane_Panagakos
 */
public class ChatServer
{
    private static HashMap<String, Socket> names = new HashMap<>();
    private static HashMap<String, PrintWriter> writers = new HashMap<>();

    /**
     * Creates a new Handler when a new client connects to the server.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println("The server is running.");
        ServerSocket listener = new ServerSocket(5678);
        try
        {
            while (true)
                new Handler(listener.accept()).start();
        }
        finally
        {
            listener.close();
        }
    }

    /**
     * A thread class that handles the clients and allows them to send messages
     * to other clients
     */
    private static class Handler extends Thread
    {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket socket)
        {
            this.socket = socket;
            System.out.println(this.socket.getInetAddress()+" has connected");
        }
        /**
         * Prompts the user to enter a screen name and then handles the
         * input and output process between clients. When input has "ONLINE "
         * preceeding it, a set of the current online users will be sent to
         * the client. When sending a message to the designated recipient,
         * the word "MESSAGE " must preceed the message to be sent in order
         * for the message to be displayed on the recipient's screen.
         */
        public void run()
        {
            try
            {

                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true)
                {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null)
                        return;
                    synchronized (names)
                    {
                        if (!names.containsKey(name.toLowerCase()))
                        {
                            names.put(name.toLowerCase(), socket);
                            break;
                        }
                    }
                }

                out.println("NAMEACCEPTED");
                writers.put(name.toLowerCase(), out);

                out.println("MESSAGE Welcome, "+name);

                while (true)
                {
                    String recipient = in.next();
                    String input = in.nextLine();
                    if (input == null&&recipient == null)
                        return;
                    else if(recipient.equalsIgnoreCase("ONLINE"))
                        out.println("MESSAGE "+names.keySet()+input);
                    else if(names.containsKey(recipient.toLowerCase()))
                    {
                        out.println("MESSAGE " + name + ": " + input);
                        writers.get(recipient.toLowerCase()).println(
                                "MESSAGE " + name + ": " + input);
                    }
                    else
                        out.println("MESSAGE /Invalid input/");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (name != null)
                    names.remove(name);
                if (out != null)
                    writers.remove(name);
                try
                {
                    socket.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
