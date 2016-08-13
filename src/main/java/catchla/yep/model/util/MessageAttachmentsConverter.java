package catchla.yep.model.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.mariotaku.library.objectcursor.converter.CursorFieldConverter;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.model.AppleMediaAttachment;
import catchla.yep.model.Attachment;
import catchla.yep.model.DribbbleAttachment;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.GithubAttachment;
import catchla.yep.model.LocationAttachment;
import catchla.yep.model.WebPageAttachment;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/11/26.
 */
public class MessageAttachmentsConverter implements CursorFieldConverter<List<Attachment>>, Constants {

    @Override
    public List<Attachment> parseField(final Cursor cursor, final int columnIndex, final ParameterizedType fieldType) {
        final String json = cursor.getString(columnIndex);
        final String mediaType = cursor.getString(cursor.getColumnIndex(Messages.MEDIA_TYPE));
        if (TextUtils.isEmpty(json) || mediaType == null) return null;
        try {
            //noinspection unchecked
            final JsonMapper<Attachment> mapper = (JsonMapper<Attachment>) getMapperForKind(mediaType);
            if (mapper == null) return null;
            return mapper.parseList(json);
        } catch (IOException e) {
            if (e instanceof JsonProcessingException && BuildConfig.DEBUG) {
                Log.w(LOGTAG, e);
            }
            return null;
        }
    }


    @Override
    public void writeField(final ContentValues values, final List<Attachment> object,
                           final String columnName, final ParameterizedType fieldType) {
        final String mediaType = values.getAsString(Messages.MEDIA_TYPE);
        if (mediaType == null) return;
        final StringWriter sw = new StringWriter();
        try {
            //noinspection unchecked
            final JsonMapper<Attachment> mapper = (JsonMapper<Attachment>) getMapperForKind(mediaType);
            if (mapper == null) return;
            values.put(columnName, mapper.serialize(object));
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.w(LOGTAG, e);
            }
            // Ignore
        } finally {
            Utils.Companion.closeSilently(sw);
        }
    }

    public static JsonMapper<? extends Attachment> getMapperForKind(final String kind) {
        switch (kind) {
            case "dribbble":
                return LoganSquare.mapperFor(DribbbleAttachment.class);
            case "github":
                return LoganSquare.mapperFor(GithubAttachment.class);
            case "location":
                return LoganSquare.mapperFor(LocationAttachment.class);
            case "apple_music":
            case "apple_movie":
            case "apple_ebook":
                return LoganSquare.mapperFor(AppleMediaAttachment.class);
            case "image":
            case "video":
            case "audio":
                return LoganSquare.mapperFor(FileAttachment.class);
            case "web_page":
                return LoganSquare.mapperFor(WebPageAttachment.class);
            default:
                return LoganSquare.mapperFor(Attachment.class);
        }
    }
}
