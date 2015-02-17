package com.erraticduck.audioslideshow;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class AudioFileSelectionDialog extends DialogFragment {

	private String[] listOfAudioFiles;
	private boolean[] checkedFiles;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		listOfAudioFiles = getArguments().getStringArray("listOfAudioFiles");
		checkedFiles = new boolean[listOfAudioFiles.length];
		for (int i = 0; i < checkedFiles.length; i++) {
			checkedFiles[i] = true;
		}

		AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
		bob.setMultiChoiceItems(listOfAudioFiles, checkedFiles,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1, boolean isChecked) {
						checkedFiles[arg1] = isChecked;
					}
				});
		bob.setPositiveButton("Start", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				ArrayList<CharSequence> filesToPlay = new ArrayList<CharSequence>();
				for (int i = 0; i < checkedFiles.length; i++) {
					if (checkedFiles[i]) {
						filesToPlay.add(listOfAudioFiles[i]);
					}
				}

				Intent i = new Intent(getActivity(), SlideShow.class);
				i.putCharSequenceArrayListExtra("audio_files", filesToPlay);
				i.putExtra("path", getArguments().getString("clickedFolderName"));
				startActivity(i);
			}
		});
		bob.setTitle("Select Audio Files");
		return bob.create();
	}
}
