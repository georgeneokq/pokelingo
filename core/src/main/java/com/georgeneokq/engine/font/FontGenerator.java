package com.georgeneokq.engine.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Generates fonts by Font enum value and caches them to avoid regenerating
 * multiple instances of the same BitmapFont
 */
public class FontGenerator {

    public static final String asciiCharacters = "!  \"  #  $  %  &  '  (  )  *  +  ,  -  .  /  0  1  2  3  4  5  6  7  8  9  :  ;  <  =  >  ?  @  A  B  C  D  E  F  G  H  I  J  K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z  [  \\  ]  ^  _  `  a  b  c  d  e  f  g  h  i  j  k  l  m  n  o  p  q  r  s  t  u  v  w  x  y  z  {  |  }  ~  \u2190  \u2191  \u2192  \u2193  \u2640  \u2642";
    private static final Set<Character> asciiCharacterSet = new HashSet<>();

    static {
        for(int i = 0; i < asciiCharacters.length(); i++) {
            asciiCharacterSet.add(asciiCharacters.charAt(i));
        }
    }

    private static FontGenerator fontGenerator;

    private String generateCharacters = asciiCharacters;
    private Map<String, IFontGenerator> fontGeneratorMapping;

    private FontGenerator() {
        // Create mapping of enum values to its generator methods
        fontGeneratorMapping = new HashMap<>();
        fontGeneratorMapping.put(FontEnum.DEFAULT.name(), this::notosans);
        fontGeneratorMapping.put(FontEnum.MGENPLUS.name(), this::mgenplus);
        fontGeneratorMapping.put(FontEnum.NOTOSANS.name(), this::notosans);
    }

    public static FontGenerator getInstance() {
        if(fontGenerator == null)
            fontGenerator = new FontGenerator();

        return fontGenerator;
    }

    /**
     * Calls createFont(String fontEnum, int size) with a default size of 12.
     * This method signature can be used for convenience when you are sure
     * that the specified font has already been generated earlier.
     * @param fontEnum A value from FontEnum to specify the font to generate
     * @return
     */
    public BitmapFont createFont(String fontEnum) {
        return createFont(fontEnum, 12);
    }

    /**
     * Creates the specified font. If the font is already cached, it will not be
     * regenerated, and the cached font will immediately be used as the return value.
     * @param fontEnum A value from FontEnum to specify the font to generate
     * @param size  Font size to generate
     * @return
     */
    public BitmapFont createFont(String fontEnum, int size) {
        // Get generator method
        IFontGenerator generator = fontGeneratorMapping.get(fontEnum);
        BitmapFont font = generator.generate(size, generateCharacters);
        return font;
    }

    public static String getUniqueCharacters(String characters) {
        // Create a set from the specified characters
        Set<Character> characterSet = new HashSet<>();
        for(int i = 0; i < characters.length(); i++) {
            characterSet.add(characters.charAt(i));
        }

        // Merge the specified characters' set and the ascii character set
        characterSet.addAll(asciiCharacterSet);

        // Join the characters in the set into one single string
        StringBuilder generateCharacters = new StringBuilder();
        for(Character c: characterSet) {
            generateCharacters.append(c);
        }

        return generateCharacters.toString();
    }

    /**
     *
     * @param generator  FreeTypeFontGenerator Initialised free type font generator
     * @param size       font size
     * @param characters Characters to generate fonts for
     * @return
     */
    public BitmapFont generateCommonFont(FreeTypeFontGenerator generator, int size, String characters) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = new Color(96f/255f, 96f/255f, 96f/255f, 1f);
        parameter.shadowColor = new Color(208f/255f, 208f/255f, 200f/255f, 1f);
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;

        if(characters != null) {
            parameter.characters = getUniqueCharacters(characters);
        }
        else {
            parameter.characters = asciiCharacters;
        }

        BitmapFont font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        generator.dispose();

        return font;
    }

    private BitmapFont mgenplus(int size, String characters) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/mgenplus_regular.ttf"));
        return generateCommonFont(generator, size, characters);
    }

    private BitmapFont notosans(int size, String characters) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/NotoSansJP-Light.otf"));
        return generateCommonFont(generator, size, characters);
    }

    public void addFontGenerator(String fontName, IFontGenerator iFontGenerator) {
        fontGeneratorMapping.put(fontName, iFontGenerator);
    }

    public String getGenerateCharacters() {
        return generateCharacters;
    }

    public void setGenerateCharacters(String generateCharacters) {
        this.generateCharacters = generateCharacters;
    }

    public void addGenerateCharacters(String generateCharacters) {
        String combinedString = this.generateCharacters.concat(generateCharacters);
        List<Character> chars = combinedString
                .chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());

        Set<Character> characterSet = new HashSet<>();
        characterSet.addAll(chars);
        this.generateCharacters = characterSet.toString();
    }
}
