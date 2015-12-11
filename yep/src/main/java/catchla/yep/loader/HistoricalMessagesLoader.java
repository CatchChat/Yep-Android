package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.Message;
import catchla.yep.model.PagedMessages;
import catchla.yep.model.Paging;
import catchla.yep.model.YepException;
import catchla.yep.util.YepAPI;

/**
 * Created by mariotaku on 15/5/27.
 */
public class HistoricalMessagesLoader extends CachedYepListLoader<Message> implements Constants {

    private final Paging mPaging;
    @YepAPI.PathRecipientType
    private final String mRecipientType;
    private final String mRecipientId;

    public HistoricalMessagesLoader(@NonNull Context context, @NonNull Account account,
                                    @YepAPI.PathRecipientType final String recipientType,
                                    final String recipientId, @Nullable Paging paging,
                                    boolean readCache, boolean writeCache, @Nullable List<Message> oldData) {
        super(context, account, Message.class, oldData, readCache, writeCache);
        mRecipientType = recipientType;
        mRecipientId = recipientId;
        mPaging = paging;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "historical_messages_cache_" + mRecipientType + "_" + mRecipientId + "_" + getAccount().name;
    }

    @Override
    protected List<Message> requestData(final YepAPI yep, List<Message> oldData) throws YepException {
        final List<Message> list = new ArrayList<>();
        if (oldData != null) {
            list.addAll(oldData);
        }
        final PagedMessages topics = yep.getHistoricalMessages(mRecipientType, mRecipientId, mPaging);
        for (Message topic : topics) {
            list.remove(topic);
            list.add(topic);
        }
        return list;
    }


}
