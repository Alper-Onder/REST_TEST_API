package test;

public class DEFAULTS {

	public static final int MIN_PASSWORD_LENGTH = 8;
	public static final int MAX_PASSWORD_LENGTH = 16;
	public static final int MIN_USERNAME_LENGTH = 4;
	public static final String[] FORBIDDEN_LINKS= {"ALLLS","CST","REG",};
	
}

enum REGISTER_MESSAGES
{
	DIFFERENT_PASSWORDS,
	WRONG_EMAIL,
	TOO_SHORT_PASSWORD,
	TOO_LONG_PASSWORD,
	TOO_SHORT_USERNAME,
	USERNAME_EXIST,
	EMAIL_EXIST,
	CONNECTION_ERROR,
	SUCCESSFUL
	
}
