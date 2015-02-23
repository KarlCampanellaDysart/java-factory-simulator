import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UpdateRequestsThreadPanel extends JPanel implements Runnable{
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		InputStream is = null;
		OutputStream os = null;
		Socket s;
		
		JButton accept;
		JButton decline;
		
		boolean terminated = false;
		boolean completedTask = true;
		FactoryGUI fg;
		ServerThread st;
		String nameOfItem;
		int price = 0;
		int id;
		public UpdateRequestsThreadPanel(Socket s, FactoryGUI fg, ServerThread st, int id){
			this.s = s;
			this.fg = fg;
			this.st = st;
			this.id = id;
			
			try {
				is = s.getInputStream();
				os = s.getOutputStream();
			} catch (IOException e) {}
			
			recieveFile();
			setupGui();
		}
		
		public void setupGui(){
			ButtonListener b = new ButtonListener();
			accept = new JButton("Accept");
			accept.addActionListener(b);
			decline = new JButton("Decline");
			decline.addActionListener(b);
			
			setPreferredSize(new Dimension(500,100));
			setMaximumSize(new Dimension(500,100));
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			BufferedImage imgWood = null;
			BufferedImage imgMetal = null;
			BufferedImage imgPlastic = null;
			try {
			    imgWood = ImageIO.read(new File("assignment5images/wood.png"));
			    imgMetal = ImageIO.read(new File("assignment5images/metal.png"));
			    imgPlastic = ImageIO.read(new File("assignment5images/plastic.png"));
			} catch (IOException e) {}
			
			JPanel woodPanel = new JPanel();
			woodPanel.setLayout(new BoxLayout(woodPanel, BoxLayout.Y_AXIS));
			woodPanel.add(new JLabel("\tWood\t"+fg.fp.wood));
			woodPanel.add(new JLabel(new ImageIcon(imgWood)));
			
			JPanel metalPanel = new JPanel();
			metalPanel.setLayout(new BoxLayout(metalPanel, BoxLayout.Y_AXIS));
			metalPanel.add(new JLabel("\tMetal\t"+fg.fp.metal));
			metalPanel.add(new JLabel(new ImageIcon(imgMetal)));
			
			JPanel plasticPanel = new JPanel();
			plasticPanel.setLayout(new BoxLayout(plasticPanel, BoxLayout.Y_AXIS));
			plasticPanel.add(new JLabel("\tPlastic\t"+fg.fp.plastic));
			plasticPanel.add(new JLabel(new ImageIcon(imgPlastic)));
			
			nameOfItem = fg.fp.nameOfItem;
			price = fg.fp.price;
			add(new JLabel(nameOfItem+" - $"+price+"\t"));
			add(woodPanel);
			add(metalPanel);
			add(plasticPanel);
			add(new JLabel(fg.fp.totalTime+"s"));
			add(accept);
			add(decline);
			
			fg.mp.ordersPanel.add(this);
		}
		
		public void recieveFile(){
			try {
				ois = new ObjectInputStream(is);
				String result = (String) ois.readObject();
				//ois.close();
				System.out.println(result);
				PrintWriter pw = new PrintWriter("assignment_5_test/example.rcp");
				pw.print(result);
				pw.close();
				
				//fg.fp = new FileParser("assignment_5_test");
				fg.fp.parseRCP();
			} catch (IOException e) {} catch (ClassNotFoundException e) {}		
		}
		
		public void accept(){
			try {
				oos = new ObjectOutputStream(os);
				System.out.println("a");
				String message = "accept";
				oos.reset();
				oos.writeObject(message);
				fg.mp.ordersPanel.remove(this);	
				fg.openFileAndStart();
				setVisible(false);
				repaint();
				revalidate();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		
		public void decline(){
			try {
				oos = new ObjectOutputStream(os);
				String message = "decline";
				oos.writeObject(message);
				//oos.flush();
				fg.mp.ordersPanel.remove(this);
				repaint();
				revalidate();
				terminated = true;
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		
		public void run() {
			while(true){
				if(completedTask){
					try {					
						String genURL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+nameOfItem;
						oos.reset();
						oos.writeObject(genURL);
						Iterator<UpdateRequestsThreadPanel> it= st.allRequests.iterator();
						while(it.hasNext()){
							UpdateRequestsThreadPanel urtp = it.next();
							if(urtp.equals(this)){
								st.allRequests.remove(this);
							}
						}
						terminated = true;
					} catch (IOException e) {}
				}
				if(terminated){
					try {
						oos.close();
						ois.close();
					} catch (IOException e) {}
					break;
				}
			}
		}
		
		public class ButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == accept){
					accept();
				}
				if(e.getSource() == decline){
					decline();			
				}			
			}	
		}
		
	}