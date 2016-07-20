package com.example.jonny.projectapp;

/**
 * Created by Jonny on 19/07/2016.
 */
public class ConfigCRUD {
    //Address of our scripts of the CRUD
    public static final String URL_ADD="http://ec2-52-91-226-96.compute-1.amazonaws.com/NutritionUpdate.php";
    public static final String URL_GET_ALL = "http://192.168.94.1/Android/CRUD/getAllEmp.php";
    public static final String URL_GET_EMP = "http://192.168.94.1/Android/CRUD/getEmp.php?id=";
    public static final String URL_UPDATE_EMP = "http://192.168.94.1/Android/CRUD/updateEmp.php";
    public static final String URL_DELETE_EMP = "http://192.168.94.1/Android/CRUD/deleteEmp.php?id=";

    //Keys that will be used to send the request to php scripts
    public static final String KEY_EMP_ID = "id";
    public static final String KEY_EMP_NAME = "name";
    public static final String KEY_EMP_DESG = "desg";
    public static final String KEY_EMP_SAL = "salary";

    //JSON Tags
    public static final String TAG_JSON_ARRAY="result";
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_DESG = "desg";
    public static final String TAG_SAL = "salary";

    //employee id to pass with intent
    public static final String EMP_ID = "emp_id";
}
