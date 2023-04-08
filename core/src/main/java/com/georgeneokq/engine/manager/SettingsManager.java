package com.georgeneokq.engine.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.Language;
import com.georgeneokq.engine.hashing.HashUtil;
import com.georgeneokq.engine.settings.BooleanSetting;
import com.georgeneokq.engine.settings.DropdownSelectSetting;
import com.georgeneokq.engine.settings.NumberSetting;
import com.georgeneokq.engine.settings.Setting;
import com.georgeneokq.engine.settings.SettingsGroup;
import com.georgeneokq.engine.settings.SettingsParser;
import com.georgeneokq.game.manager.MusicManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsManager {

    private static final String MODIFIABLE_SETTINGS_FILE_PATH = "internal/settings.yml";
    private static final String SETTINGS_FILE_HASH_PATH = "internal/settings.hash";
    private static final String DEFAULT_ORIGINAL_SETTINGS_PATH = "settings.yml";

    private static SettingsManager settingsManager;
    private List<SettingsGroup> settings;
    private HashMap<String, Setting> queryMapping;

    private Globals globals;
    private AssetManager assetManager;
    private EventManager eventManager;
    private ScreensManager screensManager;

    private SettingsManager(String settingsPath) {
        settings = loadSettings(settingsPath);

        globals = Globals.getInstance();
        this.assetManager = globals.getAssetManager();
        screensManager = ScreensManager.getInstance();
        eventManager = EventManager.getInstance();

        // loadSettings might have created a queryMapping
        if(queryMapping == null)
            queryMapping = createQueryMapping(settings);
    }

    /*
     * When SettingsManager is first used in the application,
     * the default constructor should not be used.
     * Pass in an instance of AssetManager.
     */
    public static SettingsManager getInstance() {
        return getInstance(DEFAULT_ORIGINAL_SETTINGS_PATH);
    }

    public static SettingsManager getInstance(String settingsPath) {
        if(settingsManager == null) {
            settingsManager = new SettingsManager(settingsPath);
        }

        return settingsManager;
    }

    /*
     * By default, read settings.yml
     */
    public List<SettingsGroup> getSettings() {
        return settings;
    }

    /*
     * Return a copy of the settings.
     * This allows screens to edit the settings without imemdiately taking effect.
     */
    public List<SettingsGroup> getSettingsCopy() {
        List<SettingsGroup> settingsCopy = new ArrayList<>();
        for(SettingsGroup settingsGroup: settings) {
            settingsCopy.add(settingsGroup.clone());
        }

        return settingsCopy;
    }

    /*
     * Check for `settings.hash` file, hash the current settings.yml file and compare the hashes.
     * If the hashes are different, make a modifiable copy of the original settings.yml file,
     * named according to variable MODIFIABLE_SETTINGS_FILE_PATH.
     *
     * Whenever settings are to be loaded, load the modifiable settings file.
     *
     */
    private List<SettingsGroup> loadSettings(String originalSettingsFilePath) {
        List<SettingsGroup> loadedSettings = null;

        FileHandle settingsHashFile = Gdx.files.local(SETTINGS_FILE_HASH_PATH);
        FileHandle modifiableSettingsFile = Gdx.files.local(MODIFIABLE_SETTINGS_FILE_PATH);
        FileHandle originalSettingsFile = Gdx.files.local(originalSettingsFilePath);

        if(!originalSettingsFile.exists()) {
            Gdx.app.error("SettingsManager.loadSettings",
                    String.format("Settings file %s does not exist!", originalSettingsFilePath));
            Gdx.app.exit();
        }

        // Check if there are any changes to the settings structure
        if(!settingsHashFile.exists() || !modifiableSettingsFile.exists()) {
            // Create hash file and modifiable copy of settings if they do not exist
            String hash = HashUtil.crc32(originalSettingsFile.readBytes());
            settingsHashFile.writeString(hash, false);
            modifiableSettingsFile.writeString(originalSettingsFile.readString(), false);
        } else {
            // If the hash file exists, compare the hashes
            String existingHash = settingsHashFile.readString();
            String currentSettingsFileHash = HashUtil.crc32(originalSettingsFile.readBytes());

            // If the hashes are not equal, make a new modifiable copy of settings.yml.
            // Also copy the previous settings' values over
            if(!currentSettingsFileHash.equals(existingHash)) {
                // Deserialize the previous settings file to the existing values
                List<SettingsGroup> previousSettings = SettingsParser
                        .deserializeSettings(modifiableSettingsFile.readString());

                // Create a query mapping for the current settings
                HashMap<String, Setting> previousSettingsQueryMapping =
                        createQueryMapping(previousSettings);

                // Make the new copy of settings
                modifiableSettingsFile.writeString(originalSettingsFile.readString(), false);

                // Load settings from the new copy
                loadedSettings = SettingsParser.deserializeSettings(modifiableSettingsFile.readString());

                // Create a queryMapping of the loaded settings, attach to the SettingsManager instance
                this.queryMapping = createQueryMapping(loadedSettings);

                // Loop through every key in the new settings' query mapping.
                // If the key exists in the previous settings' query mapping,
                // take the previous setting's value and put it in the new one
                for(Map.Entry<String, Setting> newSettingsMapping: this.queryMapping.entrySet()) {
                    // Check if setting can be found in previous settings' mapping
                    Setting previousSetting = previousSettingsQueryMapping
                            .get(newSettingsMapping.getKey());
                    if(previousSetting == null)
                        continue;

                    // If found, copy the value over
                    Setting newSetting = newSettingsMapping.getValue();
                    newSetting.setValue(previousSetting.getSerializableValue());
                }

                // Save the settings
                saveSettings(loadedSettings);

                // Create a new hash file
                String hash = HashUtil.crc32(originalSettingsFile.readBytes());
                settingsHashFile.writeString(hash, false);
            }
        }

        if(loadedSettings == null)
            loadedSettings = SettingsParser.deserializeSettings(modifiableSettingsFile.readString());

        return loadedSettings;
    }

    /*
     * Write values into modifiable yml file using yaml dump method
     */
    public void saveSettings(List<SettingsGroup> settings) {
        this.settings = settings;
        this.queryMapping = createQueryMapping(settings);
        String serializedSettings = SettingsParser.serializeSettings(settings);
        FileHandle file = Gdx.files.local(MODIFIABLE_SETTINGS_FILE_PATH);
        file.writeString(serializedSettings, false);
    }

    /*
     * Create a mapping of query strings to Setting objects.
     * This allows for O(1) search time.
     * A query string has the following format:
     * <root_group_name>.<subgroups_names>.<setting_name>
     * where subgroups_names are also connected by dots, if there are multiple layers.
     * Make sure to use setting_name field, not the setting label.
     *
     */
    private HashMap<String, Setting> createQueryMapping(List<SettingsGroup> settings) {
        HashMap<String, Setting> queryMapping = new HashMap<>();
        for(SettingsGroup settingsGroup: settings) {
            SettingsGroup.StatefulTraverseCallback callback =
                    (currentSettingsGroup, parentGroupNames, depth) -> {
                for(Setting setting: currentSettingsGroup.getSettings()) {
                    StringBuilder queryStringBuilder = new StringBuilder();

                    if(parentGroupNames.size() > 0) {
                        String parentGroupNamesJoined = String.join(
                            ".",
                            parentGroupNames
                        );

                        queryStringBuilder.append(parentGroupNamesJoined).append(".");
                    }

                    queryStringBuilder.append(currentSettingsGroup.getName())
                        .append(".")
                        .append(setting.getName());

                    queryMapping.put(queryStringBuilder.toString(), setting);
                }
            };
            settingsGroup.statefulTraverse(callback, true);
        }
        return queryMapping;
    }

    /*
     * @param String identifier  A string to query for the setting desired.
     *
     * A query string has the following format:
     * <root_group_name>.<subgroups_names>.<setting_name>
     * where subgroups_names are also connected by dots, if there are multiple layers.
     * Make sure to use setting_name field, not the setting label.
     *
     */
    public Setting getSetting(String queryString) {
        return getSetting(this.queryMapping, queryString);
    }

    public <T extends Setting> T getSetting(String queryString,Class<T> settingClass) {
        return (T) getSetting(queryString);
    }

    /*
     * This version of getSetting reads from a specified queryMapping
     * instead of the global queryMapping.
     */
    public Setting getSetting(HashMap<String, Setting> queryMapping, String queryString) {
        return queryMapping.get(queryString);
    }

    public <T extends Setting> T getSetting(HashMap<String, Setting> queryMapping, String queryString, Class<T> settingClass) {
        return (T) queryMapping.get(queryString);
    }

    /*
     * Apply global setting changes.
     * For example, music volume
     */
    public void applySettings() {
        // Volume settings
        BooleanSetting muteSetting = getSetting("general.mute_button", BooleanSetting.class);
        MusicManager musicManager = MusicManager.getInstance();

        // If muted, set volume to 0. If not, do volume scaling and apply
        if (muteSetting.getValue()) {
            musicManager.setMusicVolume(0);
        } else {
            NumberSetting bgmVolumeSetting = getSetting("general.bgm_volume", NumberSetting.class);
            musicManager.setMusicVolume(bgmVolumeSetting.getValue() / 100);
        }

        // Resolution settings
        Setting resolutionSetting = getSetting("graphics.resolution");
        String resolutionString = (String) resolutionSetting.getValue();

        // Remove spaces
        String trimmedResolutionString = resolutionString.replace(" ", "");
        String[] dimensionStrings = trimmedResolutionString.split("x");

        globals.resolutionWidth = Integer.parseInt(dimensionStrings[0]);
        globals.resolutionHeight = Integer.parseInt(dimensionStrings[1]);

        Gdx.graphics.setWindowedMode(globals.resolutionWidth, globals.resolutionHeight);

        screensManager.notifyResolutionChanged();

        // Language settings
        String languageSettingValue = getSetting("general.language", DropdownSelectSetting.class).getValue();
        Language language = Language.fromString(languageSettingValue);
        globals.setLanguage(language != null ? language : Language.EN);
    }
}
