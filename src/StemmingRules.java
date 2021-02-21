public class StemmingRules {

    public static String miVerb(String str) {
        if ((str.endsWith("یم") || str.endsWith("ید") || str.endsWith("ند")) && !isVowel(str.charAt(str.length() - 3))) {
            return str.substring(3, str.length() - 2);
        } else if (str.endsWith("م") || str.endsWith("ی") || str.endsWith("د")) {
            return str.substring(3, str.length() - 1);
        } else {
            return str.substring(3);
        }
    }

    public static String beVerb(String str) {
        if ((str.endsWith("یم") || str.endsWith("ید") || str.endsWith("ند")) && !isVowel(str.charAt(str.length() - 3))) {
            return str.substring(1, str.length() - 2);
        } else if (str.endsWith("م") || (str.endsWith("ی") && ! isVowel(str.charAt(str.length() - 2))) || str.endsWith("د")) {
            return str.substring(1, str.length() - 1);
        } else {
            return str;
        }
    }

    public static String tarTarin(String str) {
        if (str.endsWith("\u200Cتر")) {
            return str.substring(0, str.length() - 3);
        } else if (str.endsWith("تر")) {
            return str.substring(0, str.length() - 2);
        } else if (str.endsWith("\u200Cترین")) {
            return str.substring(0, str.length() - 5);
        } else if (str.endsWith("ترین")) {
            return str.substring(0, str.length() - 4);
        }
        return str;
    }

    public static String singular_zamir(String str) {
        return str.substring(0, str.length() - 3);
    }

    public static String plural_zamir(String str) {
        return str.substring(0, str.length() - 4);
    }

    public static String stem(String str) {
        if (str.startsWith("می\u200C")) {
            return miVerb(str);
        } else if (str.length() >=2 && str.startsWith("ب") && !isVowel(str.charAt(1))) {
            return beVerb(str);
        } else if (str.endsWith("تر") || str.endsWith("ترین")) {
            return tarTarin(str);
        } else if (str.endsWith("\u200Cام") || str.endsWith("\u200Cات") || str.endsWith("\u200Cاش")) {
            return singular_zamir(str);
        } else if (str.endsWith("\u200Cمان") || str.endsWith("\u200Cتان") || str.endsWith("\u200Cشان")) {
            return plural_zamir(str);
        } else if (str.length() >= 4 && (str.endsWith("ها") || (str.endsWith("ات") && str.length() > 4))) {
            return singular(str);
        } else if (str.length() >= 3 && (str.endsWith("ای") || str.endsWith("وی")) && ! isVowel(str.charAt(str.length() - 3))) {
            return mianji(str);
        } else if (str.endsWith("ایی") || str.endsWith("ویی")) {
            return mianji(nakare(str));
        } else if (str.endsWith("هایش") || str.endsWith("هایت") || str.endsWith("هایم")) {
            return str.substring(0, str.length() - 2);
        } else {
            return str;
        }
    }

    public static String mianji(String str) {
        return str.substring(0, str.length() - 1);
    }

    public static String singular(String str) {
        if (str.endsWith("گان") && ! isVowel(str.charAt(str.length() - 4))) {
            return (str.substring(0, str.length() - 3) + "ه");
        } else if (str.endsWith("\u200Cها")){
            return str.substring(0, str.length() - 3);
        } else if (str.endsWith("ات") || str.endsWith("ها")) {
            return str.substring(0, str.length() - 2);
        } else {
            return str;
        }
    }

    public static String nakare(String str) {
        return str.substring(0, str.length() - 1);
    }

    private static boolean isVowel(char c) {
        if (c == 'ا' || c == 'ی' || c == 'و') {
            return true;
        } else {
            return false;
        }
    }


}
