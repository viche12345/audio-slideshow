package com.erraticduck.audioslideshow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class AudioSelectionDialog extends DialogFragment {
	
	private String[] listOfAudioFolders;
	private String clickedFolderName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		listOfAudioFolders = getArguments().getStringArray("listOfAudioFolders");
		clickedFolderName = getArguments().getString("clickedFolderName");
		
		AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
		bob.setItems(listOfAudioFolders, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent i = new Intent(getActivity(), SlideShow.class);
				if (arg1 < listOfAudioFolders.length) {
					String selectedAudioFolder = listOfAudioFolders[arg1];
					i.putExtra("audio_path", selectedAudioFolder);
					i.putExtra("path", clickedFolderName);
					startActivity(i);
				}
			}
		});
		bob.setTitle("Select Audio Folder");
		return bob.create();
	}

}
