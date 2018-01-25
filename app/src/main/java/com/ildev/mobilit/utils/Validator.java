package com.ildev.mobilit.utils;

/**
 * Created by guilherme on 21/01/18.
 * Edit by ArabeLindao on 24/01/18
 */

import java.util.regex.Pattern;


/**
 * This class will store the methods that will be used to validate
 * the user in the Login Page
 */
public class Validator {

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9]+[._a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]*[a-zA-Z]*@[a-zA-Z0-9]{2,8}.[a-zA-Z.]{2,6}";
    private static final int MIN_CHARACTERS = 6;
    private static final String NAME_PATTERN = "^[a-zA-Z\\s]+";

    // Method that checks if the String Email matches with the EMAIL_PATTERN
    public static boolean validateEmail(String email){
        return email.matches(EMAIL_PATTERN);
    }

    // Method that checks if the Password has more than 5 digits
    public static boolean validatePassword(String password){
        return password.length() >= MIN_CHARACTERS;
    }

    // Methos that checks if the String Name matches with NAME_PATTERN
    public static boolean validateName(String name){
        return name.matches(NAME_PATTERN);
    }
}
