/**
 * 优分享VR
 * copy right: youkes.com
 * author:xuming
 * licence:GPL2
 */
package com.youkes.vr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.youkes.vr.ui.ColumnHorizontalScrollView;

import java.util.ArrayList;
import java.util.HashMap;

public class AppEditViewPagerActivity extends AppMenuActivity {


	@Override
	protected int getLayoutId() {
		return R.layout.activity_news_main;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initColumnData();
	}

	protected void hideAddNavBtn() {
		this.ll_more_columns.setVisibility(View.GONE);
	}
	protected void showAddNavBtn() {
		this.ll_more_columns.setVisibility(View.VISIBLE);
	}

	ArrayList<ChannelItem> userChannelLists = new ArrayList<ChannelItem>();

	/**
	 * need override
	 *
	 * @return
	 */
	protected ArrayList<ChannelItem> getUserChannelLists() {
		ArrayList<ChannelItem> list = new ArrayList<ChannelItem>();
		return list;
	}

	private void initColumnData() {
		userChannelLists = getUserChannelLists();

		initTabColumn();
		initViewPager();
		initFragments();

		if(fragments!=null&&fragments.size()>0){
			navbarView.setVisibility(View.VISIBLE);
		}else if(getEditable()){
			navbarView.setVisibility(View.VISIBLE);
		}else{
			navbarView.setVisibility(View.GONE);
		}
	}

	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

	private void initFragments() {
		fragments.clear();
		int count = userChannelLists.size();
		for (int i = 0; i < count; i++) {

			ChannelItem channelItem = userChannelLists.get(i);
			String name = channelItem.getName();
			String id = channelItem.getId();
			String siteId = channelItem.getSiteId();
			Fragment f = initFragment(siteId, name);
			fragments.add(f);
			fragmentMap.put(id, f);
		}
		mAdapetr.appendList(fragments);
	}

	/**
	 * need override
	 *
	 * @return
	 */
	protected Fragment initFragment(String siteId, String tag) {
		Fragment f = new Fragment();
		return f;
	}

	HashMap<String, Fragment> fragmentMap = new HashMap<String, Fragment>();


	private BaseFragmentPagerAdapter mAdapetr;
	protected ViewPager mViewPager = null;

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		mAdapetr = new BaseFragmentPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
	}


	public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};


	/**
	 * 选择的Column里面的Tab
	 */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			// rg_nav_content.getParent()).smoothScrollTo(i2, 0);
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
			// mItemWidth , 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}


	protected LinearLayout mRadioGroup_content;
	protected ImageView shade_left;
	protected ImageView shade_right;
	protected LinearLayout ll_more_columns;
	protected RelativeLayout rl_column;
	int mScreenWidth = 0;
	int mItemWidth = 0;
	int columnSelectIndex = 0;
	protected ColumnHorizontalScrollView mColumnHorizontalScrollView;
	protected ImageView button_more_columns;

	protected View navbarView=null;
	private void initTabColumn() {
		navbarView=findViewById(R.id.nav_bar);
		mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
		shade_left = (ImageView) findViewById(R.id.shade_left);
		shade_right = (ImageView) findViewById(R.id.shade_right);
		ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_columns);
		rl_column = (RelativeLayout) findViewById(R.id.rl_column);
		button_more_columns = (ImageView) findViewById(R.id.button_more_columns);
		button_more_columns.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onChannelMoreClick(v);

			}
		});

		//no more button
		//button_more_columns.setVisibility(View.GONE);


		resetAllNavs();
	}

	private void resetAllNavs() {
		mRadioGroup_content.removeAllViews();
		int count = userChannelLists.size();
		mScreenWidth = (int) MainApp.getInstance().getScreenWidth();
		mItemWidth = mScreenWidth / 5;
		mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left,
				shade_right, ll_more_columns, rl_column);
		for (int i = 0; i < count; i++) {
			String channelName = userChannelLists.get(i).getName();
			addChannelNav(i, channelName);

		}
	}

	private void addChannelNav(int i, String channelName) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = 5;
		params.rightMargin = 5;
		// TextView localTextView = (TextView)
		// mInflater.inflate(R.layout.column_radio_item, null);
		TextView columnTextView = new TextView(this);
		columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
		// localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
		columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
		columnTextView.setGravity(Gravity.CENTER);
		columnTextView.setPadding(5, 5, 5, 5);
		columnTextView.setId(i);
		columnTextView.setText(channelName);
		columnTextView.setTextColor(getResources().getColorStateList(
				R.color.top_category_scroll_text_color_day));
		if (columnSelectIndex == i) {
			columnTextView.setSelected(true);
		}
		columnTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
					View localView = mRadioGroup_content.getChildAt(i);
					if (localView != v)
						localView.setSelected(false);
					else {
						localView.setSelected(true);
						//mViewPager.setCurrentItem(i);
						onViewPagerSelected(i);
					}
				}
			}
		});

		mRadioGroup_content.addView(columnTextView, i, params);
	}

	private void onViewPagerSelected(int position) {
		mViewPager.setCurrentItem(position);
	}


	protected void onChannelMoreClick(View v) {


	}

	public void addNav(String siteId, String navId, String navName) {


		int sz = userChannelLists.size();
		userChannelLists.add(new ChannelItem(siteId, navId, navName, sz, 1));
		addChannelNav(sz, navName);

		Fragment fragment = initFragment(siteId, navName);
		fragments.add(fragment);
		fragmentMap.put(navId, fragment);
		mAdapetr.append(fragment);


	}


	private ChannelItem getChannelItem(String id) {
		for (ChannelItem channelItem : userChannelLists) {
			String id0 = channelItem.getId();
			if (id0 != null && id0.equals(id)) {
				return channelItem;
			}
		}
		return null;
	}


	protected void removeNav(String id) {
		ChannelItem channelItem = getChannelItem(id);
		if (channelItem == null) {
			return;
		}

		userChannelLists.remove(channelItem);
		Fragment f = fragmentMap.get(id);
		if (f == null) {
			return;
		}
		fragmentMap.remove(id);
		fragments.remove(f);

		resetAllNavs();
		mAdapetr.appendList(fragments);
		//ToastUtil.showMessage("remove Nave sldk:" + id);
	}


	protected boolean getEditable() {
		return false;
	}
}
