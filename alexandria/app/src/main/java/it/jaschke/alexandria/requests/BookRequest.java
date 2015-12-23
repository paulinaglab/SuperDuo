package it.jaschke.alexandria.requests;

import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import it.jaschke.alexandria.Book;
import it.jaschke.alexandria.utils.BookJsonParser;

/**
 * Created by Paulina on 2015-10-29.
 */
public class BookRequest extends JsonRequest<Book> {

    public BookRequest(String eanStr, Response.Listener<Book> listener, Response.ErrorListener errorListener) {
        super(Method.GET, buildUrl(eanStr), null, listener, errorListener);
    }

    private static String buildUrl(String eanStr) {
        final String BOOKS_API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
        final String QUERY_PARAM = "q";

        final String ISBN_PARAM = "isbn:" + eanStr;

        return Uri.parse(BOOKS_API_BASE_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAM, ISBN_PARAM)
                .build().toString();
    }

    @Override
    protected Response<Book> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            Book book = BookJsonParser.getBookFromJson(jsonString);
            if (book.ean > 0)
                return Response.success(book,
                        HttpHeaderParser.parseCacheHeaders(response));
            else
                return Response.error(new ParseError());
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
