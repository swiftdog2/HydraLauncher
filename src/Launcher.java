import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Launcher {
	public static final String saveLocation = System.getProperty("user.home") + "/.Hydrascape32/";
	private static final String IP = "http://178.79.128.247/";
	private static Item logo, jar;
	
	public static void main(String[] args) throws InterruptedException {
		/*
		 * Creating the frame
		 */
		
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		frame.setBackground(new Color(0,0,0,0));
		
		/*
		 * Default loading icon
		 */
		
		JLabel defaultLabel = new JLabel("Loading, please wait...");
		defaultLabel.setForeground(Color.CYAN);
		
		String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for(String f : fontList) {
			if(f.equalsIgnoreCase("impact"))
				defaultLabel.setFont(new Font("Impact", Font.BOLD, 26));
			else
				defaultLabel.setFont(new Font("Serif", Font.BOLD, 26));
		}
		
		frame.add(defaultLabel);
		frame.pack();
		frame.setLocationRelativeTo(null);

		long startTime = System.currentTimeMillis();
		int status = 1;
		
		try {
			/*
			 * Retrieves expected checksums for files
			 */

			final URL url = new URL(IP + "sums.txt");
			URLConnection conn = url.openConnection();
			// conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.connect();
			// System.setProperty("http.agent", "Chrome");

			status = 2;
			
			final Scanner s = new Scanner(conn.getInputStream());
			logo = new Item("hydrascape-logo.png", IP + "hydrascape-logo.png", s.next());
			jar = new Item("Client.jar", IP + "Client.jar", s.next());
			s.close();

			/*
			 * Downloads the resources
			 */

			
			status = 3;
			
			File f = new File(saveLocation);
			if(!f.isDirectory()) {
				boolean newFolder = f.mkdir();
				if(!newFolder)
					throw new Exception();
			}
				
			boolean retrieved = false;
				
			status = 4;
			retrieved = retrieve(logo);
			if(!retrieved)
				throw new Exception();

			status = 5;
			retrieved = retrieve(jar);
			if(!retrieved)
				throw new Exception();
		} catch (Exception ex) {
			defaultLabel.setText("Error code " + status + " , please contact an administrator.");
			frame.pack();
			frame.setLocationRelativeTo(null);
			Thread.sleep(3000);
			System.exit(0);
		}

		startTime = System.currentTimeMillis() - startTime;
		if(startTime < 1000)
			Thread.sleep(1000 - startTime);
		
		/*
		 * Replace default with icon label
		 */

		JLabel iconLabel = new JLabel();

		try {
			BufferedImage myPicture = ImageIO.read(new File(saveLocation + logo.name));
			iconLabel.setIcon(new ImageIcon(myPicture));
		} catch (IOException e) {
			defaultLabel.setText("Failed to load image, please contact an administrator.");
			frame.pack();
			frame.setLocationRelativeTo(null);
			Thread.sleep(3000);
			System.exit(0);
		}

		//Everything went well, add the image
		frame.remove(defaultLabel);
		frame.add(iconLabel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		Thread.sleep(2000); // Keep that logo up for a couple seconds

		try {
			//ProcessBuilder pb = new ProcessBuilder("java", "-jar", saveLocation + jar.name);
			//Process p = pb.start();
			
			/*
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = "";
			while((s = in.readLine()) != null)
			    System.out.println(s);
			int code = p.waitFor();
			System.out.println("Exited with status: " + code);
			*/
			
			//Desktop.getDesktop().open(new File(saveLocation + jar.name));
			
			//Runtime.getRuntime().exec(System.getProperty("java.home") + "/bin/javaw.exe -jar " + saveLocation + jar.name);
			Runtime.getRuntime().exec(new String[] { "java", "-jar", saveLocation + jar.name});

		} catch (IOException e) {
			e.printStackTrace();
			defaultLabel.setText("Failed to run client, please contact an administrator.");
			frame.pack();
			frame.setLocationRelativeTo(null);
			Thread.sleep(3000);
		}

		System.exit(0);
	}

	public static boolean retrieve(Item item) throws Exception {
		File f = new File(saveLocation + item.name);

		if (f.exists()) {
			// If the file exists, but the checksum isn't right, it's gotta be updated
			if (!checkSum(f).equals(item.csum)) {
				//System.out.println("\n\t---> File doesn't match expected checksum! Requesting new copy of " + item.name);
				return download(item);
			}
			return true;
		}
		
		//System.out.println(item.name + " doesn't exist! Downloading... ");
		return download(item);
	}

	public static boolean download(Item item) {
		try {
			URL website = new URL(item.link);
			Files.copy(website.openStream(), Paths.get(saveLocation + item.name), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (Exception ex) {
		}
		return false;
	}
	
	public static String checkSum(File f) throws Exception {
		MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");
		String shaChecksum = getFileChecksum(shaDigest, f);

		//System.out.println(shaChecksum);
		return shaChecksum;
	}

	private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);

		byte[] byteArray = new byte[1024];
		int bytesCount = 0;

		while ((bytesCount = fis.read(byteArray)) != -1)
			digest.update(byteArray, 0, bytesCount);

		fis.close();

		byte[] bytes = digest.digest();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));

		return sb.toString();
	}
}
