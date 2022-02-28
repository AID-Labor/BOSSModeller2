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
    public static final ArrayList<KeyCode> keyComboOpen = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.O
    ));

    public static final ArrayList<KeyCode> keyComboSave = new ArrayList<>(Arrays.asList(
            KeyCode.CONTROL, KeyCode.S
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
