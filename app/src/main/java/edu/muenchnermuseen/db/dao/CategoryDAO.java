package edu.muenchnermuseen.db.dao;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.muenchnermuseen.db.DataBaseHelper;
import edu.muenchnermuseen.db.DbException;
import edu.muenchnermuseen.db.entities.Category;
import edu.muenchnermuseen.db.entities.Museum;

/**
 * Created by sfrey on 05.05.2017.
 */

public class CategoryDAO {

  private static String CATEGORIES_TABLE = "CATEGORY_REGISTRY";

  private final static String ID_COLUMN = "ID";
  private final static String NAME_COLUMN = "NAME";

  private static String[] CATEGORIES_COLUMNS = new String[]{
      ID_COLUMN,
      NAME_COLUMN
  };

  private DataBaseHelper db;

  public CategoryDAO(DataBaseHelper db) {
    this.db = db;
  }

  private List<Category> categories;


  /**
   * Tries to fetch the museum categories from db. If no Categories found or an DB-Error
   * occurs an empty list is returned.
   *
   * @return The museum categories.
   */
  public List<Category> getCategories() {
    if (this.categories != null) {
      return this.categories;
    }

    List<Category> categories = new ArrayList<>();

    try {
      Cursor cursor = db.query(CATEGORIES_TABLE, CATEGORIES_COLUMNS);

      while (cursor.moveToNext()) {
        categories.add(rowToCategory(cursor));
      }
    } catch (Exception e) {
      categories = new ArrayList<>();
      Log.e("CategoryDAO", e.getMessage(), e);
    }

    return categories;
  }


  /**
   * Tries to fetch a category by its id from the db. If no category ist found a default
   * POJO is returned.
   *
   * @param id The categories id.
   * @return The category.
   */
  public Category getCategory(Integer id) {
    if (id == null) {
      return new Category();
    } else if (categories != null) {
      for (Category category : categories) {
        if (category.getId() == id) {
          return category;
        }
      }
    }

    try {
      String[] whereArgs = new String[]{id.toString()};

      Cursor cursor = db.query(CATEGORIES_TABLE, CATEGORIES_COLUMNS, "ID = ?", whereArgs);

      while (cursor.moveToNext()) {
        return rowToCategory(cursor);
      }
    } catch (Exception e) {
      Log.e("CategoryDAO", e.getMessage(), e);
    }

    return new Category();
  }

  /**
   * Transforms the data available at the current cursor position to an Category-POJO.
   *
   * @param cursor The db cursor.
   * @return The created Category-POJO.
   */
  private Category rowToCategory(Cursor cursor) {
    Category category = new Category();

    for (String column : cursor.getColumnNames()) {
      int idx = cursor.getColumnIndex(column);

      switch (column) {
        case ID_COLUMN:
          category.setId(cursor.getInt(idx));
          break;

        case NAME_COLUMN:
          category.setName(cursor.getString(idx));
          break;
      }
    }

    return category;
  }
}
