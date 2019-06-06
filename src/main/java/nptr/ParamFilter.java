package nptr;

import java.io.File;
import javax.swing.filechooser.*;

import nptr.utils.Utils;

public class ParamFilter extends FileFilter {
	private String ext;
    
	public ParamFilter(String ext) {
    	super();
    	this.ext=ext;
    }
	//Accepte tous les rep et les fichiers de types ntpr
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(this.ext)) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Paramètres";
    }
}
