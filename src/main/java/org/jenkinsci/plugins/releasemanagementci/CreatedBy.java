/*
 * The MIT License
 *
 * Copyright 2016 angoya.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.releasemanagementci;

/**
 *
 * @author angoya
 */
import java.util.HashMap;
import java.util.Map;

public class CreatedBy {

private String id;
private String displayName;
private String uniqueName;
private String url;
private String imageUrl;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* 
* @return
* The id
*/
public String getId() {
return id;
 }

/**
* 
* @param id
* The id
*/
public void setId(String id) {
this.id = id;
 }

/**
* 
* @return
* The displayName
*/
public String getDisplayName() {
return displayName;
 }

/**
* 
* @param displayName
* The displayName
*/
public void setDisplayName(String displayName) {
this.displayName = displayName;
 }

/**
* 
* @return
* The uniqueName
*/
public String getUniqueName() {
return uniqueName;
 }

/**
* 
* @param uniqueName
* The uniqueName
*/
public void setUniqueName(String uniqueName) {
this.uniqueName = uniqueName;
 }

/**
* 
* @return
* The url
*/
public String getUrl() {
return url;
 }

/**
* 
* @param url
* The url
*/
public void setUrl(String url) {
this.url = url;
 }

/**
* 
* @return
* The imageUrl
*/
public String getImageUrl() {
return imageUrl;
 }

/**
* 
* @param imageUrl
* The imageUrl
*/
public void setImageUrl(String imageUrl) {
this.imageUrl = imageUrl;
 }

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
 }

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
 }

}
