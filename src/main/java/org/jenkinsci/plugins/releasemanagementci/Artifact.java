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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author angoya
 */
public class Artifact {

private Integer id;
private String type;
private String alias;
private DefinitionReference definitionReference;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* 
* @return
* The id
*/
public Integer getId() {
return id;
 }

/**
* 
* @param id
* The id
*/
public void setId(Integer id) {
this.id = id;
 }

/**
* 
* @return
* The type
*/
public String getType() {
return type;
 }

/**
* 
* @param type
* The type
*/
public void setType(String type) {
this.type = type;
 }

/**
* 
* @return
* The alias
*/
public String getAlias() {
return alias;
 }

/**
* 
* @param alias
* The alias
*/
public void setAlias(String alias) {
this.alias = alias;
 }

/**
* 
* @return
* The definitionReference
*/
public DefinitionReference getDefinitionReference() {
return definitionReference;
 }

/**
* 
* @param definitionReference
* The definitionReference
*/
public void setDefinitionReference(DefinitionReference definitionReference) {
this.definitionReference = definitionReference;
 }

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
 }

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
 }

}
