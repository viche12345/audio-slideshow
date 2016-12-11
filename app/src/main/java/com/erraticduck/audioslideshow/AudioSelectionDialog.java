package com.erraticduck.audioslideshow;

import java.io.File;
import java.io.FileFilter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AudioSelectionDialog extends DialogFragment {

	private String[] listOfAudioFolders;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		listOfAudioFolders = getArguments().getStringArray("listOfAudioFolders");

		AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
		bob.setItems(listOfAudioFolders, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (arg1 < listOfAudioFolders.length) {
					String selectedAudioFolder = listOfAudioFolders[arg1];

					File audioFolder = new File(selectedAudioFolder);
					File[] fileList = audioFolder.listFiles(new FileFilter() {

						public boolean accept(File pathname) {
							return !pathname.isDirectory();
						}
					});
					String[] listOfAudioFiles = FileBrowser.fileArrayToStringArray(fileList);

					DialogFragment d = new AudioFileSelectionDialog();
					Bundle args = new Bundle(getArguments());
					args.putStringArray("listOfAudioFiles", listOfAudioFiles);
					d.setArguments(args);
					d.show(getFragmentManager(), "audiofileselection");
				}
			}
		});
		bob.setTitle("Select Audio Folder");
		return bob.create();
	}

}
