package com.pratham.prathamdigital.util;

public class PD_Constant {

    public static final String BASE_URL = "http://35.162.11.219/";

    // POST Request Strings
    public static final String KEY_USER_USERNAME = "username";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER = "user";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_TOTAL_COST = "total_cost";
    public static final String KEY_SELECTED_ITEM_LIST = "selected_item_list";
    public static final String KEY_COUPON = "coupon";
    public static final String KEY_REDEEM_POINTS = "redeem_points";
    public static final String KEY_DELIVERY_DATE = "delivery_date";
    public static final String KEY_TRANSACTION_MODE = "transaction_mode";
    public static final String KEY_TRANSACTION_RESPONSE = "transaction_response";
    public static final String KEY_DELIVERY_SLOT = "delivery_slot";
    public static final String KEY_DELIVERY_ADDRESS = "delivery_address";
    public static final String KEY_KITCHEN = "kitchen";
    public static final String KEY_ORDER_ITEM = "order_item";
    public static final String KEY_NAME = "name";
    public static final String KEY_ITEM = "item";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_ID = "id";
    public static final String KEY_DIET_PLAN = "diet_plan";
    public static final String KEY_START_DATE = "start_date";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_MEAL_TIME = "meal_time";
    public static final String KEY_TIME = "time";
    public static final String KEY_EXTRA = "extra";
    public static final String KEY_PLAN = "plan";
    public static final String KEY_REGISTRATION_ID = "registration_id";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_COST = "cost";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_ORGANIC = "organic";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_PAYMENT_OPTION = "payment_option";
    public static final String KEY_CREDIT = "credit";
    public static final String KEY_DEBIT = "debit";
    public static final String KEY_ISORDER = "is_order";
    public static final String KEY_ISDIETPLAN = "is_dietplan";
    public static final String KEY_TYPE = "type";
    public static final String KEY_REFER_CODE= "refer_code";
    public static final String FE_FACEBOOK = "https://www.facebook.com/FirstEat/";
    public static final String FE_TWITTER = "https://twitter.com/firsteat_in";
    public static final String FE_INSTA = "https://www.instagram.com/firsteat_in/";

    // Preference Keys
    public static final String PREF_KEY_USER_TOKEN = "user_token";
    public static final String PREF_KEY_KITCHENID = "kitchen_id";
    public static final String PREF_KEY_LOCATIONID = "location_id";
    public static final String PREF_KEY_LOCATIONNAME = "location_name";
    public static final String PREF_KEY_ORDER_ID = "order_id";
    public static final String PREF_KEY_MOBILE = "mobile";
    public static final String PREF_KEY_EMAIL = "email";
    public static final String PREF_KEY_REDEEM_POINT = "redeem_point";
    public static final String PREF_KEY_GCM_REG_ID = "gcm_reg_id";
    public static final String PREF_KEY_USER_NAME = "user_name";
    public static final String PAYTM_MERCHANT_ID = "Fefood64366571835976";
    public static final String PAYTM_MERCHANT_kEY = "Fy0vAKV!X34mrouk";
    public static final String PAYTM_CUSTOMER_ID = "cust_";
    public static final String THEME = "merchant";
    public static final String WEBSITE = "Fewap";
    public static final String PAYTM_CHANNEL_ID = "WAP";
    public static final String INDUSTRY_TYPE_ID = "Retail114";

    //Notification Keys
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    //    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String SEND_REG_TO_SERVER = "send_reg_to_server";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
//    public static final String SHARED_PREF = "ah_firebase";

    public static enum URL {

        SIGNUP(BASE_URL + "appuser/appusers/"),
        LOGIN(BASE_URL + "appuser/mobile-login/"),
        VERIFY(BASE_URL + "appuser/mobile-verify/"),
        LOCATION(BASE_URL + "appuser/location/"),
        KITCHEN_AT_LOCATION(BASE_URL + "kitchen/location/"),
        MEAL_ITEM_ADDONS(BASE_URL + "kitchen/meal-item?"),
        USER_ADDRESS(BASE_URL + "appuser/useraddress/"),
        KITCHEN_TIMESLOT(BASE_URL + "kitchen/kitchenslots/?kitchen="),
        PLACE_ORDER(BASE_URL + "order/orders/"),
        DIETITIAN(BASE_URL + "dietitian/dietitians/"),
        KITCHEN_FILTERS(BASE_URL + "kitchen/tags/"),
        DIET_PLAN(BASE_URL + "dietplan/plans/"),
        DAILY_PLAN(BASE_URL + "subscription/plans/"),
        DAILY_PLAN_MENU(BASE_URL + "subscription/plan-menu/?"),
        SHOW_PLAN(BASE_URL + "dietplan/userdietplan/"),
        SHOW_DAILY_PLAN(BASE_URL + "subscription/usersubscription/"),
        GCM(BASE_URL + "fcmdevices/"),
        APP_SCREEN(BASE_URL + "applayout/screen/"),
        BMI("https://2-dot-feapiserver.appspot.com/firsteatapi/diet-form/");

        private final String name;

        private URL(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return name;
        }

    }

}
