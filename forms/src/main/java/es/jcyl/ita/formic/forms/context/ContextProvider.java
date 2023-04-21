package es.jcyl.ita.formic.forms.context;

import org.apache.commons.io.FileUtils;

import android.content.Context;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.context.impl.DateTimeContext;
import es.jcyl.ita.formic.forms.context.impl.RepoAccessContext;
import es.jcyl.ita.formic.forms.location.LocationService;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.repo.RepositoryFactory;

public class ContextProvider {

    private final Context andContext;

    public ContextProvider(Context andContext){
        this.andContext = andContext;
    }

    public CompositeContext createContext() {
        CompositeContext context = new UnPrefixedCompositeContext();
        // TODO: create context providers to implement each context creation and configure
        //  them in project file
        context.addContext(new DateTimeContext());
        context.addContext(new BasicContext("session"));
        // TODO: configure context and default sync properties in XML in res folder
        if (this.andContext != null) {
            context.put("location", new LocationService(this.andContext));
        }
        context.put("repos", new RepoAccessContext());

        setupJobsContext(context);
        // set context to context dependant objects
        MainController.getInstance().setContext(context);
        RepositoryFactory.getInstance().setContext(context);
        return context;
    }

    /**
     * Prepares temp execution folder and context information to execute jobs.
     *
     * @param ctx
     */
    public void setupJobsContext(CompositeContext ctx) {
        // Create temporary directory for job execution if it doesn't already exists
        File osTempDirectory = FileUtils.getTempDirectory();
        if (!osTempDirectory.exists()) {
            // use cache dir
            osTempDirectory = andContext.getCacheDir();
        }
        File tmpFolder = new File(osTempDirectory, "tmp");
        if (!tmpFolder.exists()) {
            tmpFolder.mkdir();
        }
        // application context
        BasicContext appCtx = new BasicContext("app");
        appCtx.put("workingFolder", tmpFolder.getAbsolutePath());
        ctx.addContext(appCtx);
    }

    /**
     * Prepares temp execution folder and context information to execute jobs.
     *
     * @param ctx
     */
    public void setupProjectContext(CompositeContext ctx, Project prj) {
        BasicContext prjCtx = new BasicContext("project");
        prjCtx.put("folder", prj.getBaseFolder());
        prjCtx.put("dataFolder", prj.getDataFolder());
        prjCtx.put("formsFolder", prj.getFormsFolder());
        prjCtx.put("picturesFolder", prj.getPicturesFolder());
        prjCtx.putAll(prj.getProperties());
    }
}
