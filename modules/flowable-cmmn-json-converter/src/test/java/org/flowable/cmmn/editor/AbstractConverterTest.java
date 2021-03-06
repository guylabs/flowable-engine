/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.cmmn.editor;

import java.io.InputStream;

import org.flowable.cmmn.editor.json.converter.CmmnJsonConverter;
import org.flowable.cmmn.model.CmmnModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class AbstractConverterTest {

    protected CmmnModel readJsonFile() throws Exception {
        InputStream jsonStream = this.getClass().getClassLoader().getResourceAsStream(getResource());
        JsonNode modelNode = new ObjectMapper().readTree(jsonStream);
        CmmnModel cmmnModel = new CmmnJsonConverter().convertToCmmnModel(modelNode);
        return cmmnModel;
    }

    protected CmmnModel convertToJsonAndBack(CmmnModel cmmnModel) {
        ObjectNode modelNode = new CmmnJsonConverter().convertToJson(cmmnModel);
        cmmnModel = new CmmnJsonConverter().convertToCmmnModel(modelNode);
        return cmmnModel;
    }

    protected abstract String getResource();
}
