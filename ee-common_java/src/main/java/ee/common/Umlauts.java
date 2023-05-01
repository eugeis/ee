package ee.common;

public class Umlauts {
    private static final String[][] UMLAUT_REPLACEMENTS =
            {{"Ä", "Ae"}, {"Ü", "Ue"},
                    {"Ö", "Oe"}, {"ä", "ae"},
                    {"ü", "ue"}, {"ö", "oe"}, {"ß", "ss"},
                    {"Ã¤", "ae"}, {"Ã„", "Ae"},
                    {"Ã¶", "oe"}, {"Ã–", "Oe"},
                    {"Ã¼", "ue"}, {"Ãœ", "Ue"},
                    {"ÃŸ", "ss"}

            };

    public static String replaceUmlauts(String orig) {
        String ret = orig;

        for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
            ret = ret.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
        }

        return ret;
    }
}
