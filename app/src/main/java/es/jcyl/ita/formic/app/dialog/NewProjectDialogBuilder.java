package es.jcyl.ita.formic.app.dialog;/*
 * Copyright 2020 Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Dialog;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.MainActivity;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.util.FileUtils;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class NewProjectDialogBuilder extends Dialog {

	private final MainActivity mainActivity;

	private TextView confirmationDialogTitle;

	private EditText nameText;
	private Button acceptButton;

	private Button cancelButton;

	public NewProjectDialogBuilder(final MainActivity mainActivity) {

		//super(new ContextThemeWrapper(mainActivity, R.style.DialogStyle));
		super(mainActivity, es.jcyl.ita.formic.forms.R.style.DialogStyle);

		this.mainActivity = mainActivity;

		this.setTitle(mainActivity
				.getString(R.string.new_project));

		this.setContentView(R.layout.add_project_dialog);

		confirmationDialogTitle = findViewById(R.id.addproject_title);
		confirmationDialogTitle.setText(mainActivity.getString(R.string.new_project).toUpperCase());
		nameText = findViewById(R.id.addproject_name);
		acceptButton = findViewById(R.id.addproject_accept);
		cancelButton = findViewById(R.id.addproject_cancel);
		acceptButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				String name = nameText.getText().toString();

				if (name != null && name.length() > 0
						&& validateName(name)) {
					if (projectExists(name)) {
						UserMessagesHelper.toast(mainActivity, mainActivity.getString(R.string.projectalreadyexists), Toast.LENGTH_SHORT);
				} else {
						dismiss();
						createProject(name);
						openProject(name);
					}
				} else {
					UserMessagesHelper.toast(mainActivity, mainActivity.getString(R.string.error_allowed_characters_on_projectalreadyexists), Toast.LENGTH_SHORT);
				}
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				dismiss();
			}
		});
	}

	private boolean projectExists(String name) {
		boolean output = false;

		List<Project> projects = App.getInstance().getProjectRepo().listAll();
		for (Project project : projects) {
			if (project.getName().equalsIgnoreCase(name)) {
				output = true;
			}
		}
		return output;
	}

	private boolean validateName(final String name) {
		boolean output = false;
		if (name == null || name.length() == 0) {
			return output;
		}

		final Pattern p = Pattern.compile("^[a-zA-Z0-9]+");
		final Matcher m = p.matcher(name);
		output = m.matches();

		return output;
	}

	private void createProject(String name) {
		// Nombre del archivo en assets
		String assetFileName = "basicproject.fml";

		// Directorio de destino en almacenamiento externo
		String destinationPath = mainActivity.getCacheDir().getPath()+ "/" + name.trim().toUpperCase() +".fml";

		FileUtils.copyAssetToFile(mainActivity, assetFileName, destinationPath);
	}

	private void openProject(String name) {
		String filePath = mainActivity.getCacheDir().getPath()+ "/" + name.trim().toUpperCase() +".fml";
		File file = new File(filePath);

		final Uri uri = FileProvider.getUriForFile(mainActivity, mainActivity.getApplicationContext().getPackageName() + ".provider", file);

		mainActivity.importFromUri(uri, mainActivity.getString(R.string.new_project), mainActivity.getString(R.string.new_project_continue));
	}
}

