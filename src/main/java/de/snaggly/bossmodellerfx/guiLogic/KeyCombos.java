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
}
