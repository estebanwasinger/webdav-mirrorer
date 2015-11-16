package org.mule.tools;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by estebanwasinger on 11/16/15.
 */

@Mojo(name = "mirror", requiresProject = false)
@Execute(goal = "mirror")
public class MirrorerMojo extends AbstractMojo{

    @Parameter(property = "user", required = true)
    protected String user;

    @Parameter(property = "password", required = true)
    protected String password;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Sardine sardine = SardineFactory.begin(user, password);
        System.out.println("Starting");
        List<DavResource> resources = null;
        try {
            resources = sardine.getResources("https://repository-mule-connectors.forge.cloudbees.com/private/mules/");
            for (DavResource resource : resources) {
                if(resource.getName().endsWith(".zip")){
                    InputStream inputStream = sardine.get("https://repository-mule-connectors.forge.cloudbees.com" + resource.getHref().toString());
                    sardine.put("https://repository-muleconn.forge.cloudbees.com" + resource.getHref().toString(), inputStream);
                }
            }


            sardine.shutdown();
        } catch (IOException e) {
            throw new RuntimeException("Problem retrieving mule versions. Please check your credentials and ensure that are correct.");
        }
    }

}
