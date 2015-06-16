/*
 *  Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco Activiti Mobile for Android.
 *
 * Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.activiti.android.platform.provider.appIcon;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public final class AppIconSchema
{

    private AppIconSchema()
    {
    }

    public static final String TABLENAME = "appIcon";

    public static final String COLUMN_ID = "_id";

    public static final int COLUMN_ID_ID = 0;

    public static final String COLUMN_ICON = "iconId";

    public static final int COLUMN_ICON_ID = COLUMN_ID_ID + 1;

    public static final String COLUMN_TEXT_VALUE = "textValue";

    public static final int COLUMN_TEXT_VALUE_ID = COLUMN_ICON_ID + 1;

    public static final String[] COLUMN_ALL = { COLUMN_ID, COLUMN_ICON, COLUMN_TEXT_VALUE };

    private static final String QUERY_TABLE_CREATE = "CREATE TABLE " + TABLENAME + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ICON + " TEXT NOT NULL," + COLUMN_TEXT_VALUE + " TEXT"
            + ");";

    public static void onCreate(Context context, SQLiteDatabase db)
    {
        create(db);
    }

    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    private static void create(SQLiteDatabase db)
    {
        db.execSQL(QUERY_TABLE_CREATE);
        insert(db, "glyphicon-asterisk", "2a");
        insert(db, "glyphicon-plus", "2b");
        insert(db, "glyphicon-euro", "20ac");
        insert(db, "glyphicon-minus", "2212");
        insert(db, "glyphicon-cloud", "2601");
        insert(db, "glyphicon-envelope", "2709");
        insert(db, "glyphicon-pencil", "270f");
        insert(db, "glyphicon-glass", "e001");
        insert(db, "glyphicon-music", "e002");
        insert(db, "glyphicon-search", "e003");
        insert(db, "glyphicon-heart", "e005");
        insert(db, "glyphicon-star", "e006");
        insert(db, "glyphicon-star-empty", "e007");
        insert(db, "glyphicon-user", "e008");
        insert(db, "glyphicon-film", "e009");
        insert(db, "glyphicon-th-large", "e010");
        insert(db, "glyphicon-th", "e011");
        insert(db, "glyphicon-th-list", "e012");
        insert(db, "glyphicon-ok", "e013");
        insert(db, "glyphicon-remove", "e014");
        insert(db, "glyphicon-zoom-in", "e015");
        insert(db, "glyphicon-zoom-out", "e016");
        insert(db, "glyphicon-off", "e017");
        insert(db, "glyphicon-signal", "e018");
        insert(db, "glyphicon-cog", "e019");
        insert(db, "glyphicon-trash", "e020");
        insert(db, "glyphicon-home", "e021");
        insert(db, "glyphicon-file", "e022");
        insert(db, "glyphicon-time", "e023");
        insert(db, "glyphicon-road", "e024");
        insert(db, "glyphicon-download-alt", "e025");
        insert(db, "glyphicon-download", "e026");
        insert(db, "glyphicon-upload", "e027");
        insert(db, "glyphicon-inbox", "e028");
        insert(db, "glyphicon-play-circle", "e029");
        insert(db, "glyphicon-repeat", "e030");
        insert(db, "glyphicon-refresh", "e031");
        insert(db, "glyphicon-list-alt", "e032");
        insert(db, "glyphicon-lock", "e033");
        insert(db, "glyphicon-flag", "e034");
        insert(db, "glyphicon-headphones", "e035");
        insert(db, "glyphicon-volume-off", "e036");
        insert(db, "glyphicon-volume-down", "e037");
        insert(db, "glyphicon-volume-up", "e038");
        insert(db, "glyphicon-qrcode", "e039");
        insert(db, "glyphicon-barcode", "e040");
        insert(db, "glyphicon-tag", "e041");
        insert(db, "glyphicon-tags", "e042");
        insert(db, "glyphicon-book", "e043");
        insert(db, "glyphicon-bookmark", "e044");
        insert(db, "glyphicon-print", "e045");
        insert(db, "glyphicon-camera", "e046");
        insert(db, "glyphicon-font", "e047");
        insert(db, "glyphicon-bold", "e048");
        insert(db, "glyphicon-italic", "e049");
        insert(db, "glyphicon-text-height", "e050");
        insert(db, "glyphicon-text-width", "e051");
        insert(db, "glyphicon-align-left", "e052");
        insert(db, "glyphicon-align-center", "e053");
        insert(db, "glyphicon-align-right", "e054");
        insert(db, "glyphicon-align-justify", "e055");
        insert(db, "glyphicon-list", "e056");
        insert(db, "glyphicon-indent-left", "e057");
        insert(db, "glyphicon-indent-right", "e058");
        insert(db, "glyphicon-facetime-video", "e059");
        insert(db, "glyphicon-picture", "e060");
        insert(db, "glyphicon-map-marker", "e062");
        insert(db, "glyphicon-adjust", "e063");
        insert(db, "glyphicon-tint", "e064");
        insert(db, "glyphicon-edit", "e065");
        insert(db, "glyphicon-share", "e066");
        insert(db, "glyphicon-check", "e067");
        insert(db, "glyphicon-move", "e068");
        insert(db, "glyphicon-step-backward", "e069");
        insert(db, "glyphicon-fast-backward", "e070");
        insert(db, "glyphicon-backward", "e071");
        insert(db, "glyphicon-play", "e072");
        insert(db, "glyphicon-pause", "e073");
        insert(db, "glyphicon-stop", "e074");
        insert(db, "glyphicon-forward", "e075");
        insert(db, "glyphicon-fast-forward", "e076");
        insert(db, "glyphicon-step-forward", "e077");
        insert(db, "glyphicon-eject", "e078");
        insert(db, "glyphicon-chevron-left", "e079");
        insert(db, "glyphicon-chevron-right", "e080");
        insert(db, "glyphicon-plus-sign", "e081");
        insert(db, "glyphicon-minus-sign", "e082");
        insert(db, "glyphicon-remove-sign", "e083");
        insert(db, "glyphicon-ok-sign", "e084");
        insert(db, "glyphicon-question-sign", "e085");
        insert(db, "glyphicon-info-sign", "e086");
        insert(db, "glyphicon-screenshot", "e087");
        insert(db, "glyphicon-remove-circle", "e088");
        insert(db, "glyphicon-ok-circle", "e089");
        insert(db, "glyphicon-ban-circle", "e090");
        insert(db, "glyphicon-arrow-left", "e091");
        insert(db, "glyphicon-arrow-right", "e092");
        insert(db, "glyphicon-arrow-up", "e093");
        insert(db, "glyphicon-arrow-down", "e094");
        insert(db, "glyphicon-share-alt", "e095");
        insert(db, "glyphicon-resize-full", "e096");
        insert(db, "glyphicon-resize-small", "e097");
        insert(db, "glyphicon-exclamation-sign", "e101");
        insert(db, "glyphicon-gift", "e102");
        insert(db, "glyphicon-leaf", "e103");
        insert(db, "glyphicon-fire", "e104");
        insert(db, "glyphicon-eye-open", "e105");
        insert(db, "glyphicon-eye-close", "e106");
        insert(db, "glyphicon-warning-sign", "e107");
        insert(db, "glyphicon-plane", "e108");
        insert(db, "glyphicon-calendar", "e109");
        insert(db, "glyphicon-random", "e110");
        insert(db, "glyphicon-comment", "e111");
        insert(db, "glyphicon-magnet", "e112");
        insert(db, "glyphicon-chevron-up", "e113");
        insert(db, "glyphicon-chevron-down", "e114");
        insert(db, "glyphicon-retweet", "e115");
        insert(db, "glyphicon-shopping-cart", "e116");
        insert(db, "glyphicon-folder-close", "e117");
        insert(db, "glyphicon-folder-open", "e118");
        insert(db, "glyphicon-resize-vertical", "e119");
        insert(db, "glyphicon-resize-horizontal", "e120");
        insert(db, "glyphicon-hdd", "e121");
        insert(db, "glyphicon-bullhorn", "e122");
        insert(db, "glyphicon-bell", "e123");
        insert(db, "glyphicon-certificate", "e124");
        insert(db, "glyphicon-thumbs-up", "e125");
        insert(db, "glyphicon-thumbs-down", "e126");
        insert(db, "glyphicon-hand-right", "e127");
        insert(db, "glyphicon-hand-left", "e128");
        insert(db, "glyphicon-hand-up", "e129");
        insert(db, "glyphicon-hand-down", "e130");
        insert(db, "glyphicon-circle-arrow-right", "e131");
        insert(db, "glyphicon-circle-arrow-left", "e132");
        insert(db, "glyphicon-circle-arrow-up", "e133");
        insert(db, "glyphicon-circle-arrow-down", "e134");
        insert(db, "glyphicon-globe", "e135");
        insert(db, "glyphicon-wrench", "e136");
        insert(db, "glyphicon-tasks", "e137");
        insert(db, "glyphicon-filter", "e138");
        insert(db, "glyphicon-briefcase", "e139");
        insert(db, "glyphicon-fullscreen", "e140");
        insert(db, "glyphicon-dashboard", "e141");
        insert(db, "glyphicon-paperclip", "e142");
        insert(db, "glyphicon-heart-empty", "e143");
        insert(db, "glyphicon-link", "e144");
        insert(db, "glyphicon-phone", "e145");
        insert(db, "glyphicon-pushpin", "e146");
        insert(db, "glyphicon-usd", "e148");
        insert(db, "glyphicon-gbp", "e149");
        insert(db, "glyphicon-sort", "e150");
        insert(db, "glyphicon-sort-by-alphabet", "e151");
        insert(db, "glyphicon-sort-by-alphabet-alt", "e152");
        insert(db, "glyphicon-sort-by-order", "e153");
        insert(db, "glyphicon-sort-by-order-alt", "e154");
        insert(db, "glyphicon-sort-by-attributes", "e155");
        insert(db, "glyphicon-sort-by-attributes-alt", "e156");
        insert(db, "glyphicon-unchecked", "e157");
        insert(db, "glyphicon-expand", "e158");
        insert(db, "glyphicon-collapse-down", "e159");
        insert(db, "glyphicon-collapse-up", "e160");
        insert(db, "glyphicon-log-in", "e161");
        insert(db, "glyphicon-flash", "e162");
        insert(db, "glyphicon-log-out", "e163");
        insert(db, "glyphicon-new-window", "e164");
        insert(db, "glyphicon-record", "e165");
        insert(db, "glyphicon-save", "e166");
        insert(db, "glyphicon-open", "e167");
        insert(db, "glyphicon-saved", "e168");
        insert(db, "glyphicon-import", "e169");
        insert(db, "glyphicon-export", "e170");
        insert(db, "glyphicon-send", "e171");
        insert(db, "glyphicon-floppy-disk", "e172");
        insert(db, "glyphicon-floppy-saved", "e173");
        insert(db, "glyphicon-floppy-remove", "e174");
        insert(db, "glyphicon-floppy-save", "e175");
        insert(db, "glyphicon-floppy-open", "e176");
        insert(db, "glyphicon-credit-card", "e177");
        insert(db, "glyphicon-transfer", "e178");
        insert(db, "glyphicon-cutlery", "e179");
        insert(db, "glyphicon-header", "e180");
        insert(db, "glyphicon-compressed", "e181");
        insert(db, "glyphicon-earphone", "e182");
        insert(db, "glyphicon-phone-alt", "e183");
        insert(db, "glyphicon-tower", "e184");
        insert(db, "glyphicon-stats", "e185");
        insert(db, "glyphicon-sd-video", "e186");
        insert(db, "glyphicon-hd-video", "e187");
        insert(db, "glyphicon-subtitles", "e188");
        insert(db, "glyphicon-sound-stereo", "e189");
        insert(db, "glyphicon-sound-dolby", "e190");
        insert(db, "glyphicon-sound-5-1", "e191");
        insert(db, "glyphicon-sound-6-1", "e192");
        insert(db, "glyphicon-sound-7-1", "e193");
        insert(db, "glyphicon-copyright-mark", "e194");
        insert(db, "glyphicon-registration-mark", "e195");
        insert(db, "glyphicon-cloud-download", "e197");
        insert(db, "glyphicon-cloud-upload", "e198");
        insert(db, "glyphicon-tree-conifer", "e199");
        insert(db, "glyphicon-tree-deciduous", "e200");

    }

    public static long insert(SQLiteDatabase db, String iconId, String textValue)
    {
        ContentValues insertValues = new ContentValues();
        insertValues.put(COLUMN_ICON, iconId);
        insertValues.put(COLUMN_TEXT_VALUE, textValue);

        return db.insert(AppIconSchema.TABLENAME, null, insertValues);
    }

    // ////////////////////////////////////////////////////
    // DEBUG
    // ////////////////////////////////////////////////////
    private static final String QUERY_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLENAME;

    /** Use with Caution ! */
    public static void reset(SQLiteDatabase db)
    {
        db.execSQL(QUERY_TABLE_DROP);
        create(db);
    }

}
