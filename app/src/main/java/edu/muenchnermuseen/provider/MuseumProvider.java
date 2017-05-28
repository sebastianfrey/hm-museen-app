package edu.muenchnermuseen.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.util.HashMap;
import java.util.Map;

import edu.muenchnermuseen.db.DataBaseHelper;
import edu.muenchnermuseen.db.dao.CategoryDAO;
import edu.muenchnermuseen.db.dao.MuseumDAO;

/**
 * Created by Sebastian on 28.05.2017.
 */

public class MuseumProvider extends ContentProvider {

  private static final String AUTHORITY = "edu.muenchnermuseen.provider.MuseumProvider";

  private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

  private static final int MUSEUMS = 1;

  static {
    URI_MATCHER.addURI(AUTHORITY , "search_suggest_query", MUSEUMS);
  }


  private DataBaseHelper dataBaseHelper;



  @Nullable
  @Override
  public String getType(@NonNull Uri uri)
  {
    switch (URI_MATCHER.match(uri))
    {
      case MUSEUMS:
        return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".museums";
      default:
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }
  }

  @Override
  public boolean onCreate()
  {
    dataBaseHelper = new DataBaseHelper(getContext());

    return true;
  }


  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                      @Nullable String[] selectionArgs, @Nullable String sortOrder) {

    SQLiteDatabase db = dataBaseHelper.getInstance();

    switch (URI_MATCHER.match(uri)) {
      case MUSEUMS:

        StringBuilder sqlb = new StringBuilder();

        sqlb.append("SELECT ")
            .append("m.ID AS _id, ")
            .append("m.NAME AS ").append(SearchManager.SUGGEST_COLUMN_TEXT_1).append(", ")
            .append("c.ICON_RES AS ").append(SearchManager.SUGGEST_COLUMN_ICON_1).append(" ")
            .append("FROM ").append(MuseumDAO.MUSEUMS_TABLE).append(" AS m ")
            .append("JOIN ").append(CategoryDAO.CATEGORIES_TABLE).append(" AS c ")
            .append("ON (m.CATEGORY = c.ID) ")
            .append("WHERE ").append(SearchManager.SUGGEST_COLUMN_TEXT_1).append(" LIKE ?");

        String sql = sqlb.toString();

        Log.d("MuseumProvider", sql);

        if (selectionArgs.length > 0 && !selectionArgs[0].equals(""))
        {
          selectionArgs[0] = "%" + selectionArgs[0] + "%";
        }

        db.rawQuery(sql, selectionArgs);

        return db.rawQuery(sql, selectionArgs);
      default:
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }
  }


  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    return null;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }
}
