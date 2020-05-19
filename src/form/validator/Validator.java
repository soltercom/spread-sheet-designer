package form.validator;

public abstract class Validator<T> {
    abstract boolean validate(T value);
}
