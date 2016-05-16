package org.jenkinsci.plugins.releasemanagementci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinitionResponse {

private Integer count;
private List<ReleaseDefinition> value = new ArrayList<ReleaseDefinition>();
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* 
* @return
* The count
*/
public Integer getCount() {
return count;
 }

/**
* 
* @param count
* The count
*/
public void setCount(Integer count) {
this.count = count;
 }

/**
* 
* @return
* The value
*/
public List<ReleaseDefinition> getValue() {
return value;
 }

/**
* 
* @param value
* The value
*/
public void setValue(List<ReleaseDefinition> value) {
this.value = value;
 }

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
 }

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
 }

}
