package org.vudroid.core;
import easyway.Mobile.R;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// go to page dialog
public class GoToPageDialog extends Dialog {
	private final DocumentView documentView;
	private final DecodeService decodeService;

	public GoToPageDialog(final Context context,
			final DocumentView documentView, final DecodeService decodeService) {
		super(context);
		this.documentView = documentView;
		this.decodeService = decodeService;
		setTitle(R.string.SR_Gotopage);
		setContentView(R.layout.sr_gotopage);
		final Button button = (Button) findViewById(R.id.btnGoto);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				goToPageAndDismiss();
			}
		});
		final EditText editText = (EditText) findViewById(R.id.edtPageNum);
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView textView, int actionId,
					KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_NULL
						|| actionId == EditorInfo.IME_ACTION_DONE) {
					goToPageAndDismiss();
					return true;
				}
				return false;
			}
		});
	}

	private void goToPageAndDismiss() {
		navigateToPage();
		dismiss();
	}

	private void navigateToPage() {
		final EditText text = (EditText) findViewById(R.id.edtPageNum);
		final int pageNumber = Integer.parseInt(text.getText().toString());
		if (pageNumber < 1 || pageNumber > decodeService.getPageCount()) {
			Toast.makeText(
					getContext(),
					String.format(
							getContext().getString(R.string.SR_GotoRange),
							decodeService.getPageCount()), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		documentView.goToPage(pageNumber - 1);
	}
}
