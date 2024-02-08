import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class SHA {
	/*public static void main(String args[]) {
		System.out.println(getFileChecksum(new File("C:/Users/Flash/Desktop/Client.jar")));
	}*/
	
	public static String getFileChecksum(File f) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			FileInputStream fis = new FileInputStream(f);

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
		} catch (Exception ex) {
			return "Failed to return file hash";
		}
	}
}
