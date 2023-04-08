## Settings

Available settings are defined in [assets/settings.yml](/assets/settings.yml). This document explains the features of the settings framework.

### Auto updates and settings persistency

The settings displayed to the players can be updated simply by removing or adding settings to the `settings.yml` configuration file.

### How it works internally

- The game creates a modifiable copy of the original settings.yml configuration file, saved in `assets/internal/settings.yml`
- The game creates a CRC32 checksum of the the original settings.yml configuration file, saved in `assets/internal/settings.hash`
- Upon loading the settings, the game generates a CRC32 checksum of the original settings.yml configuration file and compares it with the one in `assets/internal/settings.hash`
- If the checksums do not match, generate a new copy of the original settings.yml configuration file
- The previous values of the old modifiable copy are copied over to the new modifiable copy, hence there will be no loss of settings

### `settings.yml` structure

- All settings must fall under a group
- A group has the following properties
  - group
    - Refers to the name of the group
  - settings
    - Can be omitted if there are no settings
  - subgroups
    - Can be omitted if there are no subgroups
- A setting has the following properties
  - setting_name
    - Used to refer to the setting in the code, use a code-friendly name, preferably snake-case
  - label
    - Used to display on the settings screen
  - type
    - enum: number, text, hotkey, boolean, select
    - decides what widget to render to modify the setting
      - number: slider
      - text: textbox
      - hotkey: special select-hotkey-popup
      - boolean: checkbox
      - select: dropdown select box
  - value
    - Initial value of the setting.
    - For hotkey values, see [Hotkeys](#hotkeys) for available options
  - extras
    - Any extra data that the setting type requires/accepts

### Creating new setting types

The settings in the configuration file are deserialized into _Setting_ objects at runtime. _Setting_ is an abstract class which serves as the base for creating specific setting types. An example is _HotkeySetting_.

Requirements for _Setting_ subclasses:
- Must have a default constructor, or else an _InvalidSubclassException_ will be thrown at runtime
- Must specify how the setting value should be serialized by implementing a _getSerializableValue_ method
- Must implement a _setValue_ method which also takes care of type casting

Consider the _HotkeySetting_ code below for an example.

```Java
public class HotkeySetting extends Setting<Integer> {
    // Mandatory default constructor
    public HotkeySetting() {}

    public HotkeySetting(String name, String label, int value) {
        super(name, label, SettingType.HOTKEY, value);
    }

    ...

    // setValue function which handles any type conversion/casting required
    @Override
    public void setValue(Object value) {
        this.value = Input.Keys.valueOf(value.toString());
    }

    // Define how a setting should be saved
    @Override
    public String getSerializableValue() {
        return getValueString();
    }
}
```

Additionally, you need to add an enum value for your setting type in the _SettingType_ class in `SettingType`.

```Java
public enum SettingType {
    HOTKEY("hotkey"),
    NUMBER("number"),
    TEXT("text");
    // Declare new setting types here!

    ...
}
```

Finally, associate the new enum value with your new _Setting_ subclass, in `SettingFactory`:

```Java
public class SettingFactory {

    ...

    static {
        typeClassMapping.put(SettingType.HOTKEY, HotkeySetting.class);
        typeClassMapping.put(SettingType.NUMBER, NumberSetting.class);
        typeClassMapping.put(SettingType.TEXT, TextSetting.class);
        // New typeClassMapping record for your new setting here
    }

    ...
}
```

### Handling extra data

A setting type may require some extra parameters that are exclusive to itself. An example would be the DropdownSelectSetting, which requires a list of choices. A setting can use extra data by overriding the `handleExtra` method of the Setting abstract class. Taking `DropdownSelectSetting` as an example:

```java
public class DropdownSelectSetting extends Setting<String> {

    private String[] choices;

    ...

    // Handle extra data here
    @Override
    public void handleExtra(String key, Object value) {
        super.handleExtra(key, value);  // <-- Don't forget to call the superclass' version of handleExtra!
        if(key.equals("choices")) {
            String valueString = value.toString();
            this.choices = valueString.split("\n");
        }
    }

    @Override
    public String getSerializableValue() {
        return String.join("\n", this.value);
    }
}
```

- The superclass' version `handleExtra` method should be called
- Assign variables according to the data's key name
- While data can be retrieved using `getExtra` method, it is recommended to explicitly set the extra data as a property in the class
- Consider throwing a `RuntimeException` if required data is missing

### Extra data for each setting type

select
  - **choices**
    - Choices separated by newline

number
  - min
    - Minimum value for number slider
  - max
    - Maximum value for number slider
  - step_size
    - Step size for number slider

text
  - disallowed_characters
    - String of characters not allowed in text field (e.g. !@#<>|)

### Example config file

This is a sample `setting.yml` configuration that showcases all the possible types of settings and neat organization of settings through multi-layer subgroups
```yml
- group: General
  settings:
    - setting_name: master_volume
      label: Master Volume
      type: number
      value: 100
- group: Graphics
  settings:
    - setting_name: resolution
      label: Resolution
      type: select
      extras:
        choices: |
          1920 x 1080
          1280 x 720
          1024 x 768
      value: 1920 x 1080
- group: Hotkeys
  settings:
    - setting_name: pause
      label: Pause
      type: hotkey
      value: P
    - setting_name: scoreboard
      label: Toggle scoreboard
      type: hotkey
      value: "`"
  subgroups:
    - group: Players
      subgroups:
        - group: Player 1
          settings:
            - setting_name: player_name
              label: Name
              type: text
              value: Player 1
            - setting_name: player_1_up
              label: Up
              type: hotkey
              value: Up
            - setting_name: player_1_right
              label: Right
              type: hotkey
              value: Right
            - setting_name: player_1_down
              label: Down
              type: hotkey
              value: Down
            - setting_name: player_1_left
              label: Left
              type: hotkey
              value: Left
```

### Hotkeys
- 0
- 1
- 2
- 3
- 4
- 5
- 6
- 7
- 8
- 9
- \*
- \#
- Up
- Down
- Left
- Right
- A
- B
- C
- D
- E
- F
- G
- H
- I
- J
- K
- L
- M
- N
- O
- P
- Q
- R
- S
- T
- U
- V
- W
- X
- Y
- Z
- ,
- .
- L-Alt
- R-Alt
- L-Shift
- R-Shift
- Tab
- Space
- Enter
- Delete (Use this for backspace character)
- \`
- \-
- =
- [
- ]
- \
- ;
- '
- /
- @
- Plus
- Page Up
- Page Down
- L-Ctrl
- R-Ctrl
- Escape
- End
- Insert
- Numpad 0
- Numpad 1
- Numpad 2
- Numpad 3
- Numpad 4
- Numpad 5
- Numpad 6
- Numpad 7
- Numpad 8
- Numpad 9
- :
- F1
- F2
- F3
- F4
- F5
- F6
- F7
- F8
- F9
- F10
- F11
- F12
- F13
- F14
- F15
- F16
- F17
- F18
- F19
- F20
- F21
- F22
- F23
- F24
- Num /
- Num *
- Num -
- Num +
- Num .
- Num ,
- Num Enter
- Num =
- Num (
- Num )
- Num Lock
- Caps Lock
- Scroll Lock
- Pause
- Print