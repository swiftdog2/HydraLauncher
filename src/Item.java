import java.io.File;

public class Item {
	public String name;
	public String link;
	public String csum;
	
	public Item(String...strings) {
		if(strings.length > 0)
			name = strings[0];
		if(strings.length > 1)
			link = strings[1];
		if(strings.length > 2)
			csum = strings[2];
	}

	public boolean retrieved() {
		File f = new File(Launcher.saveLocation + name);
		if(f.exists()) {
			try {
			if(Launcher.checkSum(f).equals(csum))
				return true;
			} catch (Exception ex) {
			}
		}
		return false;
	}
}
