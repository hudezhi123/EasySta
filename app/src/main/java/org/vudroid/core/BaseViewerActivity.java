package org.vudroid.core;

import easyway.Mobile.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.vudroid.core.events.CurrentPageListener;
import org.vudroid.core.events.DecodingProgressListener;
import org.vudroid.core.models.CurrentPageModel;
import org.vudroid.core.models.DecodingProgressModel;
import org.vudroid.core.models.ZoomModel;
import org.vudroid.core.views.PageViewZoomControls;

// pdf reader activity
public abstract class BaseViewerActivity extends Activity implements
		DecodingProgressListener, CurrentPageListener {
	private static final int MENU_GOTO = 1;
	private static final int DIALOG_GOTO = 0;
	private static final String DOCUMENT_VIEW_STATE_PREFERENCES = "DjvuDocumentViewState";
	private DecodeService decodeService;
	private DocumentView documentView;
	private Toast pageNumberToast;
	private CurrentPageModel currentPageModel;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDecodeService();
		final ZoomModel zoomModel = new ZoomModel();
		final DecodingProgressModel progressModel = new DecodingProgressModel();
		progressModel.addEventListener(this);
		currentPageModel = new CurrentPageModel();
		currentPageModel.addEventListener(this);
		documentView = new DocumentView(this, zoomModel, progressModel,
				currentPageModel);
		zoomModel.addEventListener(documentView);
		documentView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		decodeService.setContentResolver(getContentResolver());
		decodeService.setContainerView(documentView);
		documentView.setDecodeService(decodeService);
		decodeService.open(getIntent().getData());

		final FrameLayout frameLayout = createMainContainer();
		frameLayout.addView(documentView);
		frameLayout.addView(createZoomControls(zoomModel));
		setContentView(frameLayout);

		final SharedPreferences sharedPreferences = getSharedPreferences(
				DOCUMENT_VIEW_STATE_PREFERENCES, 0);
		documentView.goToPage(sharedPreferences.getInt(getIntent().getData()
				.toString(), 0));
		documentView.showDocument();
	}

	public void decodingProgressChanged(final int currentlyDecoding) {
		runOnUiThread(new Runnable() {
			public void run() {
				getWindow().setFeatureInt(
						Window.FEATURE_INDETERMINATE_PROGRESS,
						currentlyDecoding == 0 ? 10000 : currentlyDecoding);
			}
		});
	}

	public void currentPageChanged(int pageIndex) {
		final String pageText = (pageIndex + 1) + "/"
				+ decodeService.getPageCount();
		if (pageNumberToast != null) {
			pageNumberToast.setText(pageText);
		} else {
			pageNumberToast = Toast.makeText(this, pageText, Toast.LENGTH_SHORT);
		}
		pageNumberToast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
		pageNumberToast.show();
		saveCurrentPage();
	}

	private void setWindowTitle() {
		final String name = getIntent().getData().getLastPathSegment();
		getWindow().setTitle(name);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setWindowTitle();
	}

	private PageViewZoomControls createZoomControls(ZoomModel zoomModel) {
		final PageViewZoomControls controls = new PageViewZoomControls(this,
				zoomModel);
		controls.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		zoomModel.addEventListener(controls);
		return controls;
	}

	private FrameLayout createMainContainer() {
		return new FrameLayout(this);
	}

	private void initDecodeService() {
		if (decodeService == null) {
			decodeService = createDecodeService();
		}
	}

	protected abstract DecodeService createDecodeService();

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		decodeService.recycle();
		decodeService = null;
		super.onDestroy();
	}

	private void saveCurrentPage() {
		final SharedPreferences sharedPreferences = getSharedPreferences(
				DOCUMENT_VIEW_STATE_PREFERENCES, 0);
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(getIntent().getData().toString(),
				documentView.getCurrentPage());
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_GOTO, 0, R.string.SR_Gotopage);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_GOTO:
			showDialog(DIALOG_GOTO);
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_GOTO:
			return new GoToPageDialog(this, documentView, decodeService);
		}
		return null;
	}
}
