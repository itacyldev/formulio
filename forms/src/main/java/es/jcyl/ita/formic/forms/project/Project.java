package es.jcyl.ita.formic.forms.project;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import androidx.documentfile.provider.DocumentFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores User project information.
 */
public class Project extends Entity implements Serializable {

    private boolean opened = false;
    private static final String[] CONFIG_FOLDERS = new String[]{"data", "forms"};
    private List<ProjectResource> configFiles;

    public Project(EntitySource source, EntityMeta meta) {
        super(source, meta);
    }

    public void setBaseFolder(String path) {
        set("baseFolder", path);
    }

    public String getName() {
        return (String) get("name");
    }

    public String getDescription() {
        return (String) get("description");
    }

    public String getBaseFolder() {
        return (String) get("baseFolder");
    }

    public String getDataFolder() {
        return getFolderByType("data");
    }

    public String getFormsFolder() {
        return getFolderByType("forms");
    }

    public String getPicturesFolder() {
        return getFolderByType("pictures");
    }

    public String getFolderByType(String type) {
        return FilenameUtils.concat(this.getBaseFolder(), type);
    }

    public boolean isOpened() {
        return opened;
    }

    public List<ProjectResource> getConfigFiles(ProjectResource.ResourceType resourceType) {
        List<ProjectResource> confFiles = getConfigFiles();
        // return just the given type
        List<ProjectResource> filtered = new ArrayList<ProjectResource>();
        for (ProjectResource pr : confFiles) {
            if (pr.type == resourceType) {
                filtered.add(pr);
            }
        }
        return filtered;
    }

    public List<ProjectResource> getConfigFiles() {
        if (!opened) {
            throw new ProjectException(
                    error(String.format("Project [%s] is still close, call the open() method " +
                            "before you can get the conf files", this.getId())));
        }
        return configFiles;
    }

    private List<ProjectResource> readConfigFiles() {
        List<ProjectResource> files = new ArrayList<>();

        File f = new File(getBaseFolder());
        f = new File(getBaseFolder() + "/data");
        f = new File(getBaseFolder() + "/forms");

        ProjectResource.ResourceType type;
        for (String folder : CONFIG_FOLDERS) {

            File folderFile = new File(getBaseFolder() + "/" + folder);
            type = getFromFolderName(folder);
            if (!folderFile.exists() || !folderFile.isDirectory()) {
                throw new ProjectException(
                        error(String.format("Can't find config folder %s, did you delete it?",
                                folderFile.getAbsolutePath())));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Uri uri = Uri.parse(folderFile.toURI().getPath());
                Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
                // get document file from children uri
                DocumentFile tree = DocumentFile.fromTreeUri(App.getInstance().getAndroidContext(), childrenUri);
                DocumentFile[] treeFiles = tree.listFiles();
                for (DocumentFile confFile : treeFiles) {
                    if (!confFile.isDirectory() && confFile.getName().endsWith(".xml")) {
                        if (type == null) {
                            type = getTypeFromFilename(confFile.getName());
                        }
                        if (type == null) {
                            DevConsole.warn("Unknown file name while reading configuration, " +
                                    "skipped! : " + confFile.getUri().getPath());
                        } else {
                            //files.add(new ProjectResource(this, confFile, type));
                        }
                    }
                }

            } else {
                File[] xmlFiles = folderFile.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File current, String name) {
                        return name.endsWith(".xml");
                    }
                });


                if (ArrayUtils.isNotEmpty(xmlFiles)) {
                    for (File confFile : xmlFiles) {
                        if (type == null) {
                            type = getTypeFromFilename(confFile.getName());
                        }
                        if (type == null) {
                            DevConsole.warn("Unknown file name while reading configuration, " +
                                    "skipped! : " + confFile.getAbsolutePath());
                        } else {
                            files.add(new ProjectResource(this, confFile, type));
                        }
                    }
                } else {
                    throw new ProjectException(
                            error(String.format("Project '%s' has no files inside the folder '%s'. " +
                                    "Did you delete them?.\n Full path:\n%s", this.getId(), folder, getBaseFolder())));
                }
            }


        }
        return files;
    }

    private ProjectResource.ResourceType getTypeFromFilename(String name) {
        if (name.equalsIgnoreCase("context.xml")) {
            return ProjectResource.ResourceType.CONTEXT;
        }
        return null;
    }

    private ProjectResource.ResourceType getFromFolderName(String folder) {
        if (folder.equals("forms")) {
            return ProjectResource.ResourceType.FORM;
        } else if (folder.equals("data")) {
            return ProjectResource.ResourceType.REPO;
        }
        return null;
    }

    /**
     * Reads configuration
     */
    public void open() {
        // info parameters
        File f = new File(getBaseFolder());
        String folderName = f.getName();
        setId(folderName);
        set("name", folderName);

        // find files with .xml extension in nested folders
        this.configFiles = readConfigFiles();

        opened = true;
    }

    @Override
    public String toString() {
        return "Project{name=" + this.get("name") +
                '}';
    }
}
