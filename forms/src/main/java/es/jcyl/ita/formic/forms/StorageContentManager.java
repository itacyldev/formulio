package es.jcyl.ita.formic.forms;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class StorageContentManager {

    private static final Log LOGGER = LogFactory.getLog(StorageContentManager.class);

    public static String PATH_SEPARATOR = "/";

    private static Uri externalStoragePrimaryPathUri = null;
    private static Uri externalStorageWorkPathUri = null;

    /**
     * Consigue el punto de acceso Uri al almacenamiento compartido
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static Uri getExternalPrimaryStoragePathUri(Context context) {
        if (externalStoragePrimaryPathUri != null) {
            return externalStoragePrimaryPathUri;
        }
        ContentResolver contentResolver = context.getContentResolver();
        List<UriPermission> uriPermissionList = contentResolver.getPersistedUriPermissions();
        if (uriPermissionList.size() > 0) {
            for (UriPermission uriPermission : uriPermissionList) {
                String path = uriPermission.getUri().getPath();
                String authority = uriPermission.getUri().getAuthority();
                if (path.startsWith("/tree/primary:")
                        && "com.android.externalstorage.documents".equals(authority)) {
                    return (externalStoragePrimaryPathUri = transformPathUri(context, uriPermission.getUri()));
                }
            }
        }
        externalStoragePrimaryPathUri = null;
        return null;
    }

    /**
     * Transforma una treeUri en pathUri
     *
     * @param context
     * @param treeUri Punto de acceso raiz de un almacenamiento externo.
     * @return Punto de acceso al documento raiz de un almacenamiento externo.
     */
    @TargetApi(Build.VERSION_CODES.N)
    private static Uri transformPathUri(Context context, Uri treeUri) {
        if (DocumentsContract.isTreeUri(treeUri)) {
            if (DocumentsContract.isDocumentUri(context, treeUri)) {
                return treeUri;
            } else {
                return DocumentsContract.buildDocumentUriUsingTree(
                        treeUri,
                        DocumentsContract.getTreeDocumentId(treeUri));
            }
        }
        return null;
    }


}
