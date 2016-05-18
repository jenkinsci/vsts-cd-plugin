package org.jenkinsci.plugins.releasemanagementci;

import hudson.Launcher;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;
import java.io.IOException;
import java.util.List;
import org.json.*;

/**
 * @author Ankit Goyal
 */
public class ReleaseManagementCI extends Notifier{

    public final String collectionUrl;
    public final String projectName;
    public final String releaseDefinitionName;
    public final String personalAccessToken;
    
    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public ReleaseManagementCI(String collectionUrl, String projectName, String releaseDefinitionName, String personalAccessToken)
    {
        if (collectionUrl.endsWith("/"))
        {
            this.collectionUrl = collectionUrl;
        }
        else
        {
            this.collectionUrl = collectionUrl + "/";
        }
        
        //this.collectionUrl = this.collectionUrl.toLowerCase().replaceFirst(".visualstudio.com", ".vsrm.visualstudio.com");
        this.projectName = projectName;
        this.releaseDefinitionName = releaseDefinitionName;
        this.personalAccessToken = personalAccessToken;
    }

    /*
     * (non-Javadoc)
     *
     * @see hudson.tasks.BuildStep#getRequiredMonitorService()
     */
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

        /*
     * (non-Javadoc)
     *
     * @see
     * hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild
     * , hudson.Launcher, hudson.model.BuildListener)
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException
    {
        String jobName = build.getProject().getName();
        int buildId = build.number;
        String buildNumber = build.getDisplayName();
        if (build.getResult() == Result.SUCCESS)
        {
            ReleaseManagementHttpClient releaseManagementHttpClient = 
                    new ReleaseManagementHttpClient(
                            this.collectionUrl.toLowerCase().replaceFirst(".visualstudio.com", ".vsrm.visualstudio.com"),
                            this.personalAccessToken);
            
            try 
            {
                ReleaseDefinition releaseDefinition = null;
                List<ReleaseDefinition> releaseDefinitions = releaseManagementHttpClient.GetReleaseDefinitions(this.projectName);
                for(final ReleaseDefinition rd : releaseDefinitions)
                {
                    if(rd.getName().equals(this.releaseDefinitionName))
                    {
                        releaseDefinition = rd;
                        break;
                    }
                }

                if(releaseDefinition == null)
                {
                    listener.getLogger().printf("No release definition found with name: %s%n", this.releaseDefinitionName);
                }
                else
                {
                    CreateRelease(releaseManagementHttpClient, releaseDefinition, jobName, buildNumber, buildId, listener);
                }
            } catch (ReleaseManagementExcpetion | JSONException ex)
            {
                ex.printStackTrace(listener.error("Failed to trigger release.%n"));
            }
        }
        
        return true;
    }
    
    void CreateRelease(
            ReleaseManagementHttpClient releaseManagementHttpClient,
            ReleaseDefinition releaseDefinition,
            String jobName,
            String buildNumber,
            int buildId,
            BuildListener listener) throws ReleaseManagementExcpetion, JSONException
    {
        Artifact jenkinsArtifact = null;
        if(releaseDefinition.getArtifacts().size() > 1)
        {
            throw new ReleaseManagementExcpetion("Auto trigger does not work if their are multiple artifact sources associated to a release definition");
        }
        for(final Artifact artifact : releaseDefinition.getArtifacts())
        {
            if(artifact.getType().equalsIgnoreCase("jenkins") && artifact.getDefinitionReference().getDefinition().getName().equalsIgnoreCase(jobName))
            {
                jenkinsArtifact = artifact;
                break;
            }
        }
        if(jenkinsArtifact == null)
        {
            listener.getLogger().printf("No jenkins artifact found with name: %s%n", jobName);
        }
        else
        {
            String body = "{\"definitionId\":\"" + releaseDefinition.getId().toString()
                    + "\",\"description\":\"Continous integration from jenkins build\",\"artifacts\":[{\"alias\":\""
                    + jenkinsArtifact.getAlias()
                    + "\",\"instanceReference\":{\"name\":\""
                    + buildNumber +"\",\"id\":\""
                    + buildId + "\"}},]}";
            listener.getLogger().printf("Triggering release...%n");
            String response = releaseManagementHttpClient.CreateRelease(this.projectName, body);
            JSONObject object = new JSONObject(response);
            listener.getLogger().printf("Release Name: %s%n", object.getString("name"));
            listener.getLogger().printf("Release id: %s%n", object.getString("id"));
        }
    }
    
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher>
    {

        /*
         * (non-Javadoc)
         *
         * @see hudson.tasks.BuildStepDescriptor#isApplicable(java.lang.Class)
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) 
        {
            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see hudson.model.Descriptor#getDisplayName()
         */
        @Override
        public String getDisplayName() 
        {
            return "Release Management CI";
        }

    }
}