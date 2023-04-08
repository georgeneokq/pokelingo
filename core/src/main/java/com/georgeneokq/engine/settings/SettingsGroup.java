package com.georgeneokq.engine.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsGroup implements Cloneable {

    // Return deep copy of the settings
    @Override
    public SettingsGroup clone() {
        SettingsGroup settingsGroupClone = new SettingsGroup();

        // Clone the Setting objects
        List<Setting> settingsClone = new ArrayList<>(settings.size());
        for(Setting setting: settings) {
            settingsClone.add(setting.clone());
        }

        // Clone the SettingsGroup objects
        List<SettingsGroup> subgroupsClone = new ArrayList<>();
        for(SettingsGroup subgroup: subgroups) {
            subgroupsClone.add(subgroup.clone());
        }

        // Set properties of the clone and return
        settingsGroupClone.setName(name);
        settingsGroupClone.setSettings(settingsClone);
        settingsGroupClone.setSubgroups(subgroupsClone);

        return settingsGroupClone;
    }

    private String name;
    private List<Setting> settings = new ArrayList<>();
    private List<SettingsGroup> subgroups = new ArrayList<>();

    public SettingsGroup() {}

    public SettingsGroup(
            String name,
            Setting[] settings,
            SettingsGroup[] subgroups) {
        this.name = name;

        if(settings != null)
            this.settings.addAll(Arrays.asList(settings));

        if(subgroups != null)
            this.subgroups.addAll(Arrays.asList(subgroups));
    }

    public interface StatefulTraverseCallback {
        void execute(
            SettingsGroup currentSettingsGroup,
            List<String> parentGroupNames,
            int depth
        );
    }

    /*
     * Treat the each SettingsGroup object as a node in a tree structure.
     * Each subgroup is a branch of a parent node.
     * Traverse down the subgroups of settings using depth-first search algorithm
     */
    public void statefulTraverse(StatefulTraverseCallback callback, boolean includeSelf) {
        statefulTraverse(this, new ArrayList<String>(), callback, includeSelf);
    }

    /*
     * Actual recursive function
     */
    private void statefulTraverse(
            SettingsGroup currentSettingsGroup,
            ArrayList<String> parentGroupNames,
            StatefulTraverseCallback callback,
            boolean includeSelf) {
        if(includeSelf) {
            callback.execute(currentSettingsGroup, parentGroupNames, parentGroupNames.size());
        }

        parentGroupNames.add(currentSettingsGroup.name);
        for(SettingsGroup subgroup: currentSettingsGroup.subgroups) {
            callback.execute(subgroup, parentGroupNames, parentGroupNames.size());
            statefulTraverse(subgroup, parentGroupNames, callback, false);
        }

        // End of iteration, pop a parent group name from the stack
        parentGroupNames.remove(parentGroupNames.size() - 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSetting(Setting setting) {
        this.settings.add(setting);
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public List<SettingsGroup> getSubgroups() {
        return subgroups;
    }

    public void addSubgroup(SettingsGroup group) {
        this.subgroups.add(group);
    }

    // Setters for internal use (cloning)
    private void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    private void setSubgroups(List<SettingsGroup> subgroups) {
        this.subgroups = subgroups;
    }
}
