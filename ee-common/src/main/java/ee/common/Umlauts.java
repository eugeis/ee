package ee.common;

public class Umlauts {
    private static String[][] UMLAUT_REPLACEMENTS =
            {{new String("Ä"), "Ae"}, {new String("Ü"), "Ue"},
                    {new String("Ö"), "Oe"}, {new String("ä"), "ae"},
                    {new String("ü"), "ue"}, {new String("ö"), "oe"}, {new String("ß"), "ss"}};

    public static String replaceUmlauts(String orig) {
        String ret = orig;

        for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
            ret = ret.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
        }

        return ret;
    }
}
