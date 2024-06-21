package es.jcyl.ita.formic.jayjobs.task.processor;

/*
 * Copyright 2021 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;

/**
 * Processor for synchronizing data in Cartodruid
 *
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class CartodruidSyncProcessor extends AbstractProcessor implements NonIterProcessor {
    @Override
    public void process() throws TaskException {

    }
//
//    protected static final Log LOGGER = LogFactory
//            .getLog(CartodruidSyncProcessor.class);
//
//    protected static final int LAPSE_SECS = 10;
//
//    protected static final long MAX_WAIT_MIN = 10;
//    protected static final long MAX_WAIT_MS = MAX_WAIT_MIN * 60 * 1000;
//    /**
//     * Se hace una comprobación periódica del estado del lote, si por algún
//     * motivo si pierde la conexión momentáneamente la tarea debería seguir
//     * intentando conectarse durante un nº de reintentos
//     */
//    protected static final int NUMBER_OF_RETRIES = 3;
//
//    private android.content.Context ctx;
//
//    private String endpoint;
//    private String workspace;
//    private String user;
//    private String password;
//
//    protected List<SyncFile> files;
//
//    private String[] fileList;
//    private Boolean isDownloadFiles;
//
//    protected File tmpFolder;
//    protected File downloadFolder;
//    protected File dataFolder;
//
//    // Factory for the creation of services that cover REST services
//    private SyncTaskServiceFactory serviceFactory = null;
//
//    private SyncService syncService = null;
//    private SyncFileService syncFileService = null;
//
//    @Override
//    public void process() throws TaskException {
//
//        if (prepareSync()) {
//            executeSync();
//        }
//    }
//
//    /**
//     * Actions prior to the execution of the task
//     *
//     * @return
//     * @throws TaskException
//     */
//    private boolean prepareSync() throws TaskException {
//        boolean result = true;
//        Map<String, SyncFile> syncFiles = new HashMap<>();
//
//        // checks if the files exist
//        for (SyncFile file : files) {
//            String dbFile = checkFile(file.getName());
//            syncFiles.put(dbFile, file);
//        }
//
//        if (syncFiles.size() > 0) {
//            user = getUser();
//            if (user == null) {
//                return false;
//            }
//
//            String[] files = new String[syncFiles.size()];
//            String readWrite = "RW";
//            if (syncFiles.values().iterator().hasNext()) {
//                SyncFile syncFile = syncFiles.values().iterator().next();
//                if (!StringUtils.isEmpty(syncFile.getReadwrite())) {
//                    readWrite = syncFile.getReadwrite();
//                }
//            }
//
//            this.fileList = syncFiles.keySet().toArray(files);
//            this.isDownloadFiles = StringUtils.containsIgnoreCase(readWrite, "R");
//        } else {
//            throw new TaskException("No files have been specified to synchronize");
//        }
//        return result;
//    }
//
//    /**
//     * Checks if a file exists in the project folder or in the working folder
//     *
//     * @param file
//     * @return
//     * @throws TaskException
//     */
//    private String checkFile(String file) throws TaskException {
//        String dbFile = TaskResourceAccessor.getProjectFile(this.getGlobalContext(), file);
//        File f = new File(dbFile);
//        if (!f.exists()) {
//            dbFile = TaskResourceAccessor.getWorkingFile(this.getGlobalContext(), file);
//        }
//        f = new File(dbFile);
//
//        if (!f.exists()) {
//            throw new TaskException(String.format("Couldn't find file [%s] neither in project " +
//                            "folder [%s] nor in application tmp folder [%s].", dbFile,
//                    ContextAccessor.projectFolder(this.getGlobalContext()),
//                    ContextAccessor.workingFolder(this.getGlobalContext())));
//        }
//
//        return dbFile;
//    }
//
//    /**
//     * @throws TaskException
//     */
//    public void executeSync()
//            throws TaskException {
//        doConfigure();
//
//        doExecute();
//    }
//
//    private void doConfigure() throws TaskException {
//        this.configureFactory();
//        this.configureServices();
//        if (fileList != null && fileList.length != 0) {
//            if (this.downloadFolder == null) {
//                throw new TaskException("Download folder is not defined");
//            } else {
//                this.setTaskSize();
//            }
//        } else {
//            throw new TaskException("No files have been specified to synchronize");
//        }
//    }
//
//    private void configureFactory() throws TaskException {
//        this.serviceFactory = new SyncTaskServiceFactoryImpl();
//
//        // configuramos propiedades de la factoría
//        SyncCredentials credentials = null;
//        try {
//            credentials = new SyncCredentials(this.workspace, this.user, this.password);
//            this.serviceFactory.setProperty(SyncTaskServiceFactory.CREDENTIALS,
//                    credentials);
//            this.serviceFactory.setProperty(SyncTaskServiceFactory.ENDPOINT, this.endpoint);
//
//        } catch (Exception e) {
//            throw new TaskException(
//                    "No se han podido configurar correctamente la factoría", e);
//        }
//
//    }
//
//    private void configureServices() {
//        if (syncService == null) {
//            this.syncService = this.serviceFactory.getSyncService();
//        }
//
//        if (syncFileService == null) {
//            this.syncFileService = this.serviceFactory.getSyncFileService();
//        }
//    }
//
//    /**
//     * Recorre los ficheros comprobando que existe y calcula el tamaño total de
//     * KB a subir
//     */
//    private void setTaskSize() throws TaskException {
//        File file = null;
//        Long size = 0L;
//        for (String filePath : fileList) {
//            file = new File(filePath);
//            if (!file.exists()) {
//                throw new TaskException(String.format(
//                        "No se ha encontrado el fichero %s configurado para la sincronización",
//                        filePath));
//            }
//            size += file.length();
//        }
//        notifier.setTaskSize(getTaskId(), size);
//    }
//
//
//    protected void doExecute() throws TaskException {
//        Integer syncId = null;
//
//        try {
//            File file;
//            Long uploadedData = 0L;
//
//            updateDetailedStatus(SyncStates.UPLOADINGING_FILE);
//            for (String filePath : fileList) {
//                file = new File(filePath);
//                syncId = createNewServerSync(file);
//                notifier.setSyncId(syncId);
//                uploadedData += file.length();
//            }
//
//            updateDetailedStatus(SyncStates.FILES_UPLOADED);
//
//        } catch (Exception ex) {
//            String msg = "No se ha podido crear la tarea de sincronización.";
//            notifier.notifyError(getTaskId(), "", msg);
//        }
//
//        try {
//            waitForSyncroFinished(syncId, isDownloadFiles);
//        } catch (Exception e) {
//            return;
//        }
//
//        try {
//            if (isDownloadFiles) {
//                downLoadFiles(syncId);
//            }
//        } catch (Exception e) {
//            LOGGER.error(e);
//            return;
//        }
//
//        finishExecution();
//
//
//    }
//
//    void finishExecution() {
//        updateDetailedStatus(SYNC_FINISHED);
//    }
//
//    public Integer createNewServerSync(File file)
//            throws TaskException {
//        Integer syncId = null;
//        try {
//
//            syncId = syncService.createInServer(file);
//        } catch (Exception e) {
//            throw new TaskException(
//                    "No se puedo crear una nueva sincronización", e);
//        }
//        return syncId;
//    }
//
//    /**
//     * Active waiting checking the status of the task until it is in FINISHED or ERROR status
//     *
//     * @param syncId
//     * @param isDownloadFiles
//     * @return true if the server sync task completed successfully and should go to download or
//     * false if an error occurred and should be aborted
//     * @throws SyncClientTaskException
//     */
//    private void waitForSyncroFinished(Integer syncId, Boolean isDownloadFiles)
//            throws SyncClientTaskException {
//        SyncResource currentSync = null;
//
//        long initTime_NS = System.nanoTime();
//        long timeCounter_MS = 0;
//        boolean stateChanged = false;
//        boolean serverError = false;
//
//        int numErrorsCounter = 0;
//
//        while (timeCounter_MS < MAX_WAIT_MS && !stateChanged && !serverError) {
//            try {
//                waitFor(LAPSE_SECS);
//                currentSync = syncService.getSync(syncId);
//                // If the connection has worked reset the number retries
//                numErrorsCounter = 0;
//                if (currentSync == null) {
//                    numErrorsCounter++;
//                }
//            } catch (Exception e) {
//                LOGGER.error(
//                        "Error trying to get sync status "
//                                + currentSync,
//                        e);
//                numErrorsCounter++;
//            }
//
//            if (numErrorsCounter > NUMBER_OF_RETRIES) {
//                serverError = true;
//            }
//            if (SyncStateEnum.FINISHED.name().equals(currentSync.getState())
//                    && (((currentSync.getResources() != null
//                    && currentSync.getResources().size() > 0))
//                    || !isDownloadFiles)) { // If there are no files to download, no resources are
//                // generated and the synchronization has finished
//                stateChanged = true;
//            }
//            timeCounter_MS = (System.nanoTime() - initTime_NS) / 1000000;
//        }
//
//        if (serverError) {
//            String msg = "The synchronization task could not be completed by error on the server.";
//            notifier.notifyError(getTaskId(), "", msg);
//            throw new SyncClientTaskException(msg);
//        }
//
//        boolean waitExceed = timeCounter_MS > MAX_WAIT_MS;
//        if (waitExceed) {
//            String msg = "The synchronization task could not be completed. The timeout has been exceeded.";
//            notifier.notifyError(getTaskId(), "", msg);
//            throw new SyncClientTaskException(msg);
//
//        } else {
//            if (!SyncStateEnum.FINISHED.name().equals(currentSync.getState())) {
//                String msg = "Synchronization task could not be completed, task failed on server.";
//                notifier.notifyError(getTaskId(), "", msg);
//                throw new SyncClientTaskException(msg);
//            }
//        }
//    }
//
//    protected void waitFor(int seconds) {
//        try {
//            Thread.sleep(seconds * 1000);
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    protected void updateDetailedStatus(SyncStates status) {
//        notifier.updateDetailedStatus(getTaskId(), status.ordinal(), null);
//    }
//
//    /**
//     * Descargamos los ficheros del lote en la carpeta de descarga indicada
//     *
//     * @param syncId
//     * @throws SyncClientTaskException
//     * @throws RestException
//     */
//    private boolean downLoadFiles(Integer syncId)
//            throws SynchronizationException, SyncClientTaskException,
//            RestException {
//        updateDetailedStatus(SyncStates.DOWNLOAD_FILE_READY);
//        try {
//            syncFileService.downloadFile(syncId, this.downloadFolder.getAbsolutePath(), this.workspace);
//            updateDetailedStatus(SyncStates.FILES_DOWNLOADED);
//        } catch (Exception ex) {
//            String msg = "No se ha podido descargar el fichero.";
//            notifier.notifyError(getTaskId(), "", msg);
//            throw new SynchronizationException(msg, ex);
//        }
//
//        try {
//            SyncResource currentSync = syncService.getSync(syncId);
//            currentSync.setState("FN");
//            currentSync.setFechaFin(new Date());
//            syncService.updateSync(syncId, currentSync);
//
//        } catch (Exception ex) {
//            String msg = "No se ha podido actualizar el estado de la sincronización.";
//            notifier.notifyError(getTaskId(), "", msg);
//        }
//
//        return true;
//    }
//
//    private long getTaskId() {
//        return this.task.getId();
//    }
//
//   /* public boolean finishSync() {
//        boolean result = true;
//        Iterator<String> iterator = syncFiles.keySet().iterator();
//
//        if (iterator.hasNext()) {
//            String filePath = iterator.next();
//            SyncFile file = syncFiles.get(filePath);
//
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//            builder.setTitle("Synchronization");
//
//            if (file.getReadwrite().toLowerCase().contains("r")) {
//
//                // extraemos los ficheros del zip descargado
//                result = extractFile(tmpFolder, dataFolder);
//                if (!result) {
//
//                    this.notifier.notifyError(null, "EF", "Unable to download files");
//                    return false;
//                }
//
//                //reiniciamos la aplicación para cargar la nueva BD
//                builder.setMessage("App Restart");
//                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        // TODO App restart
//                    }
//                });
//            } else {
//                //reiniciamos la aplicación para cargar la nueva BD
//                builder.setMessage("Synchronization Finished");
//                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//
//
//                });
//
//            }
//
//            builder.show();
//        }
//        return result;
//    }*/
//
//    /**
//     * Busca el fichero zip descargado dentro de la carpeta temporal, mueve el
//     * fichero anterior a la carpeta temporal y descomprime el descargado en
//     * la carpeta de trabajo
//     *
//     * @param tmpFolder  Carpeta temporal
//     * @param dataFolder Carpeta de trabajo
//     */
//    protected boolean extractFile(File tmpFolder, File dataFolder) {
//        File newFile = SyncUtils.getLastZipFile(tmpFolder);
//
//        if (newFile != null) {
//            ZipUtils.unzip(newFile.getAbsolutePath(), dataFolder.getAbsolutePath());
//
//            //Una vez descomprimido lo borramos
//            newFile.delete();
//        } else {
//            //No se ha descargado el fichero
//            return false;
//        }
//
//        return true;
//    }
//
//    private String getUser() {
//        String user = null;
//        Context userContext = this.getGlobalContext().getContext("user");
//        user = userContext.getString("id");
//        return user;
//    }
//
//    public String getEndpoint() {
//        return endpoint;
//    }
//
//    public void setEndpoint(String endpoint) {
//        this.endpoint = endpoint;
//    }
//
//    public String getWorkspace() {
//        return workspace;
//    }
//
//    public void setWorkspace(String workspace) {
//        this.workspace = workspace;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public void setContext(Object ctx) {
//        this.ctx = (android.content.Context) ctx;
//    }
//
//    public void setTmpFolder(File tmpFolder) {
//        this.tmpFolder = tmpFolder;
//    }
//
//    public void setDownloadFolder(File downloadFolder) {
//        this.downloadFolder = downloadFolder;
//    }
//
//    public void setDataFolder(File dataFolder) {
//        this.dataFolder = dataFolder;
//    }
//
//    public List<SyncFile> getFiles() {
//        return files;
//    }
//
//    public void setFiles(List<SyncFile> files) {
//        this.files = files;
//    }
//
//    public void setSyncService(SyncService syncService) {
//        this.syncService = syncService;
//    }
//
//    public void setSyncFileService(SyncFileService syncFileService) {
//        this.syncFileService = syncFileService;
//    }

}
