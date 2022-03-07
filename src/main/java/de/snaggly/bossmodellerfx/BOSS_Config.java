package de.snaggly.bossmodellerfx;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Configuration structure for BOSSModellerFX.
 * This class is a true pure singleton.
 * @author Omar Emshani
 */
public class BOSS_Config {
    //List of currently changeable settings.
    private static Locale language; //yah. till now, you can only change the language.
    //Current supported languages. More languages can be added by translating the string.properties bundle.
    public static final List<Locale> supportedLanguages = Arrays.asList(Locale.GERMAN, Locale.ENGLISH);

    private static File configFile;
    static { //Static initializer
        try {
            configFile = new File(System.getProperty("user.home"), ".BOSSModelerFXUserconfig.cfg");
            var bufReader = new BufferedReader(new FileReader(configFile));
            while(bufReader.ready()) {
                var currentLine = bufReader.readLine();
                var splitLine = currentLine.split("=");
                if (splitLine.length < 2) {
                    continue;
                }
                if (splitLine[0].equalsIgnoreCase("LANG")) {
                    for (var _langList : supportedLanguages) {
                        if (_langList.getLanguage().equals(splitLine[1])) {
                            language = _langList;
                            break;
                        }
                    }
                }
            }
            bufReader.close();
        }
        catch (Exception e) {
            language = Locale.getDefault();
        }
    }

    public static File getConfigFile() {
        return configFile;
    }

    public static Locale getLanguage() {
        return language;
    }

    /**
     * Sets language in configuration file. Has to be from supportedLanguages.
     * Otherwise, will not set!
     * @param locale Language to set configuration to. Has to be from supportedLanguages.
     * @return If correctly set language.
     */
    public static boolean setLanguage(Locale locale) {
        if (!supportedLanguages.contains(locale))
            return false;
        language = locale;
        return writeConfig();
    }

    private static boolean writeConfig() {
        try {
            var bufWriter = new BufferedWriter(new FileWriter(configFile));
            bufWriter.write("LANG=" + language.getLanguage());
            bufWriter.newLine();
            bufWriter.flush();
            bufWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
