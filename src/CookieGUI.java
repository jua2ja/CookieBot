

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class CookieGUI extends JFrame implements ActionListener{

	/** Serial Version UID. It yells at me otherwise. */
	private static final long serialVersionUID = 8093632984562965734L;
	/** The panel containing the instructions and start button. */
	private JPanel panel;
	/** The start button for the bot*/
	private JButton startButton;
	
	/**
	 * Initializes the GUI
	 */
	public CookieGUI()
	{
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		try {GlobalScreen.registerNativeHook();}
		catch(NativeHookException ex)
		{System.out.println("critical error");System.exit(1);}
		GlobalScreen.addNativeKeyListener(new ExitCondition());
		
		initDisplay();
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}
	
	/**This just sets visibility to true, allowing the user to start the robot. */
	public void displayBot() {
		java.awt.EventQueue.invokeLater(new Runnable(){
			public void run()
			{
				setVisible(true);
			}
		});
	}

	/** This is the simple GUI for the app. This GUI only functions as giving basic instructions for the user*/
	private void initDisplay()
	{
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};

		this.setMinimumSize(new Dimension(800, 500));
		this.setSize(new Dimension(1000, 1000));
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(800, 800));
		
		startButton = new JButton();
		startButton.setText("startBot");
		panel.add(startButton);
		startButton.setBounds(150, 150, 500, 100);
		startButton.addActionListener(this);	
		
		pack();
		getContentPane().add(panel);
		panel.setVisible(true);
	}
	
	public boolean isRunning()
	{
		if(!isVisible())
			return true;
		return false;
	}
	
	/** Checks when the start button is clicked and starts the bot when it is*/
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(startButton))
		{
			setVisible(false);
			
		}
	}
		
	private class ExitCondition implements NativeKeyListener
	{

		/** Checks whether the delete key is pressed, if the delete key is pressed, the program is exited
		 *  Serves as an exit conditions for the code, otherwise the bot is stuck in an infinite while loop*/
		@Override
		public void nativeKeyPressed(NativeKeyEvent ke) {
			if(ke.getKeyCode() == NativeKeyEvent.VC_DELETE)
			{
				System.out.println("Stop the bot!");
				System.exit(0);
			}
			
		}

		/** Nothing is done for a keyTyped or keyReleased native key event */
		@Override
		public void nativeKeyReleased(NativeKeyEvent ke) {}
		@Override
		public void nativeKeyTyped(NativeKeyEvent ke) {}
		
	}
}
	
