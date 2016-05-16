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
import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.google.gson.Gson;
import org.json.*;
import org.apache.commons.httpclient.methods.PostMethod;

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
        String buildName = build.getDisplayName();
        if (build.getResult() == Result.SUCCESS)
        {
            TriggerRelease(listener, jobName, buildId, buildName);
        }
        
        return true;
    }
    
    void TriggerRelease(BuildListener listener, String jobName, int buildId, String buildName)
    {
        HttpClient client = new HttpClient();
        byte[] auth = Base64.encodeBase64(("jenkins:" + this.personalAccessToken).getBytes(Charset.defaultCharset()));
        String projectUrl = this.collectionUrl.toLowerCase().replaceFirst(".visualstudio.com", ".vsrm.visualstudio.com") + this.projectName;
        String definitionsUrl = projectUrl + "/_apis/release/definitions?$expand=artifacts";
        String releaseUrl = projectUrl + "/_apis/release/releases?api-version=3.0-preview.2";
        GetMethod getReleaseDefinitions = new GetMethod(definitionsUrl);
        PostMethod createRelease = new PostMethod(releaseUrl);
        getReleaseDefinitions.addRequestHeader("Authorization", "Basic " + new String(auth, Charset.defaultCharset()));
        createRelease.addRequestHeader("Authorization", "Basic " + new String(auth, Charset.defaultCharset()));
        createRelease.addRequestHeader("Content-Type", "application/json");
        try
        {
            ReleaseDefinition definition = null;
            int status = client.executeMethod(getReleaseDefinitions);
            String response = getReleaseDefinitions.getResponseBodyAsString();
            if(status >= 300)
            {
                listener.getLogger().printf("Status code: %s%n", status);
                listener.getLogger().printf("Response: %s%n", response);
            }
            DefinitionResponse definitionResponse = new Gson().fromJson(response, DefinitionResponse.class);
            for(final ReleaseDefinition rd : definitionResponse.getValue())
            {
                if(rd.getName().equals(this.releaseDefinitionName))
                {
                    definition = rd;
                    break;
                }
            }
            
            if(definition != null)
            {
                CreateReleaseForDefinition(definition, jobName, buildName, buildId, createRelease, listener, client);
            }
            else
            {
                listener.getLogger().printf("No release definition found with name: %s%n", this.releaseDefinitionName);
            }
        }
        catch(HttpException ex)
        {
            ex.printStackTrace(listener.error("Unable to get release definitions %s%n",
            definitionsUrl));
        }
        catch(IOException ex)
        {
            ex.printStackTrace(listener.error("Unable to get release definitions %s%n",
            definitionsUrl));
        }
        catch(Exception ex)
        {
            ex.printStackTrace(listener.error("Unable to get release definitions %s%n",
            definitionsUrl)); 
        }
        finally
        {
            getReleaseDefinitions.releaseConnection();
        }
    }

    private void CreateReleaseForDefinition(ReleaseDefinition definition, String jobName, String buildName, int buildId, PostMethod createRelease, BuildListener listener, HttpClient client) throws JSONException, IOException {
        int status;
        String response;
        Artifact jenkinsArtifact = null;
        for(final Artifact artifact : definition.getArtifacts())
        {
            if(artifact.getType().equalsIgnoreCase("jenkins") && artifact.getDefinitionReference().getDefinition().getName().equalsIgnoreCase(jobName))
            {
                jenkinsArtifact = artifact;
                break;
            }
        }
        if(jenkinsArtifact != null)
        {
            String body = "{\"definitionId\":\"" + definition.getId().toString()
                    + "\",\"description\":\"Continous integration from jenkins build\",\"artifacts\":[{\"alias\":\""
                    + jenkinsArtifact.getAlias()
                    + "\",\"instanceReference\":{\"name\":\""
                    + buildName +"\",\"id\":\""
                    + buildId + "\"}},]}";
            createRelease.setRequestBody(body);
            listener.getLogger().printf("Triggering release...%n");
            status = client.executeMethod(createRelease);
            response = createRelease.getResponseBodyAsString();
            if(status >= 200 && status < 300)
            {
                JSONObject object = new JSONObject(response);
                listener.getLogger().printf("Release Name: %s%n", object.getString("name"));
                listener.getLogger().printf("Release id: %s%n", object.getString("id"));
            }
        }
        else
        {
            listener.getLogger().printf("No jenkins artifact found with name: %s%n", jobName);
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