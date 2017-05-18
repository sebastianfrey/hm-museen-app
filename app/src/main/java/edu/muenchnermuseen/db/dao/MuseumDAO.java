package edu.muenchnermuseen.db.dao;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import edu.muenchnermuseen.db.DataBaseHelper;
import edu.muenchnermuseen.db.DbException;
import edu.muenchnermuseen.db.entities.Category;
import edu.muenchnermuseen.db.entities.Museum;

/**
 * Created by sfrey on 05.05.2017.
 */

public class MuseumDAO {

    private static String MUSEUMS_TABLE = "museen";

    private final static String ID_COLUMN = "ID";
    private final static String NAME_COLUMN = "NAME";
    private final static String DESCRIPTION_COLUMN = "DESCRIPTION";
    private final static String LAT_COLUMN = "LAT";
    private final static String LON_COLUMN = "LON";
    private final static String CATEGORY_COLUMN = "CATEGORY";

    private static String[] MUSEUMS_COLUMNS = new String[] {
            ID_COLUMN,
            NAME_COLUMN,
            DESCRIPTION_COLUMN,
            LAT_COLUMN,
            LON_COLUMN,
            CATEGORY_COLUMN
    };

    private DataBaseHelper db;

    private MuseumDAO(DataBaseHelper db) {
        this.db = db;
    }

    public List<Museum> getMuseums() throws DbException
    {
        CategoryDAO categoryDAO = new CategoryDAO(this.db);

        List<Museum> museums = new ArrayList<>();

        try {
            Cursor cursor = db.query(MUSEUMS_TABLE, MUSEUMS_COLUMNS);

            while (cursor.moveToNext())
            {
                museums.add(rowToCategory(cursor, categoryDAO));
            }
        }
        catch (DbException e)
        {
            museums = new ArrayList<>();
        }

        return museums;
    }


    public List<Museum> getMuseums(String name) throws DbException
    {
        CategoryDAO categoryDAO = new CategoryDAO(this.db);

        List<Museum> museums = new ArrayList<>();

        try {
            String[] searchArgs = new String[] { name };

            Cursor cursor = db.query(MUSEUMS_TABLE, MUSEUMS_COLUMNS, "NAME LIKE ?", searchArgs);

            while (cursor.moveToNext())
            {
                museums.add(rowToCategory(cursor, categoryDAO));
            }
        }
        catch (DbException e)
        {
            museums = new ArrayList<>();
        }

        return museums;
    }


    private Cursor getMuseumById(Integer id) throws DbException
    {
        if (id == null)
        {
            throw new DbException("Can not query museum with id = null");
        }

        String[] searchArgs = new String[] { id.toString() };

        return db.query(MUSEUMS_TABLE, MUSEUMS_COLUMNS, "ID = ?", searchArgs);
    }

    private Cursor getMuseumsByCategory(Integer category) throws DbException
    {
        if (category == null)
        {
            throw new DbException("Can not query museums by category with category == null");
        }

        String[] searchArgs = new String[] { category.toString() };

        return db.query(MUSEUMS_TABLE, MUSEUMS_COLUMNS, "CATEGORY = ?", searchArgs);
    }


    private Museum rowToCategory(Cursor cursor, CategoryDAO categoryDAO)
    {
        Museum museum = new Museum();

        for (String column : cursor.getColumnNames())
        {
            int idx = cursor.getColumnIndex(column);

            switch (column)
            {
                case ID_COLUMN:
                    museum.setId(cursor.getInt(idx));
                    break;

                case NAME_COLUMN:
                    museum.setName(cursor.getString(idx));
                    break;

                case DESCRIPTION_COLUMN:
                    museum.setDescription(cursor.getString(idx));
                    break;

                case LAT_COLUMN:
                    museum.setLat(cursor.getDouble(idx));
                    break;

                case LON_COLUMN:
                    museum.setLon(cursor.getDouble(idx));
                    break;

                case CATEGORY_COLUMN:
                    museum.setCategory(categoryDAO.getCategory(cursor.getInt(idx)));
                    break;
            }
        }

        return museum;    }
}
