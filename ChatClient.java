import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This is a client that will connect to a chat server and allow messaging
 * between this client and another.
 * @author Shane_Panagakos
 */
public class ChatClient
{

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chat Room");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);

    public ChatClient()
    {
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "South");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        /**
         * Sends the contents of the text field to the server and
         * then clears the text field.
         */
        textField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }
    /**
     * Prompts for the address of the server
     * @return the address of the server
     */
    private String getServerAddress()
    {
        return JOptionPane.showInputDialog(frame,
                "Enter IP Address of the Server:", "Welcome to the chat room",
            JOptionPane.QUESTION_MESSAGE);
    }
    /**
     * Prompts for a screen name
     * @return the users screen name
     */
    private String getName()
    {
        return JOptionPane.showInputDialog(frame, "Choose a screen name:",
            "Name selection", JOptionPane.PLAIN_MESSAGE);
    }
    /**
     * Connects to the chat server
     * @throws Exception
     */
    private void run() throws Exception
    {
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 5678);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        while (true)
        {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME"))
                out.println(getName());
            else if (line.startsWith("NAMEACCEPTED"))
                textField.setEditable(true);
            else if (line.startsWith("MESSAGE"))
                messageArea.append(line.substring(8) + "\n");
        }
    }
    /**
     * starts the chat client and window.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
