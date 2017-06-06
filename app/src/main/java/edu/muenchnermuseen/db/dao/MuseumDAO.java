package edu.muenchnermuseen.db.dao;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.muenchnermuseen.db.DataBaseHelper;
import edu.muenchnermuseen.db.DbException;
import edu.muenchnermuseen.entities.Museum;

/**
 * Created by sfrey on 05.05.2017.
 */

public class MuseumDAO {

    public static String MUSEUMS_TABLE = "MUSEUM_REGISTRY";

    public final static String ID_COLUMN = "ID";
    public final static String NAME_COLUMN = "NAME";
    public final static String DESCRIPTION_COLUMN = "DESCRIPTION";
    public final static String LAT_COLUMN = "LAT";
    public final static String LON_COLUMN = "LON";
    public final static String CATEGORY_COLUMN = "CATEGORY";

    private static String[] MUSEUMS_COLUMNS = new String[] {
            ID_COLUMN,
            NAME_COLUMN,
            DESCRIPTION_COLUMN,
            LAT_COLUMN,
            LON_COLUMN,
            CATEGORY_COLUMN
    };

    private DataBaseHelper db;

    public MuseumDAO(DataBaseHelper db) {
        this.db = db;
    }

    public List<Museum> getMuseums()
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


    public List<Museum> getMuseumsLike(String name)
    {
        CategoryDAO categoryDAO = new CategoryDAO(this.db);

        List<Museum> museums = new ArrayList<>();

        try {
            String[] searchArgs = new String[] { "%" + name + "%" };

            Cursor cursor = db.query(MUSEUMS_TABLE, MUSEUMS_COLUMNS, "NAME LIKE ?", searchArgs);

            while (cursor.moveToNext())
            {
                museums.add(rowToCategory(cursor, categoryDAO));
            }
        }
        catch (Exception e)
        {
            museums = new ArrayList<>();
        }

        return museums;
    }


    public Museum getMuseumById(Integer id)
    {
        CategoryDAO categoryDAO = new CategoryDAO(this.db);

        Museum museum = new Museum();

        try {
            String[] searchArgs = new String[] { id.toString() };

            Cursor cursor = db.query(MUSEUMS_TABLE, MUSEUMS_COLUMNS, "ID LIKE ?", searchArgs);

            while (cursor.moveToNext())
            {
                museum = rowToCategory(cursor, categoryDAO);
            }
        }
        catch (DbException e)
        {
            Log.e("MuseumDAO", e.getMessage(), e);
        }

        return museum;
    }

    public List<Museum> getMuseumsByCategory(Integer category)
    {
        CategoryDAO categoryDAO = new CategoryDAO(this.db);

        List<Museum> museums = new ArrayList();

        if (category == null)
        {
            return museums;
        }

        try {
            String[] searchArgs = new String[] { category.toString() };

            Cursor cursor = db.query(MUSEUMS_TABLE, MUSEUMS_COLUMNS, "CATEGORY LIKE ?", searchArgs);

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
                    museum.setLatitude(cursor.getDouble(idx));
                    break;

                case LON_COLUMN:
                    museum.setLongitude(cursor.getDouble(idx));
                    break;

                case CATEGORY_COLUMN:
                    museum.setCategory(categoryDAO.getCategory(cursor.getInt(idx)));
                    break;
            }
        }

        return museum;    }
}
