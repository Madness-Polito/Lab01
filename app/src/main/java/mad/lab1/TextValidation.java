package mad.lab1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// static class to match regex
public class TextValidation {

    public static boolean isValidMail(String email) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return TextValidation.matches(emailPattern, email);
    }

    public static boolean isValidPhone(String phone){
        String phonePattern = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";

        return TextValidation.matches(phonePattern, phone);
    }

    public static boolean isValidName(String name){
        String namePattern = "^[a-zA-Z\\s]+"; ;

        return TextValidation.matches(namePattern, name);
    }

    private static boolean matches(String p, String s){
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }


}
