/**
 * 优分享VR
 * copy right: youkes.com
 * author:xuming
 * licence:GPL2
 */
package com.youkes.vr.video.channels;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youkes.vr.MainApp;
import com.youkes.vr.R;


import com.youkes.vr.http.OnTaskCompleted;
import com.youkes.vr.ui.BaseFragment;
import com.youkes.vr.utils.DialogUtil;
import com.youkes.vr.utils.StringUtils;
import com.youkes.vr.widget.swipelistview.SwipeListView;

import java.util.ArrayList;

public class VideoChannelsListFragment extends BaseFragment implements
		SwipeRefreshLayout.OnRefreshListener {

	protected RelativeLayout inputBar;

	protected SwipeRefreshLayout swipeLayout;
	protected SwipeListView mListView;
	protected ProgressBar mProgressBar;
	protected VideoChannelsListAdapter listAdapter = new VideoChannelsListAdapter();


	private boolean isRefresh = false;


	int resId = R.layout.activity_circle;
	private String userId;
	private String userName;
	private String tagName;

	public VideoChannelsListFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	Dialog deleteDlg = null;
	TextView emptyView=null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		listAdapter.setUserId(userId);
		View v = inflater.inflate(this.resId, container, false);
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
		mListView = (SwipeListView) v.findViewById(R.id.listview);
		mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		inputBar = (RelativeLayout) v.findViewById(R.id.inputBar);
		emptyView = (TextView)v.findViewById(R.id.empty_tv);
		emptyView.setVisibility(View.GONE);

		swipeLayout.setOnRefreshListener(this);
		initDocDeleteDlg();

		listAdapter.setListItemActionListener(defListener);
		mListView.setAdapter(listAdapter);

		if (currentPage == 0) {
			loadData(currentPage++);
		} else {
			mProgressBar.setVisibility(View.GONE);
			mListView.onBottomComplete();
		}

		mListView.setOnBottomListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!hasMore) {
					return;
				}
				if (dataLoading) {
					return;
				}

				loadData(currentPage++);

			}
		});

		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				hideAllInputControls();
				return false;
			}
		});

		initLoadingDlg();
		return v;
	}

	int currentPage = 0;
	//Dialog loadingDlg = null;

	private void initLoadingDlg() {

	}

	public void setListItemListener(VideoChannelsListItemView.ListItemListener listItemListener) {
		this.listItemActionListener = listItemListener;
	}

	VideoChannelsListItemView.ListItemListener listItemActionListener=null;

	VideoChannelsListItemView.ListItemListener defListener=new VideoChannelsListItemView.ListItemListener() {
		@Override
		public void OnDeleteClick(VideoChannelsItem item) {

		}

		@Override
		public void OnItemClick(VideoChannelsItem item) {
			if (VideoChannelsListFragment.this.listItemActionListener != null) {
				VideoChannelsListFragment.this.listItemActionListener.OnItemClick(item);
			}
		}

		@Override
		public void OnItemTagClick(VideoChannelsItem item, String tag) {
			if (VideoChannelsListFragment.this.listItemActionListener != null) {
				VideoChannelsListFragment.this.listItemActionListener.OnItemTagClick(item,tag);
			}
		}

		@Override
		public void OnCheck(VideoChannelsItem item) {

			if (VideoChannelsListFragment.this.listItemActionListener != null) {
				VideoChannelsListFragment.this.listItemActionListener.OnCheck(item);
			}
		}

		@Override
		public void OnFocusCompleted(int status,VideoChannelsItem item) {
			if (VideoChannelsListFragment.this.listItemActionListener != null) {
				VideoChannelsListFragment.this.listItemActionListener.OnFocusCompleted(status,item);
			}
		}
	};

	protected void hideAllInputControls() {
		if (deleteDlg != null) {
			deleteDlg.dismiss();
		}

		if (inputBar != null) {
			inputBar.setVisibility(View.GONE);
		}

	}

	private void initDocDeleteDlg() {

		View v = LayoutInflater.from(getActivity()).inflate(
				R.layout.share_delete_layout, null);

		deleteDlg = DialogUtil.getMenuDialog(getActivity(), v);

		View cv = v.findViewById(R.id.cancel_click);
		cv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deleteDlg.dismiss();
			}
		});

		View confirmV = v.findViewById(R.id.confirm_click);
		confirmV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onDeleteDoc(v);
			}
		});

	}

	protected void onDeleteDoc(View v) {

		deleteDlg.dismiss();

	}



	boolean dataLoading = false;

	private void loadData(int index) {
		dataLoading = true;
		if (MainApp.getInstance().isNetworkAvailable()) {
			loadItemList(index);
		} else {
			mListView.onBottomComplete();
			mProgressBar.setVisibility(View.GONE);
			dataLoading = false;
		}
	}

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				runRefresh();
			}
		}, 1000);
	}

	boolean hasMore = true;

	protected void runRefresh() {
		hasMore = true;
		isRefresh = true;
		currentPage = 0;

		listAdapter.clear();

		loadData(currentPage++);

	}

	protected void onItemLongClick(int position) {
		// T.showLong(getActivity(), "long press");
		// DialogUtil.getAlertDlg(getActivity());
	}




	void loadItemList(int index) {

		VideoChannelsApi.getInstance().topicCircleQuery(userId, index, tagName,
				new OnTaskCompleted() {

					@Override
					public void onTaskCompleted(String result) {
						// T.showLong(getActivity(), result);
						if (!StringUtils.isEmpty(result)) {
							getResult(result);
						} else {
							swipeLayout.setRefreshing(false);
						}
					}
				});
	}

	public void getResult(String result) {
		if(getActivity()==null){
			return;
		}

		if (isRefresh) {
			isRefresh = false;
			listAdapter.clear();

		}

		// T.showLong(getActivity(), result);
		mProgressBar.setVisibility(View.GONE);
		swipeLayout.setRefreshing(false);
		ArrayList<VideoChannelsItem> list = VideoChannelsListParser.parse(result);
		dataLoading = false;
		if (list == null || list.size() == 0) {
			if(list==null){
				//ToastUtil.showMessage(getString(R.string.connect_server_error));
				emptyView.setText(getString(R.string.error_request_pull_refresh));
				if(listAdapter!=null&&listAdapter.getCount()==0) {emptyView.setVisibility(View.VISIBLE);}

			}else{
				//ToastUtil.showMessage(getString(R.string.connect_server_error));
				emptyView.setText(getString(R.string.main_empty_result));
				if(listAdapter!=null&&listAdapter.getCount()==0) {emptyView.setVisibility(View.VISIBLE);}
			}

			hasMore = false;
			swipeLayout.setRefreshing(false);
			mListView.setHasMore(hasMore);
			// swipeLayout.
			mListView.onBottomComplete();
			
			return;
		} else {
			hasMore = true;
			mListView.setHasMore(hasMore);
			emptyView.setVisibility(View.GONE);
		}

		listAdapter.appendList(list);
		mListView.onBottomComplete();
		

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}



	int type = 0;

	public void setType(int t) {
		type = t;

	}



	protected void refresh() {

		runRefresh();

	}


	@Override
	protected int getLayoutId() {

		return resId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
