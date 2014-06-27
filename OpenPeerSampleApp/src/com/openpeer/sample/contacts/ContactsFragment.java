package com.openpeer.sample.contacts;

import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.openpeer.app.OPDataManager;
import com.openpeer.datastore.DatabaseContracts;
import com.openpeer.datastore.DatabaseContracts.ContactsViewEntry;
import com.openpeer.javaapi.OPIdentityContact;
import com.openpeer.javaapi.OPRolodexContact;
import com.openpeer.sample.BaseFragment;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import static com.openpeer.datastore.DatabaseContracts.ContactsViewEntry.*;

import com.openpeer.sample.R;

public class ContactsFragment extends BaseFragment implements
		SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

	private SwipeRefreshLayout mRootLayout;
	private ListView mListView;
	private ContactsAdapter mAdapter;
	private boolean mTest;
	private DataChangeReceiver mReceiver;

	public static android.support.v4.app.Fragment newInstance() {
		return new ContactsFragment();
	}

	public static ContactsFragment newTestInstance() {
		ContactsFragment fragment = new ContactsFragment();
		fragment.mTest = true;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mReceiver = new DataChangeReceiver();
		return setupView(inflater.inflate(R.layout.fragment_contacts, null));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		getLoaderManager().initLoader(URL_LOADER, null, this);

	}

	private View setupView(View view) {
		mListView = (ListView) view.findViewById(R.id.listview);
		View emptyView = view.findViewById(R.id.empty_view);
		mListView.setEmptyView(emptyView);
		mRootLayout = (SwipeRefreshLayout) view;
		mRootLayout.setOnRefreshListener(this);
		mAdapter = new ContactsAdapter(getActivity(), null);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((ContactItemView) view).onClick();
			}
		});
		if (mTest) {
			fillTestView();
		} else {
			setupContent();
		}
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// this.setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_contacts, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActivity().registerReceiver(mReceiver,
				new IntentFilter(OPDataManager.INTENT_CONTACTS_CHANGED));

	}

	static class ContactsAdapter extends CursorAdapter {

		public ContactsAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((ContactItemView) view).updateData(cursor);
		}

		@Override
		public View newView(Context view, Cursor cursor, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return new ContactItemView(arg2.getContext());
		}

	}

	static class ContactsAdapter1 extends BaseAdapter {
		private List<OPRolodexContact> mContacts;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mContacts == null ? 0 : mContacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = new ContactItemView(arg2.getContext());
			}
			((ContactItemView) arg1)
					.updateData((OPRolodexContact) getItem(arg0));
			return arg1;
		}

	}

	@Override
	public void onRefresh() {
		OPDataManager.getInstance().refreshContacts();
		mRootLayout.setRefreshing(false);
	}

	void setupContent() {
		// mAdapter.mContacts = OPDataManager.getDatastoreDelegate()
		// .getContacts(0);
		//
		// mAdapter.notifyDataSetChanged();
	}

	// fill in test data to the view
	void fillTestView() {
		// ProfileURL Name David Gotwo identityUrl
		// identity://facebook.com/100003823387069 IdentityProvider facebook.com
		// Disposition Disposition_Update,
		// com.openpeer.javaapi.OPRolodexContact@4266cc90 ProfileURL Name David
		// Gofour identityUrl identity://facebook.com/100003952283621
		// IdentityProvider facebook.com Disposition Disposition_Update,
		// com.openpeer.javaapi.OPRolodexContact@4266d098 ProfileURL Name Cindy
		// Love identityUrl
		// mAdapter.mContacts = Arrays
		// .asList(new OPRolodexContact[] {
		// new OPRolodexContact(
		// "identity://facebook.com/100003823387069",
		// "facebook.com", "David Gotwo", null, null,
		// null, 0),
		// new OPRolodexContact(
		// "identity://facebook.com/100003952283621",
		// "facebook.com", "David Gofour", null, null,
		// null, 0),
		// new OPIdentityContact(new OPRolodexContact(
		// "identity://facebook.com/100003952283621",
		// "facebook.com", "David Gofour", null, null,
		// null, 0)) });
		// mAdapter.notifyDataSetChanged();
	}

	class DataChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.d("test", "received broadcast " + arg1.getAction());
			setupContent();
		}
	}

	// Begin: CursorCallback implementation
	private static final int URL_LOADER = 0;
	static final String LIST_PROJECTION[] = { BaseColumns._ID, COLUMN_NAME_CONTACT_NAME, COLUMN_NAME_AVATAR_URL,
			ContactsViewEntry.COLUMN_NAME_STABLE_ID };

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) {
		switch (loaderID) {
		case URL_LOADER:
			// Returns a new CursorLoader
			return new CursorLoader(
					getActivity(), // Parent activity context
					DatabaseContracts.ContactsViewEntry.CONTENT_URI, // Table to
																		// query
					LIST_PROJECTION, // Projection to return
					null, // No selection clause
					null, // No selection arguments
					null // Default sort order
			);
		default:
			// An invalid id was passed in
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.changeCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.changeCursor(null);

	}
	// End: CursorCallback implementation

}