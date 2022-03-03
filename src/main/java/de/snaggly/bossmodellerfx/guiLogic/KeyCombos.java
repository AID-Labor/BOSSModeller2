package de.snaggly.bossmodellerfx.guiLogic;

import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * List of supported KeyCombos
 * TODO: Add more KeyCombos.
 *
 * @author Omar Emshani
 */
public class KeyCombos {
    public static final ArrayList<KeyCode> keyComboNewProject = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.N
    ));

    public static final ArrayList<KeyCode> keyComboOpen = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.O
    ));

    public static final ArrayList<KeyCode> keyComboImportDB = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.ALT, KeyCode.O
    ));

    public static final ArrayList<KeyCode> keyComboSave = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.S
    ));

    public static final ArrayList<KeyCode> keyComboSaveAs = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.S
    ));

    public static final ArrayList<KeyCode> keyComboGenSQL = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.E
    ));

    public static final ArrayList<KeyCode> keyComboExportToDB = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.ALT, KeyCode.E
    ));

    public static final ArrayList<KeyCode> keyComboExportToPicture = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.P
    ));

    public static final ArrayList<KeyCode> keyComboZoomIn = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.PLUS
    ));

    public static final ArrayList<KeyCode> keyComboZoomOut = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.MINUS
    ));

    public static final ArrayList<KeyCode> keyComboZoomRestore = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.DIGIT0
    ));
}
