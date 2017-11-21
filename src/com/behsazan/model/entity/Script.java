package com.behsazan.model.entity;

import com.behsazan.model.sqlite.ScriptDb;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by admin on 09/05/2017.
 */
public class Script {
    private int id;
    private String name;
    private String text;
    private int type;
    private int lang;

    public static final int LANG_JS = 1;
    public static final int LANG_PYTHON = 2;
    public static final int LANG_RUBY = 3;

    public static final int TYPE_ON_TEST_START = 1;
    public static final int TYPE_ON_TEST_FINISH = 2;
    public static final int TYPE_ON_REQUEST_BEFORE_ASSIGNMENT = 3;
    public static final int TYPE_ON_REQUEST_AFTER_ASSIGNMENT = 4;
    public static final int TYPE_ON_RESPONSE_RECEIVED = 5;
    public static final int TYPE_ON_SEQUENCE_START = 6;
    public static final int TYPE_ON_SEQUENCE_FINISH = 7;

    private static final HashMap<Integer, String> TYPES_STRING;

    private static final HashMap<Integer, String> LANGS_STRING ;

    static {
        TYPES_STRING = new HashMap<>();
        TYPES_STRING.put(TYPE_ON_TEST_START,"ON_TEST_START");
        TYPES_STRING.put(TYPE_ON_TEST_FINISH,"ON_TEST_FINISH");
        TYPES_STRING.put(TYPE_ON_REQUEST_BEFORE_ASSIGNMENT,"ON_REQUEST_BEFORE_ASSIGNMENT");
        TYPES_STRING.put(TYPE_ON_REQUEST_AFTER_ASSIGNMENT,"ON_REQUEST_AFTER_ASSIGNMENT");
        TYPES_STRING.put(TYPE_ON_RESPONSE_RECEIVED,"ON_RESPONSE_RECEIVED");

        LANGS_STRING = new HashMap<>();
        LANGS_STRING.put(LANG_JS,"JavaScript");
        LANGS_STRING.put(LANG_PYTHON,"Python");
        LANGS_STRING.put(LANG_RUBY,"Ruby");

    }

    public static HashMap<Integer, String> getTypesString() {
        return TYPES_STRING;
    }

    public static HashMap<Integer, String> getLangsString() {
        return LANGS_STRING;
    }

    public Script() {
    }

    public Script(int id, String name, String text, int type, int lang) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.type = type;
        this.lang = lang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public static boolean isNameUsed(String name) {
        try {
            return new ScriptDb().isScriptNameUsed(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static List<Script> getAll() {
        try {
            return new ScriptDb().getAllScripts();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clone(int id, String response) throws SQLException {
        new ScriptDb().cloneScript(id,response);
    }

    public static boolean isPossibleToDelete(int id) {
        try {
            return new ScriptDb().isPossibleToDeleteScript(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void delete(int id) throws SQLException {
        new ScriptDb().deleteScript(id);
    }

    public static void update(Script script) throws SQLException {
        new ScriptDb().updateScript(script);
    }

    public static Vector<Vector<Object>> getAll_Table() {
        try {
            return new ScriptDb().getAllScripts_Table();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insert(Script script) throws SQLException {
        new ScriptDb().insertScript(script);
    }

    public static Script getById(int id) {
        try {
            return new ScriptDb().getScriptById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
