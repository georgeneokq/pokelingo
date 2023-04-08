package com.georgeneokq.engine.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Class for saving and loading game state.
 * Games should use the exact same method of instantiation
 * for saving and loading.
 */
public class GameSaver {

    public static final int NEW_SAVE = -1;

    public static final String DEFAULT_SAVES_PATH = "internal/saves";

    private Kryo kryo;

    private String savesPath;

    private Map<String, Class> nameClassMapping;

    public GameSaver(Map<String, Class> nameClassMapping) {
        this(DEFAULT_SAVES_PATH, nameClassMapping);
    }

    public GameSaver(String savesPath, Map<String, Class> nameClassMapping) {
        this.kryo = new Kryo();
        this.savesPath = savesPath;
        this.nameClassMapping = nameClassMapping;
        this.nameClassMapping.putAll(getMetadataClassMapping());

        kryoSetup();
    }

    /*
     * Only for registering custom serializers if needed
     */
    public Kryo getKryo() {
        return kryo;
    }


    private void ensureFolderCreated(String fileName) {
        Gdx.files.local(fileName).mkdirs();
    }

    /*
     * Overwrite a save file or create a new save file.
     *
     * @return  int identifier  Identifier of the save file
     */
    public int save(int saveIdentifier, Map<String, Object> objects) {
        // Get the save file from the identifier
        String fileName = getFileNameByIdentifier(saveIdentifier);
        String folderPath = String.format("%s", savesPath);
        ensureFolderCreated(folderPath);

        String filePath = String.format("%s/%s", folderPath, fileName);
        FileHandle saveFile = Gdx.files.local(folderPath);

        if(!saveFile.exists()) {
            // If save file not found, create a new save
            Gdx.app.log("GameSaver",
                    String.format("Save file %s not found, creating a new save file", saveFile.path()));
            return save(NEW_SAVE, objects);
        } else {
            int newSaveIdentifier = getIdentifierByFileName(fileName);
            objects.putAll(getMetadataFields(newSaveIdentifier));

            // Should never fail due to FileNotFoundException
            try {
                Output output = new Output(new FileOutputStream(filePath));
                kryo.writeObject(output, objects);
                output.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Save metadata
            return getIdentifierByFileName(fileName);
        }
    }

    /*
     * Return a mapping of names to objects.
     */
    public Map<String, Object> load(int saveIdentifier) {
        String fileName = getFileNameByIdentifier(saveIdentifier);
        String filePath = String.format("%s/%s", savesPath, fileName);
        FileHandle saveFile = Gdx.files.local(filePath);

        if(!saveFile.exists())
            return null;

        // Should never fail
        try {
            Input input = new Input(new FileInputStream(saveFile.path()));

            // Load the Map of objects
            Map<String, Object> objects = kryo.readObject(input, HashMap.class);

            input.close();

            return objects;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Gdx.app.exit();
            return null;
        }
    }

    public Map<String, Object>[] loadAll() {
        FileHandle savesFolder = Gdx.files.local(savesPath);

        if(!savesFolder.exists())
            return new Map[0];

        List<Map<String, Object>> saveDataList = new ArrayList<>();

        // Read all file names in the saves folder
        for(FileHandle saveFile : savesFolder.list()) {
            int saveIdentifier = getIdentifierByFileName(saveFile.name());
            Map<String, Object> saveData = load(saveIdentifier);
            saveDataList.add(saveData);
        }

        return saveDataList.toArray(new Map[0]);
    }

    /*
     * Currently only takes a number as an identifier,
     * then converts the number to a string.
     * Can be modified to make a more descriptive folder name.
     *
     * If saveIdentifier is NEW_SAVE (-1), generate a new folder name
     */
    public String getFileNameByIdentifier(int saveIdentifier) {
        if(saveIdentifier == NEW_SAVE) {
            // Read all the folder names in savesPath
            FileHandle saveDir = Gdx.files.local(savesPath);
            FileHandle[] handles = saveDir.list();

            // If no existing saves, the folder should be "1"
            int highestNumber = 0;

            for(FileHandle saveFile : handles) {
                String fileName = saveFile.name();
                int identifier = getIdentifierByFileName(fileName);
                if(identifier > highestNumber) {
                    highestNumber = identifier;
                }
            }

            return String.valueOf(highestNumber + 1);

        } else {
            return String.valueOf(saveIdentifier);
        }
    }

    public int getIdentifierByFileName(String fileName) {
        return Integer.parseInt(fileName);
    }

    /*
     * Register classes specified in nameClassMapping.
     */
    private void registerClasses() {
        Class[] classes = nameClassMapping.values().toArray(new Class[0]);
        for(int i = 0; i < classes.length; i++) {
            kryo.register(classes[i], i + 9);
        }

        // Register HashMap class: top level object in each save file
        MapSerializer mapSerializer = new MapSerializer();
        mapSerializer.setKeyClass(String.class);
        kryo.register(HashMap.class, mapSerializer);
    }

    /*
     * General fields generated when saving the settings
     */
    private Map<String, Object> getMetadataFields(int saveIdentifier) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("_id", saveIdentifier);
        metadata.put("_timestamp", new Timestamp(System.currentTimeMillis()).toString());
        return metadata;
    }

    /*
     * Name-class mapping for metadata fields
     */
    private Map<String, Class> getMetadataClassMapping() {
        Map<String, Class> metadataNameClassMapping = new HashMap<>();
        metadataNameClassMapping.put("id", int.class);
        metadataNameClassMapping.put("_timestamp", String.class);
        return metadataNameClassMapping;
    }

    /*
     * Initial Kryo instance setup
     */
    private void kryoSetup() {
        registerClasses();
        registerLibgdxClasses();
    }

    /*
     * Register serializers for LibGDX classes
     */
    private void registerLibgdxClasses() {
        kryo.register(Array.class, new Serializer<Array>() {
            {
                setAcceptsNull(true);
            }

            private Class genericType;

            public void setGenerics(Kryo kryo, Class[] generics) {
                if (generics != null && kryo.isFinal(generics[0])) genericType = generics[0];
                else genericType = null;
            }

            public void write(Kryo kryo, Output output, Array array) {
                int length = array.size;
                output.writeInt(length, true);
                if (length == 0) {
                    genericType = null;
                    return;
                }
                if (genericType != null) {
                    Serializer serializer = kryo.getSerializer(genericType);
                    genericType = null;
                    for (Object element : array)
                        kryo.writeObjectOrNull(output, element, serializer);
                } else {
                    for (Object element : array)
                        kryo.writeClassAndObject(output, element);
                }
            }

            @Override
            public Array read(Kryo kryo, Input input, Class<? extends Array> type) {
                Array array = new Array();
                kryo.reference(array);
                int length = input.readInt(true);
                array.ensureCapacity(length);
                if (genericType != null) {
                    Class elementClass = genericType;
                    Serializer serializer = kryo.getSerializer(genericType);
                    genericType = null;
                    for (int i = 0; i < length; i++)
                        array.add(kryo.readObjectOrNull(input, elementClass, serializer));
                } else {
                    for (int i = 0; i < length; i++)
                        array.add(kryo.readClassAndObject(input));
                }
                return array;
            }
        });
    }
}
