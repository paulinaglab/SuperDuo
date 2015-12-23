package it.jaschke.alexandria.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.jaschke.alexandria.Book;

/**
 * Created by Paulina on 2015-10-29.
 */
public class BookJsonParser {

    private static final String ITEMS = "items";

    private static final String VOLUME_INFO = "volumeInfo";

    private static final String INDUSTRY_IDENTIFIERS = "industryIdentifiers";
    private static final String TYPE = "type";
    private static final String ISBN_13_ID = "ISBN_13";
    private static final String IDENTIFIER = "identifier";

    private static final String TITLE = "title";
    private static final String SUBTITLE = "subtitle";
    private static final String AUTHORS = "authors";
    private static final String DESCRIPTION = "description";
    private static final String CATEGORIES = "categories";
    private static final String IMG_LINKS = "imageLinks";
    private static final String THUMBNAIL = "thumbnail";

    public static Book getBookFromJson(String bookJsonStr)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(bookJsonStr);

        Book book = new Book();

        JSONObject volumeInfoObject =
                jsonObject.getJSONArray(ITEMS).getJSONObject(0).getJSONObject(VOLUME_INFO);

        JSONArray industryIdArray = volumeInfoObject.getJSONArray(INDUSTRY_IDENTIFIERS);
        for (int i = 0; i < industryIdArray.length(); i++) {
            JSONObject identifierObject = industryIdArray.getJSONObject(i);
            if (identifierObject.getString(TYPE).equals(ISBN_13_ID)) {
                book.ean = identifierObject.getLong(IDENTIFIER);
                break;
            }
        }
        book.title = volumeInfoObject.getString(TITLE);
        book.subtitle = volumeInfoObject.optString(SUBTITLE, "");
        book.description = volumeInfoObject.optString(DESCRIPTION, "");
        JSONObject imageLinksObject = volumeInfoObject.optJSONObject(IMG_LINKS);
        if (imageLinksObject != null)
            book.imageUrl = imageLinksObject.getString(THUMBNAIL);

        JSONArray categoryArray = volumeInfoObject.optJSONArray(CATEGORIES);
        if (categoryArray != null) {
            for (int i = 0; i < categoryArray.length(); i++) {
                book.categories.add(categoryArray.getString(i));
            }
        }

        JSONArray authorArray = volumeInfoObject.optJSONArray(AUTHORS);
        if (authorArray != null) {
            for (int i = 0; i < authorArray.length(); i++) {
                book.authors.add(authorArray.getString(i));
            }
        }

        return book;
    }


}
