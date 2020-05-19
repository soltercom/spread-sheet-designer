package form.validator;

import java.util.regex.Pattern;

public class NameValidator extends Validator<String> {

    @Override
    public boolean validate(String value) {
        String pattern = "^[a-zA-Zа-яА-Я_][a-zA-Z0-9_а-яА-Я]*$";
        return Pattern.compile(pattern).matcher(value).matches();
    }
}
