package org.jenkinsci.plugins.releasemanagementci;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author Ankit Goyal
 */

public class ReleaseManagementHttpClient
{
    private final HttpClient httpClient;
    private final String personalAccessToken;
    private final String accountUrl;
    private final String basicAuth;
    
    ReleaseManagementHttpClient(String accountUrl, String personalAccessToken)
    {
        this.accountUrl = accountUrl;
        this.personalAccessToken = personalAccessToken;
        this.httpClient = new HttpClient();
        this.basicAuth = "Basic " + new String(Base64.encodeBase64((this.personalAccessToken + ":").getBytes(Charset.defaultCharset())), Charset.defaultCharset());
    }
    
    public List<ReleaseDefinition> GetReleaseDefinitions(String project) throws ReleaseManagementExcpetion
    {
        String url = this.accountUrl + project + "/_apis/release/definitions?$expand=artifacts";
        String response = this.ExecuteGetMethod(url);
        DefinitionResponse definitionResponse = new Gson().fromJson(response, DefinitionResponse.class);
        return definitionResponse.getValue();
    }
    
    public String CreateRelease(String project, String body) throws ReleaseManagementExcpetion
    {
        String url = this.accountUrl + project + "/_apis/release/releases?api-version=3.0-preview.2";
        return this.ExecutePostmethod(url, body);
    }
    
    private String ExecutePostmethod(String url, String body) throws ReleaseManagementExcpetion
    {
        PostMethod postMethod = new PostMethod(url);
        postMethod.addRequestHeader("Authorization", this.basicAuth);
        postMethod.addRequestHeader("Content-Type", "application/json");
        postMethod.setRequestBody(body);
        String response;
        try
        {
            int status = this.httpClient.executeMethod(postMethod);
            response = postMethod.getResponseBodyAsString();
            if(status >= 300)
            {
                throw new ReleaseManagementExcpetion("Error occurred.%nStatus: " + status + "%nResponse: " + response + "%n");
            }
        }
        catch(HttpException ex)
        {
            throw new ReleaseManagementExcpetion(ex);
        }
        catch(IOException ex)
        {
            throw new ReleaseManagementExcpetion(ex);
        }
        catch(Exception ex)
        {
            throw new ReleaseManagementExcpetion(ex);
        }
        
        return response;
    }
    
    private String ExecuteGetMethod(String url) throws ReleaseManagementExcpetion
    {
        GetMethod getMethod = new GetMethod(url);
        getMethod.addRequestHeader("Authorization", this.basicAuth);
        String response;
        try
        {
            int status = this.httpClient.executeMethod(getMethod);
            response = getMethod.getResponseBodyAsString();
            if(status >= 300)
            {
                throw new ReleaseManagementExcpetion("Error occurred.%nStatus: " + status + "%nResponse: " + response + "%n");
            }
        }
        catch(HttpException ex)
        {
            throw new ReleaseManagementExcpetion(ex);
        }
        catch(IOException ex)
        {
            throw new ReleaseManagementExcpetion(ex);
        }
        catch(Exception ex)
        {
            throw new ReleaseManagementExcpetion(ex);
        }
        
        return response;
    }
    
    private class DefinitionResponse
    {

        private Integer count;
        private List<ReleaseDefinition> value = new ArrayList<>();
        private final Map<String, Object> additionalProperties = new HashMap<>();

        /**
        * 
        * @return
        * The count
        */
        public Integer getCount()
        {
            return count;
        }

        /**
        * 
        * @param count
        * The count
        */
        public void setCount(Integer count)
        {
            this.count = count;
        }

        /**
        * 
        * @return
        * The value
        */
        public List<ReleaseDefinition> getValue()
        {
            return value;
        }

        /**
        * 
        * @param value
        * The value
        */
        public void setValue(List<ReleaseDefinition> value)
        {
            this.value = value;
        }

        public Map<String, Object> getAdditionalProperties()
        {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value)
        {
            this.additionalProperties.put(name, value);
        }
    }
}
