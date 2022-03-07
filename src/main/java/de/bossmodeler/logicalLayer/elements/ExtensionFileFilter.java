package de.bossmodeler.logicalLayer.elements;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * The Class ExtensionFileFilter.
 * @author Stefan Hufschmidt
 * @version 1.0.1
 * 	<p>
 * 			Since 1.0.0 Added javadoc annotations. JL
 */
public class ExtensionFileFilter extends FileFilter {

    /** The description. */
    String description;

    /** The extensions. */
    String extensions[];

    /**
     * Instantiates a new extension file filter.
     *
     * @param description the description
     * @param extension the extension
     */
    public ExtensionFileFilter(String description, String extension) {
        this(description, new String[] { extension });
    }

    /**
     * Instantiates a new ExtensionFileFilter.
     *
     * @param description the description
     * @param extensions the extensions
     */
    public ExtensionFileFilter(String description, String extensions[]) {
        if (description == null) {
            this.description = extensions[0];
        } else {
            this.description = description;
        }
        this.extensions = extensions.clone();
        toLower(this.extensions);
    }

    /**
     * Method that puts all Strings in array to lower case.
     *
     * @param array the string array
     */
    private void toLower(String array[]) {
        for (int i = 0, n = array.length; i < n; i++) {
            array[i] = array[i].toLowerCase();
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else {
            String path = file.getAbsolutePath().toLowerCase();
            for (int i = 0, n = extensions.length; i < n; i++) {
                String extension = extensions[i];
                if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                    return true;
                }
            }
        }
        return false;
    }
}