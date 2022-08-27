package com.landingis.api.constant;


import com.landingis.api.utils.ConfigurationService;

public class LandingISConstant {
    public static final String ROOT_DIRECTORY =  ConfigurationService.getInstance().getString("file.upload-dir","/tmp/upload");
    public static final String MYSQL_ENVIRONMENT_PATH =  ConfigurationService.getInstance().getString("mysql.environment.path","mysql");

    public static final Integer SETTING_GROUP_ID_ADMIN = 1;
    public static final Integer SETTING_GROUP_ID_CUSTOMER = 2;

    public static final Integer SETTINGS_GROUP_TYPE_NUMBER = 0;
    public static final Integer SETTINGS_GROUP_TYPE_NAME = 1;

    public static final String DELIM = "::";
    public static final String PASSWORD =  "AABBCCDDEEFFGGAAAVVGG";

    public static final Integer DEVICE_PLATFORM_ANDROID = 1;
    public static final Integer DEVICE_PLATFORM_IOS = 2;
    public static final Integer DEVICE_PLATFORM_WEB = 3;

    public static final Integer DEVICE_TYPE_POS = 1;
    public static final Integer DEVICE_TYPE_REMVIEW = 2;

    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_CUSTOMER = 2;
    public static final Integer USER_KIND_EMPLOYEE = 3;
    public static final Integer USER_KIND_COLLABORATOR = 4;

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_LOCK = -1;
    public static final Integer STATUS_DELETE = -2;

    public static final Integer GROUP_KIND_SUPER_ADMIN = 1;
    public static final Integer GROUP_KIND_CUSTOMER = 2;
    public static final Integer GROUP_KIND_EMPLOYEE = 3;
    public static final Integer GROUP_KIND_COLLABORATOR = 4;

    public static final Integer MAX_ATTEMPT_FORGET_PWD = 5;
    public static final Integer MAX_TIME_FORGET_PWD = 5 * 60 * 1000; //5 minutes
    public static final Integer MAX_ATTEMPT_LOGIN = 5;
    public static final Integer MAX_TIME_VERIFY_ACCOUNT = 5 * 60 * 1000; //5 minutes
    public static final Integer MAX_TIME_VERIFY_QRCODE = 60 * 1000; // 1 minute

    public static final Integer CATEGORY_KIND = 1;


    public static final Integer GENDER_MALE = 1;
    public static final Integer GENDER_FEMALE = 2;
    public static final Integer GENDER_OTHER = 3;

    public static final String VERIFY_QRCODE_PERMISSION = "/device/verify-token";
    public static final String REMVIEW_PERMISISONS = "/news/client-get-with-reaction,/news/reaction-list,/news/reaction-get,/news/reaction-create,/news/reaction-delete,/bill/check-payment,/device/mobile-device-update,/device/mobile-device-delete";
    public static final String POS_PERMISSIONS = "/settings/create,/key/delete-by-device-id,/logs/change-employee-name,/bill/list-by-525354,/device/check-status,/logs/list-by-employee,/logs/delete-by-employee,/bill/get-setting,/bill/count-order-by-employee,/bill/employee-payment,/logs/create,/logs/create-list,/logs/get,/logs/list,/logs/delete,/logs/empty,/data-backup/list,/data-backup/get,/data-backup/create,/data-backup/update,/data-backup/delete,/data-backup/empty,/device/confirm_new_session_id,/bill/list-employee-revenue,/bill/list,/bill/bill-by-employee,/bill/list-employee,/bill/get,/bill/update,/bill/update-list,/bill/empty,/bill/create,/bill/create-list,/device/offline,/device/online,/device/request-qrcode,/sync-tenant/sync,/db-config/restore,/device/get,/device/verify-token,/device/mobile-device-list,/device/mobile-device-delete";


    private LandingISConstant(){
        throw new IllegalStateException("Utility class");
    }

}
