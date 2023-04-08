package com.georgeneokq.engine.settings;

import com.georgeneokq.engine.settings.exceptions.InvalidFormatException;
import com.georgeneokq.engine.settings.exceptions.InvalidSubclassException;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsParser {

    /*
     * From a yaml config string, deserialize into a Settings object
     * SettingsGroup objects contain settings and more groups.
     */
    public static List<SettingsGroup> deserializeSettings(String configuration) {
        // Load the yaml config
        Yaml yaml = new Yaml();
        ArrayList<HashMap> unparsedGroups = yaml.load(configuration);

        // Fill SettingsGroup objects
        ArrayList<SettingsGroup> topLevelGroups = new ArrayList();
        for(HashMap unparsedGroup: unparsedGroups) {
            SettingsGroup settingsGroup = new SettingsGroup();
            topLevelGroups.add(settingsGroup);
            recursiveInitializeSettingGroups(settingsGroup, unparsedGroup);
        }

        return topLevelGroups;
    }

    /*
     * From a Settings object, serialize into a yaml string
     */
    public static String serializeSettings(List<SettingsGroup> settingsGroups) {
        // Recursively traverse through subgroups and initialize hashmaps
        ArrayList<HashMap> hashMaps = new ArrayList<>();
        for(SettingsGroup settingsGroup: settingsGroups) {
            HashMap hashMap = new HashMap();
            hashMaps.add(hashMap);
            recursiveInitializeHashmaps(hashMap, settingsGroup);
        }

        // Convert hashmaps to a yaml string
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        String serializedSettings = yaml.dump(hashMaps);
        return serializedSettings;
    }

    private static void recursiveInitializeHashmaps(HashMap<String, Object> hashMap, SettingsGroup settingsGroup) {
        hashMap.put("group", settingsGroup.getName());

        // Attach settings as ArrayList of HashMap objects
        ArrayList<HashMap> settingHashMapList = new ArrayList();
        List<Setting> settings = settingsGroup.getSettings();

        if(settings.size() > 0) {
            for(Setting setting: settings) {
                // Attach a new HashMap object
                HashMap settingHashMap = new HashMap();

                settingHashMap.put("type", SettingFactory.classTypeMapping.get(setting.getClass()).toString());
                settingHashMap.put("setting_name", setting.getName());
                settingHashMap.put("label", setting.getLabel());
                settingHashMap.put("value", setting.getSerializableValue());

                Map<String, Object> extras = setting.getExtras();
                if(extras.size() > 0) {
                    Map<String, Object> extrasMap = new HashMap<>();
                    for(Map.Entry extrasEntry : extras.entrySet()) {
                        extrasMap.put((String) extrasEntry.getKey(), extrasEntry.getValue());
                    }
                    settingHashMap.put("extras", extrasMap);
                }

                settingHashMapList.add(settingHashMap);
            }
            hashMap.put("settings", settingHashMapList);
        }

        // Set subgroups recursively
        List<SettingsGroup> subgroups = settingsGroup.getSubgroups();
        ArrayList<HashMap> subgroupHashMapList = new ArrayList<>();
        if(subgroups.size() > 0) {
            for(SettingsGroup subgroup: subgroups) {
                HashMap subgroupHashMap = new HashMap();
                recursiveInitializeHashmaps(subgroupHashMap, subgroup);
                subgroupHashMapList.add(subgroupHashMap);
            }
            hashMap.put("subgroups", subgroupHashMapList);
        }
    }

    private static void recursiveInitializeSettingGroups(SettingsGroup settingsGroup, HashMap unparsedGroup)
            throws InvalidFormatException, InvalidSubclassException {
        String groupName = (String) unparsedGroup.get("group");
        ArrayList unparsedSettings = (ArrayList) unparsedGroup.get("settings");
        ArrayList unparsedSubgroups = (ArrayList) unparsedGroup.get("subgroups");

        // Disallow dots in setting group name
        if(groupName.contains("."))
            throw new InvalidFormatException(String.format("Error parsing setting group %s: setting and setting group names must not contain dots", groupName));
        settingsGroup.setName(groupName);

        // Set Setting objects
        if(unparsedSettings != null) {
            for(int i = 0; i < unparsedSettings.size(); i++) {
                HashMap unparsedSetting = (HashMap) unparsedSettings.get(i);

                // Initialize a Setting object according to the type field
                Setting setting = SettingFactory.fromMap(unparsedSetting);

                // Disallow dots in setting name
                if(setting.getName().contains("."))
                    throw new InvalidFormatException(String.format("Error parsing setting %s: setting and setting group names must not contain dots", setting.getName()));

                settingsGroup.addSetting(setting);
            }
        }

        // Set subgroups recursively
        if(unparsedSubgroups != null) {
            for(int i = 0; i < unparsedSubgroups.size(); i++) {
                SettingsGroup subgroup = new SettingsGroup();
                HashMap unparsedSubgroup = (HashMap) unparsedSubgroups.get(i);
                recursiveInitializeSettingGroups(subgroup, unparsedSubgroup);
                settingsGroup.addSubgroup(subgroup);
            }
        }
    }
}
