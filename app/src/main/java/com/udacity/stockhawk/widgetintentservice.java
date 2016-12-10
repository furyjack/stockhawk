package com.udacity.stockhawk;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;

import static com.udacity.stockhawk.data.Contract.Quote.COLUMN_PERCENTAGE_CHANGE;
import static com.udacity.stockhawk.data.Contract.Quote.COLUMN_PRICE;
import static com.udacity.stockhawk.data.Contract.Quote.COLUMN_SYMBOL;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class widgetintentservice extends RemoteViewsService {

    public static final String[] QUOTE_COLUMNS = {
            Contract.Quote._ID,
            COLUMN_SYMBOL,
            COLUMN_PRICE,
            COLUMN_PERCENTAGE_CHANGE,

    };


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.uri,
                        QUOTE_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.list_item_quote);
                String symbol = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
                String price = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE));
                String perchange = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, symbol);
                }
                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra("symbol",symbol);
                views.setOnClickFillInIntent(R.id.list_item,fillInIntent);
                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, price);
                views.setTextViewText(R.id.change, perchange);



                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.symbol, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(Contract.Quote._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }



}
