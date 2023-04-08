package com.georgeneokq.engine.settings;

import com.badlogic.gdx.Gdx;
import com.georgeneokq.engine.settings.exceptions.InvalidFormatException;
import com.georgeneokq.engine.settings.exceptions.InvalidSubclassException;

import java.lang.reflect.Constructor;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SettingFactory {

    public static EnumMap<SettingType, Class> typeClassMapping = new EnumMap<>(SettingType.class);
    public static HashMap<Class, SettingType> classTypeMapping = new HashMap<>();

    static {
        typeClassMapping.put(SettingType.HOTKEY, HotkeySetting.class);
        typeClassMapping.put(SettingType.NUMBER, NumberSetting.class);
        typeClassMapping.put(SettingType.TEXT, TextSetting.class);
        typeClassMapping.put(SettingType.BOOLEAN, BooleanSetting.class);
        typeClassMapping.put(SettingType.SELECT, DropdownSelectSetting.class);

        // Automatically creates a reverse mapping based on typeClassMapping
        for(Map.Entry<SettingType, Class> mapping : typeClassMapping.entrySet())
            classTypeMapping.put(mapping.getValue(), mapping.getKey());
    }

    public static Setting fromMap(HashMap hashmap) {

        String settingType;
        String settingName;
        String settingLabel;
        Map<String, Object> settingExtras;
        Object settingValue;

        settingType = (String) hashmap.get("type");
        settingName = (String) hashmap.get("setting_name");
        settingLabel = (String) hashmap.get("label");
        settingExtras = (Map) hashmap.get("extras");
        settingValue = hashmap.get("value");

        if(settingType == null || settingName == null || settingLabel == null) {
            throw new InvalidFormatException(
                    String.format(
                            "Error reading setting %s: setting_name, label, type fields are mandatory",
                            settingName != null ? settingName : "Unknown"
                    )
            );
        }

        Class settingClass = null;
        Setting setting = null;

        // Use the setting type to retrieve the class to be used for the setting
        try {
            settingClass = typeClassMapping
                    .get(SettingType.valueOf(settingType.toUpperCase()));
        } catch (Exception e) {
            // Construct a nice message for developer to debug
            StringBuilder types = new StringBuilder();
            for(int i = 0; i < SettingType.values().length; i++) {
                String type = SettingType.values()[i].toString();
                types.append(type);
                if(i < SettingType.values().length - 1) {
                    types.append(", ");
                }
            }
            throw new InvalidFormatException(
                    String.format("Setting type \"%s\" is invalid. Available values are: %s", settingType, types)
            );
        }

        // Get the default constructor of the retrieved class
        Constructor cnstructor;
        try {
            cnstructor = settingClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new InvalidSubclassException(String.format("Default constructor is required in %s", settingClass.getName()));
        }

        // Use the default constructor to create a new instance of the retrieved Setting object
        try {
            setting = (Setting) cnstructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.exit();
        }

        // Set common setting properties
        setting.setName(settingName);
        setting.setLabel(settingLabel);
        setting.setValue(settingValue);

        // Set extras, if any
        if(settingExtras != null) {
            for(Map.Entry settingExtraEntry : settingExtras.entrySet()) {
                setting.handleExtra((String) settingExtraEntry.getKey(), settingExtraEntry.getValue());
            }
        }

        return setting;
    }
}
