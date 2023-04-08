package com.georgeneokq.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.georgeneokq.engine.font.FontGenerator;
import com.georgeneokq.game.actor.npc.NPC;
import com.georgeneokq.game.dialog.Dialog;
import com.georgeneokq.game.question.Choice;
import com.georgeneokq.game.question.Question;
import com.georgeneokq.game.question.QuestionFilter;
import com.georgeneokq.game.question.QuestionFilterPredicate;
import com.georgeneokq.game.question.QuestionType;
import com.georgeneokq.engine.save.GameSaver;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.engine.settings.NumberSetting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/*
 * Instantiate global variables here
 */
public class Globals {
    private static Globals globals;

    private Globals() {}

    private AssetManager assetManager;
    private GameSaver gameSaver;
    private Language language = Language.EN;
    private Map<String, String> strings = new HashMap<>();
    private Map<String, Dialog> dialogs = new HashMap<>();
    private Map<String, Question> questions = new HashMap<>();
    private Map<String, NPC> npcMap = new HashMap<>();

    private Map<String, Integer> mapStates = new HashMap<>();
    private String battleMusicPath = "audio/bgm";
    public int resolutionWidth = 1920;
    public int resolutionHeight = 1080;

    public static Globals getInstance() {
        if(globals == null)
            globals = new Globals();

        return globals;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public GameSaver getGameSaver() {
        return gameSaver;
    }

    public void setGameSaver(GameSaver gameSaver) {
        this.gameSaver = gameSaver;
    }

    public int getMapState(String mapName) {
        if(!mapStates.containsKey(mapName))
            mapStates.put(mapName, 1);
        return mapStates.get(mapName);
    }

    public void updateMapState(String mapName, int mapState) {
        mapStates.put(mapName, mapState);
    }

    public void setMapStates(Map<String, Integer> mapStates) {
        this.mapStates = mapStates;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;

        // Load the appropriate dialogs file according to language
        String dialogsFilePath = String.format("game/dialogs/%s.xml", language.name().toLowerCase());
        dialogs = loadDialogs(dialogsFilePath);

        // Load the appropriate questions file according to language
        String questionsFilePath = String.format("game/questions/%s.xml", language.name().toLowerCase());
        questions = loadQuestions(questionsFilePath);

        // Load the appropriate strings file according to language
        String stringsFilePath = String.format("strings/%s.xml", language.name().toLowerCase());
        strings = loadStrings(stringsFilePath);
    }

    /**
     *
     * @param xmlFilePath XML file to read strings from.
     *                    The root node should be named "strings". Child nodes should be named
     *                    "string", with a "name" attribute to identify the string.
     * @return Map<String, String> A mapping of string names to their contents
     */
    private Map<String, String> loadStrings(String xmlFilePath) {
        // For font generation, maintain a unique set of characters
        Set<Character> characterSet = new HashSet<>();

        Map<String, String> strings = new HashMap<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            FileHandle xmlFile = Gdx.files.internal(xmlFilePath);
            Document doc = db.parse(xmlFile.read());

            // Normalize text to handle uneven spacing/indentations
            doc.getDocumentElement().normalize();

            NodeList stringNodes = doc.getElementsByTagName("string");

            for (int i = 0; i < stringNodes.getLength(); i++) {
                Node node = stringNodes.item(i);
                Element stringElement = (Element) node;

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    String stringName = stringElement.getAttribute("name");
                    String contents = stringElement.getTextContent().trim();
                    strings.put(stringName, contents);

                    List<Character> chars = contents
                            .chars()
                            .mapToObj(e -> (char) e)
                            .collect(Collectors.toList());
                    characterSet.addAll(chars);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        String generateCharacters = characterSet.toString();
        FontGenerator fontGenerator = FontGenerator.getInstance();
        fontGenerator.addGenerateCharacters(generateCharacters);

        return strings;
    }

    public String getString(String stringName) {
        String string = strings.get(stringName);
        if(string == null)
            string = "";
        return string;
    }

    /**
     *
     * @param xmlFilePath XML file to read dialogs from.
     *                    The root node should be named "dialogs". Each child node should be named
     *                    "dialog", with a "name" attribute to identify the string.
     *                    Each line in the dialog should be wrapped in a <line></line>
     *                    element, with an optional "audio" property associated with it.
     * @return Map<String, String> A mapping of dialog names to their contents
     */
    private Map<String, Dialog> loadDialogs(String xmlFilePath) {
        String audioFilePath = "audio/voice";

        Map<String, Dialog> dialogs = new HashMap<>();

        // For font generation, maintain a unique set of characters
        Set<Character> characterSet = new HashSet<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            FileHandle xmlFile = Gdx.files.internal(xmlFilePath);
            Document doc = db.parse(xmlFile.read());

            // Normalize text to handle uneven spacing/indentations
            doc.getDocumentElement().normalize();

            NodeList dialogNodes = doc.getElementsByTagName("dialog");

            for (int i = 0; i < dialogNodes.getLength(); i++) {
                Node node = dialogNodes.item(i);
                Element dialogElement = (Element) node;

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    String dialogName = dialogElement.getAttribute("name");

                    NodeList dialogLineNodes = dialogElement.getElementsByTagName("line");

                    Dialog.Line[] lines = new Dialog.Line[dialogLineNodes.getLength()];

                    for(int dialogLineIndex = 0; dialogLineIndex < dialogLineNodes.getLength(); dialogLineIndex++) {
                        Element dialogLineElement = (Element) dialogLineNodes.item(dialogLineIndex);
                        String audioFileName = dialogLineElement.getAttribute("audio");
                        String originalText = dialogLineElement.getElementsByTagName("original")
                                .item(0).getTextContent();
                        String subtitle = dialogLineElement.getElementsByTagName("subtitle")
                                .item(0).getTextContent();

                        // Retrieve the audio, if file is provided
                        Music audio = null;
                        try {
                            audio = assetManager.get(String.format("%s/%s",
                                    audioFilePath, audioFileName), Music.class);
                        } catch (Exception e) {
                            Gdx.app.log("Dialogs", String.format("%s not loaded", audioFileName));
                        }

                        lines[dialogLineIndex] = (new Dialog.Line(originalText, subtitle, audio));
                    }

                    Dialog dialog = new Dialog(lines);
                    dialogs.put(dialogName, dialog);

                    // Insert characters into character set for font generation
                    for(Dialog.Line line : dialog.getLines()) {
                        List<Character> chars = line.getOriginalText()
                                                    .chars()
                                                    .mapToObj(e -> (char) e)
                                                    .collect(Collectors.toList());
                        characterSet.addAll(chars);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        String generateCharacters = characterSet.toString();
        FontGenerator fontGenerator = FontGenerator.getInstance();
        fontGenerator.addGenerateCharacters(generateCharacters);

        return dialogs;
    }

    public Dialog getDialog(String dialogName) {
        return dialogs.get(dialogName);
    }

    private boolean isCorrectAnswer(Element choiceElement) {
        NodeList nodeList = choiceElement.getElementsByTagName("answer");
        if(nodeList.getLength() == 1)
            return true;
        return false;
    }

    /**
     *
     * @param xmlFilePath XML file to read questions from.
     *                    The root node should be named "questions". There should be a child node named
     *                    "question", with a "name" attribute to identify the question,
     *                    and an optional "voice" attribute to play an audio for the question.
     *                    Each question node should contain a "question" child node which is the text
     *                    for the question, and should also contain 4 "choice" nodes.
     *                    There should be one and only one "choice" node with an "answer" child node
     *                    to indicate the correct answer for the question.
     * @return Map<String, String> A mapping of question names to the question contents
     */
    private Map<String, Question> loadQuestions(String xmlFilePath) {
        String audioFilePath = "audio/voice";

        // For font generation, maintain a unique set of characters
        Set<Character> characterSet = new HashSet<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            FileHandle xmlFile = Gdx.files.internal(xmlFilePath);
            Document doc = db.parse(xmlFile.read());

            // Normalize text to handle uneven spacing/indentations
            doc.getDocumentElement().normalize();

            NodeList questionNodes = doc.getElementsByTagName("question");

            for (int i = 0; i < questionNodes.getLength(); i++) {
                Node node = questionNodes.item(i);
                Element questionElement = (Element) node;

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    String questionName = questionElement.getAttribute("name");

                    Node typeNode = questionElement.getElementsByTagName("type").item(0);

                    String typeString = "";

                    if(typeNode != null) {
                        typeString = typeNode.getTextContent().trim();
                    }

                    String weightageString = questionElement.getAttribute("weightage");

                    // Get choices
                    NodeList choiceNodes = questionElement.getElementsByTagName("choice");

                    Choice[] choices = new Choice[choiceNodes.getLength()];

                    int correctChoiceIndex = -1;

                    for(int choiceIndex = 0; choiceIndex < choiceNodes.getLength(); choiceIndex++) {
                        Node choiceNode = choiceNodes.item(choiceIndex);
                        Element choiceElement = (Element) choiceNode;

                        if(isCorrectAnswer(choiceElement))
                            correctChoiceIndex = choiceIndex;

                        String choiceValue = choiceElement.getTextContent().trim();
                        choices[choiceIndex] = new Choice(choiceValue);
                    }

                    // Get question
                    NodeList questionElements = questionElement.getElementsByTagName("question");

                    if(questionElements.getLength() == 0)
                        continue;

                    String questionText = questionElement.getElementsByTagName("question")
                                                        .item(0).getTextContent().trim();

                    // Ensure that there is a correct answer specified
                    if(correctChoiceIndex == -1) {
                        throw new RuntimeException(
                                String.format("Question \"%s\" does not have a correct answer", questionText));
                    }

                    // Get voice
                    String voiceFileName = questionElement.getAttribute("voice");

                    Music voice = null;

                    try {
                        String voiceFilePath = String.format(
                                "%s/%s", audioFilePath, voiceFileName);
                        voice = assetManager.get(voiceFilePath);
                    } catch(Exception e) {
                        Gdx.app.log(voiceFileName, "Voice file not loaded");
                    }

                    int weightage = 10;
                    if(!weightageString.equals("")) {
                        try {
                            weightage = Integer.parseInt(weightageString);
                        } catch (Exception e) {
                            Gdx.app.log("Type Error",
                                    String.format("Weightage for %s is invalid, defaulting to 1", questionName));
                        }
                    }
                    QuestionType type = Question.getTypeByString(typeString);
                    Question question = new Question(questionName, type, questionText, choices,
                            correctChoiceIndex, weightage, voice);
                    questions.put(questionName, question);

                    // Choice texts for font generation
                    for(Choice choice : choices) {
                        List<Character> chars = choice.getValue()
                                .chars()
                                .mapToObj(e -> (char) e)
                                .collect(Collectors.toList());
                        characterSet.addAll(chars);
                    }

                    // Question text for font generation
                    List<Character> chars = questionText
                                .chars()
                                .mapToObj(e -> (char) e)
                                .collect(Collectors.toList());
                    characterSet.addAll(chars);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        String generateCharacters = characterSet.toString();
        FontGenerator fontGenerator = FontGenerator.getInstance();
        fontGenerator.addGenerateCharacters(generateCharacters);

        return questions;
    }

    public Question getQuestion(String questionName) {
        return questions.get(questionName);
    }

    public Question[] getQuestions(QuestionFilter questionFilter) {
        return getQuestions(new QuestionFilter[]{questionFilter});
    }

    /**
     * Returns an array of filtered questions, shuffled.
     * @param questionFilters QuestionFilter to specify questions to retrieve
     * @return A copy of the questions array, filtered
     */
    public Question[] getQuestions(QuestionFilter[] questionFilters) {
        return getQuestions(questionFilters, true);
    }

    /**
     * @param questionFilters Filters to specify questions to retrieve
     * @param shuffle  Whether to shuffle the questions or not.
     * @return A copy of the questions array, filtered
     */
    public Question[] getQuestions(QuestionFilter[] questionFilters, boolean shuffle) {
        // Create a copy of the original questions array
        List<Question> questionsCopy = new ArrayList<>(questions.values());

        Map<String, Question> filteredQuestionsMap = new HashMap<>();

        // Filter from copied questions array
        if(questionFilters != null) {

            // For each question filter, retrieve until the specified limit.
            // If 0 was specified as the limit, retrieve all
            for(QuestionFilter questionFilter : questionFilters) {
                QuestionFilterPredicate predicate = questionFilter.getPredicate();
                int retrieveLimit = questionFilter.getRetrieveLimit();
                int totalRetrieved = 0;

                // Apply predicate on every question
                for(Question question : questionsCopy) {
                    if(!filteredQuestionsMap.containsKey(question.getName()) &&
                            predicate.filter(question)) {
                        // Add to the questions mapping only if not yet added
                        filteredQuestionsMap.put(question.getName(), question);
                        totalRetrieved += 1;
                    }
                    if(retrieveLimit > 0 && totalRetrieved == retrieveLimit)
                        break;
                }
            }
        }

        List<Question> filteredQuestions = new ArrayList<>(filteredQuestionsMap.values());

        if(shuffle)
            Collections.shuffle(filteredQuestions);

        return filteredQuestions.toArray(new Question[0]);
    }

    public NPC getNPC(String name) {
        return npcMap.get(name);
    }

    public void addNPC(String name, NPC npc) {
        npcMap.put(name, npc);
    }

    public void setNPCs(Map<String, NPC> npcMap) {
        this.npcMap = npcMap;
    }

    public String getBattleMusicPath() {
        return battleMusicPath;
    }

    /**
     *
     * @param soundName Sound file name, without directories and without file extension
     */
    public void playSoundEffect(String soundName) {
        SettingsManager settingsManager = SettingsManager.getInstance();
        String soundEffectsFolder = "audio/sound_effects";
        String fileExtension = "mp3";
        String soundFilePath = String.format("%s/%s.%s", soundEffectsFolder, soundName, fileExtension);
        try {
            float volume = settingsManager.getSetting("general.sfx_volume", NumberSetting.class).getValue();
            assetManager.get(soundFilePath, Sound.class).play(volume);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
