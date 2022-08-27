package com.landingis.api.dto;

public class ErrorCode {

    /**
     * General error code
     */
    public static final String GENERAL_ERROR_UNAUTHORIZED = "ERROR-GENERAL-000";
    public static final String GENERAL_ERROR_NOT_FOUND = "ERROR-GENERAL-001";
    public static final String GENERAL_ERROR_BAD_REQUEST = "ERROR-GENERAL-002";
    public static final String GENERAL_ERROR_LOGIN_FAILED = "ERROR-GENERAL-003";
    public static final String GENERAL_ERROR_NOT_MATCH = "ERROR-GENERAL-004";
    public static final String GENERAL_ERROR_WRONG_HASH = "ERROR-GENERAL-005";
    public static final String GENERAL_ERROR_LOCKED = "ERROR-GENERAL-006";
    public static final String GENERAL_ERROR_INVALID = "ERROR-GENERAL-007";
    public static final String GENERAL_ERROR_INACTIVE = "ERROR-GENERAL-008";

    /**
     * Settings error code
     */
    public static final String SETTINGS_ERROR_UNAUTHORIZED = "ERROR-SETTINGS-000";
    public static final String SETTINGS_ERROR_NOT_FOUND = "ERROR-SETTINGS-001";
    public static final String SETTINGS_ERROR_BAD_REQUEST = "ERROR-SETTINGS-002";


    /**
     * Customer error code
     */
    public static final String CUSTOMER_ERROR_UNAUTHORIZED = "ERROR-CUSTOMER-000";
    public static final String CUSTOMER_ERROR_NOT_FOUND = "ERROR-CUSTOMER-001";
    public static final String CUSTOMER_ERROR_BAD_REQUEST = "ERROR-CUSTOMER-002";


    /**
     * Category error code
     */
    public static final String CATEGORY_ERROR_UNAUTHORIZED = "ERROR-CATEGORY-000";
    public static final String CATEGORY_ERROR_NOT_FOUND = "ERROR-CATEGORY-001";

    /**
     * Group error code
     */
    public static final String GROUP_ERROR_UNAUTHORIZED = "ERROR-GROUP-000";
    public static final String GROUP_ERROR_NOT_FOUND = "ERROR-GROUP-001";
    public static final String GROUP_ERROR_EXIST = "ERROR-GROUP-002";
    public static final String GROUP_ERROR_CAN_NOT_DELETED = "ERROR-GROUP-003";

    /**
     * Permission error code
     */
    public static final String PERMISSION_ERROR_UNAUTHORIZED = "ERROR-PERMISSION-000";
    public static final String PERMISSION_ERROR_NOT_FOUND = "ERROR-PERMISSION-001";

    /**
     * News error code
     */
    public static final String NEWS_ERROR_UNAUTHORIZED = "ERROR-NEWS-000";
    public static final String NEWS_ERROR_NOT_FOUND = "ERROR-NEWS-001";

    /**
     * Device error code
     */
    public static final String DEVICE_ERROR_UNAUTHORIZED = "ERROR-DEVICE-000";
    public static final String DEVICE_ERROR_NOT_FOUND = "ERROR-DEVICE-001";
    public static final String DEVICE_ERROR_BAD_REQUEST = "ERROR-DEVICE-002";
    public static final String DEVICE_ERROR_VERIFY_FAILED = "ERROR-DEVICE-003";
    public static final String DEVICE_ERROR_UNACTIVE = "ERROR-DEVICE-004";
    public static final String DEVICE_ERROR_QRCODE_EXPIRED = "ERROR-DEVICE-005";
    public static final String DEVICE_ERROR_QRCODE_PASSWORD_NOT_MATCHED = "ERROR-DEVICE-006";
    public static final String DEVICE_ERROR_QRCODE_DEVICE_NOT_OWNED_DB_CONFIG = "ERROR-DEVICE-007";
    public static final String DEVICE_ERROR_DEVICE_NOT_ENABLED_SYNC= "ERROR-DEVICE-008";
    public static final String DEVICE_ERROR_SQL_DELETE_ERROR= "ERROR-DEVICE-009";
    public static final String DEVICE_ERROR_DEVICE_EXISTS = "ERROR-DEVICE-010";
    public static final String DEVICE_ERROR_NEW_SESSION_ID = "ERROR-DEVICE-011";
    public static final String DEVICE_ERROR_CREATE_POS_USER = "ERROR-DEVICE-012";

    /**
     * Db config error code
     */
    public static final String DB_CONFIG_ERROR_UNAUTHORIZED = "ERROR-DB-CONFIG-000";
    public static final String DB_CONFIG_ERROR_NOT_FOUND = "ERROR-DB-CONFIG-001";
    public static final String DB_CONFIG_ERROR_NOT_INITIALIZE = "ERROR-DB-CONFIG-002";
    public static final String DB_CONFIG_ERROR_CANNOT_CREATE_DB = "ERROR-DB-CONFIG-003";
    public static final String DB_CONFIG_ERROR_CANNOT_RESTORE_DB = "ERROR-DB-CONFIG-004";
    public static final String DB_CONFIG_ERROR_UPLOAD = "ERROR-DB-RESTORE-005";


    private ErrorCode() { throw new IllegalStateException("Utility class"); }
}
