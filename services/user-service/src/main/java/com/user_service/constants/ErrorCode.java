package com.user_service.constants;

/**
 * A utility class that defines error codes used throughout the application.
 * <p>
 * This class provides a set of static constants representing specific error
 * codes for various error scenarios. These error codes are used to standardize
 * error reporting and facilitate easier debugging and client-side error
 * handling.
 * </p>
 *
 * @author BJIT
 * @version 1.0
 */
public final class ErrorCode {
    public static final String ALREADY_EXIST = "40101";
    public static final String JWT_TOKEN_EXPIRED = "40103";
    public static final String INVALID_JWT_TOKEN = "40104";
    public static final String INVALID_PASSWORD_PATTERN = "4006";
    public static final String ROLE_NOT_FOUND = "4007";
    public static final String INVALID_ARGUMENT = "4024";
    public static final String UNAUTHORIZED_OPERATION = "4025";
    public static final String INVALID_DATA = "4026";
    public static final String ACCESS_DENIED = "4027";
    public static final String USER_NOT_FOUND = "4028";
    public static final String WRONG_CREDENTIALS = "4029";
    public static final String USERNAME_LENGTH = "4030";
    public static final String INVALID_EMAIL = "4031";
    public static final String INTERNAL_SERVER_ERROR = "5000";
    public static final int MAINTENANCE_MODE = 9999;

    private ErrorCode() {
    }

}
