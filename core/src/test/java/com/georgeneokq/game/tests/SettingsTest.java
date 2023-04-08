package com.georgeneokq.game.tests;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.badlogic.gdx.Input;
import com.georgeneokq.game.GdxTestRunner;
import com.georgeneokq.engine.settings.DropdownSelectSetting;
import com.georgeneokq.engine.settings.HotkeySetting;
import com.georgeneokq.engine.settings.NumberSetting;
import com.georgeneokq.engine.settings.Setting;
import com.georgeneokq.engine.settings.SettingsGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Internal resources root is located at src/test/resources folder, accessed using Gdx.files.internal
 * Files accessed using Gdx.files.internal are read-only
 * App resources root is located at assets folder, accessed using Gdx.files.local
 */
@RunWith(GdxTestRunner.class)
public class SettingsTest {
    private List<SettingsGroup> settings = new ArrayList<>();

    public SettingsTest() {
        SettingsGroup[] testSettingsGroups = new SettingsGroup[] {
            new SettingsGroup(
                "General",
                new Setting[] {
                    new NumberSetting("master_volume", "Master Volume", 100f),
                    new NumberSetting("bgm_volume", "BGM Volume", 100f),
                    new NumberSetting("se_volume", "SE Volume", 100f)
                },
                null
            ),
            new SettingsGroup(
                "Graphics",
                new Setting[] {
                    new DropdownSelectSetting("resolution", "Resolution", "1920 x 1080", new String[] {
                            "1920 x 1080",
                            "1280 x 720",
                            "1024 x 768"
                    })
                },
                null
            ),
            new SettingsGroup(
                "Hotkeys",
                null,
                new SettingsGroup[] {
                    new SettingsGroup(
                        "Players",
                        null,
                        new SettingsGroup[] {
                            new SettingsGroup(
                                "Player 1",
                                new Setting[] {
                                    new HotkeySetting("move_up", "Up", Input.Keys.W),
                                    new HotkeySetting("move_left", "Left", Input.Keys.A),
                                    new HotkeySetting("move_down", "Right", Input.Keys.S),
                                    new HotkeySetting("move_right", "Down", Input.Keys.D)
                                },
                                null
                            )
                        }
                    )
                }
            )
        };
        settings.addAll(Arrays.asList(testSettingsGroups));
    }

    @Test
    public void testCorrectConfig() {
        assertEquals(3, settings.size());

        SettingsGroup hotkeysGroup = settings.get(2);
        SettingsGroup playersGroup = hotkeysGroup.getSubgroups().get(0);
        SettingsGroup player1Group = playersGroup.getSubgroups().get(0);

        // Test hotkey setting
        HotkeySetting player1LeftHotkeySetting = (HotkeySetting) player1Group.getSettings().get(1);
        assertEquals(Input.Keys.A, (int) player1LeftHotkeySetting.getValue());

        SettingsGroup graphicsGroup = settings.get(1);

        // Test dropdown select setting
        DropdownSelectSetting resolutionSetting = (DropdownSelectSetting) graphicsGroup.getSettings().get(0);
        String[] choices = resolutionSetting.getChoices();
        String value = resolutionSetting.getValue();
        assertEquals("1920 x 1080", value);
        assertEquals("1920 x 1080", choices[0]);
        assertEquals("1280 x 720", choices[1]);
        assertEquals("1024 x 768", choices[2]);
    }

    @Test
    public void testStatefulTraverse() {
        // Hotkeys group
        SettingsGroup settingsGroup = settings.get(1);

        settingsGroup.statefulTraverse((currentSettingsGroup, parentGroupNames, depth) -> {
            String groupName = currentSettingsGroup.getName();
            switch(depth) {
                case 0:
                    assertEquals("Hotkeys", groupName);
                    break;
                case 1:
                    assertEquals("Players", groupName);
                    break;
                case 2:
                    assertEquals("Player 1", groupName);
                    break;
            }
        }, true);
    }
}
